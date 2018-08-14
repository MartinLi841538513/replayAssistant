package com.alany.u2.base;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.Direction;
import android.support.test.uiautomator.StaleObjectException;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;

import com.alany.u2.service.KeywordReplyService;
import com.alany.u2.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseAction {

    public static UiDevice mDevice;
    public static LogUtil log;
    public static final int TIMEOUT = 5000;

    public static KeywordReplyService keywordReplyService;

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void click(UiObject2 obj) {
        if (obj != null) {
            log.i("点击对象：" + getText(obj));
            obj.click();
        } else {
            log.e("未找到点击对象");
        }
    }

    public static void click(UiObject2 obj, long ms) {
        click(obj);
        sleep(ms);
    }

    public void click(int x, int y) {
        log.i("点击屏幕坐标（" + x + "," + y + "）");
        mDevice.click(x, y);
        sleep(500);
    }

    public static String getText(UiObject2 obj) {
        String text = "";
        if (obj != null) {
            try {
                text = obj.getText();
                text = text == null || text.isEmpty() ? obj.getContentDescription() : text;
                text = text == null || text.isEmpty() ? obj.getClassName() : text;
            } catch (StaleObjectException e) {
                text = "View";
            }

        }
        return text;
    }

    public static void setText(UiObject2 obj, Object text) {
        if (obj != null) {
            log.i("输入内容：" + text);
            obj.setText(text + "");
        } else {
            log.e("未找到文本输入对象");
        }
    }

    public void swipeDown(UiObject2 obj) {
        if (obj != null) {
            log.i("向下滑动到ScrollView的顶部");
            obj.swipe(Direction.DOWN, 1.0f);
        }
    }

    public void swipeDown(UiObject2 obj, float value) {
        if (obj != null) {
            log.i("向下滑动" + value + "的区间比例");
            obj.swipe(Direction.DOWN, value, 2000);
        }
    }

    public void swipeUp(UiObject2 obj) {
        if (obj != null) {
            log.i("向下滑动到ScrollView的顶部");
            obj.swipe(Direction.UP, 1.0f);
        }
    }

    public void swipeUp(UiObject2 obj, float value) {
        if (obj != null) {
            log.i("向上滑动" + value + "的区间比例");
            obj.swipe(Direction.UP, value, 2000);
        }
    }

    public void scrollUp(UiObject2 obj, float value) {
        if (obj != null) {
            log.i("向上滚动" + value + "的区间比例");
            obj.scroll(Direction.UP, value, 2000);
        }
    }

    public static UiObject2 waitElement(BySelector by, int ms){
        return mDevice.wait(Until.findObject(by),ms);
    }

    public static boolean waitElement(BySelector by) {
        int counter = 1;
        try {
            while (!mDevice.hasObject(by)) {
                if (counter > 10) {
                    log.i("未找到对象：" + by.toString());
                    return false;
                }
                sleep(1000);
                counter++;
            }
            return mDevice.hasObject(by);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isVisible(Object object) {
        if (object == null) {
            return false;
        }
        UiObject2 uiObject = null;
        if (object instanceof BySelector) {
            if (hasObject((BySelector)object)) {
                uiObject = mDevice.findObject((BySelector)object);
            } else {
                return false;
            }
        } else if (object instanceof UiObject2) {
            uiObject = (UiObject2) object;
        }
        if (uiObject == null) {
            return false;
        }
        return uiObject.getVisibleCenter().x > 0 && uiObject.getVisibleCenter().y > 0;
    }

    public static boolean hasObject(BySelector by) {
        try {
            mDevice.hasObject(by);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean hasObject(UiObject2 parent, BySelector by) {
        try {
            parent.hasObject(by);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean hasObjects(BySelector by) {
        try {
            mDevice.findObjects(by);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean hasObjects(UiObject2 parent, BySelector by) {
        try {
            parent.findObjects(by);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static UiObject2 findObjectByXpath(UiObject2 root, String xpath) {
        if (xpath == null && "".equals(xpath)) {
            log.e("[Error]xpath expression[" + xpath + "] is invalid");
            return null;
        }
        String[] xpaths = null;
        if (xpath.contains("/")) {
            xpaths = xpath.split("/");
        } else {
            xpaths = new String[]{xpath};
        }
        UiObject2 preNode = root;
        for (String path : xpaths) {
            preNode = getChild(preNode, path);
            if (preNode == null) {
                //log.e(String.format("按xpath[%s]查找元素失败, 未找到class[%s]对应的节点", xpath, path));
                break;
            }
        }

        return preNode;
    }
    public static UiObject2 getChild(Object root, String clazz) {
        Map<String,String> params = new HashMap<String,String>();
        params.put("class", clazz);
        return getChild(root, params);
    }

    public static UiObject2 getChild(Object root, Map<String,String> params) {
        if (params == null || !params.containsKey("class")) {
            log.e("[Error]参数错误: 为空或未包含[class]key");
            return null;
        }
        String clazz = params.get("class");
        String className = clazz;
        int index = 0;
        if (clazz.endsWith("]") && clazz.contains("[")) { //有下标
            className = clazz.substring(0, clazz.lastIndexOf("["));
            String num = clazz.substring(clazz.lastIndexOf("[") + 1, clazz.lastIndexOf("]"));
            index = num != null && !"".equals(num) ? Integer.parseInt(num) : index;
        }
        List<UiObject2> childList = null;
        if (root instanceof UiObject2) {
            childList = ((UiObject2) root).getChildren();
        } else {
            childList = hasObjects(By.clazz(className)) ? mDevice.findObjects(By.clazz(className)) : null;
        }
        List<UiObject2> tempList = new ArrayList<UiObject2>();
        if (childList != null && !childList.isEmpty()) {
            for (UiObject2 child : childList) {
                boolean isMatch = child.getClassName().equals(className);
                if (params.containsKey("pkg")) {
                    isMatch = isMatch && child.getApplicationPackage().equals(params.get("pkg"));
                }

                if (params.containsKey("text")) {
                    isMatch = isMatch && child.getText().equals(params.get("text"));
                }

                if (params.containsKey("desc")) {
                    isMatch = isMatch && child.getContentDescription().equals(params.get("desc"));
                }

                if (isMatch) {
                    tempList.add(child);
                }
            }
        }

        if(tempList.isEmpty()) {
            return null;
        }

        if (index >= tempList.size()) {
            log.e(String.format("[Error]查找class[%s] 下标[%d]越界[%d]", clazz, index, tempList.size()));
            return null;
        }

        return tempList.get(index);
    }
}