package com.danyl.lscjszconcurrency.ch5;

import com.danyl.lscjszconcurrency.ch5.deadlock.DeadLockChecker;

import java.util.concurrent.locks.ReentrantLock;

public class ReenterLockInterrupt implements Runnable {

    public static ReentrantLock lock1 = new ReentrantLock();
    public static ReentrantLock lock2 = new ReentrantLock();

    int lock;

    /**
     * 控制加锁顺序，方便构造死锁
     *
     * @param lock int
     */
    public ReenterLockInterrupt(int lock) {
        this.lock = lock;
    }

    @Override
    public void run() {
        try {
            if (lock == 1) {
                lock1.lockInterruptibly();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
                lock2.lockInterruptibly();
            } else {
                lock2.lockInterruptibly();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
                lock1.lockInterruptibly();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (lock1.isHeldByCurrentThread()) {
                lock1.unlock();
            }
            if (lock2.isHeldByCurrentThread()) {
                lock2.unlock();
            }
            System.out.println(Thread.currentThread().getId() + ":线程退出");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ReenterLockInterrupt r1 = new ReenterLockInterrupt(1);
        ReenterLockInterrupt r2 = new ReenterLockInterrupt(2);

        Thread t1 = new Thread(r1);
        Thread t2 = new Thread(r2);

        t1.start();
        t2.start();
        Thread.sleep(1000);

        // 中断其中一个线程
        DeadLockChecker.check();
    }
}
