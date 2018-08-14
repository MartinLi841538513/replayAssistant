package com.alany.u2.config;

import com.alany.u2.base.BaseCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;


/**
 * Created by alany on 2018/08/02.
 */

public class ConfigParser {

    private static Properties properties = new Properties();

    private static boolean isInitSuccess = false;

    static {
        File file = new File(Config.USER_CONFIG_FILE);
        if (!file.exists()) {
            BaseCase.log.e("未能在指定路径找到用户配置文件：" + Config.USER_CONFIG_FILE);
        } else {
            try {
                InputStream inputStream = new FileInputStream(file);
                BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                properties.load(bf);
                isInitSuccess = true;
            } catch (Exception e) {
                BaseCase.log.e("解析用户配置文件出错");
                e.printStackTrace();
            }
        }
    }

    public static String getValue(String key) {
        return isInitSuccess ? (String) properties.get(key) : null;
    }

    public static int getIntValue(String key) {
        return isInitSuccess ? Integer.parseInt(getValue(key)) : 0;
    }
}
