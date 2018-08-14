package com.alany.u2.douyin.ui;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiObject2;

import com.alany.u2.base.BaseAction;
import com.alany.u2.config.Config;
import com.alany.u2.utils.ColorUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by alany on 2018/7/26.
 */
public class CommentPage extends BaseAction {
    public static final int RUNMODE_HYBIRD = 0; //遍历+刷新模式
    public static final int RUNMODE_REFRESH = 1; //刷新模式
    public static final int RUNMODE_LOOP = 2; //遍历不中断模式
    public static final int RUNMODE_LOOP_BREAK = 3; //遍历完中断模式

    private BySelector recycleViewBy = By.clazz("android.support.v7.widget.RecyclerView");
    private String defaultInputText = "有爱评论，说点儿好听的";
    private String bottomEndText = "无更多消息";
    private int commentFailedCount = 0; //连续的已评论数
    private int commentBlockedCount = 0; //连续评论受阻数
    private String accountName;

    public CommentPage(String accountName) {
        this.accountName = accountName;
    }

    public UiObject2 getScrollView() {
        return waitElement(recycleViewBy) ? mDevice.findObject(recycleViewBy) : null;
    }

    public List<UiObject2> getCommentGroups() {
        UiObject2 scrollView = getScrollView();
        if (scrollView == null) {
            log.e("[Error]未找到RecyclerView容器");
            return null;
        }

        BySelector groupViewBy = By.clazz("android.view.ViewGroup");
        List<UiObject2> list = null;
        if (hasObject(scrollView, groupViewBy)) {
            list = scrollView.findObjects(groupViewBy);
        }
        return list;
    }

    /**
     * 评论输入框
     */
    public UiObject2 getInputEditView() {
        BySelector by = By.clazz("android.widget.EditText");
        return hasObject(by) ? mDevice.findObject(by) : null;
    }

