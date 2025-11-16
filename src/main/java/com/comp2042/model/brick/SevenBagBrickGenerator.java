package com.comp2042.model.brick;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * A BrickGenerator that implements the "7-bag" randomizer.
 * Each bag contains exactly one of each of the seven tetrominoes
 * (I, O, T, S, Z, J, L) in a random order. Bricks are dealt from the bag until
 * it is empty, then a new shuffled bag is created.
 *
 * This generator also maintains a small lookahead queue so callers can ask for
 * the next brick without consuming it, matching the semantics of
 * RandomBrickGenerator#getNextBrick.
 */
public class SevenBagBrickGenerator implements BrickGenerator {

    private final Deque<Brick> queue = new ArrayDeque<>();

    public SevenBagBrickGenerator() {
        // Prime the queue with an initial bag so that getNextBrick() works
        refillIfNeeded();
        // Ensure there is always at least one lookahead item as existing code expects
        if (queue.size() == 1) {
            refillIfNeeded();
        }
    }

    @Override
    public Brick getBrick() {
        refillIfNeeded();
        return queue.pollFirst();
    }

    @Override
    public Brick getNextBrick() {
        refillIfNeeded();
        return queue.peekFirst();
    }

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


