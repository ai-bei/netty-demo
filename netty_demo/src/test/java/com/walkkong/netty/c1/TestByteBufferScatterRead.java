package com.walkkong.netty.c1;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 分散读联系
 * @author liyanan
 * @date 2022/05/23/22:40
 */
public class TestByteBufferScatterRead {
    public static void main(String[] args) {
        try (FileChannel channel = new RandomAccessFile("src/data1.txt", "r").getChannel()){
            ByteBuffer b1 = ByteBuffer.allocate(3);
            ByteBuffer b2 = ByteBuffer.allocate(3);
            ByteBuffer b3 = ByteBuffer.allocate(5);
            ByteBuffer b4 = ByteBuffer.allocate(4);
            channel.read(new ByteBuffer[]{b1, b2, b3, b4});
            ByteBufferUtil.debugAll(b1);
            ByteBufferUtil.debugAll(b2);
            ByteBufferUtil.debugAll(b3);
            ByteBufferUtil.debugAll(b4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
