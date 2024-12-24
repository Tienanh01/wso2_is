package com.example.SSO_Intergration.until;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropsUtil {
    private static Properties prop = new Properties();

    public static Properties readPropertyFile() throws Exception {
        if (prop.isEmpty()) {
            InputStream input = PropsUtil.class.getClassLoader().getResourceAsStream("application.properties");
            try {
                prop.load(input);
            } catch (IOException ex) {
                throw ex;
            } finally {
                if (input != null) {
                    input.close();
                }
            }
        }
        return prop;
    }

    public static Properties removeProperty() {
        prop= new Properties();

        return prop;
    }

    public static String get(String key) {
        if (prop.isEmpty()) {
            try {
                prop = readPropertyFile();
            } catch (Exception e) {
            }
        }
        return prop.getProperty(key);
    }
}
