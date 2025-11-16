package com.comp2042;

import com.comp2042.model.brick.Brick;
import com.comp2042.model.brick.BrickGenerator;
import com.comp2042.model.brick.RandomBrickGenerator;
import com.comp2042.model.brick.SevenBagBrickGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BrickGenerator implementations.
 * Tests both RandomBrickGenerator and SevenBagBrickGenerator strategies.
 * 
 * <p>These tests validate the Strategy Pattern implementation for brick generation,
 * ensuring proper brick distribution and preview functionality.</p>
 * 
 * @author Dong, Jia.
 */
@DisplayName("BrickGenerator Tests")
class BrickGeneratorTest {

    // ========== RandomBrickGenerator Tests ==========

    @Test
    @DisplayName("RandomBrickGenerator: getBrick returns valid brick")
    void testRandomBrickGeneratorGetBrick() {
        BrickGenerator generator = new RandomBrickGenerator();
        Brick brick = generator.getBrick();
        
        assertNotNull(brick);
        assertNotNull(brick.getShapeMatrix());
        assertFalse(brick.getShapeMatrix().isEmpty());
    }

    @Test
    @DisplayName("RandomBrickGenerator: getNextBrick returns valid brick without consuming")
    void testRandomBrickGeneratorGetNextBrick() {
        BrickGenerator generator = new RandomBrickGenerator();
        Brick nextBrick1 = generator.getNextBrick();
        Brick nextBrick2 = generator.getNextBrick();
        
        assertNotNull(nextBrick1);
        assertNotNull(nextBrick2);
        // Should return same brick (peek, not poll)
        assertEquals(nextBrick1, nextBrick2);
    }

    @Test
    @DisplayName("RandomBrickGenerator: getBrick advances to next brick")
    void testRandomBrickGeneratorAdvances() {
        BrickGenerator generator = new RandomBrickGenerator();
        Brick nextBrick = generator.getNextBrick();
        Brick currentBrick = generator.getBrick();
        
        assertNotNull(currentBrick);
        // After getBrick(), next should be different
        Brick newNextBrick = generator.getNextBrick();
        assertNotEquals(nextBrick, newNextBrick);
    }

    @Test
    @DisplayName("RandomBrickGenerator: Can generate multiple bricks")
    void testRandomBrickGeneratorMultipleBricks() {
        BrickGenerator generator = new RandomBrickGenerator();
        
        // Generate 20 bricks - should all be valid
        for (int i = 0; i < 20; i++) {
            Brick brick = generator.getBrick();
            assertNotNull(brick);
            assertNotNull(brick.getShapeMatrix());
        }
    }

    @Test
    @DisplayName("RandomBrickGenerator: Maintains queue with at least one brick ahead")
    void testRandomBrickGeneratorQueueMaintenance() {
        BrickGenerator generator = new RandomBrickGenerator();
        
        // Get initial next brick
        Brick initialNext = generator.getNextBrick();
        assertNotNull(initialNext);
        
        // Consume current brick
        generator.getBrick();
        
        // Should still have next brick available
        Brick newNext = generator.getNextBrick();
        assertNotNull(newNext);
    }

    // ========== SevenBagBrickGenerator Tests ==========

    @Test
    @DisplayName("SevenBagBrickGenerator: getBrick returns valid brick")
    void testSevenBagBrickGeneratorGetBrick() {
        BrickGenerator generator = new SevenBagBrickGenerator();
        Brick brick = generator.getBrick();
        
        assertNotNull(brick);
        assertNotNull(brick.getShapeMatrix());
        assertFalse(brick.getShapeMatrix().isEmpty());
    }

