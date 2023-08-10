package org.jingtao8a.test;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

public class testCode {
    @Test
    public void testCode() {
        String str = "你好中国";

        try {
            byte[] bytes = str.getBytes("GBK");
            String newStr = new String(bytes, "GBK");
            String he = new String(newStr.getBytes("GBK"), "GBK");
            System.out.println(he);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
