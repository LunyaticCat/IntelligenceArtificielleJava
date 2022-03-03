package OXO;

import java.util.Scanner;

/**
 * classe principale pour le jeu du TicTacToe (Morpion) en jouant en mode texte contre une IA basee sur le MCTS (MonteCarlo Tree Search)
 * @author emmanueladam
 * */
public class TicTacToeMCTS {
    /**nb de jeux a jouer au debut et entre chaque coups de la personne*/
    static int nbGames = 1000;
    public static void main(String[] args) {
        var startNode = new MCTSNodeTTT();

        long starTime = System.currentTimeMillis();
        for (int i=0; i<nbGames; i++) {
            startNode.selectAction();
        }
        System.out.println(nbGames + " parties jouées en " + (System.currentTimeMillis() - starTime) + " ms");
        System.out.println(startNode);
        System.out.println("Je commence..");
        System.out.println("----------");

        Scanner sc = new Scanner(System.in);
        var fin = false;
        var mynode = startNode;
        var iaGagne = false;
        var humainGagne = false;
        while (!fin)
        {
            var child = mynode.bestChild();
            if(child==null)
            {
                System.out.println("suite non calculee.... je m'y mets");
                mynode.expand();
                child = mynode.bestChild();
            }
            mynode = child;
            System.out.println("je choisis l'action " + mynode.getAction());
            System.out.println(mynode);
            System.out.println("-".repeat(30));
            //on a fini si l'IA a gagne ou s'il n'y a plus d'actions possibles
            fin = iaGagne = mynode.isWinner(MCTSNodeTTT.valeurLigneIA);
            fin = fin || mynode.getNbActions()==0;
            if(!fin) {
                var choix = -1;
                while(choix==-1)
                {
                    System.out.println("Votre choix ? (0 pour case(0,0), 1 pour case(1,0), 8 pour case (2,2) : ");
                    choix = sc.nextInt();
                    if (!mynode.isFree(choix)) choix=-1;
                }
                var yournode = mynode.findChild(choix);
                if (yournode == null) {
                    mynode.expand();
                    yournode = mynode.findChild(choix);
                }
                //on a fini si la personne a gagne
                fin = humainGagne = yournode.isWinner(MCTSNodeTTT.valeurLignePersonne);
                System.out.println("votre jeu ");
                mynode = yournode;
                System.out.println(mynode);
                System.out.println("-".repeat(30));
                //on rejoue quelques partie a partir du noeud courant
                for (int i=0; i<nbGames; i++) {
                    mynode.selectAction();
                }
            }
        }
        System.out.println("fin du jeu");
        if(iaGagne)
            System.out.println("j'ai gagne");
        if(humainGagne)
            System.out.println("vous avez gagne");
    }

}