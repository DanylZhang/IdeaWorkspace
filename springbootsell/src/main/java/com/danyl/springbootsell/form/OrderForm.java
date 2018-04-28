package com.danyl.springbootsell.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class OrderForm {

    /**
     * 买家姓名
     */
    @NotEmpty(message = "姓名必填")
    private String name;


    /**
     * 买家手机号
     */
    @NotEmpty(message = "手机号必填")
    private String phone;


}
