package com.comp2042.logic.bricks;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Factory class for creating Tetris bricks with enhanced functionality.
 * Provides methods for creating specific brick types, random bricks, and managing brick instances.
 * 
 * <p>This factory supports the Strategy pattern by providing different brick creation strategies
 * and implements the Factory pattern for centralized brick instantiation.</p>
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
     * @param brickType The type of brick to create (I, J, L, O, S, T, Z).
     * @return A new Brick instance of the specified type.
     * @throws IllegalArgumentException if the brick type is not supported.
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
     * Creates a random brick from all available types.
     * 
     * @return A random Brick instance.
     */
    public static Brick createRandomBrick() {
        String[] brickTypes = BRICK_TYPES.keySet().toArray(new String[0]);
        String randomType = brickTypes[RANDOM.nextInt(brickTypes.length)];
        return createBrick(randomType);
    }
    
    /**
     * Creates a brick generator that produces random bricks.
     * 
     * @return A BrickGenerator instance that creates random bricks.
     */
    public static BrickGenerator createRandomBrickGenerator() {
        return new RandomBrickGenerator();
    }
    
    /**
     * Gets all available brick types.
     * 
     * @return An array of all supported brick type names.
     */
    public static String[] getAvailableBrickTypes() {
        return BRICK_TYPES.keySet().toArray(new String[0]);
    }
    
    /**
     * Checks if a brick type is supported.
     * 
     * @param brickType The brick type to check.
     * @return true if the brick type is supported, false otherwise.
     */
    public static boolean isBrickTypeSupported(String brickType) {
        return BRICK_TYPES.containsKey(brickType.toUpperCase());
    }
    
    /**
     * Gets the number of available brick types.
     * 
     * @return The number of supported brick types.
     */
    public static int getBrickTypeCount() {
        return BRICK_TYPES.size();
    }
    
    /**
     * Creates a brick with a specific color value.
     * This method can be extended to support colored bricks in the future.
     * 
     * @param brickType The type of brick to create.
     * @param colorValue The color value to assign to the brick.
     * @return A new Brick instance with the specified color.
     */
    public static Brick createColoredBrick(String brickType, int colorValue) {
        // For now, return a standard brick
        // This method can be enhanced to support colored bricks
        return createBrick(brickType);
    }
}
