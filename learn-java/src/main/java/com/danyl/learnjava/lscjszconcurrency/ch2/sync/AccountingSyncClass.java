package com.danyl.learnjava.lscjszconcurrency.ch2.sync;

public class AccountingSyncClass implements Runnable {

    static int i = 0;

    // 锁加在类的字节码对象上
    public static synchronized void increase() {
        i++;
    }

    @Override
    public void run() {
        for (int j = 0; j < 100000000; j++) {
            increase();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 不是同一个对象锁 instance
        Thread t1 = new Thread(new AccountingSyncClass());
        Thread t2 = new Thread(new AccountingSyncClass());
        long startTime = System.currentTimeMillis();
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        long endTime = System.currentTimeMillis();
        long elapse = (endTime - startTime);
        System.out.println(i);
        System.out.println("elapse:" + elapse + "ms");
    }
}
