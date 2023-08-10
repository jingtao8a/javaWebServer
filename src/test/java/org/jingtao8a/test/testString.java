package org.jingtao8a.test;

import org.junit.Test;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;

public class testString {

    public int utf_16Length(String str) {
        return str.getBytes(StandardCharsets.UTF_16).length;
    }
    public int utf_8Length(String str) {
        return str.getBytes(StandardCharsets.UTF_8).length;
    }

    @Test
    public void testString() throws NoSuchFieldException, IllegalAccessException {
        String str = "你好中国？hahah";
//        System.out.println(utf_16Length("你"));
//        System.out.println(utf_16Length("好"));
//        System.out.println(utf_16Length("中"));
//        System.out.println(utf_16Length("国"));
//        System.out.println(utf_16Length(str));
//        char a = '你';
//        char b = '好';
//        char c = '中';
//        char d = '国';
//        String s = "你";
//        System.out.println(utf_16Length(s));
//        System.out.println(a);
//        System.out.println(b);
//        System.out.println(c);
//        System.out.println(d);
//        byte[] bytes = str.getBytes();
//        str = new String(bytes);
//        System.out.println(str);
        Class<String> clazz = (Class<String>) str.getClass();
        Field valueField = clazz.getDeclaredField("value");
        valueField.setAccessible(true);
        byte[] bytes = (byte[]) valueField.get(str);
        System.out.println(bytes.length);
    }

    @Test
    public void testString2() {
        char[] chars = new char[]{'a', 'b', 'c'};
        System.out.println(new String(chars, 1, 2));
    }
}
