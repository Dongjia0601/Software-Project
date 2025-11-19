package com.comp2042.model.mode;

import com.comp2042.model.board.Board;
import com.comp2042.service.gameloop.GameService;

/**
 * Two-Player mode mechanics handling attack power calculation and garbage line interactions (SRP).
 * Calculates attacks from line clears and manages garbage defense/combos.
 * Separated from mode orchestration for clarity.
 */
public class TwoPlayerModeMechanics {

    /**
     * Calculates attack power (1 line=0, 2=1, 3=2, 4=4 attacks).
     * 
     * @param linesCleared Lines cleared
     * @return Attack power (garbage lines)
     */
    public int calculateAttackPower(int linesCleared) {
        switch (linesCleared) {
            case 1: return 0;
            case 2: return 1;
            case 3: return 2;
            case 4: return 4;
            default: return 0;
        }
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
        
        int comboBonus = (comboCount - 1) * 2; // Each combo above 1 eliminates 2 lines
        Board targetBoard = targetService.getBoard();
        
        return targetBoard.removeGarbageLines(comboBonus);
    }
}

