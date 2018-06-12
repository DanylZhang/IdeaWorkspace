package com.danyl.lscjszconcurrency.ch2.waitnotify;

public class SimpleWN {

    final static Object object = new Object();

    public static class T1 extends Thread {
        public void run() {
            try {
                // 未拿到object锁监视器时，是不能调用它的wait方法释放当前线程持有的object对象锁，即不能释放未曾获得的锁
                object.wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
            synchronized (object) {
                System.out.println(System.currentTimeMillis() + ":T1 start!");
                try {
                    System.out.println(System.currentTimeMillis() + ":T1 wait for object!");
                    // 使持有object锁的当前线程等待并释放object锁，直到被notify且重新获得锁才继续往下执行
                    object.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(System.currentTimeMillis() + ":T1 end!");
            }
        }
    }

    public static class T2 extends Thread {
        public void run() {
            synchronized (object) {
                System.out.println(System.currentTimeMillis() + ":T2 start! notify one thread");
                // 唤醒一个等待在object上的线程，同wait需要已经获得锁
                object.notify();
                System.out.println(System.currentTimeMillis() + ":T2 end!");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new T1();
        Thread t2 = new T2();
        t1.start();
        Thread.sleep(1000);
        t2.start();
    }
}
