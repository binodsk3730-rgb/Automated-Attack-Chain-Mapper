package com.aacm.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility class for common operations
 */
public class Utils {

    /**
     * Ensure a directory exists, creating it if necessary
     */
    public static boolean ensureDirectory(String dirPath) {
        File dir = new File(dirPath);
        if (dir.exists() && dir.isDirectory()) {
            return true;
        }
        
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                System.out.println("[INFO] Created directory: " + dirPath);
            }
            return created;
        }
        
        return false;
    }

    /**
     * Check if a file exists
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    /**
     * Trim whitespace from string
     */
    public static String trim(String str) {
        if (str == null) return "";
        return str.trim();
    }

    /**
     * Check if string is empty or null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Format a number as percentage
     */
    public static String formatPercent(double value) {
        return String.format("%.2f%%", value * 100.0);
    }

    /**
     * Format a decimal number
     */
    public static String formatDecimal(double value, int places) {
        String format = "%." + places + "f";
        return String.format(format, value);
    }

    /**
     * Parse severity level to numeric score (for weighting)
     */
    public static double severityToScore(String severity) {
        switch (severity.toLowerCase()) {
            case "critical":
                return 0.95;
            case "high":
                return 0.75;
            case "medium":
                return 0.50;
            case "low":
                return 0.25;
            default:
                return 0.10;
        }
    }

    /**
     * Parse asset criticality to numeric score
     */
    public static double criticalityToScore(String criticality) {
        switch (criticality.toLowerCase()) {
            case "critical":
                return 0.95;
            case "high":
                return 0.75;
            case "medium":
                return 0.50;
            case "low":
                return 0.25;
            default:
                return 0.10;
        }
    }

    /**
     * Get current timestamp as string
     */
    public static String getTimestamp() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new java.util.Date());
    }
}
