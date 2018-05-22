package com.danyl.springbootsell.VO;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class ResultVO<T> implements Serializable {

    private static final long serialVersionUID = 1658799789925775541L;

    /**
     * 错误码
     */
    private Integer code;
    /**
     * 提示信息
     */
    private String msg;
    /**
     * 具体数据
     */
    private T data;
}
