package org.apache.rocketmq.store.fhb;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Author huabao.fang
 * @Date 2022/8/15 22:31
 **/
public class MappedFileTest {

    @Test
    public void test() throws IOException {
        String filePath = "/Users/xmly/temp/1.txt";
        FileChannel fileChannel = new RandomAccessFile(new File(filePath), "rw").getChannel();
        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 1024);

        mappedByteBuffer.put("ABCD".getBytes());

    }
}
