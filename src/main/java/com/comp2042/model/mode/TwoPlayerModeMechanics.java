package com.comp2042.model.mode;

import com.comp2042.model.board.Board;
import com.comp2042.service.gameloop.GameService;

/**
 * Encapsulates the mechanics and rules for Two-Player mode.
 * Responsibilities include calculating attack power from line clears and
 * managing garbage line interactions (attacks and defense/combos).
 * 
 * Follows Single Responsibility Principle by separating game rules from game mode orchestration.
 */
public class TwoPlayerModeMechanics {

    /**
     * Calculates attack power based on lines cleared.
     * Standard Tetris attack system:
     * - 1 line = 0 attack
     * - 2 lines = 1 attack
     * - 3 lines = 2 attacks
     * - 4 lines (Tetris) = 4 attacks
     * 
     * @param linesCleared the number of lines cleared
     * @return attack power (number of garbage lines to send)
     */
    public int calculateAttackPower(int linesCleared) {
        switch (linesCleared) {
            case 1: return 0;
            case 2: return 1;
            case 3: return 2;
            case 4: return 4; // Tetris bonus
            default: return 0;
        }
    }

    /**
     * Sends garbage lines to the opponent's board.
     * 
     * @param targetService the game service of the player receiving the attack
     * @param attackPower the number of garbage lines to send
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

