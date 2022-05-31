package com.walkkong.netty.c4.selector.msgbound;

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
import java.util.Set;

/**
 *
 * nio selector 消息边界-容量超出
 * @author liyanan
 * @date 2022/05/27 16:14
 **/
@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {

        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        SelectionKey sscKey = ssc.register(selector, SelectionKey.OP_ACCEPT);
        log.debug("register key: {}", sscKey);
        ssc.bind(new InetSocketAddress(8080));
        while (true) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();
            while (selectionKeyIterator.hasNext()) {
                SelectionKey key = selectionKeyIterator.next();
                log.debug("key: {}", key);
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    log.debug("{}", sc);
                    sc.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    // 将一个 bytebuffer 当做附件（attachment）
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("scKey {}", scKey);
                } else if (key.isReadable()) {
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        // 获取附件
                        ByteBuffer attachment = (ByteBuffer) key.attachment();
                        int read = channel.read(attachment);
                        if (read == -1) {
                            key.channel();
                            channel.close();
                        } else {
                            split(attachment);
                            if (attachment.position() != 0 && attachment.limit() == attachment.position()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(attachment.capacity() * 2);
                                attachment.flip();
                                newBuffer.put(attachment);
                                // 替换带 key 原有的 attachment
                                key.attach(newBuffer);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        key.cancel();
                    }
                }
                selectionKeyIterator.remove();
            }
        }
    }

    public static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            byte b = source.get(i);
            if (b == '\n') {
                int len = i + 1 - source.position();
                byte[] bytes = new byte[len];
                source.get(bytes);
                ByteBuffer target = ByteBuffer.wrap(bytes);
                ByteBufferUtil.debugAll(target);
            }
        }
        source.compact();
    }
}
