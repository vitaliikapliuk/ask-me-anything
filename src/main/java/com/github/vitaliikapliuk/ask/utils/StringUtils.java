package com.github.vitaliikapliuk.ask.utils;

public class StringUtils {

    public static boolean isNotBlank(CharSequence cs) {
        return !StringUtils.isBlank(cs);
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((!Character.isWhitespace(cs.charAt(i)))) {
                return false;
            }
        }
        return true;
    }

}