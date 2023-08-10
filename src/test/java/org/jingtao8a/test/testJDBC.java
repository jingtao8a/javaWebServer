package org.jingtao8a.test;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.util.Stack;

public class testJDBC {
    @Test
    public void testJDBC() throws Exception {
        Properties properties = new Properties();
        InputStream inputStream = testJDBC.class.getClassLoader().getResourceAsStream("druid.properties");
        properties.load(inputStream);
        DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);
        Connection  conn = dataSource.getConnection();
        conn.setAutoCommit(false);
        try{
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("select * from users");
            while (result.next()) {
                System.out.println(result.getString("name") + " " + result.getString("password"));
            }
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
    }
}
