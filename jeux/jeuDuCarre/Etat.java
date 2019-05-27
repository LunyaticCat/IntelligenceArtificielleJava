import javafx.scene.paint.Color;
/**
 * Etats possibles d'un trait (libre, occupé par joueur J1, ou joueur J2
 * chaque etat est associe a une couleur
 * @author Emmanuel ADAM
 * */
public enum Etat {LIBRE(Color.GRAY), J1(Color.BLUE), J2(Color.RED);
	Color couleur;
	Etat(Color _couleur){couleur = _couleur;}
	public Color getCouleur(){return couleur;}
	public void setCouleur(Color _couleur){ couleur = _couleur;}
}
