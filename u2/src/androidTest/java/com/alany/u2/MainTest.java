package com.alany.u2;

import android.provider.Settings;
import android.support.test.runner.AndroidJUnit4;

import com.alany.u2.base.BaseCase;
import com.alany.u2.config.Config;
import com.alany.u2.config.ConfigParser;
import com.alany.u2.douyin.ui.CommentPage;
import com.alany.u2.rule.Repeat;
import com.alany.u2.rule.RepeatRule;
import com.alany.u2.utils.HttpsUtil;
import com.alany.u2.utils.StringUtil;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainTest extends BaseCase {
    private static final String AUTH_CHECK_URL = "https://www.englishstudy.funwall.cn/branch/server/public/autotestLogin?username=%s&password=%s&android_id=%s";
    @Override
    public void beforeMethod() {
        // TODO NOTHING
    }

    @Override
    public void resetMethod() {
        // TODO NOTHING
    }

    @Test
    @Repeat(times = Integer.MAX_VALUE)
    public void run() {
        String androidId = "unknown";
        try {
            androidId = Settings.System.getString(mContext.getContentResolver(), Settings.System.ANDROID_ID);
            log.i("android_id:" + androidId);
        } catch (Exception e) {
            log.i("无权限获取系统android_id");
        }

        String username = ConfigParser.getValue("username");
        String password = ConfigParser.getValue("password");
        if (StringUtil.isEmpty(username) || StringUtil.isEmpty(password)) {
            log.e("[Error]未获取到用户授权信息：用户名或密码为空，无法执行测试");
            RepeatRule.stop();
            return;
        }
        String url = String.format(AUTH_CHECK_URL, username, password, androidId);
        String code = HttpsUtil.doHttps(url);
        if (StringUtil.isNotEmpty(code)) {
            log.i("测试执行策略CODE=" + code);
            try {
                int type = Integer.parseInt(code);
                String hybirdTypeStr = ConfigParser.getValue("type");
                if (type == 3 && StringUtil.isNotEmpty(hybirdTypeStr)) {
                    int hybirdType = Integer.parseInt(hybirdTypeStr);
                    type = hybirdType;
                    log.i("当前用户采用的混合模式CODE=" + type);
                }
                switch (type) {
                    case 1 : //只跑抖音
                        openApp(Config.DOUYIN_APP_PACKAGE);
                        com.alany.u2.douyin.MainTest.runMode(CommentPage.RUNMODE_HYBIRD, Config.DOUYIN_IS_LOOP_COMMENT_LIST);
                        break;
                    case 2 : //只跑快手
                        openApp(Config.KUAISHOU_APP_PACKAGE);
                        com.alany.u2.kuaishou.MainTest.runMode(Config.KUAISHOU_MAX_VIDEO_NUM);
                        break;
                    case 3 : //混合跑
                        openApp(Config.DOUYIN_APP_PACKAGE);
                        com.alany.u2.douyin.MainTest.runMode(CommentPage.RUNMODE_LOOP_BREAK, Config.DOUYIN_IS_LOOP_COMMENT_LIST);
                        exitApp(Config.DOUYIN_APP_PACKAGE);

                        sleep(1000*5); //停5s

                        openApp(Config.KUAISHOU_APP_PACKAGE);
                        com.alany.u2.kuaishou.MainTest.runMode(Config.KUAISHOU_MAX_VIDEO_NUM);
                        exitApp(Config.KUAISHOU_APP_PACKAGE);
                        break;
                    default:
                        log.i("测试执行不被允许，退出执行");
                        RepeatRule.stop();
                        break;
                }

            } catch (IllegalStateException e) {
                System.err.println(e.getCause().getMessage());
                if (e.getCause().getMessage().contains("UiAutomation not connected")) {
                    RepeatRule.stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            log.e("授权配置接口返回为空");
        }

    }

    @Override
    public String setPackageName() {
        return null;
    }

}