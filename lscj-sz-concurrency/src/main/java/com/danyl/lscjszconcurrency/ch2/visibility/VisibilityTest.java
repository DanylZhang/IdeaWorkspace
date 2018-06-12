package com.danyl.lscjszconcurrency.ch2.visibility;

public class VisibilityTest extends Thread {

    // 加上 volatile 就好了
    private boolean stop;

    public void run() {
        long i = 0;
        while (!stop) {
            i++;
        }
        System.out.println("finish loop, i = " + i);
    }

    public void stopIt() {
        stop = true;
    }

    public boolean isStop() {
        return stop;
    }

    public static void main(String[] args) throws InterruptedException {
        VisibilityTest visibilityTest = new VisibilityTest();
        visibilityTest.start();

        Thread.sleep(1000);
        visibilityTest.stopIt();
        Thread.sleep(2000);
        System.out.println("finish main");
        System.out.println(visibilityTest.isStop());
    }
}
