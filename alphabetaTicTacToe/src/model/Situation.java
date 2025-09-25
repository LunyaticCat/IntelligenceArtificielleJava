package model;
import java.util.ArrayList;
import java.util.List;

/**
 * Situation class, representing a node in the state tree
 * @author emmanuel adam
 * @version May 2014
 */
public class Situation {
    /** name of the situation */
    private String name;
    /** number of instances */
    private static int nbInstances = 0;
    /** row number */
    private int line;
    /** column number */
    private int column;
    /** indicates if the state is in Max mode */
    private boolean isMax = true;
    /** list of states accessible from the current state */
    private List<Situation> successors;
    /** indicates if the state/situation is a leaf of the tree */
    private boolean isLeaf;
    /** indicates if the state/situation is a definitive leaf of the tree
     * (i.e., it is impossible to create successors for this situation) */
    private boolean close;
    /** h = heuristic, estimation of the situation's value */
    private double heuristicEstimation;
    /** game grid corresponding to the situation */
    private int[][] gameGrid;

    /** default constructor */
    public Situation() {
        int noInstances = nbInstances++;
        name = "" + noInstances;
        heuristicEstimation = 0;
        gameGrid = new int[3][3];
        successors = new ArrayList<>();
    }

    /** constructor initializing the heuristic estimate h
     * @param estimation estimation of the situation */
    public Situation(int estimation) {
        this();
        heuristicEstimation = estimation;
    }

    /** constructor initializing the heuristic estimate h and the node type
     * @param estimation estimation of the situation
     * @param isMaximized determines if the situation's value should be maximized or not */
    public Situation(int estimation, boolean isMaximized) {
        this(estimation);
        this.isMax = isMaximized;
    }

    /** constructor initializing the heuristic estimate h, the node type, and the node position
     * @param estimation estimation of the situation
     * @param isMax determines if the situation's value should be maximized or not
     * @param isLeaf determines if the situation is final in the tree */
    public Situation(int estimation, boolean isMax, boolean isLeaf) {
        this(estimation, isMax);
        this.isLeaf = isLeaf;
    }

    /** function evaluating the current situation; calculates 'h' */
    void evaluate() {
        double eval = 0d;
        double coefSituation = (this.isMax ? -1 : 1);
        // positive values are for the AI
        // and are decreased if the next move is for the human
        // otherwise, they are increased
        double valeur = possibleDangers(true);
        valeur += 0.1 * coefSituation * Math.abs(valeur);
        eval += valeur;
        valeur = possibleDangers(false);
        valeur += 0.1 * coefSituation * Math.abs(valeur);
        eval += valeur;
        valeur = possibleDangersDiagonal();
        valeur += 0.1 * coefSituation * Math.abs(valeur);
        eval += valeur;
        heuristicEstimation = eval;
    }

    /** function that returns the value of a row or column
     * (note: works only if WIDTH=HEIGHT)
     * @param ligne true if testing a row, false if testing a column
     * @return value of the possible danger */
    private double possibleDangers(boolean ligne) {
        double result = 0;
        SpecialSituations situation;
        // scanning rows
        StringBuilder strLineB = new StringBuilder();
        for (int i = 0; i < TicTacToe.HEIGHT; i++) {
            strLineB.delete(0, strLineB.length());
            for (int j = 0; j < TicTacToe.WIDTH; j++) {
                int typeJeu = (ligne ? gameGrid[i][j] : gameGrid[j][i]);
                buildSymbol(typeJeu, strLineB);
            }
            try {
                situation = SpecialSituations.valueOf(strLineB.toString());
                result += situation.getValue();
            } catch (Exception _) {}
        }
        return result;
    }

    /** builds a danger symbol:
     * appends a character to a string buffer based on the game type */
    private void buildSymbol(int typeJeu, StringBuilder sb) {
        if (typeJeu == PlayerType.PLAYER.getType()) sb.append('O');
        else if (typeJeu == PlayerType.MACHINE.getType()) sb.append('X');
        else if (typeJeu == 0) sb.append('_');
    }

    /** function that returns the value of a diagonal */
    private double possibleDangersDiagonal() {
        double result = 0;
        SpecialSituations situation;
        StringBuilder strLineB = new StringBuilder();
        buildSymbol(gameGrid[0][0], strLineB);
        buildSymbol(gameGrid[1][1], strLineB);
        buildSymbol(gameGrid[2][2], strLineB);

        try {
            situation = SpecialSituations.valueOf(strLineB.toString());
            result += situation.getValue();
        } catch (Exception _) {}

        strLineB.delete(0, strLineB.length());
        buildSymbol(gameGrid[0][2], strLineB);
        buildSymbol(gameGrid[1][1], strLineB);
        buildSymbol(gameGrid[2][0], strLineB);

        try {
            situation = SpecialSituations.valueOf(strLineB.toString());
            result += situation.getValue();
        } catch (Exception _) {}

        return result;
    }

    /** calculates if 3 tokens are aligned in a row or column
     * @param tj type of player to check for three aligned tokens in a row
     * @param ligne true if testing rows, false if testing columns
     * @return false if no alignment, true if three tokens are aligned */
    boolean threeTokensAligned(PlayerType tj, boolean ligne) {
        boolean contigue = false;
        int valJoueur = tj.getType();
        for (int i = 0; i < TicTacToe.HEIGHT && !contigue; i++) {
            contigue = true;
            for (int j = 0; j < TicTacToe.WIDTH && contigue; j++) {
                int typeJeu = (ligne ? gameGrid[i][j] : gameGrid[j][i]);
                contigue = typeJeu == valJoueur;
            }
        }
        return contigue;
    }

