package com.danyl.concurrency.example.singleton;

import com.danyl.learnjava.mukeconcurrency.annotations.ThreadSafe;

/**
 * 饿汉模式
 * 单例实例在类装载使用时进行创建
 */
@ThreadSafe
public class SingletonExample6 {

    // 私有构造函数
    private SingletonExample6() {
    }

    // 单例对象
    private static SingletonExample6 instance = null;

    // 注意声明静态域和静态代码块的先后顺序
    static {
        instance = new SingletonExample6();
    }

    // 静态的工厂方法
    public static SingletonExample6 getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        System.out.println(getInstance());
        System.out.println(getInstance());
    }
}
