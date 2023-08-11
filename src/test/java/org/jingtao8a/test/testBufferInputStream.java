package org.jingtao8a.test;

import org.junit.Test;

import java.io.*;

public class testBufferInputStream {
    @Test
    public void test1() {
        File file = new File("./target/classes/druid.properties");
        byte[] fileArray = new byte[(int)file.length()];
        InputStream inputStream;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        int size;
        try {
            size = inputStream.read(fileArray);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(size);
        System.out.println(fileArray.length);
//        System.out.println(new String(fileArray));
    }
}
