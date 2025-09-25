package model;
import java.util.ArrayList;

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
    /** instance number */
    private int noInstances;
    /** row number */
    private int line;
    /** column number */
    private int column;
    /** indicates if the state is in Max mode */
    private boolean max = true;
    /** list of states accessible from the current state */
    private ArrayList<Situation> successors;
    /** indicates if the state/situation is a leaf of the tree */
    private boolean leaf;
    /** indicates if the state/situation is a definitive leaf of the tree
     * (i.e., it is impossible to create successors for this situation) */
    private boolean close;
    /** h = heuristic, estimation of the situation's value */
    private double h;
    /** game grid corresponding to the situation */
    private int[][] gameGrid;

    /** default constructor */
    public Situation() {
        noInstances = nbInstances++;
        name = "" + noInstances;
        h = 0;
        gameGrid = new int[3][3];
        successors = new ArrayList<>();
    }

    /** constructor initializing the heuristic estimate h
     * @param _h estimation of the situation */
    public Situation(int _h) {
        this();
        h = _h;
    }

    /** constructor initializing the heuristic estimate h and the node type
     * @param _h estimation of the situation
     * @param _estMax determines if the situation's value should be maximized or not */
    public Situation(int _h, boolean _estMax) {
        this(_h);
        max = _estMax;
    }

    /** constructor initializing the heuristic estimate h, the node type, and the node position
     * @param _h estimation of the situation
     * @param _estMax determines if the situation's value should be maximized or not
     * @param _estFeuille determines if the situation is final in the tree */
    public Situation(int _h, boolean _estMax, boolean _estFeuille) {
        this(_h, _estMax);
        leaf = _estFeuille;
    }

    /** function evaluating the current situation; calculates 'h' */
    void evaluate() {
        double eval = 0d;
        double coefSituation = (this.max ? -1 : 1);
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
        h = eval;
    }

    /** function that returns the value of a row or column
     * (note: works only if WIDTH=HEIGHT)
     * @param ligne true if testing a row, false if testing a column
     * @return value of the possible danger */
    private double possibleDangers(boolean ligne) {
        double result = 0;
        SpecialSituations situation;
        // scanning rows
        StringBuffer strLineB = new StringBuffer();
        for (int i = 0; i < TicTacToe.HEIGHT; i++) {
            strLineB.delete(0, strLineB.length());
            for (int j = 0; j < TicTacToe.WIDTH; j++) {
                int typeJeu = (ligne ? gameGrid[i][j] : gameGrid[j][i]);
                buildSymbol(typeJeu, strLineB);
            }
            try {
                situation = SpecialSituations.valueOf(strLineB.toString());
                result += situation.getValue();
            } catch (Exception e) {}
        }
        return result;
    }

    /** builds a danger symbol:
     * appends a character to a string buffer based on the game type */
    private void buildSymbol(int typeJeu, StringBuffer sb) {
        if (typeJeu == PlayerType.PLAYER.getType()) sb.append('O');
        else if (typeJeu == PlayerType.MACHINE.getType()) sb.append('X');
        else if (typeJeu == 0) sb.append('_');
    }

    /** function that returns the value of a diagonal */
    private double possibleDangersDiagonal() {
        double result = 0;
        SpecialSituations situation;
        StringBuffer strLineB = new StringBuffer();
        buildSymbol(gameGrid[0][0], strLineB);
        buildSymbol(gameGrid[1][1], strLineB);
        buildSymbol(gameGrid[2][2], strLineB);
        try {
            situation = SpecialSituations.valueOf(strLineB.toString());
            result += situation.getValue();
        } catch (Exception e) {}
        strLineB.delete(0, strLineB.length());
        buildSymbol(gameGrid[0][2], strLineB);
        buildSymbol(gameGrid[1][1], strLineB);
        buildSymbol(gameGrid[2][0], strLineB);
        try {
            situation = SpecialSituations.valueOf(strLineB.toString());
            result += situation.getValue();
        } catch (Exception e) {}
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
                contigue = contigue && (typeJeu == valJoueur);
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
                contigue = contigue && (gameGrid[i][j] == valJoueur);
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
                full = full && (this.gameGrid[i][j] != 0);
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
        return leaf || close;
    }

    /**
     * @param successors the successors to set
     */
    public void setSuccessors(ArrayList<Situation> successors) {
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
    public ArrayList<Situation> getSuccessors() {
        return successors;
    }

    /**
     * @return the heuristic value h
     */
    public double getH() {
        return h;
    }

    /**
     * @param h the heuristic value to set
     */
    public void setH(double h) {
        this.h = h;
    }

    /**
     * @return the game matrix
     */
    public int[][] getGameGrid() {
        return gameGrid;
    }

    /**
     * @param matriceJeu the game matrix to set
     */
    public void setGameGrid(int[][] matriceJeu) {
        this.gameGrid = matriceJeu;
    }

    /**
     * @return true if the state is in Max mode
     */
    public boolean isMax() {
        return max;
    }

    /**
     * @param estMax whether the state is in Max mode
     */
    public void setMax(boolean estMax) {
        this.max = estMax;
    }

    public String toString() {
        String retour = " S" + name + ", h=" + h + ", is max = " + max + "---";
        if (!successors.isEmpty()) {
            retour += "\n::::: I have " + successors.size() + " children :::::";
            for (Situation s : successors)
                retour += s.name + "(" + s.h + ") ; ";
        }
        retour += "\n ";
        return retour;
    }

    /** displays the matrix associated with the situation on the console */
    public void printMatrix() {
        String retour = "";
        for (int i = 0; i < TicTacToe.HEIGHT; i++) {
            retour += "\n|";
            for (int j = 0; j < TicTacToe.WIDTH; j++) {
                retour += gameGrid[i][j] + "|";
            }
        }
        retour += "\n val=" + h + "\n------------\n";
        System.out.println(retour);
    }

    /**
     * @return true if the state is a leaf
     */
    public boolean isLeaf() {
        return leaf || close;
    }

    /**
     * @param leaf whether the state is a leaf
     */
    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
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
