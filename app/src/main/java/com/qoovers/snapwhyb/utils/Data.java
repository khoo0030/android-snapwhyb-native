package com.qoovers.snapwhyb.utils;

public class Data
{
    private static final String NULL_CONSTANT = "null";

    public static boolean isEquals(String string, String comparison) {
        return string.equals(comparison);
    }

    public static boolean isNull(String string) {
        if(string == null) {
            return true;
        } else if(string.isEmpty()) {
            return true;
        } else if(string.equalsIgnoreCase(NULL_CONSTANT)) {
            return true;
        }

        return false;
    }
}
