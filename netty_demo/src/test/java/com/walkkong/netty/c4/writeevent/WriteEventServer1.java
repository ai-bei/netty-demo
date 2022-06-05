package com.walkkong.netty.c4.writeevent;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @author liyanan
 * @date 2022/06/02 20:38
 **/
@Slf4j
public class WriteEventServer1 {
    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();

        ssc.configureBlocking(false);
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));
        while (true) {
            selector.select();

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectionKeys.iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    SelectionKey scKey = sc.register(selector, SelectionKey.OP_READ);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 10000000; i++) {
                        sb.append("a");
                    }
                    ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());
                    // 由循环改为判断，解决没有读完的问题
                    int write = sc.write(buffer);
                    log.info("{}", write);
                    if(buffer.hasRemaining()) {
                        // 不可读时，再写入
                        scKey.interestOps(scKey.interestOps() + SelectionKey.OP_WRITE);
                        // buffer 作为 key 的附件
                        scKey.attach(buffer);
                    }

                } else if (key.isWritable()) {
                    ByteBuffer bf = (ByteBuffer) key.attachment();
                    SocketChannel channel = (SocketChannel) key.channel();
                    int write = channel.write(bf);
                    log.info("{}", write);

                    if (!bf.hasRemaining()) {
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
                        // 解绑附件
                        key.attach(null);
                    }
                }
            }
        }
    }
}
