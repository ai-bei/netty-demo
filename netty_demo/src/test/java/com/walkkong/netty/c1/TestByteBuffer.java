package com.walkkong.netty.c1;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author liyanan
 * @date 2022/05/21/20:55
 */
@Slf4j
public class TestByteBuffer {

    public static void main(String[] args) {
        testW();
//        testWrite();
    }

    public static void testW() {
        // FileChannel
        // 1. 输入输出流
        // 2. RandomAccessFile

        try (FileChannel channel = new FileInputStream("src/data.txt").getChannel()){
            // 缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);
      /*      // 从 channel 读取数据，向 buffer 写入
            channel.read(buffer);
            // 打印 buffer 的内容
            // 切换到读模式
            buffer.flip();
            while (buffer.hasRemaining()) {
                byte b = buffer.get();
                System.out.println((char) b);
            }*/
            // 从 channel 读取数据，向 buffer 写入
            int readLen = channel.read(buffer);
            log.debug("读取到的字节数 {}", readLen);
            while (readLen != -1) {
                // 切换到读模式
                buffer.flip();
                // 打印 buffer 的内容
                while (buffer.hasRemaining()) {
                    byte b = buffer.get();
                    log.debug("实际字节 {}", (char) b);
                }
                // 使用 clear 可以切换到写模式
                buffer.clear();
                readLen = channel.read(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void testR() {
        try(FileChannel channel = new FileOutputStream("src/write_data.txt").getChannel()) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(10);
            byteBuffer.put(new byte[]{0x61, 0x62, 0x63, 0x64});
            ByteBufferUtil.debugAll(byteBuffer);

            byteBuffer.flip();
            channel.write(byteBuffer);
            ByteBufferUtil.debugAll(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
