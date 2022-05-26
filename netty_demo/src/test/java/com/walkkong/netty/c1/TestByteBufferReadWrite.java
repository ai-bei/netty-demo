package com.walkkong.netty.c1;

import java.nio.ByteBuffer;

/**
 * @author liyanan
 * @date 2022/05/22/17:50
 */
public class TestByteBufferReadWrite {


    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        // 'a'
        byteBuffer.put((byte) 0x61);
        ByteBufferUtil.debugAll(byteBuffer);
        // 'b', 'c', 'd'
        byteBuffer.put(new byte[]{0x62, 0x63, 0x64});
        ByteBufferUtil.debugAll(byteBuffer);

//        System.out.println("get:" + byteBuffer.get());
//        ByteBufferUtil.debugAll(byteBuffer);

        byteBuffer.flip();
        System.out.println("get:" + byteBuffer.get());
        ByteBufferUtil.debugAll(byteBuffer);


    }
}
