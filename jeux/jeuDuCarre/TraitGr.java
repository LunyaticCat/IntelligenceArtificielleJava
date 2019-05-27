import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

/**
 * classe qui represente un trait cliquable pour le jeu du carre
 * @author emmanueladam
 * */
public class TraitGr extends Line{
	/**etat du trait (libre, joueur1 ou joueur2)*/
	public Etat etat;
	/**type de trait*/
	boolean horizontal;
	/**no de colonne  du trait*/
	int i;
	/**no de ligne  du trait*/
	int j;
	TraitGr(){etat = Etat.LIBRE;}

	/**
	 * @param xo coordonnee x de depart
	 * @param yo coordonnee y de depart
	 * @param xf coordonnee x d'arrivee
	 * @param yf coordonnee y de depart
	 * */
	TraitGr(int xo, int yo, int xf, int yf ){
		super(xo, yo, xf, yf);
		etat = Etat.LIBRE;
		this.setStrokeLineCap(StrokeLineCap.ROUND);
		setStroke(etat.getCouleur());
	    setStrokeWidth(5);
		}

	/**
	 * @param xo coordonnee x de depart
	 * @param yo coordonnee y de depart
	 * @param xf coordonnee x d'arrivee
	 * @param yf coordonnee y de depart
	 * @param _horizontal true si trait horizontal
	 * */
	TraitGr(int xo, int yo, int xf, int yf , boolean _horizontal){
		this(xo, yo, xf, yf);
		horizontal = _horizontal;
		}

	/**
	 * @param xo coordonnee x de depart
	 * @param yo coordonnee y de depart
	 * @param xf coordonnee x d'arrivee
	 * @param yf coordonnee y de depart
	 * @param _horizontal true si trait horizontal
	 * @param _i abscisse i dans la grille  de jeu
	 * @param _j ordonnee j dans la grille  de jeu
	 * */
	TraitGr(int xo, int yo, int xf, int yf , boolean _horizontal, int _i, int _j){
		this(xo, yo, xf, yf, _horizontal);
		i = _i; j = _j;
		}

	public void setEtat(Etat _etat){
		etat = _etat;
		changer();}

	/**rafraichit la couleur du trait*/
	void changer()
	{
		this.setStroke(etat.getCouleur());
	}
	

}
