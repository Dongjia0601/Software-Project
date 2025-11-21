package com.comp2042.view.manager;

import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 * Utility class for common UI operations to reduce code duplication.
 * Provides null-safe UI updates, visibility control, and time formatting.
 *
 * @author Dong, Jia
 */
public class UIHelper {

    // ==================== Text Setting ====================

    /**
     * Safely sets text on a label, checking for null first.
     *
     * @param label the label to update, may be null
     * @param text the text to set
     */
    public static void setTextSafe(Label label, String text) {
        if (label != null) {
            label.setText(text);
        }
    }

    /**
     * Safely sets text on a label with integer value, checking for null first.
     *
     * @param label the label to update, may be null
     * @param prefix the text prefix
     * @param value the integer value to append
     */
    public static void setTextSafe(Label label, String prefix, int value) {
        if (label != null) {
            label.setText(prefix + value);
        }
    }

    /**
     * Safely sets text on a label with long value, checking for null first.
     *
     * @param label the label to update, may be null
     * @param prefix the text prefix
     * @param value the long value to append
     */
    public static void setTextSafe(Label label, String prefix, long value) {
        if (label != null) {
            label.setText(prefix + value);
        }
    }

    // ==================== Visibility Control ====================

    /**
     * Safely shows a node by setting both visible and managed to true.
     *
     * @param node the node to show, may be null
     */
    public static void showNode(Node node) {
        if (node != null) {
            node.setVisible(true);
            node.setManaged(true);
        }
    }

    /**
     * Safely hides a node by setting both visible and managed to false.
     *
     * @param node the node to hide, may be null
     */
    public static void hideNode(Node node) {
        if (node != null) {
            node.setVisible(false);
            node.setManaged(false);
        }
    }

    // ==================== Time Formatting ====================

    /**
     * Formats milliseconds into MM:SS time string.
     *
     * @param millis the time in milliseconds
     * @return formatted time string "MM:SS"
     */
    public static String formatTimeMillis(long millis) {
        if (millis == Long.MAX_VALUE || millis <= 0) {
            return "--:--";
        }
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%d:%02d", minutes, remainingSeconds);
    }

    /**
     * Formats seconds into MM:SS time string.
     *
     * @param totalSeconds the time in seconds
     * @return formatted time string "MM:SS"
     */
    public static String formatTimeSeconds(long totalSeconds) {
        if (totalSeconds <= 0) {
            return "--:--";
        }
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    /**
     * Formats time for display with a prefix.
     *
     * @param prefix the prefix text (e.g., "Time: ", "Best Time: ")
     * @param millis the time in milliseconds
     * @return formatted time string with prefix
     */
    public static String formatTimeWithPrefix(String prefix, long millis) {
        return prefix + formatTimeMillis(millis);
    }

    // ==================== Utility Methods ====================

    /**
     * Checks if a string is null or empty.
     *
     * @param str the string to check
     * @return true if null or empty, false otherwise
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Safely compares two objects for equality, handling nulls.
     *
     * @param obj1 first object
     * @param obj2 second object
     * @return true if both are null or equal, false otherwise
     */
    public static boolean equalsSafe(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) {
            return true;
        }
        if (obj1 == null || obj2 == null) {
            return false;
        }
        return obj1.equals(obj2);
    }
}
