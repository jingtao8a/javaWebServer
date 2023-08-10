package org.jingtao8a.test;

import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class testPwd {
    public static String root = "D:/java_webserver/src/main/resources/root";
    @Test
    public void testPwd() throws IOException {
        File dir = new File(root + "/judge.html");
        StringBuffer stringBuffer = new StringBuffer();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(dir), StandardCharsets.UTF_8));
        String str;
        while ( (str = bufferedReader.readLine()) != null) {
            stringBuffer.append(str);
        }
        System.out.println(stringBuffer.toString());
    }
}
