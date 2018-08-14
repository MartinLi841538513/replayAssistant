package com.alany.u2.douyin.ui;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiObject2;

import com.alany.u2.base.BaseAction;
import com.alany.u2.base.BaseCase;

/**
 * Created by alany on 2018/7/25.
 */
public class MainPage extends BaseAction {

    public UiObject2 getGroupTab(String text) {
        try {
//            BySelector tabHostBy = By.clazz("android.widget.TabHost");
//            UiObject2 tabHost = waitElement(tabHostBy, TIMEOUT);
//            if (tabHost != null) {
//                String tabXpath = "android.widget.FrameLayout[0]/android.widget.FrameLayout[1]/android.widget.LinearLayout[0]";
//                UiObject2 tabLayout = findObjectByXpath(mDevice.findObject(tabHostBy), tabXpath);
//                if (tabLayout != null) {
//                    return tabLayout.findObject(By.text(text));
//                }
//            }
            return waitElement(By.text(text), TIMEOUT);
        } catch (Exception e) {
        } catch (Error e) {}
        return null;
    }

    public boolean waitTab(String text) {
        int counter = 1;
        try {
            while (getGroupTab(text) == null) {
                if (counter > 10) {
                    log.e("[Exception]等待10s仍未获取到[" + text + "]tab");
                    return false;
                }
                sleep(1000);
                counter++;
            }
            return getGroupTab(text) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean gotoTab(String text) {
        UiObject2 tab = getGroupTab(text);
        if (tab != null) {
            BaseCase.click(tab, 500);
            return true;
        }
        return false;
    }

    public String getAccountName() {
        String accountName = null;
        gotoTab("我");
        BySelector idBy = By.textStartsWith("抖音号:");
        if (hasObject(idBy)) {
            try {
                UiObject2 parent = mDevice.findObject(idBy).getParent().getParent();
                UiObject2 name = getChild(parent, "android.widget.TextView");
                if (name != null) {
                    accountName = name.getText();
                }
            } catch (Exception e) {
            }catch (Error e) {}
        }
        if (accountName == null || "".equals(accountName)) {
            log.e("[Exception]未能获取到当前登录的账号名称");
        }

        return accountName;
    }
}