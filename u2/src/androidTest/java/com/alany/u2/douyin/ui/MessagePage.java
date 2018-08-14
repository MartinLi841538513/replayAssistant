package com.alany.u2.douyin.ui;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiObject2;

import com.alany.u2.base.BaseAction;
import com.alany.u2.base.BaseCase;
import com.alany.u2.config.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alany on 2018/7/25.
 */
public class MessagePage extends BaseAction {
    public static int commentNum = 0;

    public static UiObject2 getCommentGroup() {
        BySelector RvBy = By.clazz("android.support.v7.widget.RecyclerView");
        BySelector by = By.clazz("android.view.ViewGroup");
        if (hasObjects(RvBy)) {
            UiObject2 recycleView = BaseCase.mDevice.findObject(RvBy);
            List<UiObject2> groups = recycleView.findObjects(by);
            if (groups == null) {
                log.e("[Error]未找到任何ViewGroup对象");
                return null;
            }

            BySelector byTv = By.clazz("android.widget.TextView");
            BySelector byImg = By.clazz("android.widget.ImageView");

            Map<String, String> params = new HashMap<String, String>();
            params.put("class", "android.widget.TextView");
            params.put("text", "评论");
            params.put("pkg", Config.DOUYIN_APP_PACKAGE);

            for (UiObject2 group : groups) {
                UiObject2 txtView = getChild(group, params);
                if (txtView != null) {
                    UiObject2 numLayout = getChild(group, "android.widget.FrameLayout");
                    if (numLayout != null) {
                        try {
                            String commentNumStr = getChild(numLayout, "android.widget.TextView").getText();
                            if (commentNumStr != null && !"".equals(commentNumStr)) {
                                commentNum = Integer.parseInt(commentNumStr);
                            }
                        } catch (Exception e) {
                        }
                    }
                    log.i("未处理评论数：" + commentNum);
                    return group.findObject(byImg);
                }
            }
        }
        return null;
    }

    public static boolean gotoCommentPage() {
        UiObject2 group = getCommentGroup();
        if (group != null) {
            click(group, 500);
            return true;
        } else {
            log.e("[Error]未能找到评论ImageView对象");
        }
        return false;
    }
}