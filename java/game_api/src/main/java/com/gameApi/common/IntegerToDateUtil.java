package com.gameApi.common;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class IntegerToDateUtil {
    public static String getDate(long timestamp) {
        Date date = new Date(timestamp * 1000);
        // 格式化 Date 为字符串
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static String getDate(Integer timestamp) {
        Date date = new Date(timestamp.longValue() * 1000);
        // 格式化 Date 为字符串
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static Integer getTimestamp(String date) {
        return (int) (LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli() / 1000
        );
    }

    public static String getNowDateStr() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String getLocalDateTimeStr (LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static int getNowSecond () {
        return (int) localDateTimeToSecond(LocalDateTime.now());
    }

    public static int localDateTimeToSecond (LocalDateTime localDateTime) {
        return (int) (localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000);
    }
}
