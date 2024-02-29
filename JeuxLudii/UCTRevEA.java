import game.Game;
import game.equipment.container.Container;
import main.collections.FastArrayList;
import other.AI;
import other.RankUtils;
import other.context.Context;
import other.move.Move;
import other.state.container.ContainerState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A simple example implementation of a standard UCT approach.
 *
 * Only supports deterministic, alternating-move games.
 *
 * @author Dennis Soemers
 */
public class UCTRevEA extends AI
{

    static final int[] coeff = new int[]{500, -150, 30, 10, 10, 30, -150, 500, -150, -250, 0, 0, 0, 0, -250, -150, 30, 0, 1, 2, 2, 1, 0, 30, 10, 0, 2, 16, 16, 2, 0, 10, 10, 0, 2, 16, 16, 2, 0, 10, 30, 0, 1, 2, 2, 1, 0, 30, -150, -250, 0, 0, 0, 0, -250, -150, 500, -150, 30, 10, 10, 30, -150, 500};
    //-------------------------------------------------------------------------

    /** Our player index */
    protected int player = -1;

    //-------------------------------------------------------------------------

    /**
     * Constructor
     */
    public UCTRevEA()
    {
        this.friendlyName = "UCTRevEA";
    }

    //-------------------------------------------------------------------------

    @Override
    public Move selectAction
            (
                    final Game game,
                    final Context context,
                    final double maxSeconds,
                    final int maxIterations,
                    final int maxDepth
            )
    {
        // Start out by creating a new root node (no tree reuse in this example)
        final Node root = new Node(null, null, context);
        // pieces of the board and their positions
/*        var pieces = context.state().owned();
        var  playerPieces = pieces.positions(player);
        System.out.println(Arrays.toString(playerPieces));

        var ct = root.context.components();
        System.out.println("sites = " + Arrays.toString(ct));
        int pl = player;
        System.out.printf("position du joueur %d = %s\n", pl, root.context.state().owned().sites(pl));
        pl = 1;
        System.out.printf("position du joueur %d = %s\n", pl, root.context.state().owned().sites(pl));
        pl = 2;
        System.out.printf("position du joueur %d = %s\n", pl, root.context.state().owned().sites(pl));
        var states = root.context.state().containerStates();
        var state = states[0];
        for(int i=0; i<64; i++){
            System.out.printf("(%d, %d)\t",i, state.stateCell(i));
            if ((i+1)%8==0)System.out.println();
        }

        int i=0;
        for (ContainerState state : states) {
            System.out.println(state.toString());
            System.out.println(i++ + ", "+ state.stateCell(20));
        }*/
//      System.out.println("game.board().numSites() = " +game.board().numSites());

        // We'll respect any limitations on max seconds and max iterations (don't care about max depth)
        final long stopTime = (maxSeconds > 0.0) ? System.currentTimeMillis() + (long) (maxSeconds * 1000L) : Long.MAX_VALUE;
        final int maxIts = (maxIterations >= 0) ? maxIterations : Integer.MAX_VALUE;

        int numIterations = 0;

        // Our main loop through MCTS iterations
        while
        (
                numIterations < maxIts && 					// Respect iteration limit
                        System.currentTimeMillis() < stopTime && 	// Respect time limit
                        !wantsInterrupt								// Respect GUI user clicking the pause button
        )
        {
            // Start in root node
            Node current = root;

            // Traverse tree
            while (true)
            {
                if (current.context.trial().over())
                {
                    // We've reached a terminal state
                    break;
                }

                current = select(current);

                if (current.visitCount == 0)
                {
                    // We've expanded a new node, time for playout!
                    break;
                }
            }

            Context contextEnd = current.context;

            if (!contextEnd.trial().over())
            {
                // Run a playout if we don't already have a terminal game state in node
                contextEnd = new Context(contextEnd);
                game.playout
                        (
                                contextEnd,
                                null,
                                -1.0,
                                null,
                                0,
                                -1,
                                ThreadLocalRandom.current()
                        );
            }

            // This computes utilities for all players at the of the playout,
            // which will all be values in [-1.0, 1.0]
            final double[] utilities = RankUtils.utilities(contextEnd);

            // Backpropagate utilities through the tree
            while (current != null)
            {
                current.visitCount += 1;
                for (int p = 1; p <= game.players().count(); ++p)
                {
                    current.scoreSums[p] += utilities[p];
                }
                current = current.parent;
            }

            // Increment iteration count
            ++numIterations;
        }

        // Return the move we wish to play
        return finalMoveSelection(root);
    }

