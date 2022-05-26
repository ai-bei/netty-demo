package com.walkkong.netty.c1;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

/**
 * @author liyanan
 * @date 2022/05/21/20:55
 */
@Slf4j
public class TestByteBufferExam {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        buffer.put("Hello,World\nI'm zhang".getBytes());
        buffer.put("san\nHow are you?\n".getBytes());
        split(buffer);
    }

    public static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            byte b = source.get(i);
            if (b == '\n') {
                int len = i + 1 - source.position();
         /*       ByteBuffer target = ByteBuffer.allocate(len);
                for (int j = 0; j < len; j++) {
                    target.put(source.get());
                }
                ByteBufferUtil.debugAll(target);
                target.clear();*/
                byte[] bytes = new byte[len];
                source.get(bytes);
                ByteBuffer target = ByteBuffer.wrap(bytes);
                ByteBufferUtil.debugAll(target);
                target.clear();
            }
        }
    }
}
