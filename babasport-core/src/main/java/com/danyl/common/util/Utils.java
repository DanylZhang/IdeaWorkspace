package com.danyl.common.util;

import org.springframework.http.converter.json.GsonBuilderUtils;

public class Utils {
    public static void var_dump(Object object) {
        System.out.println(GsonBuilderUtils.gsonBuilderWithBase64EncodedByteArrays().setPrettyPrinting().create().toJson(object));
    }
}