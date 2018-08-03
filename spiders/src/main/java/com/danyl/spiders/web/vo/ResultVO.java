package com.danyl.spiders.web.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class ResultVO<T> implements Serializable {

    private static final long serialVersionUID = 1658549389415774549L;

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

    public ResultVO(Integer code) {
        this.code = code;
    }

    public ResultVO(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static <T> ResultVO<T> of(T result) {
        ResultVO<T> resultVO = new ResultVO<>(ResultEnum.SUCCESS.code);
        resultVO.setData(result);
        return resultVO;
    }

    public static <T> ResultVO<T> of(Integer code, String msg) {
        return new ResultVO<>(code, msg);
    }

    public static <T> ResultVO<T> success() {
        return new ResultVO<>(ResultEnum.SUCCESS.code);
    }

    public static <T> ResultVO<T> notFound() {
        return new ResultVO<>(ResultEnum.NOT_FOUND.code, ResultEnum.NOT_FOUND.msg);
    }

    public static <T> ResultVO<T> inValidParameter() {
        return new ResultVO<>(ResultEnum.INVALID_PARAMETER.code, ResultEnum.INVALID_PARAMETER.msg);
    }

    public enum ResultEnum {
        SUCCESS(0, "Ok"),

        NOT_FOUND(404, "Not Found Resource!"),
        NOT_LOGIN(403, "User not login!"),
        INVALID_PARAMETER(401, "invalid parameter!");

        private Integer code;
        private String msg;

        ResultEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }
}