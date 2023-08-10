package org.jingtao8a.mysql;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {
    private static ConnectionFactory connectionFactory = new ConnectionFactory();
    private DataSource dataSource;
    private ConnectionFactory() {
        try {
            Properties properties = new Properties();
            InputStream inputStream = ConnectionFactory.class.getClassLoader().getResourceAsStream("druid.properties");
            properties.load(inputStream);
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ConnectionFactory getInstance() {
        return connectionFactory;
    }

    public static synchronized Connection getConnection() {
        try {
            return getInstance().dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
