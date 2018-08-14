package com.alany.u2.kuaishou.ui;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiObject2;

import com.alany.u2.base.BaseAction;
import com.alany.u2.config.Config;
import com.alany.u2.utils.TestUtil;

import java.util.ArrayList;
import java.util.List;

public class MessagePage extends BaseAction {
    private int commentFailedCount = 0;
    private BySelector recycleViewBy = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/recycler_view");
    private BySelector itemNameBy = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/title");
    private BySelector itemCommentBy = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/description");

    public UiObject2 getRecycleView (){
        return hasObject(recycleViewBy) ? mDevice.findObject(recycleViewBy) : null;
    }

    public List<UiObject2> getCommentList(){
        BySelector by = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/notice_item_container");
        List<UiObject2> commentList = new ArrayList<UiObject2>();
        if (hasObjects(by)) {
            List<UiObject2> noticeList = mDevice.findObjects(by);
            for (UiObject2 item : noticeList) {
                if (hasObject(item, itemNameBy) && hasObject(item, itemCommentBy)) {
                    commentList.add(item);//title和description同时存在才是评论，只有title就是点赞，过滤掉点赞的notice
                }
            }
        }
        return commentList;
    }

    public UiObject2 getOpLayout(){
        BySelector opLayoutBy = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/operation_layout");
        return hasObject(opLayoutBy) ? mDevice.findObject(opLayoutBy) : null;
    }

    public UiObject2 getInputEditor(){
        UiObject2 opLayout = getOpLayout();
        if (opLayout != null) {
            BySelector editorBy = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/editor");
            return hasObject(opLayout, editorBy) ? opLayout.findObject(editorBy) : null;
        }
        return null;
    }

    public UiObject2 getCommitButton(){
        UiObject2 opLayout = getOpLayout();
        if (opLayout != null) {
            BySelector buttonBy = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/finish_button_slide");
            return hasObject(opLayout, buttonBy) ? opLayout.findObject(buttonBy) : null;
        }
        return null;
    }

    public void doReply(){
        int counter = 1;
        boolean isFreshMode = false;//开启刷新模式
        while (true) {

            if (isFreshMode) {
                if (counter % 10 == 0) {
                    log.i("已开启【刷新模式】，后续操作就只刷新并检测当前页面");
                }

                sleep(5000);//休眠5s再刷新
                swipeDown(getRecycleView());
            } else {
                if (getCommentList() != null && !getCommentList().isEmpty()) {
                    log.i("正在处理第" + counter + "页的评论item");

                    for (UiObject2 item : getCommentList()) {
                        UiObject2 commentView = item.findObject(itemCommentBy);
                        String comment = TestUtil.getTextByOCR(commentView);
                        click(commentView, 1000);
                        BySelector opLayoutBy = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/operation_layout");
                        if (waitElement(opLayoutBy)) {

                        }
                    }
                    //处理完一页后就滑动翻页
                    swipeUp(getRecycleView(), 1.0f);
                    counter++;
                }
            }
        }
    }

    private void freshList(int count){
        UiObject2 scrollView = getRecycleView();
        if (scrollView != null) {
            int counter = 0;
            while (true) {
                if (counter > count) {
                    break;
                }
                swipeDown(scrollView);
                counter++;
            }
        }
    }




}