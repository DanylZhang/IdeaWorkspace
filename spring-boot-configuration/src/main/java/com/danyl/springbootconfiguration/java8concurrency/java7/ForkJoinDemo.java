package com.danyl.springbootconfiguration.java8concurrency.java7;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ForkJoinDemo {
    public static void main(String[] args) {
        //并行：多核参与，真正的同一时刻有多个任务在计算
        //并发：在一个时间段内，多个任务在执行，中间仍然存在等待挂起唤醒等步骤
        //ForkJoin线程池
        System.out.printf("当前公用 ForkJoin 线程池 并行数：%d\n",ForkJoinPool.commonPool().getParallelism());
        System.out.printf("当前 CPU 处理器数：%d\n",Runtime.getRuntime().availableProcessors());
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.invoke(new RecursiveAction() {
            @Override
            protected void compute() {
                System.out.printf("[Thread : %s]Hello world...\n", Thread.currentThread().getName());
            }
        });
        forkJoinPool.shutdown();
    }
}