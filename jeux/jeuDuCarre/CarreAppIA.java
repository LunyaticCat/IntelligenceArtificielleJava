import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Classe de base pour le jeu du Carre en Java FX
 * Petite IA qui cherche juste à placer des carres et à eviter que l'adversaire en fasse
 * et qui joue au hasard dans les autres cas
 *
 * @author emmanueladam*/
public class CarreAppIA extends CarreApp {

    /**
     * lancement automatique de l'application graphique
     */
    public void start(Stage primaryStage) {
        traitsHorizontaux = new TraitGr[nbCases + 1][nbCases];
        traitsVerticaux = new TraitGr[nbCases][nbCases + 1];
        carres = new int[nbCases][nbCases];
        construirePlateauJeu(primaryStage);
        menuAPropos.setOnAction(e -> {
            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("A propos");
            dialog.setContentText("Base pour le jeu du carre avec \"mini IA\". \n" +
                    "A etendre pour ajouter l'algo alpha-beta et.ou MCTS\n" +
                    "(c) Emmanuel ADAM");
            dialog.show();
        });
    }


    /**
     * ajoute des traits verticaux et horizontaux cliquables à la troupe et leur ajoute une écoute d'événement souris
     */
    public void ajoutTrait(TraitGr trait) {
        int i = trait.i;
        int j = trait.j;
        if (trait.horizontal) traitsHorizontaux[j][i] = trait;
        else traitsVerticaux[j][i] = trait;
        troupe.getChildren().add(trait);
        trait.setOnMouseClicked(me -> {
            if (verbose > 0) System.out.println(("trait en j,j=" + j + "," + i + "horizontal = " + trait.horizontal));
            boolean carreEffectue = placerTrait(trait);
            if (!carreEffectue) jeuIA();
        });
    }

    /**placement d'un trait par un joueur
     * @return  true si un carre a ete effectue avec ce trait*/
    private boolean placerTrait(TraitGr trait) {
        int i = trait.i;
        int j = trait.j;
        boolean carreEffectue = false;
        trait.setEtat(joueur);
        if (verbose > 0) System.out.println("joueur " + joueur + " clic sur moi en " + i + "," + j);
        int coefi, coefj;
        if (trait.horizontal) {
            coefi = 0;
            coefj = 1;
        } else {
            coefi = 1;
            coefj = 0;
        }
        for (int xx = -1; xx <= 0; xx++) {
            int ii = (i + coefi * xx);
            int jj = (j + coefj * xx);
            if ((ii >= 0 && jj >= 0) && (ii < nbCases && jj < nbCases)) {
                carres[ii][jj]++;
                if (carres[ii][jj] == 4) {
                    if (verbose > 0) System.out.println("carre effectue en " + ii + "," + jj);
                    carreEffectue = true;
                    dessinCroix(ii, jj);
                }
            }
        }
        return carreEffectue;
    }


    /**
     * mini ia qui consiste a creer des cases si possible, jouer au hasard sinon..
     * */
    private void jeuIA() {
        if (verbose > 0) System.out.println("jeu de l'ia");
        joueur = Etat.J2;
        int[] coord = new int[2];
        boolean trouve = trouveCaseGagnante(coord);
        if (trouve) {
            creerCarreIA(coord[0], coord[1]);
            jeuIA();
        } else {
            TraitGr trait = trouveTraitNonPerdant();
            placerTrait(trait);
            joueur = Etat.J1;
        }
    }

    /**cherche si un case est possible autour de la coordonnne
     * @param coord coordonnee [x,y]
     * @return true si une case est possible*/
    private boolean trouveCaseGagnante(int[] coord) {
        boolean trouve = false;
        int i = 0, j = 0;
        while (i < nbCases && !trouve) {
            j = 0;
            while (j < nbCases && !trouve) {
                trouve = (carres[i][j] == 3);
                j++;
            }
            i++;
        }
        if (trouve) {
            coord[0] = i - 1;
            coord[1] = j - 1;
        }
        return trouve;
    }

    /**
     * @return un trait qui ne permettra pas a l'adversaire de faire une case
     * */
    private TraitGr trouveTraitNonPerdant() {
        TraitGr trait = null;
        int nbTraits = 1;
        ArrayList<TraitGr> traitsLibres = new ArrayList<>();
        ArrayList<TraitGr> traits = null;
        if (Math.random() < 0.5) nbTraits = 0;
        for (int i = 0; i < nbCases; i++)
            for (int j = 0; j < nbCases; j++) {
                if (carres[i][j] == nbTraits) {
                    traits = rechercheTraitLibre(i, j);
                    traitsLibres.removeAll(traits);
                    traitsLibres.addAll(traits);
                }
            }
        Collections.shuffle(traitsLibres);
        trait = traitsLibres.get(0);
        return trait;
    }

    /**@return une liste de traits possibles*/
    private ArrayList<TraitGr> rechercheTraitLibre(int i, int j) {
        ArrayList<TraitGr> traitsLibres = new ArrayList<>();
        if (traitsHorizontaux[j][i].etat == Etat.LIBRE) traitsLibres.add(traitsHorizontaux[j][i]);
        if (traitsHorizontaux[j + 1][i].etat == Etat.LIBRE) traitsLibres.add(traitsHorizontaux[j + 1][i]);
        if (traitsVerticaux[j][i].etat == Etat.LIBRE) traitsLibres.add(traitsVerticaux[j][i]);
        if (traitsVerticaux[j][i + 1].etat == Etat.LIBRE) traitsLibres.add(traitsVerticaux[j][i + 1]);
        Collections.shuffle(traitsLibres);
        return traitsLibres;
    }

    /**
     * lorsque l'ia a fait un carre grace a un trait en i,j, elle regarde autour si elle peut en faire plus
     * */
    private void creerCarreIA(int i, int j) {
        boolean trouve;
        if (verbose > 0) System.out.println("carre possible en " + i + "," + j);
        TraitGr[] traits = {traitsHorizontaux[j][i], traitsHorizontaux[j + 1][i], traitsVerticaux[j][i], traitsVerticaux[j][i + 1]};
        trouve = false;
        int noTraits = 0;
        while (noTraits < 4 && !trouve) trouve = (traits[noTraits++].etat == Etat.LIBRE);
        if (trouve) {
            noTraits--;
            TraitGr trait = traits[noTraits];
            placerTrait(trait);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
