package com.danyl.spiders.utils;

import com.danyl.spiders.jooq.gen.proxy.tables.pojos.Proxy;
import com.danyl.spiders.jooq.gen.proxy.tables.records.ProxyRecord;
import io.vertx.core.net.ProxyOptions;
import io.vertx.core.net.ProxyType;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.ext.web.client.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.danyl.spiders.constants.ProtocolConstants.HTTP;

@Slf4j
public class ProxyUtil {
    public static final Pattern charsetPattern = Pattern.compile("(?i)\\bcharset=\\s*(?:[\"'])?([^\\s,;\"'>]*)");

    public static String getUrlProtocol(String url) {
        String protocol = HTTP;
        try {
            protocol = new URL(url).getProtocol();
        } catch (MalformedURLException e) {
            log.error("get url protocol error: {}, url: {}", e.getMessage(), url);
        }
        return protocol;
    }

    private static java.net.Proxy.Type getProxyType(String protocol) {
        if (StringUtils.isBlank(protocol)) {
            return java.net.Proxy.Type.HTTP;
        }

        if (protocol.toLowerCase().contains("http")) {
            return java.net.Proxy.Type.HTTP;
        } else if (protocol.toLowerCase().contains("socks")) {
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
        org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
        protocol = protocol.toLowerCase();

        if (protocol.contains("https")) {
            proxy.setHttpProxy(_proxy);
            proxy.setFtpProxy(_proxy);
            proxy.setSslProxy(_proxy);
        } else if (protocol.contains("socks5")) {
            proxy.setSslProxy(_proxy);
            proxy.setSocksProxy(_proxy);
            proxy.setSocksVersion(5);
        } else if (protocol.contains("socks")) {
            proxy.setSslProxy(_proxy);
            proxy.setSocksProxy(_proxy);
            proxy.setSocksVersion(4);
        } else {
            proxy.setHttpProxy(_proxy);
            proxy.setFtpProxy(_proxy);
        }

        return proxy;
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
        protocol = protocol.toLowerCase();

        if (protocol.contains("http")) {
            cliArgsCap.add(_proxy);
            cliArgsCap.add("--proxy-type=http");
        } else if (protocol.contains("socks")) {
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
        protocol = protocol.toLowerCase();
        ProxyOptions proxyOptions = new ProxyOptions();
        proxyOptions.setHost(ip);
        proxyOptions.setPort(port);
        if (protocol.contains("http")) {
            proxyOptions.setType(ProxyType.HTTP);
        } else if (protocol.contains("socks5")) {
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
        String charset = "UTF-8";
        if (StringUtils.isBlank(content)) {
            return charset;
        }

        Matcher m = charsetPattern.matcher(content);
        if (m.find()) {
            charset = m.group(1).trim();
            charset = charset.replace("charset=", "");
            return charset;
        } else {
            return charset;
        }
    }

    public static String decodeBody(HttpResponse<Buffer> response) {
        String charset = getCharset(response.bodyAsString());
        String body = "";
        try {
            body = response.bodyAsString(charset);
        } catch (Exception e) {
            log.error("decodeBody error: {}, charset: {}", e.getMessage(), charset);
        }
        return body;
    }
}