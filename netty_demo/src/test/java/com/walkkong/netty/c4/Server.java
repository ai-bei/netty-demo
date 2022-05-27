package com.walkkong.netty.c4;

import com.walkkong.netty.c1.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * 使用 nio 理解阻塞模式，单线程
 * @author liyanan
 * @date 2022/05/27 16:14
 **/
@Slf4j
public class Server {
    private static ByteBuffer buffer = ByteBuffer.allocate(1024);

    public static void main(String[] args) throws IOException {
        // 1. 创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();

        // ServerSocketChannel 设置为非阻塞
        ssc.configureBlocking(false);

        // 2 绑定监听端口
        ssc.bind(new InetSocketAddress(8080));

        // 连接集合
        List<SocketChannel> channels = new ArrayList<>();
        while (true) {
            // 3. accept 建立与客户端连接，返回的 SocketChannel 用来与客户端通信
//            log.debug("connecting...");
            SocketChannel sc = ssc.accept();
            if (sc != null) {
                log.debug("connecting... {}", sc);
                // SocketChannel 也设置为非阻塞模式
                sc.configureBlocking(false);
                channels.add(sc);
            }

            // 5. 接收客户端发送的数据，并打印
            for (SocketChannel channel : channels) {
                log.debug("before read...{}", channel);
                int read = channel.read(buffer);
                if (read > 0) {
                    buffer.flip();
                    ByteBufferUtil.debugRead(buffer);
                    buffer.clear();
                    log.debug("after read...{}", channel);
                }

            }
        }


    }
}
