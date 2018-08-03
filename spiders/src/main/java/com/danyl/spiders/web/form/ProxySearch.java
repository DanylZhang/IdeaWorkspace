package com.danyl.spiders.web.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class ProxySearch {
    // 如果属性名和json字段一一对应就不需要添加JsonProperty注解纠正了
    @JsonProperty("page")
    @Min(value = 1, message = "page must great than 1")
    private Integer pageIndex;

    @JsonProperty("limit")
    @Max(value = 500, message = "limit too large")
    private Integer pageSize;

    @URL(message = "请输入正确的URL地址")
    private String url;
    private String regex;

    @Min(value = 1, message = "至少1秒超时等待时间")
    @Max(value = 60, message = "至多60秒超时等待时间")
    private Integer timeout;

    private String where;
    private List<Map<String,String>> orderBy;
}