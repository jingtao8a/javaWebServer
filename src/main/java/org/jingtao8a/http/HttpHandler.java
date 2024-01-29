package org.jingtao8a.http;

import org.jingtao8a.mysql.ConnectionFactory;
import org.jingtao8a.server.TCPConnection;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HttpHandler {
    private static String staticRoot = "D:/javaWebServer/src/main/resources/root";
    private CharBuffer inputBuffer;
    private ByteBuffer outputBuffer;
    private CHECK_STATE checkState;
    private int checkedIdx;
    private int startLine;
    private int textHeader;
    private String charSetName;
    private enum CHECK_STATE { CHECK_STATE_REQUESTLINE, CHECK_STATE_HEADER, CHECK_STATE_CONTENT } // 主状态机的状态
    private enum LINE_STATUS { LINE_OK, LINE_BAD, LINE_OPEN } //从状态机解析结果
    private enum HTTP_CODE { // 主状态机解析结果
        NO_REQUEST, //请求还未解析完
        GET_REQUEST, //请求已经解析成功
        BAD_REQUEST,//400 请求错误
        NO_RESOURCE,//404 请求资源不存在
        FORBIDDEN_REQUEST,//403 请求被拒绝
        FILE_REQUEST, //200 文件请求
        INTERNAL_ERROR,//500 服务器内部错误
    }
    private enum METHOD { GET, POST } //报文的请求头,仅仅支持GET和POST
    METHOD method;// 请求方法
    String uri;// 请求uri
    String version;// 版本号
    int contentLength; //请求数据体长度
    String content;// 请求数据体
    static String ok_200_title = "OK";
    static String error_400_title = "Bad Request";
    static String error_400_form = "Your request has bad syntax or is inherently impossible to satisfy.\n";
    static String error_403_title = "Forbidden";
    static String error_403_form = "You do not have permission to get file form this server.\n";
    static String error_404_title = "Not Found";
    static String error_404_form = "The requested file was not found on this server.\n";
    static String error_500_title = "Internal Error";
    static String error_500_form = "There was an unusual problem serving the request file.\n";

    public HttpHandler() {
        init();
    }
    private void init() {
        inputBuffer = CharBuffer.allocate(1024);
        inputBuffer.flip();
        outputBuffer = ByteBuffer.allocate(1024);
        checkState = CHECK_STATE.CHECK_STATE_REQUESTLINE;// 主状态机初始状态
        checkedIdx = 0; // 从状态机使用的游标
        startLine = 0;// 主状态机使用的游标, 已经解析的字符个数(同时也是新行的开始)
        textHeader = 0;// 主状态机使用的游标，用于指向待解析的一行的开始
        method = null;
        uri = null;
        version = null;
        contentLength = 0;
        content = null;
    }
    public void process(TCPConnection connection, ByteBuffer buffer, String charSetName) {
        this.charSetName = charSetName;
        String newStr = null;
        try {
            newStr = new String(buffer.array(), buffer.position(), buffer.limit(), charSetName);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String oldStr = new String();
        if (inputBuffer.hasRemaining()) {// inputBuffer中还存在数据
            oldStr = new String(inputBuffer.array(), inputBuffer.position(), inputBuffer.remaining());
        }
        inputBuffer.clear();
        int totalLength = newStr.length() + oldStr.length();
        if (inputBuffer.limit() < totalLength) { //空间不足
            inputBuffer = CharBuffer.allocate(totalLength * 2);
        }
        inputBuffer.put(oldStr);
        inputBuffer.put(newStr);
        inputBuffer.flip();
        char[] request = inputBuffer.array();
        HTTP_CODE res = processRead(request);
        if (res != HTTP_CODE.NO_REQUEST) {// 解析报文并处理完毕
            processWrite(connection, res);
            if (checkedIdx < inputBuffer.limit()) { // inputBuffer仍然有多余的数据
                String remainStr = new String(inputBuffer.array(), checkedIdx, inputBuffer.limit() - checkedIdx);
                init();
                inputBuffer.clear();
                if (remainStr.length() > inputBuffer.limit()) {
                    inputBuffer = CharBuffer.allocate(remainStr.length() * 2);
                }
                inputBuffer.put(remainStr);
                inputBuffer.flip();
                process(connection, ByteBuffer.allocate(0), charSetName);// 继续下一轮处理
            } else {
                init();
            }
        }
    }

    private HTTP_CODE processRead(char[] request) {
        HTTP_CODE ret;
        LINE_STATUS line_status;
        while (true) { //成功解析一行
            line_status = parseLine(request);
            if (line_status == LINE_STATUS.LINE_BAD) {
                return HTTP_CODE.BAD_REQUEST;
            }
            if (line_status == LINE_STATUS.LINE_OPEN) {
                if (checkState != CHECK_STATE.CHECK_STATE_CONTENT) {
                    return HTTP_CODE.NO_REQUEST;
                }
            }
            textHeader = startLine;
            startLine = checkedIdx;
            switch (checkState) {
                case CHECK_STATE_REQUESTLINE:
                    ret = parseRequestLine(request);
                    if (ret == HTTP_CODE.BAD_REQUEST) {
                        return ret;
                    }
                    break;
                case CHECK_STATE_HEADER:
                    ret = parseRequestHeader(request);
                    if (ret == HTTP_CODE.BAD_REQUEST) {
                        return ret;
                    } else if (ret == HTTP_CODE.GET_REQUEST) {
                        return doRequest();
                    }
                    break;
                case CHECK_STATE_CONTENT:
                    content = new String(request, textHeader, contentLength);
                    //开始处理
                    return doRequest();
                default:
                    return HTTP_CODE.INTERNAL_ERROR;
            }
        }
    }

    private HTTP_CODE parseRequestLine(char[] request) {
        int i;
        //跳过前置空格
        while (request[textHeader] == ' ' || request[textHeader] == '\t') {
            textHeader++;
        }
        //解析method
        for (i = textHeader; i < checkedIdx; ++i) {
            if (request[i] == ' ' || request[i] == '\t') {
                String methodStr = new String(request, textHeader, i - textHeader);
                textHeader = i;
                if (methodStr.equalsIgnoreCase("GET")) {
                    method = METHOD.GET;
                } else if (methodStr.equalsIgnoreCase("POST")) {
                    method = METHOD.POST;
                } else {
                    return HTTP_CODE.BAD_REQUEST;
                }
                break;
            }
        }
        if (i == checkedIdx) {
            return HTTP_CODE.BAD_REQUEST;
        }
        //跳过前置空格
        while (request[textHeader] == ' ' || request[textHeader] == '\t') {
            textHeader++;
        }
        //解析uri
        for (i = textHeader; i < checkedIdx; ++i) {
            if (request[i] == ' ' || request[i] == '\t') {
                uri = new String(request, textHeader, i - textHeader);
                textHeader = i;
                break;
            }
        }
        if (i == checkedIdx) {
            return HTTP_CODE.BAD_REQUEST;
        }
        //跳过前置空格
        while (request[textHeader] == ' ' || request[textHeader] == '\t') {
            textHeader++;
        }
        //解析版本号
        for (i = textHeader; i < checkedIdx; ++i) {
            if (request[i] == ' ' || request[i] == '\t' || request[i] == '\0') {
                version = new String(request, textHeader, i - textHeader);
                if (!version.equalsIgnoreCase("HTTP/1.1") ) {
                    return HTTP_CODE.BAD_REQUEST;
                }
                break;
            }
        }
        checkState = CHECK_STATE.CHECK_STATE_HEADER;// 更新主状态机状态
        return HTTP_CODE.NO_REQUEST;
    }

    private HTTP_CODE parseRequestHeader(char[] request) {
        if (request[textHeader] == '\0') {//请求头结束
            if (contentLength != 0) {// 为POST请求
                checkState = CHECK_STATE.CHECK_STATE_CONTENT;
                return HTTP_CODE.NO_REQUEST;
            }
            return HTTP_CODE.GET_REQUEST;// 为GET请求 解析完毕
        } else if (new String(request, textHeader, 15).equalsIgnoreCase("Content-length:")) {
            textHeader += 15;
            //跳过前置空格
            while (request[textHeader] == ' ' || request[textHeader] == '\t') {
                textHeader++;
            }
            //解析congtentLength
            int i = 0;
            for (i = textHeader; i < checkedIdx; ++i)  {
                if (request[i] == ' ' || request[i] == '\t' || request[i] == '\0') {
                    contentLength = Integer.valueOf(new String(request, textHeader, i - textHeader));
                    break;
                }
            }
            if (i == checkedIdx) {
                return HTTP_CODE.BAD_REQUEST;
            }
        }
        return HTTP_CODE.NO_REQUEST;
    }

    //从状态机用于分析一行内容以/r/n结尾返回LINE_OK
    private LINE_STATUS parseLine(char[] request) {
        char temp;
        for (; checkedIdx < inputBuffer.limit(); checkedIdx++) {
            temp = request[checkedIdx];
            if (temp == '\r') {
                if (checkedIdx + 1 == inputBuffer.limit()) {
                    return LINE_STATUS.LINE_OPEN;
                } else if (request[checkedIdx + 1] == '\n') {
                    request[checkedIdx++] = '\0';
                    request[checkedIdx++] = '\0';
                    return LINE_STATUS.LINE_OK;
                }
                return LINE_STATUS.LINE_BAD;
            } else if (temp == '\n') {
                if (checkedIdx > 1 && request[checkedIdx - 1] == '\r') {
                    request[checkedIdx - 1] = '\0';
                    request[checkedIdx++] = '\0';
                    return LINE_STATUS.LINE_OK;
                }
                return LINE_STATUS.LINE_BAD;
            }
        }
        return LINE_STATUS.LINE_OPEN;
    }

    private HTTP_CODE doRequest() {
        if (method == METHOD.GET) {
            if (uri.equalsIgnoreCase("/")) {
                uri = "/judge.html";
            }
            if (uri.charAt(1) == '0') {
                uri = "/register.html";
            } else if (uri.charAt(1) == '1') {
                uri = "/log.html";
            } else if (uri.charAt(1) == '4') {
                uri = "/stu_id.html";
            } else if (uri.charAt(1) == '5') {
                uri = "/photo.html";
            }
            File file = new File(staticRoot + uri);
            if (!file.exists()) {
                return HTTP_CODE.NO_RESOURCE;
            }
        } else if (method == METHOD.POST) {
                Connection connection = ConnectionFactory.getConnection();
                try {
                    connection.setAutoCommit(false);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                //提取名字和密码
                int i;
                for (i = 5; i < content.length(); ++i) {
                    if (content.charAt(i) == '&') {
                        break;
                    }
                }
                String name = content.substring(5, i);
                int start = i + 10;
                for (i = start; i < content.length(); ++i) {
                    if (content.charAt(i) == '\0') {
                        break;
                    }
                }
                String password = content.substring(start, i);
                try {
                    if (uri.charAt(1) == '3') {//注册
                        Statement queryStmt = connection.createStatement();
                        ResultSet queryRes = queryStmt.executeQuery("select * from users where name=\"" + name + "\"");
                        if (queryRes.next()) {
                            uri = "/registerError.html";
                        } else {
                            Statement insertStmt = connection.createStatement();
                            insertStmt.executeUpdate("insert into users(name, password) values(\"" + name + "\",\"" + password + "\")");
                            uri = "/log.html";
                        }
                    } else if (uri.charAt(1) == '2') {// 登入
                        Statement queryStmt = connection.createStatement();
                        ResultSet queryRes = queryStmt.executeQuery("select * from users where name=\"" + name + "\"");
                        if (queryRes.next()) {
                            uri = "/welcome.html";
                        } else {
                            uri = "/logError.html";
                        }
                    }
                    connection.commit();
                } catch (SQLException e) {
                    try {
                        connection.rollback();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    throw new RuntimeException(e);
                } finally {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        }
        return HTTP_CODE.FILE_REQUEST;
    }

    private void processWrite(TCPConnection connection,  HTTP_CODE code) {
        File file;
        byte[] fileArray;
        InputStream inputStream;
        switch (code) {
            case INTERNAL_ERROR:
                addStatusLine(500, error_500_title);
                addContentLength(error_500_form.length());
                addConteType();
                addBlankLine();
                addContent(strToBytes(error_500_form));
                break;
            case BAD_REQUEST:
                addStatusLine(400, error_400_title);
                addContentLength(error_400_form.length());
                addConteType();
                addBlankLine();
                addContent(strToBytes(error_404_form));
                break;
            case FORBIDDEN_REQUEST:
                addStatusLine(403, error_403_title);
                addContentLength(error_403_form.length());
                addConteType();
                addBlankLine();
                addContent(strToBytes(error_403_form));
                break;
            case NO_RESOURCE:
                addStatusLine(404, error_404_title);
                addContentLength(error_404_form.length());
                addConteType();
                addBlankLine();
                addContent(strToBytes(error_404_form));
                break;
            case FILE_REQUEST:
                addStatusLine(200, ok_200_title);
                addConteType();
                file = new File(staticRoot + uri);
                fileArray = new byte[(int)file.length()];
                try {
                    inputStream = new BufferedInputStream(new FileInputStream(file));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                try {
                    inputStream.read(fileArray);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                addContentLength(fileArray.length);
                addBlankLine();
                addContent(fileArray);
                break;
        }
        outputBuffer.flip();
        connection.send(outputBuffer);
        outputBuffer.clear();
    }

    private void addStatusLine(int status, String title) {
        String statusLineStr = String.format("%s %d %s\r\n", "HTTP/1.1", status, title);
        addResponse(strToBytes(statusLineStr));
    }
    private void addContentLength(int len) {
        String contentLengthStr = String.format("Content-Length:%d\r\n", len);
        addResponse(strToBytes(contentLengthStr));
    }

    private void addConteType() {
        String contentTypeStr = String.format("Content-Type:%s\r\n", "text/html");
        addResponse(strToBytes(contentTypeStr));
    }

    private void addBlankLine() {
        addResponse(strToBytes("\r\n"));
    }

    private void addContent(byte[] text) {
        addResponse(text);
    }

    private byte[] strToBytes(String str) {
        byte[] newArray;
        try {
            newArray = str.getBytes(charSetName);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return newArray;
    }
    private void addResponse(byte[] newArray) {
        byte[] oldArray = outputBuffer.array();
        int offset = 0;
        int count = outputBuffer.position();

        int totalLength = count + newArray.length;
        if (totalLength > outputBuffer.limit()) { // outputBuffer扩容
            outputBuffer = ByteBuffer.allocate(totalLength * 2);
            outputBuffer.put(oldArray, offset, count);
        }
        outputBuffer.put(newArray);
    }
}