    /** calculates if 3 tokens are aligned diagonally
     * @param tj type of player to check for three aligned tokens in a diagonal
     * @return false if no alignment, true if three tokens are aligned */
    boolean threeTokensAlignedDiagonal(PlayerType tj) {
        boolean contigue = false;
        int valJoueur = tj.getType();
        for (int d = 0; d < 2 && !contigue; d++) {
            contigue = true;
            for (int j = 0; j < TicTacToe.WIDTH && contigue; j++) {
                int i = (d == 0 ? j : 2 - j);
                contigue = gameGrid[i][j] == valJoueur;
            }
        }
        return contigue;
    }

    /** calculates if 3 tokens are aligned in a row, column, or diagonal, regardless of the player
     * @return false if no alignment, true if three tokens are aligned */
    boolean threeTokensAligned() {
        boolean result = threeTokensAligned(PlayerType.PLAYER, false);
        result = result || threeTokensAligned(PlayerType.PLAYER, true);
        result = result || threeTokensAligned(PlayerType.MACHINE, false);
        result = result || threeTokensAligned(PlayerType.MACHINE, true);
        result = result || threeTokensAlignedDiagonal(PlayerType.PLAYER);
        result = result || threeTokensAlignedDiagonal(PlayerType.MACHINE);
        return result;
    }

    /** checks if the situation still has a degree of freedom
     * @return true if it is no longer possible to play */
    boolean isFull() {
        boolean full = true;
        for (int i = 0; i < TicTacToe.HEIGHT && full; i++)
            for (int j = 0; j < TicTacToe.WIDTH && full; j++)
                full = this.gameGrid[i][j] != 0;
        return full;
    }

    /** adds a successor to the current situation
     * @param s successor to the current situation */
    public void addSuccessor(Situation s) {
        successors.add(s);
        s.name = name + "." + s.name;
    }

    /** @return true if the state is a leaf */
    public boolean estFeuille() {
        return isLeaf || close;
    }

    /**
     * @param successors the successors to set
     */
    public void setSuccessors(List<Situation> successors) {
        this.successors = successors;
    }

    /**
     * @param index index of the successor
     * @return the successor at index i
     */
    public Situation getSuccessors(int index) {
        Situation retour = null;
        if (successors != null && index < successors.size())
            retour = successors.get(index);
        return retour;
    }

    /**
     * @return the successors
     */
    public List<Situation> getSuccessors() {
        return successors;
    }

    /**
     * @return the heuristic value h
     */
    public double getHeuristicEstimation() {
        return heuristicEstimation;
    }

    /**
     * @param heuristicEstimation the heuristic value to set
     */
    public void setHeuristicEstimation(double heuristicEstimation) {
        this.heuristicEstimation = heuristicEstimation;
    }

    /**
     * @return the game matrix
     */
    public int[][] getGameGrid() {
        return gameGrid;
    }

    /**
     * @param gameMatrix the game matrix to set
     */
    public void setGameGrid(int[][] gameMatrix) {
        this.gameGrid = gameMatrix;
    }

    /**
     * @return true if the state is in Max mode
     */
    public boolean isMax() {
        return isMax;
    }

    /**
     * @param estMax whether the state is in Max mode
     */
    public void setMax(boolean estMax) {
        this.isMax = estMax;
    }

    public String toString() {
        StringBuilder log = new StringBuilder(" S" + name + ", h=" + heuristicEstimation + ", is max = " + isMax + "---");
        if (!successors.isEmpty()) {
            log.append("\n::::: I have ").append(successors.size()).append(" children :::::");
            for (Situation s : successors)
                log.append(s.name).append("(").append(s.heuristicEstimation).append(") ; ");
        }
        log.append("\n ");
        return log.toString();
    }

    /** displays the matrix associated with the situation on the console */
    public void printMatrix() {
        StringBuilder log = new StringBuilder();
        for (int i = 0; i < TicTacToe.HEIGHT; i++) {
            log.append("\n|");
            for (int j = 0; j < TicTacToe.WIDTH; j++) {
                log.append(gameGrid[i][j]).append("|");
            }
        }
        log.append("\n val=").append(heuristicEstimation).append("\n------------\n");
        System.out.println(log);
    }

    /**
     * @return true if the state is a leaf
     */
    public boolean isLeaf() {
        return isLeaf || close;
    }

    /**
     * @param leaf whether the state is a leaf
     */
    public void setLeaf(boolean leaf) {
        this.isLeaf = leaf;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return true if the state is closed
     */
    public boolean isClose() {
        return close;
    }

    /**
     * @param close whether the state is closed
     */
    public void setClose(boolean close) {
        this.close = close;
    }

    /**
     * @return the row number
     */
    public int getLine() {
        return line;
    }

    /**
     * @param line the row number to set
     */
    public void setLine(int line) {
        this.line = line;
    }

    /**
     * @return the column number
     */
    public int getColumn() {
        return column;
    }

    /**
     * @param column the column number to set
     */
    public void setColumn(int column) {
        this.column = column;
    }
}
