package com.example.textapp.notepad.utils;

import java.text.SimpleDateFormat;

/**
 * 日期工具类
 */
public class DateUtil {
    /**
     * 格式化时间
     *
     * @param time 时间戳
     * @return
     */
    public static String formate(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        return sdf.format(time);
    }
}
