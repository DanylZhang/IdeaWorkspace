package com.danyl.learnjava.lscjszconcurrency.ch5;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class SemaphoreDemo implements Runnable {

    final Semaphore semaphore = new Semaphore(5);

    @Override
    public void run() {
        try {
            semaphore.acquire();
            // 模拟耗时操作
            Thread.sleep(2000);
            System.out.println(Thread.currentThread().getId() + ":done!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        final SemaphoreDemo demo = new SemaphoreDemo();
        for (int i = 0; i < 20; i++) {
            executorService.submit(demo);
        }
    }
}
