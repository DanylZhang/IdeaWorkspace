package com.danyl.lscjszconcurrency.ch2.thread;

public class DaemonDemo {

    public static class DaemonT extends Thread {
        @Override
        public void run() {
            while (true) {
                System.out.println("I am alive");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread daemonT = new DaemonT();
        // 设置为守护线程即和垃圾回收线程和jit线程一样是守护线程
        // 守护线程的运行与否不影响主线程的运行
        // 主线程该退出就退出，不管守护线程
        // 如果不设置为守护线程，java的子线程默认是会阻塞住父线程的
        daemonT.setDaemon(true);
        daemonT.start();

        //Thread.sleep(2000);
    }
}
