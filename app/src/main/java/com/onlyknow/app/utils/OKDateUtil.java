package com.onlyknow.app.utils;

import com.onlyknow.app.OKConstant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OKDateUtil {
    public static String formatTime(Date date) {
        if (date == null) return "no create date";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
        String s = dateFormat.format(date);

        String time[] = s.split("/");
        if (time.length != 5) {
            return s;
        }
        String nowDate = OKConstant.getNowDateByString();
        String nowTime[] = nowDate.split("/");
        if ((time[0].equals(nowTime[0])) && (time[1].equals(nowTime[1])) && (time[2].equals(nowTime[2]))) {
            return "今天 " + time[3] + ":" + time[4];
        } else if ((time[0].equals(nowTime[0])) && (time[1].equals(nowTime[1])) && (Integer.parseInt(nowTime[2]) - Integer.parseInt(time[2]) == 1)) {
            return "昨天 " + time[3] + ":" + time[4];
        } else if (time[0].equals(nowTime[0])) {
            return time[1] + "月" + time[2] + "日" + " " + time[3] + ":" + time[4];
        } else {
            return time[0] + "年" + time[1] + "月" + time[2] + "日" + " " + time[3] + ":" + time[4];
        }
    }

    public static String formatTime(String s) {

        String time[] = s.split("/");
        if (time.length != 5) {
            return s;
        }
        String nowDate = OKConstant.getNowDateByString();
        String nowTime[] = nowDate.split("/");
        if ((time[0].equals(nowTime[0])) && (time[1].equals(nowTime[1])) && (time[2].equals(nowTime[2]))) {
            return "今天 " + time[3] + ":" + time[4];
        } else if ((time[0].equals(nowTime[0])) && (time[1].equals(nowTime[1])) && (Integer.parseInt(nowTime[2]) - Integer.parseInt(time[2]) == 1)) {
            return "昨天 " + time[3] + ":" + time[4];
        } else if (time[0].equals(nowTime[0])) {
            return time[1] + "月" + time[2] + "日" + " " + time[3] + ":" + time[4];
        } else {
            return time[0] + "年" + time[1] + "月" + time[2] + "日" + " " + time[3] + ":" + time[4];
        }

    }

    public static Date dateByString(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = null;
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String stringByDate(Date date) {
        if (date == null) return null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(date);
    }
}
