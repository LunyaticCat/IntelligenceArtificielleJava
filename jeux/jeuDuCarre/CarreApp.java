import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Classe de base pour le jeu du Carre en Java FX
 * Les joueurs placent un trait à tour de rôle
 * L'application détecte si un carre est effectué
 *
 * @author emmanueladam
 */
public class CarreApp extends Application {
    /**
     * taille d'une case en pixels
     */
    final int tailleCase = 60;
    /**
     * marge du dessin par rapport aux bords de la fenetre
     */
    final int decalageTrait = 5;
    /**
     * nb de cases sur un cote
     */
    final int nbCases = 6;
    /**
     * Matrice des dessins des carres
     */
    int[][] carres;
    /**
     * Matrice des dessins des traits
     */
    TraitGr[][] traitsHorizontaux;
    /**
     * Matrice des dessins des traits
     */
    TraitGr[][] traitsVerticaux;
    /**
     * joueur qui a la main
     */
    static Etat joueur = Etat.J1;
    /**
     * troupe des éléments graphiques sur la fenêtre (traits, ...)
     */
    Group troupe;
    /**
     * la scene
     */
    Scene scene;
    /**
     * menu d'explication de l'application
     * */
    MenuItem menuAPropos;
    /**niveau d'elocution (0=pas d'affichage texte)*/
    int verbose = 0;


    /**
     * lancement automatique de l'application graphique
     */
    public void start(Stage primaryStage) {
        traitsHorizontaux = new TraitGr[nbCases + 1][nbCases];
        traitsVerticaux = new TraitGr[nbCases][nbCases + 1];
        carres = new int[nbCases][nbCases];
        construirePlateauJeu(primaryStage);
    }

    /**
     * construction du théatre et de la scène
     */
    void construirePlateauJeu(Stage primaryStage) {
        //definir la scene principale
        troupe = new Group();
        scene = new Scene(troupe, tailleCase + nbCases * tailleCase + tailleCase, tailleCase + nbCases * tailleCase, Color.ANTIQUEWHITE);
        primaryStage.setTitle("Jeu du carré...");
        primaryStage.setScene(scene);
        //definir les acteurs et les habiller
        dessinEnvironnement();
        MenuBar menuBar = new MenuBar();

        // --- Menu Couleurs
        Menu menuCouleurs = new Menu("Couleurs");
        MenuItem menuChoixCouleurs = new MenuItem("Changer les couleurs");
        // si action sur l'item alors lancer la fonction choixCouleurs
        menuChoixCouleurs.setOnAction((ActionEvent t) -> {
            choixCouleurs(primaryStage);
        });
        menuCouleurs.getItems().add(menuChoixCouleurs);

        // --- Menu Aide
        Menu menuHelp = new Menu("Aide");
        menuAPropos = new MenuItem("A propos");
        menuAPropos.setOnAction(e->{
            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("A propos");
            dialog.setContentText("Base pour le jeu du carre pour 2 joueur. \n" +
                    "A etendre pour ajouter l'algo alpha-beta et.ou MCTS\n" +
                    "(c) Emmanuel ADAM");
            dialog.show();});
        menuHelp.getItems().add(menuAPropos);
        menuBar.getMenus().addAll(menuCouleurs, menuHelp);
        troupe.getChildren().addAll(menuBar);
        //afficher le theatre
        primaryStage.show();
    }

    /**
     * fonction qui lance une fenetre modale definie en fxml par JavaFX Scene
     * Builder
     *
     * @param primaryStage theatre de l'application
     */
    void choixCouleurs(Stage primaryStage) {
        String sceneFile = "/ChoixCouleursGui.fxml";
        URL url = null;
        AnchorPane page = null;
        FXMLLoader fxmlLoader = null;
        try {
            url = getClass().getResource(sceneFile);
            fxmlLoader = new FXMLLoader(url);
            page = fxmlLoader.load();
            System.out.println("  fxmlResource = " + sceneFile);
        } catch (Exception ex) {
            System.out.println("Exception on FXMLLoader.load()");
            System.out.println("  * url: " + url);
            System.out.println("  * " + ex);
            System.out.println("    ----------------------------------------\n");
        }

        // si le chargement a reussi
        if (primaryStage != null) {
            // creation d'un petit theatre pour la fenetre de choix de couleurs
            Stage dialogStage = new Stage();

            dialogStage.setTitle("Jeu du Carre...Couleurs...");
            // fenetre modale, obligation de quitter pour revenir a la fenetre
            // principale
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene miniScene = new Scene(page);
            dialogStage.setScene(miniScene);
            // recuperation du controleur associe a la fenetre
            ChoixCouleursControleur controller = fxmlLoader.getController();
            controller.setDialogStage(dialogStage);
            controller.setAppliLink(this);
            // affichage de la fenetre
            dialogStage.showAndWait();
        }

    }

    /**
     * creation des cellules et de leurs habits
     */
    void dessinEnvironnement() {
        int decalage = tailleCase / 2;
        TraitGr trait = null;
        // dessin des lignes
        for (int i = 0; i <= nbCases; i++) {
            for (int j = 0; j <= nbCases; j++) {
                if (j < nbCases) {
                    trait = new TraitGr(decalage + tailleCase * i, decalage + decalageTrait + tailleCase * j, decalage + tailleCase * i, decalage - decalageTrait + tailleCase * (j + 1), false, i, j);
                    ajoutTrait(trait);
                }
                if (i < nbCases) {
                    trait = new TraitGr(decalage + decalageTrait + tailleCase * i, decalage + tailleCase * j, decalage - decalageTrait + tailleCase * (i + 1), decalage + tailleCase * j, true, i, j);
                    ajoutTrait(trait);
                }
            }
        }
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
            boolean carreEffectue = false;
            trait.setEtat(CarreApp.joueur);
            if(verbose>0) System.out.println("joueur " + joueur + " clic sur moi en " + i + "," + j);
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
                        if(verbose>0) System.out.println("carre effectue en " + ii + "," + jj);
                        carreEffectue = true;
                        dessinCroix(ii, jj);
                    }
                }
            }
            if (!carreEffectue) CarreApp.changeJoueur();
        });
    }

    /**
     * dessine une croix dans la case i,j
     */
    void dessinCroix(int i, int j) {
        int decalage = tailleCase / 2;
        TraitGr trait = new TraitGr(decalage + decalageTrait + tailleCase * i, decalage + decalageTrait + tailleCase * j, decalage - decalageTrait + tailleCase * (i + 1), decalage - decalageTrait + tailleCase * (j + 1));
        trait.setEtat(CarreApp.joueur);
        troupe.getChildren().add(trait);
        trait = new TraitGr(decalage - decalageTrait + tailleCase * (i + 1), decalage + decalageTrait + tailleCase * j, decalage + decalageTrait + tailleCase * i, decalage - decalageTrait + tailleCase * (j + 1));
        trait.setEtat(CarreApp.joueur);
        troupe.getChildren().add(trait);

    }


    /**
     * suite au changement de couleur, demande la recoloration des pions
     */
    public void recolorer() {
        for (Node n : troupe.getChildren())
            if (n instanceof TraitGr)
                ((TraitGr) n).setEtat(((TraitGr) n).etat);
    }

    /**
     * @param backgrd couleur de fond de l'application
     */
    public void setBackground(Color backgrd) {
        scene.setFill(backgrd);
    }

    /**
     * @return la couleur de fond de l'application
     */
    public Color getBackground() {
        return (Color) scene.getFill();
    }

    static void changeJoueur() {
        joueur = (joueur == Etat.J1) ? Etat.J2 : Etat.J1;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
