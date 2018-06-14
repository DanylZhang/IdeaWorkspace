package com.danyl.concurrency.example.publish;

import com.danyl.concurrency.annotations.NotRecommend;
import com.danyl.concurrency.annotations.NotThreadSafe;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NotThreadSafe
@NotRecommend
public class Escape {

    private int thisCanBeEscape = 0;

    public Escape() {
        new InnerClass();
    }

    private class InnerClass {
        public InnerClass() {
            // 这里是不正确的创建，最好使用工厂方法和私有构造方法创建对象，未完创建之前不可以发布对象
            log.info("{}", Escape.this.thisCanBeEscape);
        }
    }

    public static void main(String[] args) {
        new Escape();
    }
}
