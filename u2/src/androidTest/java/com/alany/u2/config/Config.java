package com.alany.u2.config;

import android.os.Environment;

import java.io.File;
/**
 * Created by alany on 2018/7/26.
 */
public class Config {

    public static final String MAIN_PATH = Environment.getExternalStorageDirectory() + File.separator + "u2Test" + File.separator;

    public static final String LOG_PATH = MAIN_PATH + "log" + File.separator;

    public static final String LOG_FILE = LOG_PATH + "u2Test.log";//日志文件路径

    public static final String KEYWORD_CONFIG_FILE = Environment.getExternalStorageDirectory() + File.separator + "2_keywords.txt";//关键字回复文件的路径

    public static final String USER_CONFIG_FILE = Environment.getExternalStorageDirectory() + File.separator + "config.properties";//用户配置文件的路径

    public static final String KEY_VALUE_SEPARATOR = "="; //key和value之间的分隔符，要与文件中保持一致

    public static final String VALUE_TEXT_SEPARATOR = "/";//key或value一行有多个时的分隔符，要与文件中保持一致

    public static final String DOUYIN_APP_PACKAGE = "com.ss.android.ugc.aweme";//抖音包名

    public static final boolean DOUYIN_IS_LOOP_COMMENT_LIST = false; //是否遍历抖音最内层的评论列表
    //抖音最大的无法评论数，达到这个阈值，group列表向下滑动去刷新页面
    public static final int DOUYIN_MAX_CANNOT_COMMENT_NUM = ConfigParser.getIntValue("douyin.max.interrupt.num");
    //抖音最大的连续评论受阻数（比如提交时界面提示评论太快等），达到阈值，测试退出
    public static final int DOUYIN_MAX_COMMENT_BLOCKED_NUM = 5;

    public static final String KUAISHOU_APP_PACKAGE = "com.smile.gifmaker"; //快手包名

    public static final int KUAISHOU_MAX_VIDEO_NUM = ConfigParser.getIntValue("kuaishou.max.video.loop.num"); //快手作品集的最大遍历数

    public static final String BAIDU_OCR_APPID = "11613056";
    public static final String BAIDU_OCR_APIKEY = "9UG9la20PT61GtS7P667wjqp";
    public static final String BAIDU_OCR_SECRET = "NUvb7oXE708RPjRFOvA4GYTCxEhWG0SQ";
}