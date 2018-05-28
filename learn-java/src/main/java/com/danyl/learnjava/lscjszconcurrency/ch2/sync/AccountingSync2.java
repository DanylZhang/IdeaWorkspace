package com.danyl.learnjava.lscjszconcurrency.ch2.sync;

public class AccountingSync2 implements Runnable {

    static AccountingSync2 instance = new AccountingSync2();

    static int i = 0;

    // 锁加在对象实例上，如果传入的不是同一个实例则锁失效，线程不安全
    public synchronized void increase() {
        i++;
    }

    @Override
    public void run() {
        for (int j = 0; j < 100000000; j++) {
            increase();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 还是同一个对象锁 instance
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
