import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * classe liee au fichier ChoixCouleursGui.fxml
 * permet de changer les couleurs des traits et du fond du jeu
 * @author Emmanuel ADAM
 * */
public class ChoixCouleursControleur {
	/**couleur pour le joueur 1*/
	Color colJ1;
	/**couleur pour le joueur 2*/
	Color colJ2;
	/**couleur pour le fond de l'application*/
	Color colBackground;
	/**selecteur de couleur pour J1 d�crit dans le fichier fxml*/
	@FXML private ColorPicker colPick1;
	/**selecteur de couleur pour J2 d�crit dans le fichier fxml*/
	@FXML private ColorPicker colPick2;
	/**selecteur de couleur pour le fond d�crit dans le fichier fxml*/
	@FXML private ColorPicker colPickBackground;
	
	/**petit theatre associe */
	Stage dialogStage;

	/**application associe*/
	CarreApp application;
	
	
	@FXML void choixCouleurJ1(ActionEvent event) { colJ1 = colPick1.getValue(); }

	@FXML void choixCouleurJ2(ActionEvent event) { colJ2 = colPick2.getValue(); }

	@FXML void choixCouleurBackground(ActionEvent event) { colBackground = colPickBackground.getValue(); }

	/**acceptation du choix des couleurs, utilisateur clique sur OK*/
    @FXML protected void gestionBoutonGo(ActionEvent event) {
    	Etat.J1.setCouleur(this.colJ1);
    	Etat.J2.setCouleur(this.colJ2);
    	// modification du fond d'ecran de l'application
    	this.application.setBackground(this.colBackground);
    	//demande de recoloration de tous les jetons
    	this.application.recolorer();
    	//fermeture de la fenetre
      	dialogStage.close();
      }

    /**annulation : fermeture de la fenetre sans prendre en compte les couleurs*/
    @FXML protected void gestionBoutonCancel(ActionEvent event) {
      	dialogStage.close();
      }

	/**
	 * @param dialogStage lien vers le petit theatre associe
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	/**lien vers l'application
	 * recuperation des couleurs actuellement utilisee
	 * @param _ay une reference a l'application*/
	public void setAppliLink(CarreApp _ay)
	{
		application = _ay;
		this.colJ1 = Etat.J1.getCouleur();
		this.colJ2 = Etat.J2.getCouleur();
		this.colBackground = application.getBackground();
 
		colPick1.setValue(colJ1); 
		colPick2.setValue(colJ2);
		this.colPickBackground.setValue(this.colBackground);
	}
	


}
