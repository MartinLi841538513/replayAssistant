package com.alany.u2.kuaishou.ui;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiObject2;

import com.alany.u2.base.BaseAction;
import com.alany.u2.config.Config;

import java.util.List;

public class MainPage extends BaseAction {

    private BySelector tabBy = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/irb_radioButton");

    public UiObject2 getTab(String text) {
        if (waitElement(tabBy)) {

            List<UiObject2> tabs = mDevice.findObjects(tabBy);
            for (UiObject2 tab : tabs) {
                if (text.equals(tab.getText())) {
                    return tab;
                }
            }
        }
        return null;
    }

    public UiObject2 getLeftButton(){
        BySelector by = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/left_btn");
        return hasObject(by) ? mDevice.findObject(by) : null;
    }

    public boolean waitTab(String text) {
        int counter = 1;
        try {
            while (getTab(text) == null) {
                if (counter > 10) {
                    log.i("等待10s仍未获取到["+ text +"]tab");
                    return false;
                }
                sleep(1000);
                counter++;
            }
            return getTab(text) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean gotoHomeMenu(){
        click(getLeftButton(), 1000);
        BySelector menuLayoutBy = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/menu_layout");
        return waitElement(menuLayoutBy);
    }


    public boolean gotoNoticeTab(){
        BySelector by = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/tab_notice");
        if (hasObject(by)) {
            click(mDevice.findObject(by), 1000);

            return  waitTab("消息");
        }
        return false;
    }

    public boolean gotoHomePage(){
        BySelector by = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/tab_avatar");
        if (waitElement(by)) {
            click(mDevice.findObject(by), 1000);
            return waitElement(HomePage.titleRootBy);
        }
        return false;
    }

    public String getAccountName(){
        BySelector by = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/tab_name");
        if (hasObject(by)) {
            return mDevice.findObject(by).getText();
        }
        return null;
    }
}