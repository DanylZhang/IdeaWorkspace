package com.danyl.learnjava.lscjszconcurrency.ch8;

import io.netty.buffer.ByteBuf;
import org.apache.http.cookie.CookieAttributeHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadNIOEchoServer {

    public static Map<Socket, Long> time_stat = new HashMap<>(10240);

    class EchoClient {
        private LinkedList<ByteBuffer> outq;

        public EchoClient() {
            outq = new LinkedList<ByteBuffer>();
        }

        public LinkedList<ByteBuffer> getOutputQueue() {
            return outq;
        }

        public void enqueue(ByteBuffer bb) {
            outq.addFirst(bb);
        }
    }

    class HandleMsg implements Runnable {
        SelectionKey sk;
        ByteBuffer bb;

        public HandleMsg(SelectionKey sk, ByteBuffer bb) {
            this.sk = sk;
            this.bb = bb;
        }


        @Override
        public void run() {
            EchoClient echoClient = (EchoClient) sk.attachment();
            echoClient.enqueue(bb);

            sk.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            // 让selector立即返回
            selector.wakeup();
        }
    }

    private Selector selector;
    private ExecutorService tp = Executors.newCachedThreadPool();

    private void doAccept(SelectionKey sk) {
        ServerSocketChannel server = (ServerSocketChannel) sk.channel();
        SocketChannel clientChannel;

        try {
            clientChannel = server.accept();
            clientChannel.configureBlocking(false);

            SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_READ);

            EchoClient echoClient = new EchoClient();
            clientKey.attach(echoClient);

            InetAddress clientAddress = clientChannel.socket().getInetAddress();
            System.out.println("Accepted connection from " + clientAddress.getHostAddress());
        } catch (Exception e) {
            System.out.println("Failed to close client socket channel.");
            e.printStackTrace();
        }
    }

    private void doRead(SelectionKey sk){
        SocketChannel channel = (SocketChannel)sk.channel();
        ByteBuffer bb = ByteBuffer.allocate(8192);
        int len;

        try {
            len = channel.read(bb);
            if (len<0){
                disconnect(sk);
                return;
            }
        }catch (Exception e){
            System.out.println("Failed to read from client.");
            e.printStackTrace();
            disconnect(sk);
            return;
        }

        // flip the buffer.
        bb.flip();
        tp.execute(new HandleMsg(sk,bb));
    }

    private void doWrite(SelectionKey sk){
        SocketChannel channel = (SocketChannel)sk.channel();
        EchoClient echoClient= (EchoClient) sk.attachment();
        LinkedList<ByteBuffer> outq = echoClient.getOutputQueue();

        ByteBuffer bb = outq.getLast();
        try {
            int len = channel.write(bb);
            if (len==-1){
                disconnect(sk);
                return;
            }
            if (bb.remaining()==0){
                outq.removeLast();
            }
        }catch (Exception e){
            System.out.println("Failed to write to client.");
            e.printStackTrace();
            disconnect(sk);
        }

        if (outq.size()==0){
            sk.interestOps(SelectionKey.OP_READ);
        }
    }

    private void disconnect(SelectionKey sk){
        try {
            sk.channel().close();
        } catch (IOException e) {
            System.out.println("Failed to close client socket channel.");
            e.printStackTrace();
        }
    }

    private void startServer() throws Exception {
        selector = SelectorProvider.provider().openSelector();

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        InetSocketAddress isa = new InetSocketAddress(8889);
        ssc.socket().bind(isa);

        SelectionKey acceptKey = ssc.register(selector, SelectionKey.OP_ACCEPT);

        for (; ; ) {
            selector.select();

//            if (selector.selectNow() == 0) {
//                continue;
//            }

            Set readyKeys = selector.selectedKeys();
            Iterator iterator = readyKeys.iterator();
            long e = 0;
            while (iterator.hasNext()) {
                SelectionKey sk = (SelectionKey) iterator.next();
                iterator.remove();

                if (sk.isAcceptable()) {
                    doAccept(sk);
                } else if (sk.isValid() && sk.isReadable()) {
                    if (!time_stat.containsKey(((SocketChannel) sk.channel()).socket())) {
                        time_stat.put(((SocketChannel) sk.channel()).socket(), System.currentTimeMillis());
                    }
                    doRead(sk);
                } else if (sk.isValid() && sk.isWritable()) {
                    doWrite(sk);
                    e = System.currentTimeMillis();
                    long b = time_stat.remove(((SocketChannel) sk.channel()).socket());
                    System.out.println("spend:" + (e - b) + "ms");
                }
            }
        }
    }

    public static void main(String[] args) {
        MultiThreadNIOEchoServer echoServer = new MultiThreadNIOEchoServer();
        try {
            echoServer.startServer();
        } catch (Exception e) {
            System.out.println("Exception caught, program exiting...");
            e.printStackTrace();
        }
    }
}
