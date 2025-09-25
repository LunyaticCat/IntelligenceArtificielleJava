package model;

import java.util.Arrays;
import java.util.List;

/**
 * Enumeration representing potential danger patterns in the game.
 */
public enum DangerPattern {
    DANGER1_1(new int[]{-1, 1, 1, 1}, 1),
    DANGER1_2(new int[]{1, 1, 1, -1}, 1),
    DANGER1_3(new int[]{1, -1, 1, 1}, 1),
    DANGER1_4(new int[]{1, 1, -1, 1}, 1),

    DANGER2_1(new int[]{-1, 2, 2, 2}, 2),
    DANGER2_2(new int[]{2, 2, 2, -1}, 2),
    DANGER2_3(new int[]{2, -1, 2, 2}, 2),
    DANGER2_4(new int[]{2, 2, -1, 2}, 2),

    POSSIBLE_DANGER1_1(new int[]{-1, -1, 1, 1}, 1, true),
    POSSIBLE_DANGER1_2(new int[]{1, 1, -1, -1}, 1, true),
    POSSIBLE_DANGER1_3(new int[]{1, -1, 1, -1}, 1, true),
    POSSIBLE_DANGER1_4(new int[]{-1, 1, -1, 1}, 1, true),

    POSSIBLE_DANGER2_1(new int[]{-1, -1, 2, 2}, 2, true),
    POSSIBLE_DANGER2_2(new int[]{2, 2, -1, -1}, 2, true),
    POSSIBLE_DANGER2_3(new int[]{2, -1, 2, -1}, 2, true),
    POSSIBLE_DANGER2_4(new int[]{-1, 2, -1, 2}, 2, true);

    final int[] pattern;
    final int playerTurn;
    final boolean isPossibleDanger;

    /**
     * Constructor for DangerPattern.
     * @param pattern the pattern associated with the danger
     * @param playerTurn which turn the player start with
     * @param isPossibleDanger is the danger only possible
     */
    DangerPattern(int[] pattern, int playerTurn, boolean isPossibleDanger) {
        this.pattern = pattern;
        this.playerTurn = playerTurn;
        this.isPossibleDanger = isPossibleDanger;
    }

    DangerPattern(int[] pattern, int playerTurn) {
        this.pattern = pattern;
        this.playerTurn = playerTurn;
        this.isPossibleDanger = false;
    }

    /**
     * @return the pattern
     */
    public int[] getPattern() {
        return pattern;
    }

    public int getPlayerTurn() {return playerTurn;}

    public static List<DangerPattern> getDangersByPlayer(int playerTurn, boolean isPossibleDanger) {
        return Arrays.stream(values())
                .filter(s -> s.isPossibleDanger == isPossibleDanger)
                .filter(s -> s.playerTurn == playerTurn)
                .toList();
    }
}
