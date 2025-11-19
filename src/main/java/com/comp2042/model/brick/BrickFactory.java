package com.comp2042.model.brick;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Factory class for creating Tetris bricks (Factory Pattern).
 * Provides type-safe brick creation and supports multiple randomization strategies.
 * 
 * @author Dong, Jia.
 */
public class BrickFactory {
    
    private static final Random RANDOM = new Random();
    private static final Map<String, Class<? extends Brick>> BRICK_TYPES = new HashMap<>();
    
    static {
        // Initialize brick type mappings
        BRICK_TYPES.put("I", IBrick.class);
        BRICK_TYPES.put("J", JBrick.class);
        BRICK_TYPES.put("L", LBrick.class);
        BRICK_TYPES.put("O", OBrick.class);
        BRICK_TYPES.put("S", SBrick.class);
        BRICK_TYPES.put("T", TBrick.class);
        BRICK_TYPES.put("Z", ZBrick.class);
    }
    
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private BrickFactory() {
    }
    
    /**
     * Creates a brick of the specified type.
     * 
     * @param brickType Type to create (I, J, L, O, S, T, Z)
     * @return New Brick instance
     * @throws IllegalArgumentException if type is unsupported
     */
    public static Brick createBrick(String brickType) {
        Class<? extends Brick> brickClass = BRICK_TYPES.get(brickType.toUpperCase());
        if (brickClass == null) {
            throw new IllegalArgumentException("Unsupported brick type: " + brickType);
        }
        
        try {
            return brickClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create brick of type: " + brickType, e);
        }
    }
    
    /**
     * Creates a random brick using pure random selection.
     * 
     * @return Random Brick instance
     */
    public static Brick createRandomBrick() {
        String[] brickTypes = BRICK_TYPES.keySet().toArray(new String[0]);
        String randomType = brickTypes[RANDOM.nextInt(brickTypes.length)];
        return createBrick(randomType);
    }
    
    /**
     * Creates a pure random brick generator.
     * 
     * @return RandomBrickGenerator instance
     */
    public static BrickGenerator createRandomBrickGenerator() {
        return new RandomBrickGenerator();
    }

    /**
     * Creates a 7-bag brick generator (one of each piece per bag, shuffled).
     * This provides fairer randomization than pure random.
     *
     * @return SevenBagBrickGenerator instance
     */
    public static BrickGenerator createSevenBagBrickGenerator() {
        return new SevenBagBrickGenerator();
    }
    
    /**
     * Gets all available brick types.
     * 
     * @return Array of supported brick type names
     */
    public static String[] getAvailableBrickTypes() {
        return BRICK_TYPES.keySet().toArray(new String[0]);
    }
    
    /**
     * Checks if a brick type is supported.
     * 
     * @param brickType Type to check
     * @return true if supported, false otherwise
     */
    public static boolean isBrickTypeSupported(String brickType) {
        return BRICK_TYPES.containsKey(brickType.toUpperCase());
    }
    
    /**
     * Gets the number of available brick types.
     * 
     * @return Number of supported types (always 7)
     */
    public static int getBrickTypeCount() {
        return BRICK_TYPES.size();
    }
    
    /**
     * Creates a brick with a specific color value (future extension point).
     * Currently returns a standard brick; color customization not yet implemented.
     * 
     * @param brickType Type to create
     * @param colorValue Color value (currently unused)
     * @return New Brick instance
     */
    public static Brick createColoredBrick(String brickType, int colorValue) {
        // Returns a standard brick; color customization can be added in the future
        return createBrick(brickType);
    }
}
