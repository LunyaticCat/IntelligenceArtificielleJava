import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.IntVar;


public class NQueens {

    static int[] nQueens_1(int n)
    {
        Model model = new Model("probleme des "+ n + " reines");
        IntVar[] colonnes = model.intVarArray("Reine", n, 1, n, false);

        for(int i  = 0; i < n-1; i++){
            for(int j = i + 1; j < n; j++){
                colonnes[i].ne(colonnes[j]).post();
                colonnes[i].ne(colonnes[j].sub(j - i)).post();
                colonnes[i].ne(colonnes[j].add(j - i)).post();
            }
        }
        System.out.println("nb contraintes = "+ model.getCstrs().length);
        Solution solution = model.getSolver().findSolution();
        if(solution != null){
            System.out.println(solution);
            int[] res = new int[n];
            for(int i=0; i<n; i++){
                res[i] = solution.getIntVal(colonnes[i]);
            }
            return res;
        }
        return null;

    }

    /**
     * draw the board with the queens
     */
    private static void drawBoard(int[] queens) {
        int n = queens.length;
        for (int queen : queens) {
            for (int j = 0; j < n; j++) {
                if (queen == j) {
                    System.out.print(" Q ");
                } else {
                    System.out.print(" . ");
                }
            }
            System.out.println();
        }
    }

    // Programme principal
    public static void main(String[] args) {
        int n = 8;
        int[] positions = nQueens_1(n);

        if (n<25 && positions != null) {
                drawBoard(positions);
            }
    }


}