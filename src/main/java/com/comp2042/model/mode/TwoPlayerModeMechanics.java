package com.comp2042.model.mode;

import com.comp2042.model.board.Board;
import com.comp2042.service.gameloop.GameService;

/**
 * Two-Player mode mechanics handling attack power calculation and garbage line interactions (SRP).
 * Calculates attacks from line clears and manages garbage defense/combos.
 * Separated from mode orchestration for clarity.
 */
public class TwoPlayerModeMechanics {

    private static final int SINGLE_LINE_ATTACK = 0;
    private static final int DOUBLE_LINE_ATTACK = 1;
    private static final int TRIPLE_LINE_ATTACK = 2;
    private static final int TETRIS_ATTACK = 4;
    private static final int NO_ATTACK = 0;
    private static final int COMBO_DEFENSE_MULTIPLIER = 2;
    
    /**
     * Calculates attack power (1 line=0, 2=1, 3=2, 4=4 attacks).
     * 
     * @param linesCleared Lines cleared
     * @return Attack power (garbage lines sent to opponent)
     */
    public int calculateAttackPower(int linesCleared) {
        return switch (linesCleared) {
            case 1 -> SINGLE_LINE_ATTACK;
            case 2 -> DOUBLE_LINE_ATTACK;
            case 3 -> TRIPLE_LINE_ATTACK;
            case 4 -> TETRIS_ATTACK;
            default -> NO_ATTACK;
        };
    }

    /**
     * Sends garbage lines to opponent.
     * 
     * @param targetService Target player's game service
     * @param attackPower Garbage lines count
     * @return true if the attack caused a game over for the target, false otherwise
     */
    public boolean sendAttack(GameService targetService, int attackPower) {
        if (attackPower <= 0 || targetService == null) {
            return false;
        }
        
        Board targetBoard = targetService.getBoard();
        boolean causedGameOver = false;
        
        for (int i = 0; i < attackPower; i++) {
            boolean result = targetBoard.addGarbageLine();
            if (result) {
                causedGameOver = true;
            }
        }
        
        return causedGameOver;
    }

    /**
     * Eliminates garbage lines from a player's board based on combo count.
     * 
     * @param targetService the game service of the player
     * @param comboCount the current combo count
     * @return the actual number of lines eliminated
     */
    public int processComboDefense(GameService targetService, int comboCount) {
        if (comboCount < 2 || targetService == null) {
            return 0;
        }
        
        int comboBonus = (comboCount - 1) * COMBO_DEFENSE_MULTIPLIER;
        Board targetBoard = targetService.getBoard();
        
        return targetBoard.removeGarbageLines(comboBonus);
    }
}

