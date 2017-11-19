package com.danyl.springbootconfiguration.java8concurrency.java5;

import java.sql.SQLException;
import java.util.concurrent.*;

public class FutureDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Future<String> future = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return String.format("[Thread : %s]Hello world...\n", Thread.currentThread().getName());
                // 没有办法处理异常
                //throw new SQLException("数据库连接失败了");
            }
        });
        //等待完成
        while (true) {
            if (future.isDone()) {
                break;
            }
        }
        //get 和 join一样会阻塞当前线程
        String s = future.get();
        System.out.println(s);
        executorService.shutdown();
    }
}