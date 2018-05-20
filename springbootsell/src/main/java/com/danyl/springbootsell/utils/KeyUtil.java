package com.danyl.springbootsell.utils;

import org.apache.commons.lang3.RandomUtils;

import java.util.UUID;

public class KeyUtil {

    /**
     * 生成唯一的整数主键
     * 格式： 毫秒数+随机数
     */
    public static synchronized String genUniqueKey() {
        int i = RandomUtils.nextInt(100000, 999999);
        return System.currentTimeMillis() + String.valueOf(i);
    }

    /**
     * 生成唯一的 UUID without "-"
     */
    public static synchronized String genUUIDWithoutDelimiter() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
