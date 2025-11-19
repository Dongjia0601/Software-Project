package com.comp2042;

import com.comp2042.model.score.Score;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Score class.
 * Tests the scoring system functionality and validates score calculations.
 */
class ScoreTest {

    private Score score;

    @BeforeEach
    void setUp() {
        score = new Score();
    }

    @Test
    void testInitialScore() {
        // Test that initial score is zero
        assertEquals(0, score.getScore());
        assertEquals(0, score.scoreProperty().get());
    }

    @Test
    void testAddScore() {
        // Test adding score points
        score.add(100);
        assertEquals(100, score.getScore());
        assertEquals(100, score.scoreProperty().get());
        
        score.add(50);
        assertEquals(150, score.getScore());
        assertEquals(150, score.scoreProperty().get());
    }

    @Test
    void testAddZeroScore() {
        // Test adding zero score
        score.add(0);
        assertEquals(0, score.getScore());
    }

    @Test
    void testAddNegativeScore() {
        // Test adding negative score (should decrease score)
        score.add(100);
        score.add(-50);
        assertEquals(50, score.getScore()); // Score should decrease
    }

    @Test
    void testScorePropertyBinding() {
        // Test that score property is properly bound
        score.add(200);
        assertEquals(200, score.scoreProperty().get());
        
        // Test property change notifications
        score.add(50);
        assertEquals(250, score.scoreProperty().get());
    }

    @Test
    void testMultipleAdditions() {
        // Test multiple score additions
        score.add(10);
        score.add(20);
        score.add(30);
        score.add(40);
        
        assertEquals(100, score.getScore());
    }

    @Test
    void testLargeScoreAddition() {
        // Test adding large score values
        score.add(1000000);
        assertEquals(1000000, score.getScore());
        
        score.add(500000);
        assertEquals(1500000, score.getScore());
    }
}
