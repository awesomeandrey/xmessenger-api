package com.xmessenger.model.util;

public class Utility {
    public static boolean isBlank(String p) {
        return p == null || p.isEmpty();
    }

    public static boolean isBlank(Integer p) {
        return p == null || p == 0;
    }

    public static boolean isNotBlank(String p) {
        return !isBlank(p);
    }

    public static boolean isNotBlank(Integer p) {
        return !isBlank(p);
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }
}