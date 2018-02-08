package com.onlyknow.app.utils;

import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/12/7.
 */

public class OKLogUtil {
    private static final String TAG = "OKLogUtil";

    public static void print(String mes) {
        if (TextUtils.isEmpty(mes)) {
            return;
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy--MM--dd HH:mm:ss");// 设置日期格式
        String nowDate = df.format(new Date());
        Log.i(TAG + "--" + nowDate, mes);
    }

    public static void print(String tag, String mes) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(mes)) {
            return;
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyy--MM--dd HH:mm:ss");// 设置日期格式
        String nowDate = df.format(new Date());
        Log.i(tag + "--" + nowDate, mes);
    }

}
