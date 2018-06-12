package com.danyl.lscjszconcurrency.ch2.sync;

public class AccountingSync implements Runnable {

    static AccountingSync instance = new AccountingSync();

    static int i = 0;

    @Override
    public void run() {
        for (int j = 0; j < 100000000; j++) {
            // 锁加载对象实例上
            synchronized (instance) {
                i++;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(instance);
        Thread t2 = new Thread(instance);
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
