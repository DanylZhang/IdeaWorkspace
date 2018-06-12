package com.danyl.lscjszconcurrency.ch10;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureAsync {

    public static Integer calc(Integer param) {
        try {
            // 模拟一个长时间的执行
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return param * param;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture
                .supplyAsync(() -> calc(50))
                .thenCompose((i)->CompletableFuture.supplyAsync(()->calc(i)))
                .thenApply((i) -> Integer.toString(i))
                .thenApply((str) -> "\"" + str + "\"")
                .thenAccept(System.out::println);
        voidCompletableFuture.get();
    }
}
