package com.danyl;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * Hello world!
 */
public class App {

    private static final String connectString = "192.168.100.101:2181,192.168.100.102:2181,192.168.100.103:2181";
    private static final int sessionTimeout = 30000;
    private static ZooKeeper zkClient = null;

    @Before
    public void init() throws IOException {
        zkClient = new ZooKeeper(connectString, sessionTimeout, watchedEvent -> {
            // 收到事件通知后的回调函数
            System.out.println(watchedEvent.getType() + "---" + watchedEvent.getPath());
            try {
                zkClient.getChildren("/", true);
            } catch (Exception ignored) {
            }
        });
    }

    // 创建znode
    @Test
    public void testCreate() throws KeeperException, InterruptedException {
        String s = zkClient.create("/test", "hello".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    // 判断znode是否存在
    @Test
    public void testExist() throws KeeperException, InterruptedException {
        Stat stat = zkClient.exists("/test", false);
        System.out.println(stat == null ? "not exist" : "exist");
    }

    // 获取子节点
    @Test
    public void getChildren() throws KeeperException, InterruptedException {
        List<String> children = zkClient.getChildren("/", true);
        for (String child : children) {
            System.out.println(child);
        }
        Thread.sleep(Integer.MAX_VALUE);
    }

    // 获取znode的数据
    @Test
    public void getData() throws KeeperException, InterruptedException {
        byte[] data = zkClient.getData("/test", false, null);
        System.out.println(new String(data));
    }

    // 删除znode
    @Test
    public void deleteZnode() throws KeeperException, InterruptedException {
        // 参数2: 指定要删除的版本，-1表示删除所有版本
        zkClient.delete("/test", -1);
    }

    // set data
    @Test
    public void setData() throws KeeperException, InterruptedException {
        zkClient.setData("/test", "aaa".getBytes(), -1);
        byte[] data = zkClient.getData("/test", false, null);
        System.out.println(new String(data));
    }

    @After
    public void tearDown() throws InterruptedException {
        zkClient.close();
    }
}