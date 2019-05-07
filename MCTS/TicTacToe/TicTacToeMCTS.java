import java.util.Scanner;

/**
* classe principale permettant de jouer au TicTacToe contre une IA entrainée par MCTS
*/
public class TicTacToeMCTS {
    public static void main(String[] args) {
        var startNode = new MCTSNodeTTT();
        long starTime = System.currentTimeMillis();
        int n = 500000;
        for (int i=0; i<n; i++) {
            startNode.selectAction();
        }
        System.out.println(n + " parties jouées en " + (System.currentTimeMillis() - starTime) + " ms");
        Scanner sc = new Scanner(System.in);
        System.out.println("Je commence..");
        var fin = false;
        var mynode = startNode;
        System.out.println("----------");
        System.out.println(mynode);
        var iaGagne = false;
        var humainGagne = false;
        while (!fin)
        {
            var child = mynode.bestChild();
            while(child==null)
            {
                System.out.println("suite non calculee.... je m'y mets");
                for (int i=0; i<10000; i++) mynode.selectAction();
                child = mynode.bestChild();
            }
            mynode = child;
            System.out.println("je choisis l'action " + mynode.getAction());
            System.out.println(mynode);
            System.out.println("----------");
            fin = iaGagne = mynode.isWinner(1);
            fin = fin || mynode.getNbActions()==0;
            if(fin)break;
            System.out.println("Votre choix ? (0 pour case(0,0), 1 pour case(1,0), 8 pour case (2,2) : ");
            var choix = sc.nextInt();
            var yournode = mynode.findChild(choix);
            while(yournode==null)
            {
                System.out.println("choix non calcule.... je m'y mets");
                for (int i=0; i<10000; i++) mynode.selectAction();
                yournode = mynode.findChild(choix);
            }
            fin = humainGagne = yournode.isWinner(2);
            if(fin)break;
            if(yournode!=null)
            {
                System.out.println("votre jeu ");
                mynode = yournode;
                System.out.println(mynode);
                System.out.println("----------");
            }
        }
        if(fin) {
            System.out.println("fin du jeu");
            if(iaGagne)
                System.out.println("j'ai gagne");
            if(humainGagne)
                System.out.println("vous avez gagne");
        }

    }

}
