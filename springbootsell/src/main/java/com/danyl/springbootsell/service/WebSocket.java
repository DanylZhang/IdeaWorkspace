package com.danyl.springbootsell.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
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
    private static ConcurrentHashMap<String, CopyOnWriteArraySet<WebSocket>> webSocketMap = new ConcurrentHashMap<>();

    // 记录当前连接数
    private static AtomicInteger onlineCount = new AtomicInteger(0);

    @OnOpen
    public void onOpen(@PathParam("uid") String uid, Session session) {
        this.uid = uid;
        this.session = session;
        // 总连接数加1
        onlineCount.incrementAndGet();

        if (webSocketMap.containsKey(this.uid)) {
            log.info("【websocket消息】用户:{} 已在其他终端连接", this.uid);
            webSocketMap.get(this.uid).add(this);
        } else {
            log.info("【websocket消息】用户:{} 首次连接", this.uid);
            CopyOnWriteArraySet<WebSocket> webSocketSet = new CopyOnWriteArraySet<>();
            webSocketSet.add(this);
            webSocketMap.put(this.uid, webSocketSet);
        }
        log.info("【websocket消息】用户:{} 的终端连接数为{}", this.uid, webSocketMap.get(this.uid).size());
        log.info("【websocket消息】当前在线用户数为：{}，所有终端个数为：{}", webSocketMap.size(), onlineCount.get());
    }

    @OnClose
    public void onClose() {
        //总连接数减1
        onlineCount.decrementAndGet();
        CopyOnWriteArraySet<WebSocket> userWebSocketSet = webSocketMap.get(this.uid);
        if (userWebSocketSet == null) {
            log.error("【websocket消息】用户:{} 未在线", this.uid);
            return;
        } else if (userWebSocketSet.size() == 0) {
            log.error("【websocket消息】用户:{} 无活动连接", this.uid);
            webSocketMap.remove(this.uid);
            return;
        } else if (userWebSocketSet.size() == 1) {
            userWebSocketSet.remove(this);
            webSocketMap.remove(this.uid);
        } else {
            userWebSocketSet.remove(this);
        }
        log.info("【websocket消息】用户:{} 连接断开，连接数:{}，系统总连接数{}", this.uid, Optional.ofNullable(userWebSocketSet).orElse(new CopyOnWriteArraySet<WebSocket>()).size(), onlineCount.get());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("【websocket消息】收到用户:{}发来的消息:{}", this.uid, message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("【websocket消息】用户:{} 的连接错误:{}", this.uid, error);
    }

    /**
     * @param uid
     * @param message
     * @return 成功 true ,失败 false
     * @Description:发送消息给用户下的所有终端
     */
    public Boolean sendMessageToUser(String uid, String message) {
        if (webSocketMap.containsKey(uid)) {
            for (WebSocket webSocket : webSocketMap.get(uid)) {
                try {
                    webSocket.session.getBasicRemote().sendText(message);
                } catch (Exception e) {
                    log.error("【websocket消息】给用户:{}发送消息时一个连接失败", uid);
                    return false;
                }
            }
            return true;
        } else {
            log.error("【websocket消息】用户:{}无有效连接", uid);
            return false;
        }
    }
}
