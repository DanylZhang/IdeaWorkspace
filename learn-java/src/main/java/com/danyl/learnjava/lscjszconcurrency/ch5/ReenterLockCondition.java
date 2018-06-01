package com.danyl.learnjava.lscjszconcurrency.ch5;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReenterLockCondition implements Runnable {

    public static ReentrantLock lock = new ReentrantLock();
    public static Condition condition = lock.newCondition();

    @Override
    public void run() {
        try {
            // 和 object.wait一样，需要先获得锁监视器
            // condition.await();

            lock.lock();
            condition.await();
            System.out.println("Thread is going on");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ReenterLockCondition reenterLockCondition = new ReenterLockCondition();
        Thread t1 = new Thread(reenterLockCondition);
        t1.start();
        Thread.sleep(2000);

        //通知线程t1继续执行
        lock.lock();
        condition.signal();
        lock.unlock();
    }
}
