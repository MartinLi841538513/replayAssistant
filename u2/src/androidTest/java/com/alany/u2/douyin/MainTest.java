package com.alany.u2.douyin;

import android.support.test.runner.AndroidJUnit4;

import com.alany.u2.base.BaseCase;
import com.alany.u2.config.Config;
import com.alany.u2.douyin.ui.CommentPage;
import com.alany.u2.douyin.ui.MainPage;
import com.alany.u2.douyin.ui.MessagePage;
import com.alany.u2.rule.Repeat;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

/**
 * Created by alany on 2018/7/25.
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainTest extends BaseCase {

    @Test
    @Repeat(times = 100)
    public void run() {
        runMode(CommentPage.RUNMODE_HYBIRD, Config.DOUYIN_IS_LOOP_COMMENT_LIST);
    }

    public static void runMode(int runMode, boolean isLoopList){
        MainPage mainPage = new MainPage();
        if (mainPage.waitTab("消息")) {
            String accountName = mainPage.getAccountName();
            log.i("当前账号名称：" + accountName);
            mainPage.gotoTab("消息");

            if (MessagePage.gotoCommentPage()) {
                CommentPage commentPage = new CommentPage(accountName);
                commentPage.doReply(runMode, isLoopList);
            }
        }
    }

    @Override
    public String setPackageName() {
        return Config.DOUYIN_APP_PACKAGE;
    }
}