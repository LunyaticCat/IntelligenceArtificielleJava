package centralised;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class RandomQueens
{
   /**
    * columns of the queens distributed by row : queen 0 is on the row 0, column
    * queens[0]
    */
   int[] queens;
   /** nb of conflicts by queens */
   int[] conflicts;
   /** queen that has recently moved */
   int movedQueen;
   /** nb of queens */
   int nb;
   /** queens with conflicts */
   ArrayList<Integer> conflictsQueens;
   /**
    * verbose level: 0=only the placement of the queens, 1 = show moves, 2 = show
    * grid
    */
   int verboseLevel = 2;
   /** randomizer used to setup the initial grid and to pickup a queens */
   Random rand;
   /** times spent to found the solutions */
   ArrayList<Integer> times;
   /** nb of moves spent to found the solutions */
   ArrayList<Integer> moves;
   /** memory used to found the solutions */
   ArrayList<Long> mem;
   Runtime runtime;


   RandomQueens(int _nb) {
      nb = _nb;
      queens = new int[nb];
      conflicts = new int[nb];
      movedQueen = -1;
      conflictsQueens = new ArrayList<>();
      times = new ArrayList<>();
      moves = new ArrayList<>();
      mem = new ArrayList<>();
      runtime = Runtime.getRuntime();
      }

   /** distribute the queens randomly */
   void randomize()
   {
      if (rand == null) rand = new Random();
      Arrays.setAll(queens, i -> rand.nextInt(nb));
   }

   /** compute the conflicts for all the queens */
   void computeConflicts()
   {
      Arrays.fill(conflicts, 0);
      for (int i = 0; i < nb; i++)
      {
         int col = queens[i];
         for (int j = i + 1; j < nb; j++)
         {
            if (col == queens[j] || (col - i) == (queens[j] - j) || (col + i) == (queens[j] + j))
            {
               conflicts[i]++;
               conflicts[j]++;
            }
         }
      }
   }

   /** return a list of queens having the maximal conflicts */
   ArrayList<Integer> maxConflictIndices()
   {
      conflictsQueens.clear();
      int max = max(conflicts);
      for (int i = 0; i < nb; i++)
      {
         if (conflicts[i] == max)
         {
            conflictsQueens.add(i);
         }
      }
//      Collections.shuffle(conflictsQueens);

      return conflictsQueens;
   }

   int max(int[] tab)
   {
      int max = tab[0];
      for(int v:tab) if(v>max)max=v;
      return max;
//      OptionalInt max = IntStream.of(tab).max();
//      return max.getAsInt();
   }

   /** move the queen with max conflicts to reduce the nb of conflicts */
   void resolveOneConflict()
   {
      List<Integer> liste = maxConflictIndices();
      int nbConflicts = liste.size();
      int noQueens = rand.nextInt(nbConflicts);
      int queenWithMaxConflicts = liste.get(noQueens);
      if (queenWithMaxConflicts == movedQueen && nbConflicts > 1) 
         queenWithMaxConflicts = liste.get((noQueens+1)%nbConflicts);
      movedQueen = queenWithMaxConflicts;
      int best = searchConflictMinimum(queenWithMaxConflicts);
      if (verboseLevel > 0) System.out.println(
            "queens(" + queenWithMaxConflicts + ") moves from " + queens[queenWithMaxConflicts] + " to " + best);
      queens[queenWithMaxConflicts] = best;
   }

   /** find a column where the queen will have the minimum of conflicts */
   int searchConflictMinimum(int queen)
   {
      int min = Integer.MAX_VALUE;
      int colQ = queens[queen];
      int minC = 0;
      int nbConflicts = 0;
      for (int col = 0; col < nb; col++)
      {
         if (col == colQ) continue;
         nbConflicts = 0;
         for (int j = 0; j < nb; j++)
         {
            if (col != j && (col == queens[j] || (col - queen) == (queens[j] - j) || (col + queen) == (queens[j] + j)))
               nbConflicts++;
         }
         if ((min > nbConflicts) || (min == nbConflicts && rand.nextBoolean()))
         {
            min = nbConflicts;
            minC = col;
         }
      }
      return minC;
   }

   /**
    * launch a cycle resolveOneConflict()-computeConflicts() until the solution is
    * found or until a threshold
    */
   void solve()
   {
      computeConflicts();
      int sum = sumConflicts();
      int i = 0;
      long timeA = System.currentTimeMillis();
      while (sum > 0 && i < nb * nb)
      {
         resolveOneConflict();
         computeConflicts();

         sum = sumConflicts();
         if (movedQueen != -1) conflicts[movedQueen]--;
         i++;
      }
      long timeB = System.currentTimeMillis();
      System.out.println("solution found in " + (timeB - timeA) + " ms for " + nb + " queens..");
      long memory = (runtime.totalMemory() - runtime.freeMemory())/1024L;
      mem.add(memory);
      System.out.println("memory used = " + memory + "ko");
      times.add((int) (timeB - timeA));
      if (movedQueen >= 0) conflicts[movedQueen]++;
      System.out.println("nb of conflicts=" + sumConflicts() + ",  nb of moves= " + i);
      moves.add(i);
      System.out.println("here is the solution : ");
      System.out.println(this);
   }

   /** return the sum of conflicts */
   int sumConflicts()
   {
      int sum = 0;
      for (int c : conflicts) sum += c;
      return sum;
   }

   /** return the chessboard, the pos of the queens and theirs conflicts */
   public String toString()
   {
      StringBuilder sb = new StringBuilder();
      if (verboseLevel > 1)
      {
         for (int i = 0; i < nb; i++)
         {
            for (int j = 0; j < nb; j++)
            {
               if (queens[i] == j)
                  sb.append("X");
               else
                  sb.append("-");
            }
            sb.append("\n");
         }
      }
      sb.append("queens: ");
      for (int i = 0; i < nb; i++)
         sb.append(queens[i]).append(",");
      sb.append("\n");
      sb.append("conflicts: ");
      for (int i = 0; i < nb; i++)
         sb.append(conflicts[i]).append(",");
      return sb.toString();
   }

   /** return the array conflicts as a string */
   String conflictsToString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("conflicts: ");
      for (int i = 0; i < nb; i++)
         sb.append(conflicts[i]).append(",");
      return sb.toString();
   }

   public static void main(String[] args)
   {
      int nb = 1000;
      RandomQueens rq = new RandomQueens(nb);
      rq.verboseLevel = 0;
      int nbTests = 5;
      for (int i = 0; i < nbTests; i++)
      {
         rq.randomize();
         rq.computeConflicts();
         System.out.println("######## test " + i + ", placement des reines au hasard");
         System.out.println(rq);
         rq.solve();
         System.out.println("___________________________________________________________");
         System.gc();
         try { Thread.sleep(100); } catch (InterruptedException e){ }
      }
      IntStream isTime = rq.times.stream().mapToInt(Integer::intValue);
      IntStream isMoves = rq.moves.stream().mapToInt(Integer::intValue);
      LongStream isMem = rq.mem.stream().mapToLong(Long::longValue);
      System.out.println("pour " + nb + " reines, sur " + nbTests + " tests,");
      System.out.println("----  le tps moyen de résolution est " + isTime.average().getAsDouble() + "ms");
      System.out.println("--------  le tps max de résolution est " + Collections.max(rq.times) + "ms");
      System.out.println("--------  le tps min de résolution est " + Collections.min(rq.times) + "ms");
      System.out.println("----  le nb moyen de déplacements est " + isMoves.average().getAsDouble());
      System.out.println("--------  le nb max de déplacements est de " + Collections.max(rq.moves));
      System.out.println("--------  le nb min de déplacements est de " + Collections.min(rq.moves));
      System.out.println("----  la mémoire utilisée est en moyenne de " + isMem.average().getAsDouble() + " Mo");
   }

}
