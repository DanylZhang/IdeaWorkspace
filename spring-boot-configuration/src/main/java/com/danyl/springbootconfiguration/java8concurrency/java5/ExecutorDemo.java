package com.danyl.springbootconfiguration.java8concurrency.java5;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorDemo {
    public static void main(String[] args) {
        Executor executor = Executors.newFixedThreadPool(1);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.printf("[Thread : %s]Hello world...\n", Thread.currentThread().getName());
            }
        });
        //合理的关闭线程池
        if (executor instanceof ExecutorService){
            ExecutorService executorService = ExecutorService.class.cast(executor);
            executorService.shutdown();
        }
    }
}