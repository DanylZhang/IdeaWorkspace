package com.danyl.dangdangspider.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.jooq.DSLContext;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import static com.danyl.dangdangspider.jooq.gen.proxy.tables.Proxy.PROXY;

@Slf4j
@Service
public class ProxyEnumSingletonService {



    private List<Proxy> proxies = new ArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock(true); // 防止高并发下写锁拿不到

    public Proxy get() {
        Proxy result = null;
        lock.readLock().lock();
        System.out.println(proxies);
        System.out.println(getInstance());
        System.out.println(getInstance().proxies);
        System.out.println(this.proxies);
        final int size = proxies.size();
        int i = RandomUtils.nextInt(0, size);
        final Iterator<Proxy> iterator = proxies.iterator();
        while (iterator.hasNext()) {
            result = iterator.next();
            if (--i == 0) {
                break;
            }
        }
        lock.readLock().unlock();
        return result;
    }

    // 提供一个便捷的静态方法获取使用代理的Jsoup
    public static Connection getJsoup(String url) {
        final ProxyEnumSingletonService instance = getInstance();
        return Jsoup.connect(url).proxy(instance.get());
    }

    // 私有的构造函数
    private ProxyEnumSingletonService() {
    }

    public static ProxyEnumSingletonService getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        private ProxyEnumSingletonService instance;

        // JVM保证这个方法绝对只调用一次
        Singleton() {
            instance = new ProxyEnumSingletonService();
        }

        public ProxyEnumSingletonService getInstance() {
            return instance;
        }
    }
}
