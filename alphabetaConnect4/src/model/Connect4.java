package model;

import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;
import algo.Resolution;
import gui.Connect4Window;

public class Connect4 extends Thread {

    /**
     * Matrix representing the game; the game is played on an 8x7 grid.<br>
     * The last row contains the number of elements in the columns.<br>
     * The last column contains the number of elements in the rows.
     */
    private int[][] gameMatrix;

    /** Depth of the search tree */
    private int gameDepth = 4;

    /** Number of cells in width + 1 */
    public static final int WIDTH = 7;

    /** Number of cells in height + 1 */
    public static final int HEIGHT = 6;

    /** Graphical interface associated with the game */
    Connect4Window gui;

    /** Constructor */
    public Connect4() {
        super("Connect Four");
        gameMatrix = new int[Connect4.HEIGHT + 1][Connect4.WIDTH + 1];
        Arrays.fill(gameMatrix[0], -1);
        gui = new Connect4Window(this);
        gui.setVisible(true);
        init();
        start();
    }

    public void init() {
        gameMatrix = new int[Connect4.HEIGHT + 1][Connect4.WIDTH + 1];
        Arrays.fill(gameMatrix[0], -1);

        Object[] possibleValues = { "Normal", "Hard" };
        Object selectedValue = JOptionPane.showInputDialog(null, "Choose the level", "Level",
                JOptionPane.QUESTION_MESSAGE, null, possibleValues, possibleValues[0]);

        if (selectedValue instanceof String level) {
            switch (level) {
                case "Easy" -> gameDepth = 3;
                case "Normal" -> gameDepth = 4;
                case "Hard" -> gameDepth = 8;
            }
        }

        int response = JOptionPane.showConfirmDialog(gui, "Do you want to start placing your tokens?",
                "Choose the first player", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);


        if (response == JOptionPane.NO_OPTION) {
            gui.setHelpLabelText("The machine is thinking...");
            roundGame(-1);
        }
    }

    /**
     * Game turn = human move and computer move (using alpha-beta algorithm).
     *
     * @param column column number played
     * @return true if the move is allowed
     */
    public boolean roundGame(int column) {
        boolean result;
        if (column == -1) {
            result = true;
        } else {
            result = playMove(PlayerType.PLAYER, column);
        }

        if (result) {
            Situation s = new Situation();
            s.setColumnNumber(column);
            s.setGameMatrix(gameMatrix);

            while (!gui.updateGame(gameMatrix)) {
                Thread.yield();
            }

            createSituationTree(s, gameDepth);
            int bestValue = Resolution.alphaBeta(s, Integer.MIN_VALUE, Integer.MAX_VALUE);
            s.setH(bestValue);

            boolean found = false;
            ArrayList<Situation> successors = s.getSuccesseurs();
            int nbSuccessors = successors.size();
            StringBuilder sb = new StringBuilder("Machine found ").append(nbSuccessors)
                    .append(" situations under the previous situation where h=").append(s.getH()).append("\n");

            int i = 0;
            Situation bestSituation = null;
            for (; i < nbSuccessors && !found; i++) {
                found = (successors.get(i).getH() == bestValue);
                if (found)  bestSituation = successors.get(i);
                sb.append("col").append(successors.get(i).getColumnNumber()).append("=")
                        .append(successors.get(i).getH()).append(", ");
            }

            for (; i < nbSuccessors; i++) {
                sb.append("col").append(successors.get(i).getColumnNumber()).append("= --, ");
            }

            gui.setHelpLabelText(sb.toString());

            if (found && bestSituation != null) {
                result = playMove(PlayerType.MACHINE, bestSituation.getColumnNumber());
            }
        }
        return result;
    }

