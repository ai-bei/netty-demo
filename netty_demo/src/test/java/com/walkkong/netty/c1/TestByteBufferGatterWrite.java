package com.walkkong.netty.c1;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * 集中写
 * @author liyanan
 * @date 2022/05/23/22:40
 */
public class TestByteBufferGatterWrite {
    public static void main(String[] args) {
        try (FileChannel channel = new RandomAccessFile("src/data2.txt", "rw").getChannel()){
            ByteBuffer b1 = StandardCharsets.UTF_8.encode("hello");
            ByteBuffer b2 = StandardCharsets.UTF_8.encode(",");
            ByteBuffer b3 = StandardCharsets.UTF_8.encode("张三");
            channel.write(new ByteBuffer[]{b1, b2, b3});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
