package com.danyl.springbootsell.utils;

import com.danyl.springbootsell.VO.ResultVO;

public class ResultVOUtil {

    public static ResultVO success(Object object) {
        ResultVO<Object> resultVO = new ResultVO<>();
        resultVO.setData(object);
        resultVO.setCode(0);
        resultVO.setMsg("成功");
        return resultVO;
    }

    public static ResultVO success() {
        return success(null);
    }

    public static ResultVO error(Integer code, String msg) {
        ResultVO<Object> resultVO = new ResultVO<>();
        return resultVO.setCode(code).setMsg(msg);
    }
}
