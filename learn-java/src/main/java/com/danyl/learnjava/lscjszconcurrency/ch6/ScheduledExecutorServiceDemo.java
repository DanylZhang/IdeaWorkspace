package com.danyl.learnjava.lscjszconcurrency.ch6;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutorServiceDemo {
    public static void main(String[] args) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
        // 如果前面的任务没有完成，则调度也不会启动
        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    System.out.println(System.currentTimeMillis() / 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 2, TimeUnit.SECONDS);
    }
}
