package com.comp2042;

import javafx.beans.property.IntegerProperty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Extended unit tests for Score class.
 * Tests additional functionality beyond basic ScoreTest.
 * 
 * <p>These tests complement the existing ScoreTest by covering
 * reset functionality, property binding, and edge cases.</p>
 * 
 * @author Dong, Jia.
 */
@DisplayName("Score Extended Tests")
class ScoreExtendedTest {

    private Score score;

    @BeforeEach
    void setUp() {
        score = new Score();
    }

    @Test
    @DisplayName("reset: Resets score to zero")
    void testReset() {
        score.add(1000);
        assertEquals(1000, score.getScore());
        
        score.reset();
        
        assertEquals(0, score.getScore());
        assertEquals(0, score.scoreProperty().get());
    }

    @Test
    @DisplayName("reset: Can reset multiple times")
    void testResetMultipleTimes() {
        score.add(500);
        score.reset();
        assertEquals(0, score.getScore());
        
        score.add(300);
        score.reset();
        assertEquals(0, score.getScore());
        
        score.reset(); // Reset when already zero
        assertEquals(0, score.getScore());
    }

    @Test
    @DisplayName("scoreProperty: Property updates when score changes")
    void testScorePropertyUpdates() {
        IntegerProperty property = score.scoreProperty();
        
        assertEquals(0, property.get());
        
        score.add(100);
        assertEquals(100, property.get());
        
        score.add(50);
        assertEquals(150, property.get());
        
        score.reset();
        assertEquals(0, property.get());
    }

    @Test
    @DisplayName("scoreProperty: Property binding works correctly")
    void testScorePropertyBinding() {
        IntegerProperty property = score.scoreProperty();
        
        // Add score
        score.add(200);
        assertEquals(200, property.get());
        
        // Reset
        score.reset();
        assertEquals(0, property.get());
        
        // Add again
        score.add(75);
        assertEquals(75, property.get());
    }

    @Test
    @DisplayName("getScore: Returns same value as property")
    void testGetScoreMatchesProperty() {
        score.add(250);
        
        assertEquals(score.getScore(), score.scoreProperty().get());
        
        score.reset();
        assertEquals(score.getScore(), score.scoreProperty().get());
        
        score.add(999);
        assertEquals(score.getScore(), score.scoreProperty().get());
    }

    @Test
    @DisplayName("add: Handles large score values")
    void testAddLargeValues() {
        score.add(Integer.MAX_VALUE - 100);
        assertEquals(Integer.MAX_VALUE - 100, score.getScore());
        
        score.add(50);
        // May overflow, but should not crash
        assertNotNull(score.getScore());
    }

    @Test
    @DisplayName("add: Handles negative values (penalty)")
    void testAddNegativeValues() {
        score.add(100);
        score.add(-30);
        
        assertEquals(70, score.getScore());
    }

    @Test
    @DisplayName("add: Score can go negative")
    void testScoreCanGoNegative() {
        score.add(50);
        score.add(-100);
        
        assertEquals(-50, score.getScore());
    }

    @Test
    @DisplayName("Integration: Complete score lifecycle")
    void testScoreLifecycle() {
        // Initial state
        assertEquals(0, score.getScore());
        
        // Add points
        score.add(100);
        assertEquals(100, score.getScore());
        
        // Add more
        score.add(50);
        assertEquals(150, score.getScore());
        
        // Penalty
        score.add(-25);
        assertEquals(125, score.getScore());
        
        // Reset
        score.reset();
        assertEquals(0, score.getScore());
        
        // Start fresh
        score.add(200);
        assertEquals(200, score.getScore());
    }
}

