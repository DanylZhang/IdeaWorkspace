package com.danyl.concurrency.example.publish;

import com.danyl.concurrency.annotations.NotThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
@NotThreadSafe
public class UnsafePublish {

    private String[] states = {"a", "b", "c"};

    // public 发布类内部的私有字段的"引用"会对类内部的数据造成不可预知的修改，因为引用是指针，会改变堆内数据
    // 以此方式发布的对象是线程不安全的
    public String[] getStates() {
        return states;
    }

    public static void main(String[] args) {
        UnsafePublish unsafePublish = new UnsafePublish();
        log.info("{}", Arrays.toString(unsafePublish.getStates()));

        // 这种无法保证其他线程对字段的安全性
        unsafePublish.getStates()[0] = "d";
        log.info("{}", Arrays.toString(unsafePublish.getStates()));

    }
}
