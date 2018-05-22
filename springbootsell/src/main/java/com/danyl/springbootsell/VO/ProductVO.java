package com.danyl.springbootsell.VO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 商品（包含类目信息）
 */
@Data
@Accessors(chain = true)
public class ProductVO implements Serializable {

    private static final long serialVersionUID = -8083052505415038844L;

    @JsonProperty("name")
    private String categoryName;

    @JsonProperty("type")
    private Integer categoryType;

    @JsonProperty("foods")
    private List<ProductInfoVO> productInfoVOList;
}
