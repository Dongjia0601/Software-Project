package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implementation of BrickGenerator that randomly selects bricks from a predefined list.
 * Uses a queue to hold the current brick to be played and the next one to come,
 * ensuring the next brick is always available.
 */
public class RandomBrickGenerator implements BrickGenerator {

    private final List<Brick> brickList; // List of all possible brick types

    private final Deque<Brick> nextBricks = new ArrayDeque<>(); // Queue holding the current and next bricks

    /**
     * Constructs a RandomBrickGenerator.
     * Initializes the list of all brick types and fills the queue with two randomly selected bricks.
     */
    public RandomBrickGenerator() {
        brickList = new ArrayList<>();
        brickList.add(new IBrick());
        brickList.add(new JBrick());
        brickList.add(new LBrick());
        brickList.add(new OBrick());
        brickList.add(new SBrick());
        brickList.add(new TBrick());
        brickList.add(new ZBrick());
        // Add two initial random bricks to the queue
        nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
    }

    @Override
    /**
     * Gets the current brick from the queue.
     * Ensures the queue always has at least one brick ahead by adding a new random one if needed.
     * Removes and returns the first brick in the queue.
     *
     * @return The current Brick instance.
     */
    public Brick getBrick() {
        if (nextBricks.size() <= 1) {
            nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        }
        return nextBricks.poll(); // Remove and return the first brick
    }

    @Override
    /**
     * Gets the next brick in the queue without removing it.
     *
     * @return The next Brick instance.
     */
    public Brick getNextBrick() {
        return nextBricks.peek(); // Return the first brick without removing it
    }
}