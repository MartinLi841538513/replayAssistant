package com.alany.u2.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiObject2;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.alany.u2.base.BaseCase;
import com.alany.u2.config.Config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by alany on 2018/7/25.
 */
public class TestUtil {

    public static void sleep( long ms){
        try{
            Thread.sleep(ms);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static boolean isAppInstalled(Context context, String packagename){
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        }catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        boolean isInstalled = packageInfo != null ;
        Log.i(BaseCase.testTag,"待测应用"+ (isInstalled ? "已":"未") +"安装");
        return isInstalled;
    }

    public static boolean isAppRunning(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : list) {
            String processName = appProcess.processName;
            if (processName != null && processName.equals(packageName)) {
                Log.i(BaseCase.testTag, packageName + "-" + packageName);
                return true;
            }
        }
        return false;
    }

    public static boolean install(Context context, String apkPath) {
        boolean isInstalled = true;
        String command     = "chmod " + 777 + " " + apkPath;
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + apkPath),"application/vnd.android.package-archive");
        context.startActivity(intent);

        while(BaseCase.waitElement(By.text("下一步"))){
            BaseCase.click(BaseCase.mDevice.findObject(By.text("下一步")), 500);
        }

        BySelector by = By.text("安装");
        if(! BaseCase.waitElement(by)){
            Log.i(BaseCase.testTag,"安装待测应用异常：未找到【安装】按钮");
            return false;
        }
        BaseCase.click(BaseCase.mDevice.findObject(by), 500);

        by = By.text("安装成功");
        if(! BaseCase.waitElement(by)){
            Log.e(BaseCase.testTag,"待测应用安装失败");
            return false;
        }
        BaseCase.click(BaseCase.mDevice.findObject(By.text("完成")), 500);
        return isInstalled;
    }

    public static int getScreenWidth(){
        WindowManager wm = (WindowManager) BaseCase.mAppContext.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    public static int getScreenHeight(){
        WindowManager wm = (WindowManager) BaseCase.mAppContext.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

    public static String getCurrentMethodName() {
        int level = 1;
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        String methodName = stacks[level].getMethodName();
        return methodName;
    }

    public static int getMethodCount(Class clazz, String tag) {
        int count = 0;
        for (Method method : clazz.getDeclaredMethods()) {
            if(method != null && method.getName().startsWith(tag)){
                count ++;
            }
        }
        return count;
    }

    public static String getCurrentClassName() {
        int level = 1;
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        String className = stacks[level].getClassName();
        return className;
    }

    public static boolean isActivedKeyborad(){
        InputMethodManager imm = (InputMethodManager) BaseCase.mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive();
    }

    public static boolean takeScreenshot(String path) {
        try {
            File file = new File(path);
            return BaseCase.mDevice.takeScreenshot(file);
        } catch (Exception e) {
            Log.w(BaseCase.testTag,"截屏失败：" + path);
            e.printStackTrace();
            return false;
        }
    }

    public static String takeElementshot(UiObject2 object, String...paths) {
        if (paths == null || paths.length < 1) {
            throw new IllegalArgumentException("paths参数不能为空");
        }
        if (object == null || !takeScreenshot(paths[0])) {
            return null;
        }

        BitmapFactory.Options bfOptions = new BitmapFactory.Options();
        bfOptions.inDither = false;
        bfOptions.inTempStorage = new byte[12 * 1024];
        bfOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(paths[0]);
        Rect rect = object.getVisibleBounds();
        bitmap = bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height());//获取区域

        String jpgCutPath = paths.length > 1 ? paths[1] : paths[0];
        File filePic = new File(jpgCutPath);
        try {
            if (!filePic.exists()) {
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(jpgCutPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return filePic.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getTextByOCR(UiObject2 object){
        String jpgPath = Config.LOG_PATH + "ocr.jpg";
        String jpgCutPath = Config.LOG_PATH + "ocr_cut.jpg";
        if (object == null) {
            return null;
        }
        String path = takeElementshot(object, jpgPath, jpgCutPath);
        if (path != null) {
            return BaiduOCRUtil.doOcr(jpgCutPath);
        } else {
            return null;
        }
    }
}
