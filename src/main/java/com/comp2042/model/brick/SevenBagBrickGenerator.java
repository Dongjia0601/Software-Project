package com.comp2042.model.brick;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * "7-bag" randomizer implementation for fairer brick distribution.
 * Each bag contains exactly one of each tetromino (I, O, T, S, Z, J, L) in random order.
 * Guarantees no piece appears more than 13 bricks apart, reducing frustrating droughts.
 * 
 * @author Dong, Jia.
 */
public class SevenBagBrickGenerator implements BrickGenerator {

    private final Deque<Brick> queue = new ArrayDeque<>();

    /**
     * Constructs a SevenBagBrickGenerator with an initial shuffled bag.
     */
    public SevenBagBrickGenerator() {
        // Prime the queue with an initial bag so that getNextBrick() works
        refillIfNeeded();
        // Ensure there is always at least one lookahead item as existing code expects
        if (queue.size() == 1) {
            refillIfNeeded();
        }
    }

    /**
     * Gets and consumes the current brick from the bag.
     * 
     * @return Current Brick instance
     */
    @Override
    public Brick getBrick() {
        refillIfNeeded();
        return queue.pollFirst();
    }

    /**
     * Peeks at the next brick without consuming it.
     * 
     * @return Next Brick instance
     */
    @Override
    public Brick getNextBrick() {
        refillIfNeeded();
        return queue.peekFirst();
    }

    /**
     * Refills the queue with a new shuffled bag when empty.
     */
    private void refillIfNeeded() {
        if (queue.isEmpty()) {
            // Build a fresh bag with one of each tetromino
            List<Brick> bag = new ArrayList<>(7);
            bag.add(new IBrick());
            bag.add(new JBrick());
            bag.add(new LBrick());
            bag.add(new OBrick());
            bag.add(new SBrick());
            bag.add(new TBrick());
            bag.add(new ZBrick());
            // Shuffle in-place to randomize order
            Collections.shuffle(bag);
            // Append all to the queue
            queue.addAll(bag);
        }
    }
}


