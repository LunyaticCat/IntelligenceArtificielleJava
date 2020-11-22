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


    /**check is the state is a leaf*/
    abstract void checkLeaf();

    /**return the states reachable from the current state*/
    public abstract LinkedList<State> nextStates();

    /**check is the state a success (= a leaf without conflict)*/
    public abstract void checkState();

    public abstract boolean isSuccess();
    public abstract boolean isLeaf();

}
