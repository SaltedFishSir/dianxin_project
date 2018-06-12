package com.iflytek.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    public static Properties properties = new Properties();

    static {

        InputStream is = ClassLoader.getSystemResourceAsStream("kafka.properties");
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String getProperty(String key){

        return properties.getProperty(key);

    }

}
