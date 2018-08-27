package com.danyl.spiders.utils;

import com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy;
import com.danyl.spiders.jooq.gen.proxy.tables.records.ProxyRecord;
import io.vertx.core.net.ProxyOptions;
import io.vertx.core.net.ProxyType;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyUtil {
    public static final Pattern charsetPattern = Pattern.compile("(?i)\\bcharset=\\s*(?:[\"'])?([^\\s,;\"']*)");

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

    public static java.net.Proxy getProxy(String ip, Integer port, String protocol) {
        java.net.Proxy.Type proxyType = getProxyType(protocol);
        InetSocketAddress inetSocketAddress = InetSocketAddress.createUnresolved(ip, port);
        return new java.net.Proxy(proxyType, inetSocketAddress);
    }

    public static java.net.Proxy getProxy(Proxy proxy) {
        return getProxy(proxy.getIp(), proxy.getPort(), proxy.getProtocol());
    }

    public static java.net.Proxy getProxy(ProxyRecord proxyRecord) {
        Proxy proxy = proxyRecord.into(Proxy.class);
        return getProxy(proxy);
    }

    private static org.openqa.selenium.Proxy getChromeProxy(String ip, Integer port, String protocol) {
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

    public static org.openqa.selenium.Proxy getChromeProxy(Proxy proxy) {
        return getChromeProxy(proxy.getIp(), proxy.getPort(), proxy.getProtocol());
    }

    public static org.openqa.selenium.Proxy getChromeProxy(ProxyRecord proxyRecord) {
        Proxy proxy = proxyRecord.into(Proxy.class);
        return getChromeProxy(proxy);
    }

    private static List<String> getPhantomJSProxy(String ip, Integer port, String protocol) {
        String _proxy = "--proxy=" + ip + ":" + port;
        List<String> cliArgsCap = new ArrayList<>();

        if (protocol.toLowerCase().startsWith("http")) {
            cliArgsCap.add(_proxy);
            cliArgsCap.add("--proxy-type=http");
        } else if (protocol.toLowerCase().startsWith("socks")) {
            cliArgsCap.add(_proxy);
            cliArgsCap.add("--proxy-type=socks5");
        } else {
            cliArgsCap.add(_proxy);
            cliArgsCap.add("--proxy-type=http");
        }

        return cliArgsCap;
    }

    public static List<String> getPhantomJSProxy(Proxy proxy) {
        return getPhantomJSProxy(proxy.getIp(), proxy.getPort(), proxy.getProtocol());
    }

    public static List<String> getPhantomJSProxy(ProxyRecord proxyRecord) {
        Proxy proxy = proxyRecord.into(Proxy.class);
        return getPhantomJSProxy(proxy);
    }

    private static ProxyOptions getProxyOptions(String ip, Integer port, String protocol) {
        ProxyOptions proxyOptions = new ProxyOptions();
        proxyOptions.setHost(ip);
        proxyOptions.setPort(port);
        if (protocol.toLowerCase().startsWith("http")) {
            proxyOptions.setType(ProxyType.HTTP);
        } else if (protocol.toLowerCase().startsWith("socks5")) {
            proxyOptions.setType(ProxyType.SOCKS5);
        } else {
            proxyOptions.setType(ProxyType.SOCKS4);
        }

        return proxyOptions;
    }

    public static ProxyOptions getProxyOptions(Proxy proxy) {
        return getProxyOptions(proxy.getIp(), proxy.getPort(), proxy.getProtocol());
    }

    public static ProxyOptions getProxyOptions(ProxyRecord proxyRecord) {
        Proxy proxy = proxyRecord.into(Proxy.class);
        return getProxyOptions(proxy);
    }

    public static String getCharset(String content) {
        Matcher m = charsetPattern.matcher(content);
        if (m.find()) {
            String charset = m.group(1).trim();
            charset = charset.replace("charset=", "");
            return charset;
        } else {
            return "UTF-8";
        }
    }
}