import app.StartDesktopApp;
import manager.ai.AIRegistry;

/**Launch Ludii Application, adding specific AI implementations*/
public class TestLudii {

    public static void main(final String[] args)
    {
        // AI playing randomly
        String label = "EAAI";
        if (!AIRegistry.registerAI(label, MonAgentLudique::new, (game) -> {return true;}))
            System.err.println("WARNING! Failed to register "+label+" because one with that name already existed!");
        // UCT Algorithm proposed by Soemers
        label = "UCTSoemers";
        if (!AIRegistry.registerAI(label, UCTSoemers::new, (game) -> {return true;}))
            System.err.println("WARNING! Failed to register "+label+" because one with that name already existed!");
        // Run Ludii
        StartDesktopApp.main(new String[0]);
    }
}
