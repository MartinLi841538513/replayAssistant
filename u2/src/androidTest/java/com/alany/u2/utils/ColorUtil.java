package com.alany.u2.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.test.uiautomator.UiObject2;

import com.alany.u2.base.BaseCase;
import com.alany.u2.config.Config;

public class ColorUtil {

    public static int[] getColor(UiObject2 object) {
        String jpgPath = Config.LOG_PATH + "bg.jpg";
        if (object == null || !TestUtil.takeScreenshot(jpgPath)) {
            return null;
        }
        return getColorRgb(object.getVisibleBounds(), jpgPath);
    }

    /**
     * 图片区域中指定坐标的rgb
     *
     * @param rect
     * @param filePath
     * @return
     */
    public static int[] getColorRgb(Rect rect, String filePath) {
        BitmapFactory.Options bfOptions = new BitmapFactory.Options();
        bfOptions.inDither = false;
        bfOptions.inTempStorage = new byte[12 * 1024];
        bfOptions.inJustDecodeBounds = true;
        Bitmap m = BitmapFactory.decodeFile(filePath);
        m = m.createBitmap(m, rect.left, rect.top, rect.width(), rect.height());//获取区域

        //ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //m.compress(Bitmap.CompressFormat.JPEG, 90, bos);//压缩图片
        int pixelX = rect.width() / 2; //取中心点位置
        int pixelY = rect.height() / 2;
        int color = m.getPixel(pixelX, pixelY);

        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        m.recycle();
        int[] rgb = {r, g, b};
        return rgb;
    }

    public static boolean assertColor(int[] actualRgb, int[] expectedRgb) {
        if (actualRgb == null || expectedRgb == null || actualRgb.length != 3 || expectedRgb.length != 3) {
            BaseCase.log.e("RGB数组为空或长度不为3");
            return false;
        }
        String actual = rgbToString(actualRgb);
        String expected = rgbToString(expectedRgb);

        try {
            org.junit.Assert.assertEquals(actual, expected);
            //BaseCase.log.i("实际的RGB值：" + actual + " ，跟期望RGB值：" + expected + " 相匹配");
            return true;
        } catch (Error e) {
            //BaseCase.log.i("实际的RGB值：" + actual + " ，跟期望RGB值：" + expected + " 不匹配，下面对比HSV的色度值：");
            double[] actualHsv = rgbToHsv(actualRgb[0], actualRgb[1], actualRgb[2]);
            double[] expectedHsv = rgbToHsv(expectedRgb[0], expectedRgb[1], expectedRgb[2]);

            double hRate = Math.abs(actualHsv[0] - expectedHsv[0]) / actualHsv[0];
            double sRate = Math.abs(actualHsv[1] - expectedHsv[1]) / actualHsv[1];

            if (hRate < 0.05) {
                //BaseCase.log.i("实际的HSV色度值：" + actualHsv[0] + " ，跟期望的色度值：" + expectedHsv[0] + " 相匹配");
                return true;
            }
            //BaseCase.log.e("实际的HSV色度值：" + actualHsv[0] + " ，跟期望的色度值：" + expectedHsv[0] + " 不匹配");

            return false;
        }
    }

    private static String rgbToString(int[] rgb) {
        if (rgb == null || rgb.length != 3) {
            return "";
        }
        String r = Integer.toHexString(rgb[0]).toUpperCase();
        String g = Integer.toHexString(rgb[1]).toUpperCase();
        String b = Integer.toHexString(rgb[2]).toUpperCase();

        r = r.length() == 1 ? "0" + r : r;
        g = g.length() == 1 ? "0" + g : g;
        b = b.length() == 1 ? "0" + b : b;

        return r + g + b;
    }

    private static double[] rgbToHsv(int r, int g, int b) {
        double h, s, v;
        double min, max, delta;

        min = Math.min(Math.min(r, g), b);
        max = Math.max(Math.max(r, g), b);

        // V 亮度
        v = max;
        delta = max - min;

        // S 饱和度
        if (max != 0) {
            s = delta / max;
        } else {
            s = 0;
            h = -1;
            return new double[]{h, s, v};
        }

        // H 色度
        if (r == max) {
            h = (g - b) / delta; // between yellow & magenta
        } else if (g == max) {
            h = 2 + (b - r) / delta; // between cyan & yellow
        } else {
            h = 4 + (r - g) / delta; // between magenta & cyan
        }
        h *= 60;    // degrees

        if (h < 0) {
            h += 360;
        }
        return new double[]{h, s, v};
    }
}