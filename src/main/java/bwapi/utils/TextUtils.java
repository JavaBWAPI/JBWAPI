package bwapi.utils;

import bwapi.values.TextColor;

public class TextUtils {
    /**
     * Format text with a textcolor to display on broodwar
     */
    public static String formatText(final String text, final TextColor format){
        byte[] data = text.getBytes();
        int len = text.length();
        byte[] formatted = new byte[len + 1];
        formatted[0] = format.value;
        System.arraycopy(data, 0, formatted, 1, len);
        return new String(formatted);
    }
}