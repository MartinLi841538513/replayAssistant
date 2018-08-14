package com.alany.u2.utils;

import com.alany.u2.base.BaseCase;
import com.alany.u2.config.Config;

import org.apache.log4j.Level;

import java.io.File;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * Created by alany on 2018/7/25.
 */
public class Log4jConfigure {
    private static final String TAG = BaseCase.testTag;

    public static void configure() {
        final LogConfigurator logConfigurator = new LogConfigurator();
        try {
            File logFile = new File(Config.LOG_FILE);
            if(logFile.exists()){
                logFile.delete();
            }
            FileUtil.createFile(Config.LOG_FILE);
            logConfigurator.setFileName(Config.LOG_FILE);

            //以下为通用配置
            logConfigurator.setUseLogCatAppender(false);//不输出到logcat
            logConfigurator.setUseFileAppender(true);
            logConfigurator.setImmediateFlush(true);
            logConfigurator.setRootLevel(Level.DEBUG);
            logConfigurator.setFilePattern("%d\t%p/%c:\t%m%n");
            logConfigurator.configure();

            android.util.Log.i(TAG, "Log4j config finished");
        } catch (Throwable throwable) {
            logConfigurator.setResetConfiguration(true);
            android.util.Log.e(TAG, "Log4j config error, use default config. Error:" + throwable);
        }
    }
}
