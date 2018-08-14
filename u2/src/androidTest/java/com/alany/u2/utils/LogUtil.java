package com.alany.u2.utils;

import android.util.Log;

import org.apache.log4j.Logger;

import java.io.File;

/**
 * Created by alany on 2016/12/27.
 */
public class LogUtil {
    private String tag;
    private String logPath;

    private Logger logger;

    public LogUtil(String tag, String logPath) {
        this.tag = tag;
        this.logPath = logPath;
        logger = getLogger(tag);
        File logFile = new File(logPath);
        if (! logFile.exists()) {
            logFile.mkdir();
        }
    }

    public void i(Object msg){
        Log.i(this.tag, msg + "");
        logger.info(msg);
    }

    public void v(Object msg){
        Log.v(this.tag, msg + "");
        logger.info(msg);
    }

    public void d(Object msg){
        Log.d(this.tag, msg + "");
        logger.debug(msg);
    }


    public void w(Object msg){
        Log.w(this.tag, msg + "");
        logger.warn(msg);
    }


    public void e(Object msg){
        Log.e(this.tag, msg + "");
        logger.error(msg);
    }

    public void e(Object msg, String srceenshot){
        Log.e(this.tag, msg + "");
        logger.error(msg);
        takeScreenshot(srceenshot);
    }

    public void takeScreenshot(final String srceenshot){
        new Thread(new Runnable() {
            @Override
            public void run() {
                TestUtil.takeScreenshot(logPath + "screenshot" + File.separator + srceenshot + ".jpg");
            }
        }).start();
    }

    private Logger getLogger(String tag) {
        Log4jConfigure.configure();
        if ("".equals(tag)) {
            return Logger.getRootLogger();
        }
        return Logger.getLogger(tag);
    }
}
