package model;

import gui.WindowTTT;

import java.util.List;

import algo.Resolution;

/**
 * Class for managing the Tic-Tac-Toe game.<br>
 * Contains the 3x3 game matrix,<br>
 * allows the creation of the situation tree,<br>
 * and detects the end of the game.
 * @author emmanuel adam
 */
public class TicTacToe {
    /** Matrix representing the game; the game is played on a 3x3 grid. */
    private int[][] gameMatrix;

    /** Number of cells in width */
    public static final int WIDTH = 3;

    /** Number of cells in height */
    public static final int HEIGHT = 3;

    /** Depth of the search tree */
    public final int GAME_DEPTH = 2;

    /** Graphical interface associated with the game */
    WindowTTT gui;

    /** Constructor */
    public TicTacToe() {
        gameMatrix = new int[HEIGHT][WIDTH];
        gui = new WindowTTT(this);
        gui.setVisible(true);
    }

    /**
     * Game turn = human move and computer move (using alpha-beta algorithm).
     * @param line row number played
     * @param column column number played
     * @return true if the move is allowed
     */
    public boolean tourDeJeu(int line, int column) {
        boolean result = true;
        assert (line >= 0 && line < HEIGHT) : "Row index out of bounds";
        assert (column >= 0 && column < WIDTH) : "Column index out of bounds";

        result = playMove(PlayerType.PLAYER, line, column);
        if (result) {
            Situation s = new Situation();
            s.setMax(true);
            s.setGameGrid(gameMatrix);
            createSituationTree(s, GAME_DEPTH);

            double bestValue = Resolution.alphaBeta(s, -Double.MAX_VALUE, Double.MAX_VALUE);
            s.setHeuristicEstimation(bestValue);

            boolean found = false;
            List<Situation> successors = s.getSuccessors();
            int nbSuccessors = successors.size();
            Situation bestSituation = null;

            for (int i = 0; i < nbSuccessors && !found; i++) {
                found = (successors.get(i).getHeuristicEstimation() == bestValue);
                if (found) {
                    bestSituation = successors.get(i);
                }
            }

            if (found) {
                System.out.printf("Found a possible move at (%d, %d), value is %.2f\n",
                        bestSituation.getLine(), bestSituation.getColumn(), bestSituation.getHeuristicEstimation());
                result = playMove(PlayerType.MACHINE, bestSituation.getLine(), bestSituation.getColumn());
            } else {
                System.err.println("Strange, I couldn't find the solution with value " + bestValue);
            }
        }
        return result;
    }

    /**
     * Performs a move in the game.
     * @param player type of player
     * @param i row number played
     * @param j column number played
     * @return true if the move is allowed
     */
    public boolean playMove(PlayerType player, int i, int j) {
        boolean result = true;
        gameMatrix[i][j] = player.getType();
        gui.updateJeu(gameMatrix);

        Situation s = new Situation();
        s.setGameGrid(gameMatrix);
        result = !isEndOfGame(s, player);
        return result;
    }

    /**
     * Determines if it is the end of the game.<br>
     * i.e., if someone has won.
     */
    private boolean isEndOfGame(Situation s, PlayerType player) {
        boolean win = false;
        String message = "";
        boolean full = false;
        String playerDesc = "Player ";

        if (s.isFull()) {
            message += "The entire grid is filled!!!";
            full = true;
        }

        if (s.threeTokensAligned(player, false)) {
            win = true;
            message += playerDesc + player + " won on a column!\n";
        }

        if (s.threeTokensAligned(player, true)) {
            win = true;
            message += playerDesc + player + " won on a row!\n";
        }

        if (s.threeTokensAlignedDiagonal(player)) {
            win = true;
            message += playerDesc + player + " won on a diagonal!\n";
        }

        if (win || full) {
            javax.swing.JOptionPane.showMessageDialog(gui, message, "End of game!!",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
        }
        return win;
    }

    /**
     * Checks if a move is possible on the provided matrix.
     * @param row row number played
     * @param column column number played
     * @param gameMatrix game matrix on which to play (different from the actual game matrix)
     * @return true if the move is allowed
     */
    public boolean isMovePossible(int row, int column, int[][] gameMatrix) {
        return gameMatrix[row][column] == 0;
    }

    /**
     * Creates a situation tree with 2 levels from the current game situation.
     * @param s situation from which to expand the tree
     * @param remainingLevels number of levels remaining to create in the tree
     */
    void createSituationTree(Situation s, int remainingLevels) {
        if (s.threeTokensAligned() || s.isFull()) {
            s.setClose(true);
        }

        if (remainingLevels == 0) {
            s.setLeaf(true);
        }

        if (s.isClose() || s.isLeaf() || remainingLevels == 0) {
            s.evaluate();
        } else {
            int[][] currentMatrix = s.getGameGrid();
            for (int i = 0; i < HEIGHT; i++) {
                for (int j = 0; j < WIDTH; j++) {
                    if (isMovePossible(i, j, currentMatrix)) {
                        Situation newSituation = new Situation(0, !s.isMax());
                        int[][] deducedMatrix = new int[HEIGHT][WIDTH];
                        copyMatrix(currentMatrix, deducedMatrix);

                        PlayerType tj = (s.isMax() ? PlayerType.MACHINE : PlayerType.PLAYER);
                        deducedMatrix[i][j] = tj.getType();
                        newSituation.setColumn(j);
                        newSituation.setLine(i);
                        newSituation.setGameGrid(deducedMatrix);
                        s.addSuccessor(newSituation);
                        createSituationTree(newSituation, remainingLevels - 1);
                    }
                }
            }
        }
    }

    /**
     * Copies the 3x3 game matrix.
     * @param from matrix to copy
     * @param to copied matrix
     */
    private void copyMatrix(int[][] from, int[][] to) {
        for (int i = 0; i < HEIGHT; i++) {
            System.arraycopy(from[i], 0, to[i], 0, WIDTH);
        }
    }

    /**
     * @return the game matrix
     */
    public int[][] getGameMatrix() {
        return gameMatrix;
    }
}
