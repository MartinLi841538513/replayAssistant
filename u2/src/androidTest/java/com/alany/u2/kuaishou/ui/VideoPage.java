package com.alany.u2.kuaishou.ui;

import android.graphics.Rect;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiObject2;

import com.alany.u2.base.BaseAction;
import com.alany.u2.base.BaseCase;
import com.alany.u2.config.Config;
import com.alany.u2.utils.ColorUtil;
import com.alany.u2.utils.StringUtil;
import com.alany.u2.utils.TestUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VideoPage extends BaseAction {
    private String accountName;

    public static BySelector commentIconBy = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/comment_icon");
    private static BySelector noCommentTipsBy = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/comment_tips_desc");

    private static BySelector recycleViewBy = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/recycler_view");

    private static BySelector commentLayoutBy = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/comment_frame");
    private static BySelector commentAccountNameBy = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/name");
    private static BySelector commentContentBy = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/comment");

    private static BySelector commentLikeBy = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/comment_like");
    private static BySelector commentLikeCountBy = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/comment_like_count");

    private BySelector alertListBy = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/alert_dialog_list");

    public VideoPage(String accountName) {
        this.accountName = accountName;
    }

    public UiObject2 getCommentIcon() {
        return hasObject(commentIconBy) ? mDevice.findObject(commentIconBy) : null;
    }

    public List<UiObject2> getCommentList() {
        if (getRecycleView() != null) {
            List<UiObject2> children = getRecycleView().getChildren();
            List<UiObject2> list = new ArrayList<UiObject2>();
            if (children != null) {
                String pkg = Config.KUAISHOU_APP_PACKAGE + ":id/comment_frame";
                for (UiObject2 child : children) {
                    if (pkg.equals(child.getResourceName()) && isVisible(child)) {
                        list.add(child);
                    }
                }
            }
            return list;
        }
        return null;
    }

    public UiObject2 getCommentInput() {
        BySelector by = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/comment_editor_holder_text");
        return hasObject(by) ? mDevice.findObject(by) : null;
    }

    public UiObject2 getCommentEndLable() {
        BySelector by = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/no_more_content");
        return hasObject(by) ? mDevice.findObject(by) : null;
    }

    public UiObject2 getAlertItem(String text) {
        BySelector by = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/qlist_alert_dialog_item_text").text(text);
        return hasObject(by) ? mDevice.findObject(by) : null;
    }

    public UiObject2 getRecycleView() {
        return hasObject(recycleViewBy) ? mDevice.findObject(recycleViewBy) : null;
    }

    public UiObject2 getOpLayout() {
        BySelector opLayoutBy = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/operation_layout");
        return hasObject(opLayoutBy) ? mDevice.findObject(opLayoutBy) : null;
    }

    public UiObject2 getInputEditor() {
        UiObject2 opLayout = getOpLayout();
        if (opLayout != null) {
            BySelector editorBy = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/editor");
            return hasObject(opLayout, editorBy) ? opLayout.findObject(editorBy) : null;
        }
        return null;
    }

    public UiObject2 getCommitButton() {
        UiObject2 opLayout = getOpLayout();
        if (opLayout != null) {
            BySelector buttonBy = By.res(Config.KUAISHOU_APP_PACKAGE + ":id/finish_button_slide");
            return hasObject(opLayout, buttonBy) ? opLayout.findObject(buttonBy) : null;
        }
        return null;
    }

    public void doReply() {
        click(getCommentIcon());
        boolean isOver = false;
        while (true) {
            loopList();
            if (isOver) {
                log.i("[End]本视频所有评论已遍历完");
                break;
            }
            hideInputBoard();

            UiObject2 recycleView = getRecycleView();
            UiObject2 inputView = getCommentInput();
            if (isVisible(recycleView) && isVisible(inputView)) {
                Rect rect = recycleView.getVisibleBounds();
                Rect inputRect = inputView.getVisibleBounds();

                int offset = inputRect.height() / 3;
                int startX = TestUtil.getScreenWidth() / 2;
                int startY = inputRect.top - offset;
                int endX = TestUtil.getScreenWidth() / 2;
                int endY = rect.top + offset;
                mDevice.swipe(startX, startY, endX, endY, 50);
            } else {
                log.e("[Error]recycleView和评论输入框为null或不可见");
                isOver = true;
            }

            if (isVisible(getCommentEndLable()) || getChild(getRecycleView(), "android.widget.LinearLayout") != null) {
                isOver = true;
            }
        }
    }

    public boolean loopList() {
        int count = 0; //跳过评论或评论失败总数
        List<UiObject2> commentList = getCommentList();
        if (commentList != null && !commentList.isEmpty()) {
            for (int i = 0; i < commentList.size(); i++) {
                UiObject2 nameView = commentList.get(i).findObject(commentAccountNameBy);
                UiObject2 commentView = commentList.get(i).findObject(commentContentBy);
                UiObject2 likeView = commentList.get(i).findObject(commentLikeBy);
                UiObject2 likeNumView = commentList.get(i).findObject(commentLikeCountBy);
                //元素不可见或部分被遮住跳出本次操作
                if (!isVisible(nameView) || !isVisible(commentView) || !isVisible(likeView) || !isVisible(likeNumView)) {
                    continue;
                }
                String userName = TestUtil.getTextByOCR(nameView);
                String comment = TestUtil.getTextByOCR(commentView);
                boolean isContainsAt = StringUtil.isNotEmpty(comment) && (comment.contains("@") || comment.length() > 20);
                float similartity = StringUtil.getSimilarity(accountName, userName);
                //异常或者当前是自己的评论或者记录已经点过赞了，跳过
                if (userName == null || isContainsAt || similartity > 0.7 || isClickedLike(likeView, likeNumView)) {
                    log.i("[Skip]当前评论是自己的或包含@或太长超过20个字符或已经被点赞过，跳过");
                    count++;
                    continue;
                }

                if (StringUtil.isEmpty(comment)) { //OCR未识别的话，再通过复制来获取评论消息
                    boolean opSuccess = operateItem(commentList.get(i), "复制");
                    if (opSuccess) {
                        comment = BaseCase.getClipboardContent();
                    } else {
                        count++;
                        continue;
                    }
                }

                if (StringUtil.isNotEmpty(comment)) {
                    List<String> replyList = keywordReplyService.getReplyList(comment);
                    if (replyList == null || replyList.size() < 1) {
                        log.i("[Skip]评论[" + comment.substring(0, comment.length() > 10 ? 10 : comment.length()) + "]未匹配到预留关键字，不自动评论");
                        count++;
                        continue;
                    }
                    clickLike(likeView);//评论前先点赞
                    operateItem(commentList.get(i), "回复");

                    boolean isCommit = assertCommit(comment, replyList);
                    if (isCommit) {
                        i++;//成功会新增一条已评论的消息在第一条，所以要跳过一条
                    } else {
                        click(likeView, 500);//评论失败了取消点赞
                        log.i("[Retry]本次自动回复提交失败，重试");
                        i--;
                    }

                    hideInputBoard();
                    //界面可能刷新了，重新获取对象
                    commentList = getCommentList();
                } else {
                    count++;
                }
            }
            return count != commentList.size();
        } else {
            log.e("[Error]该视频评论列表为空");
            return false;
        }
    }

    private void hideInputBoard() {
        boolean isShowInputBoard = isVisible(alertListBy) || getInputEditor() != null || !isVisible(recycleViewBy);
        if (isShowInputBoard) {
            mDevice.pressBack();//收起输入法
            sleep(500);
        }
    }

    private boolean operateItem(UiObject2 item, String text) {
        boolean isSuccess = true;
        UiObject2 commentView = item.findObject(commentContentBy);
        click(commentView, 500);

        if (waitElement(alertListBy)) {
            click(getAlertItem(text), 500);
        } else {
            if (isVisible(commentLayoutBy)) {
                click(item, 500);
                click(getAlertItem(text), 500);
            } else {
                log.e("[Exception]不知道当前是在哪个页面...");
                isSuccess = false;
            }
        }

        if (!"回复".equals(text)) {
            hideInputBoard();//最后一定要隐藏这对话框，不然影响其他操作
        }
        return isSuccess;
    }

    private boolean assertCommit(String comment, List<String> replyList) {
        boolean isCommit = false;
        if (getInputEditor() == null) {
            log.e("[Error]输入法未弹出，无法输入");
            return false;
        }

        Random random = new Random();
        int index = random.nextInt(replyList.size());
        List<String> unuseList = replyList;
        String reply = replyList.get(index);
        unuseList.remove(index);
        while (!isCommit) {
            setText(getInputEditor(), reply);
            UiObject2 commitView = getCommitButton();
            if (commitView != null) {
                click(commitView, 1000);

                isCommit = getInputEditor() == null; //提交成功了，会隐藏输入法
                if (isCommit) {
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
                return true;
            }
        }

        return isCommit;
    }

    private void clickLike(UiObject2 likeView) {
        if (getOpLayout() != null) {
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
                int[] expectRgbs = new int[]{251, 88, 88};
                int[] rgbs = ColorUtil.getColor(likeView);
                return ColorUtil.assertColor(rgbs, expectRgbs);
            } catch (Exception e) {
            }
        }
        return false;
    }
}