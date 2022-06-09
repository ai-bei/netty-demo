package com.walkkong.netty.c4;

import com.walkkong.netty.c1.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liyanan
 * @date 2022/06/05 16:17
 **/
@Slf4j
public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("Boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(8080));
        Selector boss = Selector.open();
        ssc.register(boss, SelectionKey.OP_ACCEPT);

        Worker[] workers = new Worker[2];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker("worker-" + i);
        }
        AtomicInteger atomicInteger = new AtomicInteger();
        while (true) {
            boss.select();
            Iterator<SelectionKey> iter = boss.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();
                try {
                    if (key.isAcceptable()) {
                        SocketChannel sc = ssc.accept();
                        sc.configureBlocking(false);
                        log.debug("connected...{}", sc.getRemoteAddress());
                        // 关联 selector
                        log.debug("before register...{}", sc.getRemoteAddress());
                        workers[atomicInteger.get() % workers.length].register(sc);
                        log.debug("after register...{}", sc.getRemoteAddress());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    key.cancel();
                    key.channel().close();
                }
            }
        }
    }

    static class Worker implements Runnable{
        // 运行线程
        private Thread thread;
        private volatile Selector selector;
        private String name;
        private volatile boolean start = false;

        public Worker(String name) {
            this.name = name;
        }

        // 初始化线程和 Selector
        public void register(SocketChannel sc) throws IOException {
            if (!start) {
                thread = new Thread(this, name);
                selector = Selector.open();
                thread.start();
                start = true;
            }
            // 唤醒 selector
            selector.wakeup();
            sc.register(selector, SelectionKey.OP_READ);

        }

        @Override
        public void run() {
            while (true) {
                try {
                    selector.select();              // 阻塞，事件出现才会执行
                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();
                        try {
                            if (key.isReadable()) {
                                ByteBuffer buffer = ByteBuffer.allocate(16);
                                SocketChannel channel = (SocketChannel) key.channel();
                                log.debug(Thread.currentThread().getName() + " read...{}", channel.getRemoteAddress());
                                channel.read(buffer);
                                buffer.flip();
                                ByteBufferUtil.debugAll(buffer);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            key.cancel();
                            key.channel().close();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
