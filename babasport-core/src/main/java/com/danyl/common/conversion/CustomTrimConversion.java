package com.danyl.common.conversion;

import org.springframework.core.convert.converter.Converter;

public class CustomTrimConversion implements Converter<String, String> {
    @Override
    public String convert(String s) {
        try {
            if (null != s) {
                s = s.trim();
                if (!"".equals(s)) {
                    return s;
                }
            }
        } catch (Exception e) {

        }
        return null;
    }
}