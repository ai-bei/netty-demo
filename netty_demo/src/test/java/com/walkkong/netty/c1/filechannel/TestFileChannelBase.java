package com.walkkong.netty.c1.filechannel;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author liyanan
 * @date 2022/05/26 10:49
 **/
@Slf4j
public class TestFileChannelBase {
    public static void main(String[] args) {
        getFileChannel();
    }

    public static void getFileChannel() {
        // 三种方式获取

        // 从 FileInputStream 获取，只能读不能写
        try(FileChannel channel = new FileInputStream("src/data.txt").getChannel()) {
            log.info("从 FileInputStream 获取");
            ByteBuffer buffer = ByteBuffer.allocate(10);
            int readLen = 0;
            while (readLen != -1) {
                readLen = channel.read(buffer);
                // 切换到读模式
                buffer.flip();
                System.out.println(StandardCharsets.UTF_8.decode(buffer));
                // 清空 buffer 并切换到写模式
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 从 FileOutputStream 获取，只能写不能读
        try(FileChannel channel = new FileOutputStream("src/fileChannel1.txt").getChannel()) {
            log.info("从 FileOutputStream 获取");
            ByteBuffer buffer = ByteBuffer.allocate(100);
            buffer.put(new byte[]{'a', 'b', 'c', 'd'});
            // buffer 切换到读模式
            buffer.flip();

            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 从 RandomAccessFile 获取，通过设置读写模式，来确定是否可以读写
        try(FileChannel channel = new RandomAccessFile("src/fileChannel2.txt", "rw").getChannel()) {
            log.info("从 RandomAccessFile 获取");
            ByteBuffer buffer = ByteBuffer.allocate(100);
            buffer.put(new byte[]{'a', 'b', 'c', 'd', 'e'});
            // buffer 切换到读模式
            buffer.flip();

            // channel 写入
            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }

            buffer.clear();
            // 读取
            int readLen = 0;
            while (readLen != -1) {
                readLen = channel.read(buffer);
                System.out.println(StandardCharsets.UTF_8.decode(buffer));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

