package com.alany.u2.kuaishou;

import android.support.test.runner.AndroidJUnit4;

import com.alany.u2.base.BaseCase;
import com.alany.u2.config.Config;
import com.alany.u2.kuaishou.ui.HomePage;
import com.alany.u2.kuaishou.ui.MainPage;
import com.alany.u2.rule.Repeat;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

/**
 * Created by alany on 2018/7/30.
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainTest extends BaseCase {

    @Test
    @Repeat(times = 100)
    public void run() {
        runMode(Config.KUAISHOU_MAX_VIDEO_NUM);
    }

    public static void runMode(int maxCount){
        MainPage mainPage = new MainPage();
        if (mainPage.waitTab("发现")) {
            if (mainPage.gotoHomeMenu()) {
                String accountName = mainPage.getAccountName();

                if (mainPage.gotoHomePage()) {
                    HomePage homePage = new HomePage();
                    homePage.loopVideoList(accountName, maxCount);
                }
            }
        }
    }

    @Override
    public String setPackageName() {
        return Config.KUAISHOU_APP_PACKAGE;
    }
}