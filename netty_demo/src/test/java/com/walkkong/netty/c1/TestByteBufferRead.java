package com.walkkong.netty.c1;

import java.nio.ByteBuffer;

/**
 * ByteBuffer 读的相关方法的一些操作
 * @author liyanan
 * @date 2022/05/23/21:36
 */
public class TestByteBufferRead {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put(new byte[]{'a', 'b', 'c', 'd'});

//        testRewind(buffer);
//        testMarkReset(buffer);
        testGetI(buffer);
    }

    public static void testRewind(ByteBuffer buffer) {
        buffer.flip();
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        // position 重置为 0
        buffer.rewind();
        ByteBufferUtil.debugAll(buffer);
    }

    public static void testMarkReset(ByteBuffer buffer) {
        buffer.flip();
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        // 打标记
        buffer.mark();
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        // reset()，position = mark，就可以重复读取
        buffer.reset();
        ByteBufferUtil.debugAll(buffer);
    }

    public static void testGetI(ByteBuffer buffer) {
        buffer.flip();
        // get(i) 不会影响 position 的位置，就是根据索引拿取
        System.out.println((char) buffer.get(3));
        ByteBufferUtil.debugAll(buffer);
    }
}
