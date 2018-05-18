package com.danyl.springbootsell.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@ServerEndpoint("/webSocket/{uid}")
@Slf4j
public class WebSocket {

    private String uid;

    private Session session;

    // 存储每个用户的多个终端连接
    private static ConcurrentHashMap<String, Set<WebSocket>> webSocketMap = new ConcurrentHashMap<>();

    // 记录当前连接数
    private static AtomicInteger onlineCount = new AtomicInteger(0);

    @OnOpen
    public void onOpen(@PathVariable("uid") String uid, Session session) {
        this.uid = uid;
        this.session = session;
        onlineCount.incrementAndGet();

        if (webSocketMap.containsKey(this.uid)) {
            log.info("【websocket消息】用户:{} 已在其他终端连接", this.uid);
            webSocketMap.get(this.uid).add(this);
        } else {
            log.info("【websocket消息】用户:{} 首次连接", this.uid);
            Set<WebSocket> webSocketSet = new HashSet<>();
            webSocketSet.add(this);
            webSocketMap.put(this.uid, webSocketSet);
        }
        log.info("【websocket消息】用户{}的终端连接数为{}", this.uid, webSocketMap.get(this.uid).size());
        log.info("【websocket消息】当前在线用户数为：{}，所有终端个数为：{}", webSocketMap.size(), onlineCount.get());
    }

    @OnClose
    public void onClose() {
        if (webSocketMap.get(this.uid).size() == 0) {
            webSocketMap.remove(this.uid);
        } else {
            webSocketMap.get(this.uid).remove(this);
        }
        log.info("【websocket消息】连接断开，总数:{}", webSocketMap.size());

    }

    @OnMessage
    public void onMessage(String message) {
        log.info("【websocket消息】收到客户端发来的消息:{}", message);
    }

    public void sendMessage(String message) {
        for (Map.Entry<String, Set<WebSocket>> webSocketEntry : webSocketMap.entrySet()) {
            log.info("【websocket消息】广播消息，message={}", message);
//            try {
//                webSocketEntry.getValue().session.getBasicRemote().sendText(message);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }
}