    /**
     * 评论输入框中的提交View
     */
    public UiObject2 getCommitView() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("class", "android.widget.ImageView[1]");
        params.put("pkg", Config.DOUYIN_APP_PACKAGE);
        return getChild(mDevice, params);
    }

    private UiObject2 getGroupCommentText(UiObject2 group) {
        try {
            UiObject2 item = getChild(group, "android.widget.TextView[1]");
            if (isVisible(item)) {
                return item;
            }
        } catch (Exception e) {
        }
        return null;
    }

    public UiObject2 getBottomEndLabel() {
        return hasObject(By.text(bottomEndText)) ? mDevice.findObject(By.text(bottomEndText)) : null;
    }

    /**
     * @param runMode    0表示循环模式，1表示y刷新模式，2表示混合模式
     * @param isLoopList
     */
    public void doReply(int runMode, boolean isLoopList) {
        int counter = 1;
        boolean isreFreshMode = false;//开启刷新模式
        while (true) {

            if (runMode == RUNMODE_HYBIRD) {
                //连续遇到无需评论的group超过阈值，group列表向下滑动去刷新页面，不再向上加载历史group
                if (commentFailedCount == Config.DOUYIN_MAX_CANNOT_COMMENT_NUM) {
                    refreshListView(counter);
                    isreFreshMode = true;
                }

                if (isreFreshMode) {
                    if (counter % 10 == 0) {
                        log.i("已开启【刷新模式】，后续操作就只刷新并检测当前页面");
                    }
                    refreshCurrentList(isLoopList);
                } else {
                    log.i("正在处理第" + counter + "页的评论group");
                    if (loopGroupList(isLoopList)) {
                        counter++;
                    } else {
                        log.i("已滑到底部：" + bottomEndText + "，后续开启刷新模式");
                        refreshListView(counter);
                        isreFreshMode = true;
                    }
                }
            } else if (runMode == RUNMODE_REFRESH) {
                if (counter % 10 == 0) {
                    log.i("已开启【刷新模式】，后续操作就只刷新并检测当前页面");
                }
                refreshCurrentList(isLoopList);
            } else if (runMode == RUNMODE_LOOP) {
                log.i("正在处理第" + counter + "页的评论group");
                if (loopGroupList(isLoopList)) {
                    counter++;
                } else {
                    refreshListView(counter);
                }
            } else if (runMode == RUNMODE_LOOP_BREAK) {
                //连续遇到无需评论的group超过阈值，group列表向下滑动去刷新页面，不再向上加载历史group
                if (commentFailedCount > Config.DOUYIN_MAX_CANNOT_COMMENT_NUM) {
                    break;
                }
                log.i("正在处理第" + counter + "页的评论group");
                if (loopGroupList(isLoopList)) {
                    counter++;
                } else {
                    refreshListView(counter);
                }
            } else {
                log.e("[Error]无法识别的模式：" + runMode);
            }

            if (commentBlockedCount > Config.DOUYIN_MAX_COMMENT_BLOCKED_NUM) {
                log.w(String.format("连续评论受阻次数[%d]达到阈值[%d]，退出测试",commentBlockedCount, Config.DOUYIN_MAX_COMMENT_BLOCKED_NUM));
                break;
            }
        }
    }

    public boolean loopGroupList(boolean isLoopList) {
        if (getCommentGroups() != null && !getCommentGroups().isEmpty()) {
            loopViewList(getCommentGroups(), isLoopList);

            //处理完一页后就滑动翻页
            swipeUp(getScrollView(), 1.0f);
        }

        if (isVisible(getBottomEndLabel())) {
            loopViewList(getCommentGroups(), isLoopList);//要记得处理完最后一页再退出循环
            return false;
        }
        return true;
    }

    public void refreshCurrentList(boolean isLoopList) {
        loopViewList(getCommentGroups(), isLoopList); //就只处理当前页面
        sleep(5000);//休眠5s再刷新
        swipeDown(getScrollView());
    }

    private void refreshListView(int count) {
        UiObject2 scrollView = getScrollView();
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

    /**
     * 遍历当前页面的group
     *
     * @param groupList
     * @param isLoopList
     * @return
     */
    private void loopViewList(List<UiObject2> groupList, boolean isLoopList) {
        if (groupList != null && !groupList.isEmpty()) {
            for (int i = 0; i < groupList.size(); i++) {
                UiObject2 item = getGroupCommentText(groupList.get(i));
                if (item == null) {
                    continue;
                }
                click(item, 1000);//新View中会加载出所有的评论
                if (!hasObject(By.textContains(defaultInputText))) {
                    log.e("[Error]页面异常：未找到评论列表页底部的评论输入框");
                    break;
                }
                if (hasObject(recycleViewBy)) {
                    UiObject2 scrollView = mDevice.findObject(recycleViewBy);

                    int count = 0;
                    while (reply(scrollView, isLoopList)) {
                        if (!isLoopList) { //非遍历评论list就跑一次就退出循环
                            break;
                        }
                        if (count > 5) {
                            log.i("滑动次数超过5次，退出group[" + i + "]的遍历");
                            break;
                        }
                        swipeUp(scrollView, 1.0f);
                        scrollView = mDevice.findObject(recycleViewBy);
                        if (scrollView == null) {
                            log.e("[Error]界面异常，scrollView为空或不可见");
                            break;
                        }
                        count++;
                    }
                }

                if (getInputEditView() != null) {
                    mDevice.pressBack();
                }
            }
        }
    }


    /**
     * 处理当前屏幕上可见的评论list，isLoopList=false就只处理当list的第一条评论
     * 默认已评论的是在最后，所以当前都是已评论的话，就说明处理完了，返回false, 未处理完则返回ture；
     *
     * @param scrollView
     */
    private boolean reply(UiObject2 scrollView, boolean isLoopList) {
        int count = 0;//出现已评论的次数
        List<UiObject2> childList = scrollView.getChildren();
        String commentXpath = "android.widget.LinearLayout/android.widget.LinearLayout/android.widget.TextView[0]";
        String userXpath = "android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.TextView[0]";
        String likeXpath = "android.widget.LinearLayout/android.widget.RelativeLayout/android.widget.ImageView";
        String likeNumXpath = "android.widget.LinearLayout/android.widget.RelativeLayout/android.widget.TextView";
        if (childList != null && !childList.isEmpty()) {
            if (!isLoopList) {
                UiObject2 firstItem = childList.get(0);
                childList.clear();
                childList.add(firstItem);
            }
            for (int i = 0; i < childList.size(); i++) {
                UiObject2 userView = findObjectByXpath(childList.get(i), userXpath);
                UiObject2 likeView = findObjectByXpath(childList.get(i), likeXpath);
                UiObject2 likeNumView = findObjectByXpath(childList.get(i), likeNumXpath);
                UiObject2 commentView = findObjectByXpath(childList.get(i), commentXpath);
                if (userView != null && commentView != null) {
                    String userName = userView.getText();
                    //异常或者当前是自己的评论或者记录已经点过赞了，跳过
                    if (userName == null || userName.equals(accountName) || isClickedLike(likeView, likeNumView)) {
                        log.i("[Skip]当前评论是自己的或已经被点赞过，跳过");
                        count++;
                        commentFailedCount++;
                        continue;
                    }
                    String comment = commentView.getText();
                    if (comment != null) {
                        List<String> replyList = keywordReplyService.getReplyList(comment);
                        if (replyList == null || replyList.size() < 1) {
                            log.i("[Skip]对应评论未匹配到预留关键字，不自动评论");
                            hideInputBoard();
                            continue;
                        }
                        clickLike(likeView);//评论前先点赞
                        click(childList.get(i), 500);
                        int counter = 0;
                        while (getInputEditView() == null) {//可能点到@内容到主页了
                            mDevice.pressBack();
                            sleep(500);
                            if (counter > 5) {
                                break;
                            }
                            click(commentView, 500);
                        }
                        boolean isCommit = assertCommit(comment, replyList);
                        if (isCommit) {
                            i++;//成功会新增一条已评论的消息在第一条，所以要跳过一条
                        } else {
                            click(likeView, 500);//评论失败了取消点赞
                            log.i("[Retry]本次自动回复提交失败，重试");
                            i--;
                        }
                        hideInputBoard();
                        if (isLoopList) {//循环列表才刷新childList，不需要循环就直接退出了
                            //界面可能刷新了，重新获取对象
                            scrollView = mDevice.findObject(recycleViewBy);
                            childList = scrollView.getChildren();
                        }
                    }
                }
            }
        }

        return count != childList.size();
    }

    private void hideInputBoard() {
        boolean isShowInputBoard = getInputEditView() != null && !hasObject(recycleViewBy);
        if (isShowInputBoard) {
            mDevice.pressBack();//收起输入法
            sleep(500);
        }
    }

    private boolean assertCommit(String comment, List<String> replyList) {
        commentFailedCount = 0;//有提交操作，就将连续无评论数初始化
        boolean isCommit = false;
        Random random = new Random();
        int index = random.nextInt(replyList.size());
        List<String> unuseList = replyList;
        String reply = replyList.get(index);
        unuseList.remove(index);
        while (!isCommit) {
            setText(getInputEditView(), reply);
            sleep(random.nextInt(4)*1000);//输入后等待0-3s
            UiObject2 commitView = getCommitView();
            if (commitView != null) {
                click(commitView, 1000);

                String content = getInputEditView().getText();
                isCommit = content != null && (content.contains(defaultInputText) || content.startsWith("回复"));

                if (isCommit) {
                    commentBlockedCount = 0;//有提交成功，就将连续的评论受阻数初始化
                    log.i(String.format("comment[%s], reply[%s]", comment, reply));
                    break;
                }
            }

            if (unuseList.size() > 0) {
                index = random.nextInt(unuseList.size());
                reply = unuseList.get(index);
                unuseList.remove(index);
            } else {
                log.i("[Skip]评论[" + comment + "]对应的预留消息" + replyList + "都尝试了，仍未提交成功，放弃");
                commentBlockedCount++;
                return true;
            }
        }

        return isCommit;
    }

    private void clickLike(UiObject2 likeView) {
        if (!hasObject(recycleViewBy)) {
            mDevice.pressBack();//可能有输入法遮挡了，返回一下
        }
        click(likeView, 500);
    }

    private boolean isClickedLike(UiObject2 likeView, UiObject2 likeNumView) {
        if (likeNumView != null) {
            try {
                String likeText = likeNumView.getText();
                if (likeText != null && !"".equals(likeText)) {
                    int likeNum = Integer.parseInt(likeText);
                    if (likeNum == 0) {//为0肯定没点赞过，避免再去验证颜色，颜色验证慢很多
                        return false;
                    }
                }
            } catch (Exception e) {
            }
        }
        if (likeView != null) {
            try {
                int[] expectRgbs = new int[]{254, 44, 85};
                int[] rgbs = ColorUtil.getColor(likeView);
                return ColorUtil.assertColor(rgbs, expectRgbs);
            } catch (Exception e) {
            }
        }
        return false;
    }

}