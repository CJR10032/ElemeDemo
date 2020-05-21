package com.konomi.elemedemo.utils;

import android.content.Context;
import android.util.Log;

import com.konomi.elemedemo.BuildConfig;
import com.konomi.elemedemo.base.BaseActivity;


/**
 * 创建者     CJR
 * 创建时间   2017-09-19 16:35
 * 描述	      日志控制类,如果对安全性要求较高的话要把日志全删掉,
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述
 *
 * @author CJR
 */
public class LogUtils {

    private static boolean flag;

    //====================== add CJR ====================

    public static void myLog(String msg) {
        myLog("vivi", msg);
    }

    public static void myLog(String tag, String msg) {
        if (BuildConfig.DEBUG && flag) {
            Log.e(tag, msg);
        }
    }

    public static void netLog(String msg) {
        if (BuildConfig.DEBUG && flag) {
            msg = msg.trim();
            int index = 0;
            int maxLength = 3800;
            String sub;
            while (index < msg.length()) {
                // java的字符不允许指定超过总的长度end
                if (msg.length() <= index + maxLength) {
                    sub = msg.substring(index);
                } else {
                    sub = msg.substring(index, maxLength + index);
                }

                index += maxLength;
                Log.e("vovo", sub.trim());
            }
        }
    }

    public static void toast(Context context, String msg) {
        if (context instanceof BaseActivity) {
            ((BaseActivity) context).doToast(msg);
        }
    }

}
