package com.danyl.concurrency.example.singleton;

import com.danyl.learnjava.mukeconcurrency.annotations.NotThreadSafe;

/**
 * 懒汉模式 -》 双重同步锁单例模式
 * 单例实例在第一次使用时进行创建
 */
@NotThreadSafe
public class SingletonExample4 {

    // 私有构造函数
    private SingletonExample4() {
    }

    //1、memory = allocate() 分配对象的内存空间
    //2、ctorInstance() 初始化对象
    //3、instance = memory 设置instance指向刚分配的内存

    // JVM和CPU优化，发生了指令重排，所以考虑使用volatile禁止指令重排
    //1、memory = allocate() 分配对象的内存空间
    //3、instance = memory 设置instance指向刚分配的内存
    //2、ctorInstance() 初始化对象

    // 单例对象
    private static SingletonExample4 instance = null;

    // 静态的工厂方法
    public static SingletonExample4 getInstance() {
        if (instance == null) { // 双重检查机制    // B thread 第一步，然后instance != null 可能会返回未ctorInstance()的对象
            synchronized (SingletonExample4.class) { // 同步锁加在类的class对象上
                if (instance == null) {
                    instance = new SingletonExample4(); // A thread 第三步
                } else {
                    return instance;
                }
            }
        }
        return instance;
    }
}
