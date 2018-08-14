package com.alany.u2.base;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.Until;
import android.util.Log;

import com.alany.u2.config.Config;
import com.alany.u2.rule.RepeatRule;
import com.alany.u2.service.impl.KeywordReplyServiceImpl;
import com.alany.u2.utils.LogUtil;
import com.alany.u2.utils.TestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;

import java.io.IOException;

/**
 * Created by alany on 2018/7/25.
 */
public abstract class BaseCase extends BaseAction {
    public static Context mContext;
    public static Context mAppContext;
    public static String testTag = "u2Test";

    @Rule
    public RepeatRule rule = new RepeatRule();

    @BeforeClass
    public static void initClass() {
        log = new LogUtil(testTag, Config.LOG_PATH);
        keywordReplyService = new KeywordReplyServiceImpl();
        keywordReplyService.importData();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mContext = InstrumentationRegistry.getContext();
        mAppContext = InstrumentationRegistry.getTargetContext();

        //激活屏幕
        try {
            if (!mDevice.isScreenOn()) {
                mDevice.wakeUp();
                mDevice.wait(1000);
                //向上滑动解锁
                mDevice.swipe(mDevice.getDisplayWidth() / 2, mDevice.getDisplayHeight(), mDevice.getDisplayWidth() / 2, mDevice.getDisplayHeight() / 2, 50);
                mDevice.wait(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //返回到桌面
        mDevice.pressHome();
    }

    @Before
    public void beforeMethod(){
        String packageName = setPackageName();
        openApp(packageName);
    }

    @After
    public void resetMethod() {
        //退出app
        //exitApp(setPackageName());
    }

    public static void openApp(String packageName) {
        //检测demo安装
        if (!TestUtil.isAppInstalled(mContext, packageName)) {
            Log.e(testTag, "[Error]应用未安装");
            return;
        }

        //启动app
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
        mDevice.wait(Until.hasObject(By.pkg(packageName).depth(0)), TIMEOUT);
    }

    public static boolean exitApp(String packageName) {
        try {
            mDevice.executeShellCommand("am force-stop " + packageName);
            log.i("退出应用(" + packageName + ")成功");
            return true;
        } catch (IOException e) {
            log.e("[Error]退出应用(" + packageName + ")失败");
            e.printStackTrace();
            return false;
        }
    }

    public static String getClipboardContent(){
        Handler handler = new Handler(Looper.getMainLooper());
        final StringBuffer data = new StringBuffer();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ClipboardManager myClipboard = (ClipboardManager)mAppContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = myClipboard.getPrimaryClip();
                if (clipData != null && clipData.getItemCount() > 0) {
                    // 从数据集中获取（粘贴）第一条文本数据
                    CharSequence text = clipData.getItemAt(0).getText();
                    data.append(text.toString());
                }
            }
        }, 500);
        sleep(800);
        return data.toString();
    }

    public abstract String setPackageName();
}