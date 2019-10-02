package com.upc.gessi.spring;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

    public static String getProperty(String key) {
        try {
            return loadProperties("application.properties").getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Properties loadProperties(String resourceFileName) throws IOException {
        Properties configuration = new Properties();
        InputStream inputStream = PropertiesLoader.class
                .getClassLoader()
                .getResourceAsStream(resourceFileName);
        configuration.load(inputStream);
        inputStream.close();
        return configuration;
    }
}
