package com.danyl.lscjszconcurrency.ch5;

import java.util.concurrent.locks.LockSupport;

public class LockSupportDemo {

    public static Object u = new Object();
    static ChangeObjectThread t1 = new ChangeObjectThread("t1");
    static ChangeObjectThread t2 = new ChangeObjectThread("t2");

    public static class ChangeObjectThread extends Thread {
        public ChangeObjectThread(String name) {
            super.setName(name);
        }

        @Override
        public void run() {
            synchronized (u) {
                System.out.println("in " + getName());
                // park被中断不会抛异常，会立即返回，并可以从Thread.interrupted()的到中断标志
                LockSupport.park();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        t1.start();
        Thread.sleep(100);
        t2.start();
        LockSupport.unpark(t1);
        LockSupport.unpark(t2);
        t1.join();
        t2.join();
        // park 和 unpark 就是优化过的 suspend 和 resume
        // unpark 和 park 没有先后顺序的要求，只要曾经unpark过一次，就能抵消掉一次park
        // resume 必须在 suspend 之后，实际应用很难保证先后顺序，故弃用
    }
}
