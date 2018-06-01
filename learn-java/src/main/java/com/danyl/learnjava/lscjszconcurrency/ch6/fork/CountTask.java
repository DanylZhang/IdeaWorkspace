package com.danyl.learnjava.lscjszconcurrency.ch6.fork;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class CountTask extends RecursiveTask<Long> {

    public static Long suM(long end) {
        long sum = 0L;
        for (int i = 0; i < end; i++) {
            sum += i;
        }
        return sum;
    }

    private static final int THRESHOLD = 10000;
    private long start;
    private long end;

    public CountTask(long start, long end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public Long compute() {
        long sum = 0;
        boolean canCompute = (end - start) < THRESHOLD;
        if (canCompute) {
            for (long i = start; i <= end; i++) {
                sum += suM(i);
            }
        } else {
            // 分成100个小任务
            long step = (start + end) / 100;
            ArrayList<CountTask> subTasks = new ArrayList<CountTask>();
            long pos = start;
            for (int i = 0; i < 100; i++) {
                long lastOne = pos + step;
                if (lastOne > end) {
                    lastOne = end;
                }
                CountTask subTask = new CountTask(pos, lastOne);
                pos += step + 1;
                subTasks.add(subTask);
                subTask.fork();
            }
            for (CountTask subTask : subTasks) {
                sum += subTask.join();
            }
        }
        return sum;
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        long end = 0;
        long sum = 0L;
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        CountTask countTask = new CountTask(0, 200000L);
        ForkJoinTask<Long> result = forkJoinPool.submit(countTask);
        try {
            sum = result.get();
            System.out.println("sum=" + sum);
            end = System.currentTimeMillis();
            System.out.println((end - start) / 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        start = System.currentTimeMillis();
        sum = 0L;
        for (int i = 0; i <= 200000L; i++) {
            sum += suM(i);
        }
        end = System.currentTimeMillis();
        System.out.println(sum);
        System.out.println((end - start) / 1000);
    }
}
