package algo;

import java.util.ArrayList;
import model.Situation;

/**
 * Resolution class, it contains the alpha-beta algorithm.
 * @author emmanuel adam
 */
public class Resolution {

    /**
     * Alpha-beta function, determines the value of the node/situation s.
     * To retrieve the situation to take, scan the list of successors of s
     * and take the first situation with the same estimate.
     * @param s situation, state
     * @param alpha minimum bound
     * @param beta maximum bound
     * @return estimate of the situation s based on the opponent's play
     */
    public static double alphaBeta(Situation s, double alpha, double beta) {
        double bound, value;
        if (s.isLeaf() || s.isClose()) {
            return s.getH();
        }

        if (s.isMax()) {
            ArrayList<Situation> successors = s.getSuccessors();
            bound = alpha;
            for (Situation successor : successors) {
                value = alphaBeta(successor, bound, beta);
                successor.setH(value);
                if (value > bound) {
                    bound = value;
                }
                if (value >= beta) {
                    return bound;
                }
            }
        } else {
            ArrayList<Situation> successors = s.getSuccessors();
            bound = beta;
            for (Situation successor : successors) {
                value = alphaBeta(successor, alpha, bound);
                successor.setH(value);
                if (value < bound) {
                    bound = value;
                }
                if (value <= alpha) {
                    return bound;
                }
            }
        }
        return bound;
    }
}