    /**
     * Performs a move in the game.
     *
     * @param player type of player
     * @param column column number played
     * @return true if the move is allowed
     */
    public boolean playMove(PlayerType player, int column) {
        boolean result;
        int nb = gameMatrix[HEIGHT][column];
        if (nb == HEIGHT) {
            result = false;
        } else {
            gameMatrix[HEIGHT][column]++;
            gameMatrix[nb][WIDTH]++;
            gameMatrix[nb][column] = player.getType();
            if (nb < HEIGHT - 1) {
                gameMatrix[nb + 1][column] = -1;
            }
            gui.updateGame(gameMatrix, column);
            Situation s = new Situation();
            s.setColumnNumber(column);
            s.setGameMatrix(gameMatrix);
            result = !isEndOfGame(s, player);
        }
        return result;
    }

    /**
     * Determines if it is the end of the game.<br>
     * i.e., if someone has won.
     */
    private boolean isEndOfGame(Situation s, PlayerType player) {
        boolean win = false;
        String message = "";
        boolean full = true;

        for (int j = 0; j < Connect4.WIDTH && full; j++) {
            full = full && (gameMatrix[Connect4.HEIGHT][j] == 6);
        }

        if (full) {
            message += "The entire grid is filled!!!";
        }

        if (s.fourAlignedInColumn(player) != 0) {
            win = true;
            message += "Player " + player + " won on a column!\n";
        }

        if (s.fourAlignedInRow(player) != 0) {
            win = true;
            message += "Player " + player + " won on a row!\n";
        }

        if (s.fourAlignedInDiagonal(player) != 0) {
            win = true;
            message += "Player " + player + " won on a diagonal!\n";
        }

        if (win || full) {
            javax.swing.JOptionPane.showMessageDialog(gui, message, "End of game!!",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
            gui.endOfGame();
        }
        return win;
    }

    /**
     * Performs a move on the provided matrix.
     *
     * @param player type of player
     * @param column column number played
     * @param matrix game matrix on which to play (different from the actual game matrix)
     * @return true if the move is allowed
     */
    public boolean playMove(PlayerType player, int column, int[][] matrix) {
        boolean result = true;
        assert (column >= 0 && column < WIDTH) : "Column index out of bounds";
        int nb = matrix[HEIGHT][column];
        if (nb == HEIGHT) {
            result = false;
        } else {
            matrix[HEIGHT][column]++;
            matrix[nb][WIDTH]++;
            matrix[nb][column] = player.getType();
            if (nb < HEIGHT - 1) {
                matrix[nb + 1][column] = -1;
            }
        }
        return result;
    }

    /**
     * Creates a situation tree with the specified number of levels from the current game situation.
     *
     * @param s situation from which to expand the tree
     * @param remainingLevels number of levels remaining to create in the tree
     */
    void createSituationTree(Situation s, int remainingLevels) {
        for (int j = 0; j < WIDTH; j++) {
            int[][] deducedMatrix = new int[HEIGHT + 1][WIDTH + 1];
            copyMatrix(s.getGameMatrix(), deducedMatrix);
            PlayerType tj = (s.isMax() ? PlayerType.MACHINE : PlayerType.PLAYER);

            if (deducedMatrix[HEIGHT][j] < HEIGHT && playMove(tj, j, deducedMatrix)) {
                Situation newSituation = new Situation(0, !s.isMax());
                newSituation.setColumnNumber(j);
                newSituation.setGameMatrix(deducedMatrix);
                s.addSuccessor(newSituation);

                if (newSituation.fourAlignedInColumn(tj) != 0 || newSituation.fourAlignedInRow(tj) != 0
                        || newSituation.fourAlignedInDiagonal(tj) != 0) {
                    newSituation.setClose(true);
                    newSituation.evaluate();
                } else {
                    if (remainingLevels > 1) {
                        createSituationTree(newSituation, remainingLevels - 1);
                    } else {
                        newSituation.setLeaf(true);
                        newSituation.evaluate();
                    }
                }
            }
        }
    }

    /**
     * Copies the 6x6 game matrix.
     *
     * @param from matrix to copy
     * @param to copied matrix
     */
    private void copyMatrix(int[][] from, int[][] to) {
        for (int i = 0; i < HEIGHT + 1; i++) {
            System.arraycopy(from[i], 0, to[i], 0, WIDTH + 1);
        }
    }

    /**
     * @return the game matrix
     */
    public int[][] getGameMatrix() {
        return gameMatrix;
    }
}
