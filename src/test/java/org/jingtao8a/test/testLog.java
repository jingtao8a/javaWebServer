package org.jingtao8a.test;

import org.junit.Test;
import org.jingtao8a.log.Log;

import java.io.*;

public class testLog {
    @Test
    public void testLog() throws IOException {
        Log logger = Log.getInstance();
        try {
            logger.init("./log", "yuxintao", 3, Log.LogLevel.ERROR);
            for (int i = 0; i < 10; ++i) {
                logger.writeLog(Log.LogLevel.ERROR, "%s", "hello");
            }
            logger.writeLog(Log.LogLevel.DEBUG, "%s", "world");
            logger.writeLog(Log.LogLevel.ERROR, "%s", "world");
            logger.writeLog(Log.LogLevel.ERROR, "%s", "world");
            logger.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            logger.flush();
        }
    }

    @Test
    public void testString() {
        String str1 = String.format("%s", "yu");
        String str2 = String.format("%s", "yu");
        System.out.println(str1 == str2);
    }

    @Test
    public void testOS() throws IOException {

        OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(new File("./a.txt")));
        os.write("hello");
    }
}
