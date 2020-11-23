import java.util.LinkedList;

/**
 * this class contains a generic algo for a "classic" search
 * using free nodes and closed ones
 * @author emmanueladam
 * */
public class DeepAndBreadthSearch {

    /**generic algorithm
     * @param s0 the initial state
     * @param breadth true for a breath search, false for a deep search
     * @return a solution state, or null */
    public static State solve(State s0, boolean breadth)
    {
        State solution=null;
        var freeNodes = new LinkedList<State>();
        var closedNodes = new LinkedList<State>();
        freeNodes.add(s0);
        var success = false;
        while (!freeNodes.isEmpty() && !success)
        {
            var node = freeNodes.removeFirst();
            closedNodes.add(node);
            var nodes = node.nextStates();
            for (State s:nodes)
            {
                if (!freeNodes.contains(s) && !closedNodes.contains(s)) {
                    s.setParent(node);
                    success = success || s.isSuccess();
                    if(success) solution = s;
                    if(breadth) freeNodes.addLast(s);
                    else freeNodes.addFirst(s);
                }
            }
        }
        return solution;
    }
}
