package com.danyl.springbootconfiguration.java8concurrency.before5;

public class CompletableRunnableDemo {

    public static void main(String[] args) throws InterruptedException {
        CompletableRunnable runnable = new CompletableRunnable();
        Thread thread = new Thread(runnable,"Sub");
        thread.start();
        thread.join();
        System.out.printf("[Thread : %s]Starting...\n", Thread.currentThread().getName());
        System.out.printf("[Thread : %s]Complete %s...\n", Thread.currentThread().getName(),runnable.isCompleted());
    }

    private static class CompletableRunnable implements Runnable {
        private volatile boolean completed = false;

        @Override
        public void run() {
            System.out.printf("[Thread : %s]Hello world...\n", Thread.currentThread().getName());
            completed = true;
        }

        public boolean isCompleted() {
            return completed;
        }
    }
}
