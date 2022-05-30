package com.walkkong.netty.c4.selector;

import com.walkkong.netty.c1.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * nio selector
 * @author liyanan
 * @date 2022/05/27 16:14
 **/
@Slf4j
public class Server {
    private static ByteBuffer buffer = ByteBuffer.allocate(1024);

    public static void main(String[] args) throws IOException {
        // 1. 建立 selector
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        // 2. 建立 selector 和 channel 的联系（注册）
        // SelectionKey 就是将来事件发生后，通过它可以知道事件和哪个 channel 的事件
        SelectionKey sscKey = ssc.register(selector, SelectionKey.OP_ACCEPT);
        //  Key 只关注 Accept 事件
//        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("register key: {}", sscKey);
        ssc.bind(new InetSocketAddress(8080));
        while (true) {
          // 3. select 方法，没有事件发生，线程阻塞；有事件，才会恢复运行
            // select 事件未处理时，不会阻塞，必须处理和取消事件
            selector.select();
          // 处理事件，返回所有可用事件
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();
            while (selectionKeyIterator.hasNext()) {
                SelectionKey key = selectionKeyIterator.next();
                log.debug("key: {}", key);
                // 区分时间类型
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    log.debug("{}", sc);
                    sc.configureBlocking(false);
                    SelectionKey scKey = sc.register(selector, SelectionKey.OP_READ);
//                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("scKey {}", scKey);
                } else if (key.isReadable()) {
                    // 如果时 read，需要做读取数据
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        int read = channel.read(buffer);
                        if (read == -1) {
                            // 正常断开，read 方法返回 -1
                            key.channel();
                            channel.close();
                        } else {
                            buffer.flip();
                            ByteBufferUtil.debugRead(buffer);
                            buffer.clear();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        // 客户端断开了，需要将 key channel，其实就是从 SelectionKey 集合中真正删除。
                        key.cancel();
                    }
                }
                // 处理 key 后，要删除，否则下次处理会有问题
                selectionKeyIterator.remove();
//                key.cancel();

            }
        }


    }
}
