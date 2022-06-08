package com.walkkong.netty.c4;

import com.walkkong.netty.c1.ByteBufferUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author liyanan
 * @date 2022/06/05 16:17
 **/
@Slf4j
public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        Selector boss = Selector.open();
        SelectionKey bossKey = ssc.register(boss, 0, null);
        bossKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));
        // 创建固定数量的 worker 并初始化
        Worker worker = new Worker("worker-0");
//        worker.register();
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
                        worker.register(sc);
//                    sc.register(worker.selector, SelectionKey.OP_READ, null);
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
        private Selector selector;
        private String name;
        private volatile boolean start = false;
        // 队列解决两个线程数据传递的问题
        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

        public Worker(String name) {
            this.name = name;
        }

        // 初始化线程和 Selector
        public void register(SocketChannel sc) throws IOException {
            if (!start) {
                thread = new Thread(this, name);
                thread.start();

                selector = Selector.open();

                start = true;
            }
            /*queue.add(() -> {
                try {
                    sc.register(selector, SelectionKey.OP_READ, null);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
            // 唤醒 selector
            selector.wakeup();*/

            // 唤醒 selector
            selector.wakeup();
            sc.register(selector, SelectionKey.OP_READ, null);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    selector.select();              // 阻塞，事件出现才会执行
                /*    Runnable task = queue.poll();
                    if (!Objects.isNull(task)) {
                        task.run();                     // 执行注册
                    }*/
                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();
                        try {
                            if (key.isReadable()) {
                                ByteBuffer buffer = ByteBuffer.allocate(16);
                                SocketChannel channel = (SocketChannel) key.channel();
                                log.debug("read...{}", channel.getRemoteAddress());
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
