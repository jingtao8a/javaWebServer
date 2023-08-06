package org.jingtao8a.log;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class Log {
    public enum LogLevel{
        DEBUG(1), INFO(2), WARN(3), ERROR(4);

        private int value;
        LogLevel(int value) {
            this.value = value;
        }
        int getValue() {return this.value;}
    }
    private static Log instance;

    private String dirName;//路径名
    private String logName;//日志名
    private String today;//当前日期 年 月 日
    private int splitLines;//一篇日志最大行数
    private long count;//同一天内日志行数记录
    private LogLevel logLevel;
    private BufferedWriter bufferedWriter;
    public void init(String dirName, String logName, int splitLines, LogLevel logLevel) throws IOException {
        this.logLevel = logLevel;
        this.dirName = dirName;
        this.logName = logName;
        File dir = new File(dirName);
        if (!dir.exists()) {//创建目录
            dir.mkdirs();
        }
        //获取当前时间构建日志名
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.today = date.format(formatter);
        File logFile = new File(new File(this.dirName), this.logName + this.today + ".txt");
        if (!logFile.exists()) {
            logFile.createNewFile();
        }
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile)));
        this.count = 0;
        this.splitLines = splitLines;
    }
    public void flush() throws IOException {
        if (this.bufferedWriter != null) {
            this.bufferedWriter.flush();
        }
    }
    public void writeLog(LogLevel logLevel, String format, Object... args) throws IOException {
        if (logLevel.getValue() < this.logLevel.getValue()) {//忽略该日志
            return;
        }
        this.count++;
        //获取当前时间
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String nowDay = date.format(formatter);
        if (!this.today.equals(nowDay) || this.count % this.splitLines == 0) {
            File logFile;
            this.bufferedWriter.flush();
            if (!this.today.equals(nowDay)) {//不是同一天
                this.today = nowDay;
                logFile = new File(new File(this.dirName), this.logName + this.today + ".txt");
            } else { //日志行数到了一篇日志的最大限制
                logFile = new File(new File(this.dirName), this.logName + this.today + this.count / this.splitLines + ".txt");
            }
            logFile.createNewFile();
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile)));
        }
        //构建好这条日志
        StringBuilder strBuilder = new StringBuilder();
        switch (logLevel) {
            case DEBUG:
                strBuilder.append("[DEBUG]");
                break;
            case INFO:
                strBuilder.append("[INFO]");
                break;
            case WARN:
                strBuilder.append("[WARN]");
                break;
            case ERROR:
                strBuilder.append("[ERROR]");
                break;
            default:
                throw new RuntimeException("Wrong LogLevel");
        }
        strBuilder.append(":" + Thread.currentThread().getStackTrace()[2].getFileName());
        strBuilder.append("," + Thread.currentThread().getStackTrace()[2].getLineNumber());
        strBuilder.append(":" + String.format(format, args) + "\r\n");
        this.bufferedWriter.write(strBuilder.toString());
    }

    //懒汉式 单例
    public static Log getInstance() {
        if (instance == null) {
            synchronized (Log.class) {
                if (instance == null) {
                    instance = new Log();
                }
            }
        }
        return instance;
    }
}
