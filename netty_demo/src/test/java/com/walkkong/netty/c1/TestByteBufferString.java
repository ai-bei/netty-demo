package com.walkkong.netty.c1;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author liyanan
 * @date 2022/05/23/22:21
 */
@Slf4j
public class TestByteBufferString {
    public static void main(String[] args) {
        // String -> ByteBuffer
        // 不会切换成读模式
        ByteBuffer byteBuffer1 = ByteBuffer.allocate(16);
        byteBuffer1.put("hello".getBytes());
        ByteBufferUtil.debugAll(byteBuffer1);

        // 会切换成读模式
        ByteBuffer byteBuffer2 = StandardCharsets.UTF_8.encode("hello");
        ByteBufferUtil.debugAll(byteBuffer2);

        // 会切换成读模式
        ByteBuffer byteBuffer3 = ByteBuffer.wrap("hello".getBytes());
        ByteBufferUtil.debugAll(byteBuffer3);

        // ByteBuffer to String
        log.debug(new String(byteBuffer1.array()));

        String str2 = StandardCharsets.UTF_8.decode(byteBuffer2).toString();
        log.debug(str2);

    }
}
