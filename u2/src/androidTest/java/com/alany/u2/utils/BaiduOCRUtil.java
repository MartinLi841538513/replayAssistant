package com.alany.u2.utils;

import com.alany.u2.base.BaseCase;
import com.alany.u2.config.Config;
import com.baidu.aip.ocr.AipOcr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

public class BaiduOCRUtil {

    // 初始化一个AipOcr
    private static AipOcr client = new AipOcr(Config.BAIDU_OCR_APPID, Config.BAIDU_OCR_APIKEY, Config.BAIDU_OCR_SECRET);
    private static int retryTimes = 2;

    public static String doOcr(String filePath) {
        File imgFile = new File(filePath);
        try {
            if (imgFile.exists()) {
                JSONArray array = requestBaiduOcrApi(filePath);
                if (array != null && array.length() > 0) {
                    String result = array.getJSONObject(0).getString("words");
                    if (result != null) {
                        result = result.replaceAll(" ", "");
                    }
                    return result;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static JSONArray  requestBaiduOcrApi(String filePath){
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
        //        client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
        //        client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理

        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
//        System.setProperty("aip.log4j.conf", "log4j.properties");

        // 调用接口
        JSONObject res = client.basicGeneral(filePath, new HashMap<String, String>());
        JSONArray results = null;
        if (res.has("words_result")) {
            try {
                results = res.getJSONArray("words_result");
            } catch (JSONException e) {}
        }

        if (null == results) {
            BaseCase.log.e("OCR failed, results=" + results);
        }
        return results;
    }

    public static void main(String[] args) throws InterruptedException {
        String path = "E:\\data\\capture\\test.png";
        System.out.println(requestBaiduOcrApi(path));
    }
}