package com.walkkong.netty.c1;

import java.nio.ByteBuffer;

/**
 * @author liyanan
 * @date 2022/05/22/18:07
 */
public class TestByteBufferAllocate {
    public static void main(String[] args) {
        // class java.nio.HeapByteBuffer
        System.out.println(ByteBuffer.allocate(16).getClass());

        // class java.nio.DirectByteBuffer
        System.out.println(ByteBuffer.allocateDirect(16).getClass());
    }
}
