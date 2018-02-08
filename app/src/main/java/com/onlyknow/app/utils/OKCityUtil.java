package com.onlyknow.app.utils;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.onlyknow.app.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by Administrator on 2017/12/7.
 */

public class OKCityUtil {
    private final String TAG = "OKCityUtil";
    private Context context;

    public OKCityUtil(Context con) {
        this.context = con;
    }

    public String getCityID(String cityName) {
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
                        //一般都是获取标签的属性值，所以在这里数据你需要的数据
                        String tagName = xmlParser.getName();
                        if (tagName.equals("d")) {

                            String xmlCityID = xmlParser.getAttributeValue(0);
                            String xmlCityName = xmlParser.nextText();

                            if (xmlCityName.equals(cityName)) {
                                OKLogUtil.print(cityName + "的CITY_D为: " + xmlCityID);
                                return xmlCityID;
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
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
