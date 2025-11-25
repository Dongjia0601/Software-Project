package com.comp2042;

import com.comp2042.model.brick.Brick;
import com.comp2042.model.brick.BrickGenerator;
import com.comp2042.model.brick.RandomBrickGenerator;
import com.comp2042.model.brick.SevenBagBrickGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BrickGenerator Tests")
class BrickGeneratorTest {

    @Test
    @DisplayName("RandomBrickGenerator: getBrick returns valid brick")
    void testRandomBrickGeneratorGetBrick() {
        BrickGenerator generator = new RandomBrickGenerator();
        Brick brick = generator.getBrick();
        assertNotNull(brick);
        assertNotNull(brick.getShapeMatrix());
    }

    @Test
    @DisplayName("RandomBrickGenerator: getNextBrick returns valid brick without consuming")
    void testRandomBrickGeneratorGetNextBrick() {
        BrickGenerator generator = new RandomBrickGenerator();
        Brick nextBrick1 = generator.getNextBrick();
        Brick nextBrick2 = generator.getNextBrick();
        
        assertNotNull(nextBrick1);
        assertNotNull(nextBrick2);
        assertEquals(nextBrick1, nextBrick2);
    }

    @Test
    @DisplayName("SevenBagBrickGenerator: getBrick returns valid brick")
    void testSevenBagBrickGeneratorGetBrick() {
        BrickGenerator generator = new SevenBagBrickGenerator();
        Brick brick = generator.getBrick();
        assertNotNull(brick);
        assertNotNull(brick.getShapeMatrix());
    }

    @Test
    @DisplayName("SevenBagBrickGenerator: Complete bag contains all 7 brick types")
    void testSevenBagBrickGeneratorCompleteBag() {
        SevenBagBrickGenerator generator = new SevenBagBrickGenerator();
        java.util.Set<Class<?>> brickTypes = new java.util.HashSet<>();
        
        for (int i = 0; i < 7; i++) {
            Brick brick = generator.getBrick();
            brickTypes.add(brick.getClass());
        }
        
        assertEquals(7, brickTypes.size());
    }

    @Test
    @DisplayName("Strategy Pattern: Both generators implement same interface")
    void testStrategyPatternInterface() {
        BrickGenerator randomGen = new RandomBrickGenerator();
        BrickGenerator bagGen = new SevenBagBrickGenerator();
        
        assertTrue(randomGen instanceof BrickGenerator);
        assertTrue(bagGen instanceof BrickGenerator);
        assertNotNull(randomGen.getBrick());
        assertNotNull(bagGen.getBrick());
    }

    @Test
    @DisplayName("SevenBagBrickGenerator: reset() ensures fresh 7-bag")
    void testSevenBagBrickGeneratorReset() {
        SevenBagBrickGenerator generator = new SevenBagBrickGenerator();
        
        // Consume some bricks from first bag
        generator.getBrick();
        generator.getBrick();
        generator.getBrick();
        
        // Reset should clear queue and start fresh
        generator.reset();
        
        // After reset, next 7 bricks should contain all 7 types
        java.util.Set<Class<?>> brickTypes = new java.util.HashSet<>();
        for (int i = 0; i < 7; i++) {
            Brick brick = generator.getBrick();
            brickTypes.add(brick.getClass());
        }
        
        assertEquals(7, brickTypes.size(), "After reset, 7-bag should contain all 7 brick types");
    }

    @Test
    @DisplayName("RandomBrickGenerator: reset() reinitializes lookahead queue")
    void testRandomBrickGeneratorReset() {
        RandomBrickGenerator generator = new RandomBrickGenerator();
        
        // Consume some bricks
        generator.getBrick();
        generator.getBrick();
        
        // Reset should reinitialize
        generator.reset();
        
        // After reset, should still be able to get bricks
        Brick brick = generator.getBrick();
        assertNotNull(brick);
        assertNotNull(brick.getShapeMatrix());
    }
}

