package org.jingtao8a.test;

import org.junit.Test;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public class testBuffer {
    void printInfo(Buffer buffer) {
        System.out.println("position: " + buffer.position());
        System.out.println("limit: " + buffer.limit());
        System.out.println("remaining: " + buffer.remaining());
    }
    @Test
    public void testByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(new byte[]{1, 2, 3, 4, 5});
        printInfo(buffer);
        buffer.flip();
        System.out.println();
        printInfo(buffer);
        buffer.clear();
        printInfo(buffer);
    }
    @Test
    public void testCharBuffer() {
        CharBuffer buffer = CharBuffer.allocate(1024);
        buffer.put("abc");
//        printInfo(buffer);
//        buffer.flip();
//        System.out.println();
//        printInfo(buffer);
//        buffer.clear();
//        printInfo(buffer);
        buffer.flip();
        char[] chars = buffer.array();
        System.out.println(chars[0]);
        System.out.println(chars[1]);
        System.out.println(chars[2]);
        chars[0] = '余';
        chars = buffer.array();
        System.out.println(chars[0]);
    }
}
