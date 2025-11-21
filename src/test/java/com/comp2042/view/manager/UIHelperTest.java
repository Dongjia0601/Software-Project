package com.comp2042.view.manager;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UIHelper utility class.
 * Tests all static methods with null values and edge cases.
 *
 * @author Dong, Jia
 */
class UIHelperTest {

    private Label testLabel;
    private VBox testVBox;

    @BeforeEach
    void setUp() {
        testLabel = new Label();
        testVBox = new VBox();
    }

    // ==================== Text Setting Tests ====================

    @Test
    void testSetTextSafe_WithValidLabel() {
        UIHelper.setTextSafe(testLabel, "Test Text");
        assertEquals("Test Text", testLabel.getText());
    }

    @Test
    void testSetTextSafe_WithNullLabel() {
        UIHelper.setTextSafe(null, "Test Text");
        // Should not throw exception
    }

    @Test
    void testSetTextSafe_WithPrefixAndValue() {
        UIHelper.setTextSafe(testLabel, "Score: ", 100);
        assertEquals("Score: 100", testLabel.getText());
    }

    @Test
    void testSetTextSafe_WithPrefixAndLongValue() {
        UIHelper.setTextSafe(testLabel, "Time: ", 1500L);
        assertEquals("Time: 1500", testLabel.getText());
    }

    // ==================== Visibility Control Tests ====================

    @Test
    void testShowNode_WithValidNode() {
        UIHelper.showNode(testVBox);
        assertTrue(testVBox.isVisible());
        assertTrue(testVBox.isManaged());
    }

    @Test
    void testShowNode_WithNullNode() {
        UIHelper.showNode(null);
        // Should not throw exception
    }

    @Test
    void testHideNode_WithValidNode() {
        UIHelper.hideNode(testVBox);
        assertFalse(testVBox.isVisible());
        assertFalse(testVBox.isManaged());
    }

    @Test
    void testHideNode_WithNullNode() {
        UIHelper.hideNode(null);
        // Should not throw exception
    }

    // ==================== Time Formatting Tests ====================

    @Test
    void testFormatTimeMillis_ValidTime() {
        String result = UIHelper.formatTimeMillis(65000); // 1 minute 5 seconds
        assertEquals("1:05", result);
    }

    @Test
    void testFormatTimeMillis_ZeroTime() {
        String result = UIHelper.formatTimeMillis(0);
        assertEquals("--:--", result);
    }

    @Test
    void testFormatTimeMillis_MaxValue() {
        String result = UIHelper.formatTimeMillis(Long.MAX_VALUE);
        assertEquals("--:--", result);
    }

    @Test
    void testFormatTimeMillis_OnlySeconds() {
        String result = UIHelper.formatTimeMillis(45000); // 45 seconds
        assertEquals("0:45", result);
    }

    @Test
    void testFormatTimeSeconds_ValidTime() {
        String result = UIHelper.formatTimeSeconds(125); // 2 minutes 5 seconds
        assertEquals("2:05", result);
    }

    @Test
    void testFormatTimeSeconds_ZeroTime() {
        String result = UIHelper.formatTimeSeconds(0);
        assertEquals("--:--", result);
    }

    @Test
    void testFormatTimeWithPrefix() {
        String result = UIHelper.formatTimeWithPrefix("Elapsed: ", 90000); // 1 minute 30 seconds
        assertEquals("Elapsed: 1:30", result);
    }

    @Test
    void testFormatTimeWithPrefix_InvalidTime() {
        String result = UIHelper.formatTimeWithPrefix("Time: ", Long.MAX_VALUE);
        assertEquals("Time: --:--", result);
    }

    // ==================== Utility Method Tests ====================

    @Test
    void testIsNullOrEmpty_WithNullString() {
        assertTrue(UIHelper.isNullOrEmpty(null));
    }

    @Test
    void testIsNullOrEmpty_WithEmptyString() {
        assertTrue(UIHelper.isNullOrEmpty(""));
    }

    @Test
    void testIsNullOrEmpty_WithWhitespaceString() {
        assertTrue(UIHelper.isNullOrEmpty("   "));
    }

    @Test
    void testIsNullOrEmpty_WithValidString() {
        assertFalse(UIHelper.isNullOrEmpty("test"));
    }

    @Test
    void testEqualsSafe_BothNull() {
        assertTrue(UIHelper.equalsSafe(null, null));
    }

    @Test
    void testEqualsSafe_FirstNull() {
        assertFalse(UIHelper.equalsSafe(null, "test"));
    }

    @Test
    void testEqualsSafe_SecondNull() {
        assertFalse(UIHelper.equalsSafe("test", null));
    }

    @Test
    void testEqualsSafe_BothEqual() {
        assertTrue(UIHelper.equalsSafe("test", "test"));
    }

    @Test
    void testEqualsSafe_BothDifferent() {
        assertFalse(UIHelper.equalsSafe("test1", "test2"));
    }

    // ==================== Integration Tests ====================

    @Test
    void testCompleteWorkflow() {
        // Test a complete workflow similar to how UIHelper would be used

        // Initially hide the node
        UIHelper.hideNode(testVBox);
        assertFalse(testVBox.isVisible());
        assertFalse(testVBox.isManaged());

        // Show the node
        UIHelper.showNode(testVBox);
        assertTrue(testVBox.isVisible());
        assertTrue(testVBox.isManaged());

        // Set some text
        UIHelper.setTextSafe(testLabel, "Level ", 5);
        assertEquals("Level 5", testLabel.getText());

        // Format some time
        String timeString = UIHelper.formatTimeWithPrefix("Time: ", 75000);
        assertEquals("Time: 1:15", timeString);
    }
}