    @Test
    @DisplayName("SevenBagBrickGenerator: Complete bag contains all 7 brick types")
    void testSevenBagBrickGeneratorCompleteBag() {
        SevenBagBrickGenerator generator = new SevenBagBrickGenerator();
        
        // Generate 7 bricks - should get all types (no duplicates in one bag)
        java.util.Set<Class<?>> brickTypes = new java.util.HashSet<>();
        
        for (int i = 0; i < 7; i++) {
            Brick brick = generator.getBrick();
            brickTypes.add(brick.getClass());
        }
        
        // Should have 7 different types
        assertEquals(7, brickTypes.size());
    }

    @Test
    @DisplayName("SevenBagBrickGenerator: Bag refills after 7 bricks")
    void testSevenBagBrickGeneratorBagRefill() {
        SevenBagBrickGenerator generator = new SevenBagBrickGenerator();
        
        // Generate first bag (7 bricks)
        java.util.List<Class<?>> firstBag = new java.util.ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Brick brick = generator.getBrick();
            firstBag.add(brick.getClass());
        }
        
        // Generate second bag (7 more bricks)
        java.util.List<Class<?>> secondBag = new java.util.ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Brick brick = generator.getBrick();
            secondBag.add(brick.getClass());
        }
        
        // Both bags should have 7 unique types
        assertEquals(7, new java.util.HashSet<>(firstBag).size());
        assertEquals(7, new java.util.HashSet<>(secondBag).size());
    }

    @Test
    @DisplayName("SevenBagBrickGenerator: getNextBrick previews correctly")
    void testSevenBagBrickGeneratorPreview() {
        SevenBagBrickGenerator generator = new SevenBagBrickGenerator();
        
        Brick next1 = generator.getNextBrick();
        Brick next2 = generator.getNextBrick();
        
        // Should be same (peek, not poll)
        assertEquals(next1, next2);
        
        // After getBrick(), next should advance
        generator.getBrick();
        Brick next3 = generator.getNextBrick();
        assertNotEquals(next1, next3);
    }

    @Test
    @DisplayName("SevenBagBrickGenerator: Fair distribution over multiple bags")
    void testSevenBagBrickGeneratorFairDistribution() {
        SevenBagBrickGenerator generator = new SevenBagBrickGenerator();
        java.util.Map<Class<?>, Integer> counts = new java.util.HashMap<>();
        
        // Generate 70 bricks (10 bags)
        for (int i = 0; i < 70; i++) {
            Brick brick = generator.getBrick();
            counts.put(brick.getClass(), counts.getOrDefault(brick.getClass(), 0) + 1);
        }
        
        // Each type should appear approximately 10 times (70 bricks / 7 types)
        for (Integer count : counts.values()) {
            // Allow some variance (9-11 times) due to randomness in bag shuffling
            assertTrue(count >= 9 && count <= 11, 
                "Brick type should appear ~10 times, got: " + count);
        }
    }

    // ========== Strategy Pattern Tests ==========

    @Test
    @DisplayName("Strategy Pattern: Both generators implement same interface")
    void testStrategyPatternInterface() {
        BrickGenerator randomGen = new RandomBrickGenerator();
        BrickGenerator bagGen = new SevenBagBrickGenerator();
        
        // Both should implement BrickGenerator interface
        assertTrue(randomGen instanceof BrickGenerator);
        assertTrue(bagGen instanceof BrickGenerator);
        
        // Both should have same methods
        assertNotNull(randomGen.getBrick());
        assertNotNull(randomGen.getNextBrick());
        assertNotNull(bagGen.getBrick());
        assertNotNull(bagGen.getNextBrick());
    }

    @Test
    @DisplayName("Strategy Pattern: Generators can be used interchangeably")
    void testStrategyPatternInterchangeable() {
        // Test that both generators can be used in same context
        BrickGenerator[] generators = {
            new RandomBrickGenerator(),
            new SevenBagBrickGenerator()
        };
        
        for (BrickGenerator generator : generators) {
            Brick brick = generator.getBrick();
            assertNotNull(brick);
            
            Brick next = generator.getNextBrick();
            assertNotNull(next);
        }
    }
}

