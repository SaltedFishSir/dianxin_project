package com.iflytek.util;

import java.sql.*;

public class JDBCUtil {

    private static final String MYSQL_JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String MYSQL_JDBC_URL = "jdbc:mysql://hadoop131:3306/ct?useUnicode=true&characterEncoding=UTF-8";
    private static final String MYSQL_JDBC_USERNAME = "root";
    private static final String MYSQL_JDBC_PASSWORD = "123456";


    public static Connection getConnection() {

        try {
            Class.forName(MYSQL_JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(MYSQL_JDBC_URL, MYSQL_JDBC_USERNAME, MYSQL_JDBC_PASSWORD);

            return connection;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void close(Connection connection, Statement statement, ResultSet resultSet) {

        //连接
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //编译sql
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //结果集
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
