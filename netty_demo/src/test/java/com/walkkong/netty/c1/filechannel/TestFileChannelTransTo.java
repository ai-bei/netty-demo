package com.walkkong.netty.c1.filechannel;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * 实际案例，将文件 data.txt 拷贝一份。
 * @author liyanan
 * @date 2022/05/26 16:10
 **/
@Slf4j
public class TestFileChannelTransTo {
    public static void main(String[] args) {
        try( FileChannel from = new RandomAccessFile("1.zip", "r").getChannel();
             FileChannel to = new RandomAccessFile("src/2.zip", "rw").getChannel()) {
            // 比一般的读入写入更快速（操作系统的零拷贝，减少一次拷贝）
//            from.transferTo(0, from.size(), to);

            // transferTo 方法一次最多只能读取 2g，所以使用 transferTo 方法的正确写法应该为
            long left = 0;
            do {
                long transferLen = from.transferTo(left, from.size(), to);
                log.info("此次写入字节数：{}", transferLen);
                left = left + transferLen;
            } while (left != from.size());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