    /**
     * Selects child of the given "current" node according to UCB1 equation.
     * This method also implements the "Expansion" phase of MCTS, and creates
     * a new node if the given current node has unexpanded moves.
     *
     * @param current
     * @return Selected node (if it has 0 visits, it will be a newly-expanded node).
     */
    public static Node select(final Node current)
    {
        if (!current.unexpandedMoves.isEmpty())
        {
            // randomly select an unexpanded move
            final Move move = current.unexpandedMoves.remove(
                    ThreadLocalRandom.current().nextInt(current.unexpandedMoves.size()));

            // create a copy of context
            final Context context = new Context(current.context);
            var r = context.region();

            // apply the move
            context.game().apply(context, move);

            // create new node and return it
            return new Node(current, move, context);
        }

        // use UCB1 equation to select from all children, with random tie-breaking
        Node bestChild = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        final double twoParentLog = 2.0 * Math.log(Math.max(1, current.visitCount));
        int numBestFound = 0;

        final int numChildren = current.children.size();
        final int mover = current.context.state().mover();

        for (int i = 0; i < numChildren; ++i)
        {
            final Node child = current.children.get(i);
            final double exploit = child.scoreSums[mover] / child.visitCount;

            final double explore = getExplorationPreference(child); //Math.sqrt(twoParentLog / child.visitCount);

            final double ucb1Value = exploit + explore;

            if (ucb1Value > bestValue)
            {
                bestValue = ucb1Value;
                bestChild = child;
                numBestFound = 1;
            }
            else if
            (
                    ucb1Value == bestValue &&
                            ThreadLocalRandom.current().nextInt() % ++numBestFound == 0
            )
            {
                // this case implements random tie-breaking
                bestChild = child;
            }
        }

        return bestChild;
    }

    static double getExplorationPreference(final Node node){
        int player = node.context.player();
        player = 1;
        var states = node.context.state().containerStates()[0];
        int sum = 0;
        for(int i=0; i<64; i++){
            if (states.stateCell(i)==player) sum+=coeff[i];
        }
//        System.out.printf("sum pour la situation %s = %d\t",state, sum);
        return ((double)sum)/500.0;
    }



    /**
     * Selects the move we wish to play using the "Robust Child" strategy
     * (meaning that we play the move leading to the child of the root node
     * with the highest visit count).
     *
     * @param rootNode
     * @return
     */
    public static Move finalMoveSelection(final Node rootNode)
    {
        Node bestChild = null;
        int bestVisitCount = Integer.MIN_VALUE;
        int numBestFound = 0;

        final int numChildren = rootNode.children.size();

        for (int i = 0; i < numChildren; ++i)
        {
            final Node child = rootNode.children.get(i);
            final int visitCount = child.visitCount;

            if (visitCount > bestVisitCount)
            {
                bestVisitCount = visitCount;
                bestChild = child;
                numBestFound = 1;
            }
            else if
            (
                    visitCount == bestVisitCount &&
                            ThreadLocalRandom.current().nextInt() % ++numBestFound == 0
            )
            {
                // this case implements random tie-breaking
                bestChild = child;
            }
        }

        return bestChild.moveFromParent;
    }

    @Override
    public void initAI(final Game game, final int playerID)
    {
        this.player = playerID;
    }

    @Override
    public boolean supportsGame(final Game game)
    {
        if (game.isStochasticGame())
            return false;

        if (!game.isAlternatingMoveGame())
            return false;

        return true;
    }

    //-------------------------------------------------------------------------

    /**
     * Inner class for nodes used by example UCT
     *
     * @author Dennis Soemers
     */
    private static class Node
    {
        /** Our parent node */
        private final Node parent;

        /** The move that led from parent to this node */
        private final Move moveFromParent;

        /** This objects contains the game state for this node (this is why we don't support stochastic games) */
        private final Context context;

        /** Visit count for this node */
        private int visitCount = 0;

        /** For every player, sum of utilities / scores backpropagated through this node */
        private final double[] scoreSums;

        /** Child nodes */
        private final List<Node> children = new ArrayList<Node>();

        /** List of moves for which we did not yet create a child node */
        private final FastArrayList<Move> unexpandedMoves;

        /**
         * Constructor
         *
         * @param parent
         * @param moveFromParent
         * @param context
         */
        public Node(final Node parent, final Move moveFromParent, final Context context)
        {
            this.parent = parent;
            this.moveFromParent = moveFromParent;
            this.context = context;
            final Game game = context.game();
            scoreSums = new double[game.players().count() + 1];

            // For simplicity, we just take ALL legal moves.
            // This means we do not support simultaneous-move games.
            unexpandedMoves = new FastArrayList<Move>(game.moves(context).moves());

            if (parent != null)
                parent.children.add(this);
        }

    }

    //-------------------------------------------------------------------------

}