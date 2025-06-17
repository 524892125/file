package com.gameApi.common;

public class CommonUtil {
    public static String unicodeToChinese(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < str.length()) {
            if (str.charAt(i) == '\\' && i + 1 < str.length() && str.charAt(i + 1) == 'u') {
                String unicode = str.substring(i + 2, i + 6);
                sb.append((char) Integer.parseInt(unicode, 16));
                i += 6; // Skip over the wre part
            } else {
                sb.append(str.charAt(i));
                i++;
            }
        }
        return sb.toString();
    }
}
