package com.danyl.springbootconfiguration.java8concurrency.java5;

import java.util.concurrent.*;

public class CallableDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Future<String> future = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return String.format("[Thread : %s]Hello world...\n", Thread.currentThread().getName());
            }
        });
        String s = future.get();
        System.out.println(s);
        executorService.shutdown();
    }
}