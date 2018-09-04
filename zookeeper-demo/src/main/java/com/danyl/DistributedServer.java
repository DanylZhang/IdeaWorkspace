package com.danyl;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.CountDownLatch;

public class DistributedServer {

    private final String connectString = "192.168.100.101:2181,192.168.100.102:2181,192.168.100.103:2181";
    private final int sessionTimeout = 2000;
    private final String groupNode = "/servers";

    // 信号量，阻塞程序执行，用于等待zookeeper连接成功，发送成功信号
    private static final CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private ZooKeeper zkClient = null;

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {

        // 获取zk连接
        DistributedServer distributedServer = new DistributedServer();
        distributedServer.getConnect();

        // 注册服务器信息
        distributedServer.registerServer(InetAddress.getLocalHost().getHostName());
        // 业务逻辑
        distributedServer.handleBussiness(InetAddress.getLocalHost().getHostName());
    }

    /**
     * 获取zk连接
     */
    public void getConnect() throws IOException {
        zkClient = new ZooKeeper(connectString, sessionTimeout, watchedEvent -> {

            Watcher.Event.KeeperState state = watchedEvent.getState();
            Watcher.Event.EventType type = watchedEvent.getType();
            // 如果是建立连接
            if (Watcher.Event.KeeperState.SyncConnected == state) {
                if (Watcher.Event.EventType.None == type) {
                    // 如果建立连接成功，则发送信号量，让后续阻塞程序向下执行
                    connectedSemaphore.countDown();
                    System.out.println("zookeeper 成功建立连接");
                }
            }

            try {
                zkClient.getChildren("/", true);
            } catch (Exception ignored) {
            }
        });

        // 进行阻塞
        try {
            connectedSemaphore.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向zk集群注册服务器信息
     */
    public void registerServer(String hostname) throws KeeperException, InterruptedException {
        String s = zkClient.create(groupNode + "/server", hostname.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(hostname + " is online... " + s);
    }

    /**
     * 业务功能
     */
    public void handleBussiness(String hostname) throws InterruptedException {
        System.out.println(hostname + " is working...");
        Thread.sleep(Long.MAX_VALUE);
    }
}
