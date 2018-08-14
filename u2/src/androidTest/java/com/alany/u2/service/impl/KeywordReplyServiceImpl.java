package com.alany.u2.service.impl;

import com.alany.u2.base.BaseCase;
import com.alany.u2.config.Config;
import com.alany.u2.service.KeywordReplyService;
import com.alany.u2.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by alany on 2018/7/27.
 */
public class KeywordReplyServiceImpl implements KeywordReplyService {

    private static Map<String, List<String>> krMap = new HashMap<String, List<String>>();

    @Override
    public void importData() {
        File file = new File(Config.KEYWORD_CONFIG_FILE);
        if (file.exists() && file.isFile()) {
            List<String> lines = FileUtil.getFileAllLine(file);

            if (lines != null && !lines.isEmpty()) {

                for (String line : lines) {
                    if (checkLine(line)) {
                        String[] arr = line.split(Config.KEY_VALUE_SEPARATOR);
                        String keys = arr[0];
                        String values = null;
                        if (arr.length > 1) {
                            values = arr[1];
                        }

                        List<String> list = null;
                        if (values != null && !"".equals(values)) {
                            if (values.contains(Config.VALUE_TEXT_SEPARATOR)) {
                                list = Arrays.asList(values.split(Config.VALUE_TEXT_SEPARATOR));
                            } else {
                                list = new ArrayList<String>();
                                list.add(values);
                            }
                        }

                        if (keys.contains(Config.VALUE_TEXT_SEPARATOR)) {
                            for (String key : keys.split(Config.VALUE_TEXT_SEPARATOR)) {
                                krMap.put(key, list);
                            }
                        } else {
                            krMap.put(keys, list);
                        }
                    }
                }
                BaseCase.log.i("import keywords data: " + krMap);
            } else {
                BaseCase.log.e("配置文件内容为空");
            }
        } else {
            BaseCase.log.e("配置文件不存在：" + Config.KEYWORD_CONFIG_FILE);
        }
    }

    @Override
    public List<String> getReplyList(String comment) {
        List<String> replyList = new ArrayList<String>();
        if (krMap != null && !krMap.isEmpty()) {
            for (Map.Entry<String, List<String>> entry : krMap.entrySet()) {
                if (comment.contains(entry.getKey())) {
                    if (entry.getValue() != null) {
                        replyList.addAll(entry.getValue());
                    }
                }
            }
        }

        if (replyList.isEmpty()) {
            replyList = getDefaultReplyList();
        }
        return replyList;
    }

    public List<String> getDefaultReplyList() {
        String key = "默认回复";
        List<String> replyList = new ArrayList<String>();
        if (krMap != null && !krMap.isEmpty()) {
            for (Map.Entry<String, List<String>> entry : krMap.entrySet()) {
                if (entry.getKey().contains(key)) {
                    if (entry.getValue() != null) {
                        replyList.addAll(entry.getValue());
                    }
                }
            }
        }
        return replyList;
    }

    @Override
    public String getDefaultReply() {
        String[] basePeffix = new String[]{"hah, ", "哈哈，", "嗷~", "喵~", "[笑脸] ", "[比心] ","[卖萌] ", "[握手] ", "[飞吻] ", "[抱拳] ", "[娇羞] ", ""};
        String[] baseStr = new String[]{"谢谢", "谢谢你", "多谢", "感谢关注", "请多指教"};
        String[] basesuffix = new String[]{".", "。", "!", "~", "~~~", "^_^", "", ""};
        Random random = new Random();
        int peffixIndex = random.nextInt(basePeffix.length);
        int strIndex = random.nextInt(baseStr.length);
        int suffixIndex = random.nextInt(basesuffix.length);
        return basePeffix[peffixIndex] + baseStr[strIndex] + basesuffix[suffixIndex];
    }

    private boolean checkLine(String line) {
        boolean isValid = line != null && !"".equals(line);
        isValid = isValid && line.contains(Config.KEY_VALUE_SEPARATOR);//分隔符
        return isValid;
    }
}