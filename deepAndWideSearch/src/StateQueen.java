import java.util.Arrays;
import java.util.LinkedList;

/**
 * class to illustrate the n-queens problem resolved by deep and bread search
 * @author emmanueladam
 * */
public class StateQueen extends State{
    /** size of the chessboard*/
    static int nb=8;
    /** column taken by the queens (chess[0] is the column taken by the queen in line 0,
     * -1 if the queen has not been placed)*/
    int[] chess;
    /** no of the queen linked to the state*/
    int noQueen=-1;


    /**create a chessboard (an unary array) filled with -1 (no queen is placed)*/
    StateQueen() {
        chess = new int[nb];
        Arrays.fill(chess, -1);
    }

    /**
     * @param no indice of queen (and so its line)
     * @param placedQueens an array that contains the colones of the queens < no
     * @param col the colone of the queen*/
    private StateQueen(int no, int[] placedQueens, int col) {
        this();
        noQueen = no;
        System.arraycopy(placedQueens, 0, chess, 0, no);
        chess[no] = col;
    }


    /**
     * two states are equals iff the chess contains the same values
     * */
    @Override
    public boolean equals(Object o)
    {
        boolean eq = false;
        if(o!=null && o.getClass().equals(this.getClass()))
        {
            StateQueen other = (StateQueen)o;
            eq = Arrays.equals(chess, other.chess);
        }
        return eq;
    }

    /**check if all the queens are placed without conflicts*/
    private void checkOk()
    {
        ok = true;
        for (int i = 0; i < nb && ok; i++)
        {
            int col = chess[i];
            if(col!=-1)
                for (int j = i + 1; j < nb && ok; j++)
                    if(chess[j]!=-1)
                        if (col == chess[j] || (col - i) == (chess[j] - j) || (col + i) == (chess[j] + j))
                            ok = false;
        }
    }


    /**check is the state is a leaf (no more place to put a queen*/
    void checkLeaf()
    {
        leaf = true;
        for (int i=0; i<nb && leaf; i++)
            leaf = chess[i]!=-1;
    }

    /**check is the state a success (= a leaf without conflict)*/
    public void checkState()
    {
        success = ok && leaf;

    }

    /**compute the possible states for the next queen
     * @return a list of state where the next queen can be placed without conflict */
    public LinkedList<State> nextStates()
    {
        var list = new LinkedList<State>();
        if(noQueen<nb-1) {
            for (int i = 0; i < nb; i++) {
                StateQueen s = new StateQueen(noQueen + 1, chess, i);
                s.checkOk();
                if(s.ok)
                {
                    s.checkLeaf();
                    s.checkState();
                    list.addLast(s);
                }
            }
        }
        return list;
    }


    public boolean isSuccess()
    {
        return success;
    }

    @Override
    public boolean isLeaf() {
        return leaf;
    }

    public String toString()
    {

        return "I'm queen " + noQueen + ", col of the queens = " + Arrays.toString(chess) + ", leaf=" + leaf;
    }



    /**
     * launch the resolution of the n-queens problem
     * */
    public static void main(String ...args)
    {
        StateQueen.nb = 10;
        System.out.println("recherche du pb des " + StateQueen.nb + " reines, parcours en profondeur ");
        System.out.println("patientez env. 15 ms pour 10 reines.");
        long debut = System.currentTimeMillis();
        DeepAndBreadthSearch.solve(new StateQueen(), false);
        long fin = System.currentTimeMillis();
        System.out.println("temps passe en profondeur = " + (fin - debut) + " ms");

        System.out.println("-".repeat(30));

        System.out.println("recherche du pb des " + StateQueen.nb + " reines, parcours en largeur ");
        System.out.println("patientez env. 10 sec pour 10 reines.");
        debut = System.currentTimeMillis();
        DeepAndBreadthSearch.solve(new StateQueen(), true);
        fin = System.currentTimeMillis();
        System.out.println("temps passe en largeur = " + (fin - debut) + " ms");
    }

}
