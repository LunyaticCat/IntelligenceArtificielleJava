package algo;

import java.util.List;

import model.Situation;

/**
 * Resolution class, it contains the alpha-beta algorithm.
 */
public final class Resolution {
    private Resolution() {
    }

    /**
     * Alpha-beta function, determines the value of the node/situation.
     * To retrieve the situation to take, scan the list of successors
     * and take the first situation with the same estimate.
     *
     * @param node situation, state
     * @param alpha minimum bound
     * @param beta maximum bound
     * @return estimate of the situation based on the opponent's play
     */
    public static int alphaBeta(final Situation node, final int alpha, final int beta) {
        int bound = 0;
        Thread.yield();

        if (node.isLeaf()) {
            bound = node.getHeuristic();
        }

        if (!node.isLeaf()) {
            final List<Situation> successors = node.getSuccessors();
            bound = node.isMax() ? alpha : beta;
            int i = 0;
            boolean found = false;

            while (i < successors.size() && !found) {
                final Situation successor = successors.get(i);
                final int value = node.isMax() ? alphaBeta(successor, bound, beta) : alphaBeta(successor, alpha, bound);
                successor.setHeuristic(value);

                if (node.isMax() && value > bound) {
                    bound = value;
                    found = (bound >= beta);
                }

                if (!node.isMax() && value < bound) {
                    bound = value;
                    found = (bound <= alpha);
                }
                i++;
            }
        }
        return bound;
    }
}
