package org.jtop.utils;

public class FormatUtils {

    public static String bytesToString(long bytes){
        long GB = 1024L * 1024 * 1024;
        long MB = 1024L * 1024;
        if (bytes >= GB) {
            return String.format("%.1fG", (double) bytes / GB);
        } else if (bytes >= MB) {
            return String.format("%.1fM", (double) bytes / MB);
        } else {
            return String.format("%dK", bytes / 1024);
        }
    }

    public static String cpuTimeToString(long millis) {
        long minutes = (millis / 1000) / 60;
        long seconds = (millis / 1000) % 60;
        long hundredths = (millis % 1000) / 10;

        return String.format("%02d:%02d.%02d", minutes, seconds, hundredths);
    }
}
