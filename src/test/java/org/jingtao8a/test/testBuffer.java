package org.jingtao8a.test;

import org.junit.Test;

import java.nio.ByteBuffer;

public class testBuffer {
    void printInfo(ByteBuffer byteBuffer) {
        System.out.println("position: " + byteBuffer.position());
        System.out.println("limit: " + byteBuffer.limit());
        System.out.println("remaining: " + byteBuffer.remaining());
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
}
