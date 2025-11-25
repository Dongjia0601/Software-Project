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
        refillIfNeeded();
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
            List<Brick> bag = new ArrayList<>(7);
            bag.add(new IBrick());
            bag.add(new JBrick());
            bag.add(new LBrick());
            bag.add(new OBrick());
            bag.add(new SBrick());
            bag.add(new TBrick());
            bag.add(new ZBrick());
            Collections.shuffle(bag);
            queue.addAll(bag);
        }
    }

    /**
     * Resets the generator to its initial state.
     * Clears the queue and fills it with a fresh shuffled bag.
     * Ensures each new game starts with a complete 7-bag.
     */
    @Override
    public void reset() {
        queue.clear();
        refillIfNeeded();
    }
}
