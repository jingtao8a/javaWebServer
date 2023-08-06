package org.jingtao8a.test;

import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class testDate {
    @Test
    public void testDate() {
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        System.out.println(date.format(formatter));
    }
}
