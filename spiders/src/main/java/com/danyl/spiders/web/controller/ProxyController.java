package com.danyl.spiders.web.controller;

import com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy;
import com.danyl.spiders.jooq.gen.proxy.tables.records.ProxyRecord;
import com.danyl.spiders.tasks.CheckProxyTask;
import com.danyl.spiders.web.form.ProxySearch;
import com.danyl.spiders.web.vo.ResultVO;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.DSLContext;
import org.jooq.SelectQuery;
import org.jooq.SortField;
import org.jooq.SortOrder;
import org.jooq.impl.DSL;
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

        int total = proxy.fetchCount(PROXY);
        SelectQuery<ProxyRecord> query = proxy.selectFrom(PROXY).getQuery();
        if (StringUtils.isNotBlank(proxySearch.getWhere())) {
            query.addConditions(DSL.condition(proxySearch.getWhere()));
        }
        List<? extends SortField<?>> orderList = proxySearch.getOrderBy().stream()
                .flatMap(map -> map.entrySet().stream().map(entry -> {
                    // h2里面全是大写的字段名
                    String fieldName = entry.getKey().toUpperCase();
                    // SQL order
                    String fieldOrder = "ascending".equalsIgnoreCase(entry.getValue()) ? "ASC" : "DESC";
                    return PROXY.field(fieldName).sort(SortOrder.valueOf(fieldOrder));
                }))
                .distinct()
                .collect(Collectors.toList());
        query.addOrderBy(orderList);
        query.addLimit((proxySearch.getPageIndex() - 1) * proxySearch.getPageSize(), proxySearch.getPageSize());
        List<Proxy> proxies = query.fetchInto(Proxy.class);

        // 如果传入了校验URL
        if (StringUtils.isNotEmpty(proxySearch.getUrl())) {
            ExecutorService fixedThreadPool = Executors.newFixedThreadPool(64);
            CountDownLatch countDownLatch = new CountDownLatch(proxySearch.getPageSize());
            final ConcurrentHashMap<String, Proxy> proxiesMap = new ConcurrentHashMap<>();
            proxy.selectFrom(PROXY)
                    .where(PROXY.IS_VALID.eq(true))
                    .fetchInto(Proxy.class)
                    .parallelStream()
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
            proxies = proxiesMap.values().stream().sorted(Comparator.comparingInt(Proxy::getSpeed)).collect(Collectors.toList());
            total = proxies.size();
        }

        Map<Object, Object> result = ImmutableMap.builder()
                .put("proxies", proxies)
                .put("total", total)
                .build();
        return ResultVO.of(result);
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