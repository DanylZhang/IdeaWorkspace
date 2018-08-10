package com.danyl.spiders.web.controller;

import com.danyl.spiders.jooq.gen.proxy.tables.records.ProxyRecord;
import com.danyl.spiders.tasks.CheckProxyTask;
import com.danyl.spiders.web.form.ProxySearch;
import com.danyl.spiders.web.vo.ResultVO;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.danyl.spiders.jooq.gen.proxy.Tables.PROXY;

@Slf4j
@Controller
@RequestMapping("/api")
public class ProxyController {

    @Resource(name = "DSLContextProxy")
    private DSLContext proxy;

    @GetMapping(value = {"/", "/index"})
    public String index() {
        return "index.html";
    }

    @PostMapping("/proxy/table")
    @ResponseBody
    public ResultVO<Map<Object, Object>> table(@RequestBody @Valid final ProxySearch proxySearch, BindingResult bindingResult) {
        System.out.println(proxySearch);
        if (bindingResult.hasErrors()) {
            return ResultVO.of(403, bindingResult.getFieldError().getDefaultMessage());
        }

        Integer total = 0;
        String formatJSON;

        // 如果传入了校验URL
        if (StringUtils.isNotBlank(proxySearch.getUrl())) {
            Pair<String, Integer> pair = getValidProxies(proxySearch);
            formatJSON = pair.getLeft();
            total = pair.getRight();
        } else {
            SelectQuery<Record> query = null;

            // 如果传入了where
            if (StringUtils.isNotBlank(proxySearch.getWhere())) {
                String sql = proxySearch.getWhere().trim();
                // 如果where由select开头
                if (sql.toLowerCase().startsWith("select")) {
                    Table<Record> nested = DSL.table("(" + sql + ")").asTable("TMP");
                    try {
                        total = proxy.selectCount().from(nested).fetchOneInto(Integer.class);
                    } catch (Exception e) {
                        return ResultVO.of(500, e.getMessage());
                    }
                    query = proxy.selectQuery(nested);
                }
                // 如果where由where开头
                else if (sql.toLowerCase().startsWith("where")) {
                    sql = sql.replaceFirst("(?i)where", " ");
                    try {
                        total = proxy.selectCount().from(PROXY).where(sql).fetchOneInto(Integer.class);
                    } catch (Exception e) {
                        return ResultVO.of(500, e.getMessage());
                    }
                    query = proxy.select().from(PROXY).where(sql).getQuery();
                }
            }
            // 默认排序
            else {
                total = proxy.fetchCount(PROXY);
                query = proxy.select().from(PROXY).getQuery();
            }
            try {
                List<? extends SortField<?>> orderList = proxySearch.getOrderBy().stream()
                        .flatMap(map -> map.entrySet().stream().map(entry -> {
                            // h2里面全是大写的字段名
                            String fieldName = entry.getKey().toUpperCase();
                            // SQL order
                            String fieldOrder = "ascending".equalsIgnoreCase(entry.getValue()) ? "ASC" : "DESC";
                            return DSL.field(DSL.quotedName(fieldName)).sort(SortOrder.valueOf(fieldOrder));
                        }))
                        .distinct()
                        .collect(Collectors.toList());
                query.addOrderBy(orderList);
                query.addLimit((proxySearch.getPageIndex() - 1) * proxySearch.getPageSize(), proxySearch.getPageSize());
                formatJSON = query.fetch().formatJSON(JSONFormat.DEFAULT_FOR_RESULTS.recordFormat(JSONFormat.RecordFormat.ARRAY));
            } catch (Exception e) {
                return ResultVO.of(500, e.getMessage());
            }
        }
        Map<String, Object> map = new GsonJsonParser().parseMap(formatJSON);
        Map<Object, Object> result = ImmutableMap.builder()
                .put("fields", map.get("fields"))
                .put("records", map.get("records"))
                .put("total", total)
                .build();
        return ResultVO.of(result);
    }

    /**
     * 获取符合proxySearch url校验的代理
     *
     * @param proxySearch 包含 url
     */
    private Pair<String, Integer> getValidProxies(ProxySearch proxySearch) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(64);
        CountDownLatch countDownLatch = new CountDownLatch(proxySearch.getPageSize());
        final ConcurrentHashMap<String, ProxyRecord> proxiesMap = new ConcurrentHashMap<>();
        proxy.selectFrom(PROXY)
                .where(PROXY.IS_VALID.eq(true))
                .fetch()
                .stream()
                .forEach(proxy1 -> CompletableFuture.runAsync(() -> {
                    Pair<Boolean, Integer> pair = CheckProxyTask.doCheckProxy(proxy1.getIp(), proxy1.getPort(), proxySearch.getUrl(), proxySearch.getRegex(), proxySearch.getTimeout() * 1000);
                    if (pair.getLeft()) {
                        proxy1.setIsValid(true);
                        proxy1.setSpeed(pair.getRight());
                        proxiesMap.put(proxy1.getIp(), proxy1);
                        countDownLatch.countDown();
                    }
                }, fixedThreadPool));
        try {
            countDownLatch.await(proxySearch.getTimeout(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("countDown await error: {}", e.getMessage());
        } finally {
            fixedThreadPool.shutdownNow().clear();
        }
        List<ProxyRecord> proxies = proxiesMap.values().stream().sorted(Comparator.comparingInt(ProxyRecord::getSpeed)).collect(Collectors.toList());

        String formatJSON;

        int total = proxies.size();
        GsonJsonParser gsonJsonParser = new GsonJsonParser();
        List<Object> records = proxies.stream().map(proxyRecord -> {
            String formatJSON1 = proxyRecord.formatJSON(JSONFormat.DEFAULT_FOR_RECORDS.recordFormat(JSONFormat.RecordFormat.ARRAY));
            return gsonJsonParser.parseMap(formatJSON1);
        }).collect(Collectors.toList());
        // 拿到fields
        String formatJSON1 = proxy.selectFrom(PROXY).limit(1).fetch().formatJSON(JSONFormat.DEFAULT_FOR_RESULTS);
        Object fields = gsonJsonParser.parseMap(formatJSON1).get("fields");
        ImmutableMap<Object, Object> build = ImmutableMap.builder()
                .put("fields", fields)
                .put("records", records)
                .build();
        formatJSON = new Gson().toJson(build);
        return Pair.of(formatJSON, total);
    }

    @GetMapping("/proxy/getSqlHint")
    @ResponseBody
    public ResultVO<Map> getSqlHint() {
        Map tables = proxy.fetch("select TABLE_NAME,COLUMN_NAME from INFORMATION_SCHEMA.COLUMNS where TABLE_SCHEMA='PUBLIC';").intoGroups("TABLE_NAME", "COLUMN_NAME");
        Map result = ImmutableMap.builder()
                .put("tables", tables)
                .put("defaultTable", "proxy")
                .build();
        return ResultVO.of(result);
    }
}