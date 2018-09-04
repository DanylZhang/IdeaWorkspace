package com.danyl;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DistributedClient {

    private final String connectString = "192.168.100.101:2181,192.168.100.102:2181,192.168.100.103:2181";
    private final int sessionTimeout = 2000;
    private final String groupNode = "/servers";

    // 信号量，阻塞程序执行，用于等待zookeeper连接成功，发送成功信号
    private static final CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private ZooKeeper zkClient = null;
    private volatile List<String> serverList;

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {

        // 获取zk连接
        DistributedClient distributedClient = new DistributedClient();
        distributedClient.getConnect();

        // 获取servers的子节点信息(并监听)，从中获取服务器信息列表
        distributedClient.getServerList();
        // 业务逻辑
        distributedClient.handleBussiness();
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

            // 收到事件通知后的回调函数
            try {
                getServerList();
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
     * 获取服务器信息列表
     */
    public void getServerList() throws KeeperException, InterruptedException {
        // 获取服务器子节点信息，并且对父节点进行监听
        List<String> children = zkClient.getChildren(groupNode, true);
        List<String> servers = new ArrayList<>();
        for (String child : children) {
            byte[] data = zkClient.getData(groupNode + "/" + child, false, null);
            servers.add(new String(data));
        }
        // 把servers赋值给成员变量serverList,以提供给各业务线程使用
        serverList = servers;
        // 打印服务器列表
        System.out.println(serverList);
    }

    /**
     * 业务功能
     */
    public void handleBussiness() throws InterruptedException {
        System.out.println("client start working...");
        Thread.sleep(Long.MAX_VALUE);
    }
}
