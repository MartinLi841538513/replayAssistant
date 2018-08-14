package com.alany.u2.kuaishou.ui;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiObject2;

import com.alany.u2.base.BaseAction;
import com.alany.u2.config.Config;
import com.alany.u2.utils.StringUtil;

import java.util.List;

public class HomePage extends BaseAction {
    public static BySelector titleRootBy = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/title_root");

    public UiObject2 getVideoTab(){
        BySelector by = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/portfolio_button");
        if (hasObjects(by)) {
            List<UiObject2> buttons = mDevice.findObjects(by);
            if (buttons != null) {
                for (UiObject2 button : buttons) {
                    String text = button.getText();
                    if (text != null && text.contains("作品")) {
                        return button;
                    }
                }
            }
        }
        return null;
    }

    public int getVideoCount(){
        if (getVideoTab() != null) {
            String text = getVideoTab().getText();
            if (text != null) {
                text = StringUtil.subNumber(text);
                return StringUtil.isEmpty(text) ? 0 : Integer.parseInt(text);
            }
        }
        return 0;
    }

    public List<UiObject2> getVideoList(){
        BySelector by = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/player_cover");
        return hasObjects(by) ? mDevice.findObjects(by) : null;
    }

    public void loopVideoList(String accountName, int maxCount){
        int totalCount = getVideoCount();
        log.i("当前账号的作品总数：" + totalCount);

        List<UiObject2> videoList = getVideoList();
        if (videoList != null && !videoList.isEmpty()) {
            int counter = 1;
            VideoPage videoPage = null;
            for (UiObject2 video : videoList) {
                if (counter > maxCount) {
                    log.i("已达到设置的最大遍历数["+ maxCount +"], 退出作品遍历");
                    break;
                }
                log.i("开始遍历第" + counter + "个作品");
                click(video, 1000);

                if (waitElement(VideoPage.commentIconBy)) {
                    videoPage = new VideoPage(accountName);
                    videoPage.doReply();
                }

                while (!isVisible(HomePage.titleRootBy)) {//跑完一轮得回到主页面
                    mDevice.pressBack();
                    sleep(500);
                }

                counter++;
            }
            log.i(String.format("[End]本轮作品遍历已经完成，总作品数[%d]，已遍历作品数[%d]", totalCount, counter));
        }
    }
}