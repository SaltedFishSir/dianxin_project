package com.iflytek.util;

import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;

public class ConnectionUtil {
    private static Connection connection;

    private ConnectionUtil() {
    }


    public static Connection getConnectionInstance(){

        if (connection == null || connection.isClosed())
        {
            try {
                connection = ConnectionFactory.createConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return connection;

    }


}
