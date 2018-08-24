package com.danyl.spiders.utils;

import com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy;
import com.danyl.spiders.jooq.gen.proxy.tables.records.ProxyRecord;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;

public class ProxyUtil {

    private static java.net.Proxy.Type getProxyType(String protocol) {
        if (StringUtils.isBlank(protocol)) {
            return java.net.Proxy.Type.HTTP;
        }

        if (protocol.toLowerCase().startsWith("http")) {
            return java.net.Proxy.Type.HTTP;
        } else if (protocol.toLowerCase().startsWith("socks")) {
            return java.net.Proxy.Type.SOCKS;
        } else {
            return java.net.Proxy.Type.HTTP;
        }
    }

    private static java.net.Proxy getProxy(String protocol, String ip, Integer port) {
        java.net.Proxy.Type proxyType = getProxyType(protocol);
        InetSocketAddress inetSocketAddress = InetSocketAddress.createUnresolved(ip, port);
        return new java.net.Proxy(proxyType, inetSocketAddress);
    }

    public static java.net.Proxy getProxy(Proxy proxy) {
        return getProxy(proxy.getProtocol(), proxy.getIp(), proxy.getPort());
    }

    public static java.net.Proxy getProxy(ProxyRecord proxyRecord) {
        Proxy proxy = proxyRecord.into(Proxy.class);
        return getProxy(proxy);
    }

    private static org.openqa.selenium.Proxy getSeleniumProxy(String protocol, String ip, Integer port) {
        String _proxy = ip + ":" + port;
        org.openqa.selenium.Proxy proxy1 = new org.openqa.selenium.Proxy();

        if (protocol.toLowerCase().startsWith("https")) {
            proxy1.setHttpProxy(_proxy);
            proxy1.setFtpProxy(_proxy);
            proxy1.setSslProxy(_proxy);
        } else if (protocol.toLowerCase().startsWith("socks5")) {
            proxy1.setSslProxy(_proxy);
            proxy1.setSocksProxy(_proxy);
            proxy1.setSocksVersion(5);
        } else if (protocol.toLowerCase().startsWith("socks4")) {
            proxy1.setSslProxy(_proxy);
            proxy1.setSocksProxy(_proxy);
            proxy1.setSocksVersion(4);
        } else {
            proxy1.setHttpProxy(_proxy);
            proxy1.setFtpProxy(_proxy);
        }

        return proxy1;
    }

    public static org.openqa.selenium.Proxy getSeleniumProxy(Proxy proxy) {
        return getSeleniumProxy(proxy.getProtocol(), proxy.getIp(), proxy.getPort());
    }

    public static org.openqa.selenium.Proxy getSeleniumProxy(ProxyRecord proxyRecord) {
        Proxy proxy = proxyRecord.into(Proxy.class);
        return getSeleniumProxy(proxy);
    }
}
