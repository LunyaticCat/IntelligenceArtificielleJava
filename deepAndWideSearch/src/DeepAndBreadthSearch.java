import java.util.LinkedList;

/**
 * this class contains a generic algo for a "classic" search
 * using free nodes and closed ones
 * @author emmanueladam
 * */
public class DeepAndBreadthSearch {

    /**generic algorithm
     * @param s0 the initial state
     * @param breadth true for a breath search, false for a deep search*/
    public static void solve(State s0,boolean breadth)
    {
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
                    success = success || s.isSuccess();
                    if(breadth) freeNodes.addLast(s);
                    else freeNodes.addFirst(s);
                }
            }
        }
        if(success)
        {
            System.out.println("success !!");
            freeNodes.forEach(s ->  {if (s.isSuccess()) System.out.println(s);});
        }
        else  System.out.println("Echec !!");
        System.out.println("nb de noeuds libres : " + freeNodes.size());
        System.out.println("nb de noeuds clos : " + closedNodes.size());

    }
}
