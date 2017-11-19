package com.danyl.springbootconfiguration.java8concurrency.before5;

public class ThreadDemo {
    public static void main(String[] args) throws InterruptedException {
        boolean finished = false;
        Thread thread = new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                System.out.printf("[Thread : %s]Hello world...\n", Thread.currentThread().getName());
            }
        }, "sub");
        thread.start();
        System.out.printf("[Thread : %s]Starting...\n", Thread.currentThread().getName());
    }
}