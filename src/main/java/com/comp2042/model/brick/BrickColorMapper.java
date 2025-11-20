package com.comp2042.model.brick;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps brick type codes to their respective colors using polymorphism (Strategy Pattern).
 * Eliminates switch statements by delegating color selection to Brick objects.
 * Provides a centralized, maintainable color mapping that follows the Open-Closed Principle.
 * 
 * <p><strong>Design Pattern:</strong> Strategy Pattern + Singleton</p>
 * <ul>
 *   <li>Each Brick type defines its own color (Strategy)</li>
 *   <li>Mapper delegates to Brick instances instead of using type codes</li>
 *   <li>Adding new brick types requires NO modification to existing code (OCP)</li>
 * </ul>
 * 
 * @author Dong, Jia.
 */
public class BrickColorMapper {
    
    private static final Map<Integer, Paint> COLOR_CACHE = new HashMap<>();
    
    static {
        // Initialize color cache by creating one instance of each brick type
        // and storing their colors. This preserves the mapping between int codes
        // (used in matrices) and Brick-defined colors.
        COLOR_CACHE.put(0, Color.TRANSPARENT);  // Empty cell
        COLOR_CACHE.put(1, new IBrick().getColor());  // I-brick
        COLOR_CACHE.put(2, new JBrick().getColor());  // J-brick
        COLOR_CACHE.put(3, new LBrick().getColor());  // L-brick
        COLOR_CACHE.put(4, new OBrick().getColor());  // O-brick
        COLOR_CACHE.put(5, new SBrick().getColor());  // S-brick
        COLOR_CACHE.put(6, new TBrick().getColor());  // T-brick
        COLOR_CACHE.put(7, new ZBrick().getColor());  // Z-brick
        COLOR_CACHE.put(8, Color.GRAY);  // Garbage line
    }
    
    /**
     * Private constructor to prevent instantiation (utility class).
     */
    private BrickColorMapper() {
    }
    
    /**
     * Gets the color for a given brick type code (replaces switch statements).
     * Uses polymorphism: each Brick type knows its own color.
     * 
     * <p><strong>Before (Code Smell):</strong></p>
     * <pre>
     * switch (typeCode) {
     *     case 1: return Color.CYAN;
     *     case 2: return Color.BLUE;
     *     ...
     * }
     * </pre>
     * 
     * <p><strong>After (Polymorphic):</strong></p>
     * <pre>
     * return BrickColorMapper.getColor(typeCode);
     * // Delegates to brick.getColor()
     * </pre>
     * 
     * @param typeCode Brick type (0=empty, 1=I, 2=J, 3=L, 4=O, 5=S, 6=T, 7=Z, 8=garbage)
     * @return Paint color for the brick type
     */
    public static Paint getColor(int typeCode) {
        return COLOR_CACHE.getOrDefault(typeCode, Color.WHITE); // White as fallback
    }
    
    /**
     * Gets the color directly from a Brick object (pure polymorphism).
     * Use this when you have a Brick reference available.
     * 
     * @param brick Brick instance
     * @return Paint color from the brick
     */
    public static Paint getColor(Brick brick) {
        return brick != null ? brick.getColor() : Color.TRANSPARENT;
    }
}

