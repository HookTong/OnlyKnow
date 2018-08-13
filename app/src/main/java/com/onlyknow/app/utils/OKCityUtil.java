package com.onlyknow.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.os.Message;

import com.onlyknow.app.R;
import com.onlyknow.app.service.OKCoordinator;
import com.onlyknow.app.service.OKMainService;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * 获取城市ID;
 * <p>
 * Created by Reset on 2018/05/24.
 */

public class OKCityUtil {
    private final static String TAG = "OKCityUtil";

    public static synchronized void requestCityID(final Context context, final String cityName,
                                                  final OKCoordinator coordinator) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                SharedPreferences cityPreferences = context.getSharedPreferences("city", Context.MODE_PRIVATE);
                String cityId = "";
                if (cityPreferences.getBoolean("CITY_INIT", false)) {
                    cityId = cityPreferences.getString(cityName, "");

                    OKLogUtil.print(TAG, "查找CityId结果: " + cityId);

                    Message ms = new Message();
                    ms.what = OKMainService.WHAT_CITY_ID_GET;
                    ms.obj = cityId;
                    coordinator.sendCoordinatorMessage(ms);
                } else {
                    cityId = initCityId(context, cityPreferences, cityName);

                    OKLogUtil.print(TAG, "查找CityId结果: " + cityId);

                    Message ms = new Message();
                    ms.what = OKMainService.WHAT_CITY_ID_GET;
                    ms.obj = cityId;
                    coordinator.sendCoordinatorMessage(ms);
                }
            }
        }.start();
    }

    private static synchronized String initCityId(Context context, SharedPreferences preferences, String cityName) {
        String cityId = "";
        XmlResourceParser xmlParser = context.getResources().getXml(R.xml.ok_citys);
        try {
            // 先获取当前解析器光标在哪
            int event = xmlParser.getEventType();
            //如果还没到文档的结束标志，那么就继续往下处理
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        OKLogUtil.print(TAG, "xml解析开始");
                        break;
                    case XmlPullParser.START_TAG:
                        String tagName = xmlParser.getName();
                        if (tagName.equals("d")) {
                            String xmlCityID = xmlParser.getAttributeValue(0);
                            String xmlCityName = xmlParser.nextText();
                            preferences.edit().putString(xmlCityName, xmlCityID).commit();
                            if (xmlCityName.equals(cityName)) {
                                cityId = xmlCityID;
                            }
                            OKLogUtil.print("CITY_D== " + xmlCityID + "--" + xmlCityName);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                event = xmlParser.next();   //将当前解析器光标往下一步移
            }
            preferences.edit().putBoolean("CITY_INIT", true).commit();
            return cityId;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static synchronized String getCityID(Context context, String cityName) {
        SharedPreferences cityPreferences = context.getSharedPreferences("city", Context.MODE_PRIVATE);
        String cityId = "";
        if (cityPreferences.getBoolean("CITY_INIT", false)) {
            cityId = cityPreferences.getString(cityName, "");

            OKLogUtil.print(TAG, "查找CityId结果: " + cityId);

            return cityId;
        } else {
            cityId = initCityId(context, cityPreferences, cityName);

            OKLogUtil.print(TAG, "查找CityId结果: " + cityId);

            return cityId;
        }
    }
}
