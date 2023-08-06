package org.jingtao8a.test;

import org.junit.Test;

import java.io.File;

public class testFile {
    @Test
    public void testFile() {
        System.out.println(new File(new File("./hello"), "jenny").getPath());
    }

    @Test
    public void testFile1() {
        File test =  new File("target/test-classes/org/jingtao8a/test/testDate.class");
        if (test.exists()) {
            System.out.println(test.getAbsolutePath() + test.length());
        }
    }
}
