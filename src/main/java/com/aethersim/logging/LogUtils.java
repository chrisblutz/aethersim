package com.aethersim.logging;

public class LogUtils {

    private static String[] units = new String[] {"B", "KB", "MB", "GB", "TB"};

    public static String formatBytes(long bytes) {
        int magnitude = 0;
        while (bytes > 1000 && magnitude < units.length) {
            bytes /= 1000;
            magnitude++;
        }

        return String.format("%,d%s", bytes, units[magnitude]);
    }
}
