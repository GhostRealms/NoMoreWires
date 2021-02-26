package me.jraynor.common.util;

/**
 * Stores utilities that are used on both the server and client
 */
public final class StringUtils {
    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


}
