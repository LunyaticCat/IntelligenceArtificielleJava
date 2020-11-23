import java.util.LinkedList;

/**
 * abstract class to implent for using the DeepAndBreadth search algorithm
 * @author emmanueladam
 * */
public abstract class State {

    /** state is a leaf (impossible to reach other state)*/
    boolean leaf = false;
    /** state is correct or not*/
    boolean ok = true;
    /** state is a success or not*/
    boolean success = false;
    /**parent of the state*/
    State parent;

    /** return the path from start node to this node*/
    public LinkedList<State> rebuildPath()
    {
        var l = new LinkedList<State>();
        State s = this;
        while (s!=null)
        {
            l.addFirst(s);
            s = s.getParent();
        }
        return l;
    }

    /**check is the state is a leaf*/
    abstract void checkLeaf();

    /**return the states reachable from the current state*/
    public abstract LinkedList<State> nextStates();

    /**check is the state a success (= a leaf without conflict)*/
    public abstract boolean checkState();

    public abstract boolean isSuccess();
    public abstract boolean isLeaf();
    public State getParent() { return parent; }
    public void setParent(State parent) { this.parent = parent; }

}
