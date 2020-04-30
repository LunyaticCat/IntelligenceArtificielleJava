import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;
/**
* plateau du jeu du surakarta
* @author E.ADAM
*/
@SuppressWarnings("serial")
public class PanneauJeu extends JPanel {
	/** indique si le tour est au joueur 1 ou 2 */
	int tourDeJeu = 1;
	/**liste de messages predefinis*/
	final static String[] messages = {"Le tour est aux rouges", "Pion rouge selectionne", "Le tour est aux bleus", "Pion bleu selectionne"};
	/**no du message courant*/
	static int indiceMessage = 0;

	/**point haut gauche dans la matrice (0,0)*/
	static final Coord hautGauche = new Coord(0,0);
	/**point haut droit dans la matrice (0,5)*/
	static final Coord hautDroit = new Coord(0,5);
	/**point bas gauche dans la matrice (5,0)*/
	static final Coord basGauche = new Coord(5,0);
	/**point bas droit dans la matrice (5, 5)*/
	static final Coord basDroit = new Coord(5,5);

	/**booleen indiquant si un pion a ete selectionne (est en cours de deplacement) ou non*/
	boolean selection = false;

	/**ligne du pion selectionne*/
	int pi;
	/**colonne du pion selectionne*/
	int pj;
	/**hauteur du panneau*/
	int height;
	/**largeur du panneau*/
	int width;
	/**espace entre lignes horizontales*/
	int  ph;
	/**espace entre lignes verticales*/
	int  pw;

	static Color couleurPionJoueur1 = Color.red;
	static Color couleurSelectionJoueur1 = new Color(255,200,200);
	static Color couleurPionJoueur2 = Color.blue;
	static Color couleurSelectionJoueur2 = new Color(200,200,255);

	/** pour un dessin de qualite*/
	RenderingHints qualityHints;

	/**image cree representant la grille*/
	BufferedImage biGrille;	 


	/**lien vers la fenetre principale */
	JeuSuraKarta parent;
	
	/**liste des actions menant a une prise*/
	ArrayList<Etat>  result ;

	PanneauJeu(JeuSuraKarta _parent)
	{
		parent = _parent;
		if(!parent.finDeJeu) parent.labelTexte.setText(PanneauJeu.messages[PanneauJeu.indiceMessage]);
		// pour un dessin de qualite
		qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		// fin de mise en place de la qualite de dessin

		this.addMouseListener(new MaGestionSouris());
	}

	/**dessine un arc 3/4 (de 270�)
	 * @param point centre de l'arc en coordonnees matricielles (i, j) 
	 * @param taille taille de l'arc (=1 ou 2 pour les grands arcs)
	 * @param g2 contexte graphique dans lequel dessiner les arcs
	 * */
	void dessineArc(Coord point, int taille, Graphics2D g2)
	{
		int coefTaille = (taille*2);
		int decallage = 2-taille;
		int x = (point.j+decallage);
		int y = (point.i+decallage);
		int angleDebut = 0;
		if (point.j==0) angleDebut = (point.i==0?0:90);
		if (point.j==5) angleDebut = (point.i==0?270:180);
		g2.drawArc(x*pw,  y*ph, coefTaille*pw, coefTaille*ph, angleDebut, 270);

	}

	/**
	 * dessin de la grille dans l'image biGrille
	 */ 
	public void createGrille() 
	{
		biGrille = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2bi = biGrille.createGraphics();
		// pour un dessin de qualite
		g2bi.setRenderingHints(qualityHints);
		// fin de mise en place de la qualite de dessin

		g2bi.setColor(Color.DARK_GRAY);
		g2bi.fillRect(0,0,  width, height);


		Stroke pinceauDouble = new CompositeStroke( new BasicStroke( 4f ), new BasicStroke( 2f ) );
		Stroke pinceauLeger = new  BasicStroke(2);
		g2bi.setStroke(pinceauDouble);

		//les arcs
		for(int i=1; i<3; i++)
		{
			if(i==1) g2bi.setColor(Color.ORANGE);
			else g2bi.setColor(Color.GREEN);
			dessineArc(PanneauJeu.hautGauche, i, g2bi);
			dessineArc(PanneauJeu.basGauche, i, g2bi);
			dessineArc(PanneauJeu.basDroit, i, g2bi);
			dessineArc(PanneauJeu.hautDroit, i, g2bi);
		}

		//Les lignes
		for(int i=0; i<5; i++)
		{
			if(i==0 || i==5)
			{
				g2bi.setStroke(pinceauLeger);
				g2bi.setColor(Color.GRAY);
			}
			else
			{
				g2bi.setStroke(pinceauDouble);
				if(i==1 || i==4) g2bi.setColor(Color.ORANGE);
				if(i==2 || i==3) g2bi.setColor(Color.GREEN);				 
			}
			g2bi.drawLine(2*pw, (i+2)*ph, 7*pw, (i+2)*ph);
			g2bi.drawLine((i+2)*pw, 2*ph, (i+2)*pw, 7*ph);			 
		}

		//les places

		int halfDimPlace = pw/4;
		AlphaComposite acTrans = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
		g2bi.setComposite(acTrans);
		g2bi.setColor(Color.WHITE);

		for(int i=0; i<6; i++)
			for(int j=0; j<6; j++)
				g2bi.fillOval((j+2)*pw-halfDimPlace, (i+2)*ph-halfDimPlace, 2*halfDimPlace, 2*halfDimPlace);

		g2bi.setColor(Color.BLACK);
		AlphaComposite acNorm = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
		g2bi.setComposite(acNorm);
	}

	/**
	 * affichage de l'image de la grille 
	 */ 
	public void paintGrille(Graphics2D g2) 
	{
		g2.drawImage(biGrille, 0, 0, this);
	}

	/**dessin des jeux*/
	void paintJeu(Graphics2D g2)
	{
		int halfDimToken = pw/5;

		for(int i=0; i<6; i++)
			for(int j=0; j<6; j++)
			{
				if (parent.gestion.jeu[i][j]!=0)
				{
					if (parent.gestion.jeu[i][j]==1)	 g2.setColor(couleurPionJoueur1);
					if (parent.gestion.jeu[i][j]==11)	 g2.setColor(couleurSelectionJoueur1);
					if (parent.gestion.jeu[i][j]==2)	 g2.setColor(couleurPionJoueur2);
					if (parent.gestion.jeu[i][j]==12)	 g2.setColor(couleurSelectionJoueur2);
					g2.fillOval((j+2)*pw-halfDimToken, (i+2)*ph-halfDimToken, 2*halfDimToken, 2*halfDimToken);
				}
			}
		g2.setColor(Color.BLACK);
	}



	/**
	 * dessin de la grille et des objets selon la matrice de jeu, calcule si un joueur a gagne 
	 */ 
	public void paint(Graphics gg) 
	{
		int oldHeight = height;
		int oldWidth = width;
		height = this.getHeight();
		width = this.getWidth();
		ph = height/9;
		pw = width/9;

		boolean gridUpToDate = (oldHeight == height) && (oldWidth == width);
		if(!gridUpToDate) createGrille();

		// Le graphique
		Graphics2D g2 = (Graphics2D)gg;

		// pour un dessin de qualite
		g2.setRenderingHints(qualityHints);
		// fin de mise en place de la qualite de dessin

		// dessin de la grille
		paintGrille(g2);

		// dessin des traits
		paintJeu(g2);
		
		//dessin du chemin menan � une prise s'il existe
		if(result!=null) afficheCheminPrise(g2);

		parent.labelTexte.setText(PanneauJeu.messages[PanneauJeu.indiceMessage]);

	}
	
	/**surligne le chemin pris pour prendre un pion
	 * @param chemin suite d'etats/actions suivis pour prendre le pion*/
	void afficheCheminPrise(Graphics2D g2)
	{
		AlphaComposite acTrans = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
		g2.setComposite(acTrans);
		g2.setColor(Color.YELLOW);		
		Stroke pinceauJaune = new  BasicStroke(10);
		g2.setStroke(pinceauJaune);

		for(Etat e:result)
		{
				g2.drawLine((e.j0+2)*pw, (e.i0+2)*ph, (e.j1+2)*pw, (e.i1+2)*ph);
				if(e.type == Etat.BOUCLE)
				{				
					Coord c = PanneauJeu.hautDroit;
					int taille = 0;
					if(e.j1==5)
					{
						if(e.i1<3) {c = PanneauJeu.hautDroit; taille = e.i1;}
						else {c = PanneauJeu.basDroit; taille = 5-e.i1;}
						dessineArc( c, taille,  g2);
					}
					else if(e.j1==0)
					{
						if(e.i1<3) {c = PanneauJeu.hautGauche; taille = e.i1;}
						else {c = PanneauJeu.basGauche; taille = 5-e.i1;}
						dessineArc( c, taille,  g2);
					}
					else if(e.i1==5)
					{
						if(e.j1<3) {c = PanneauJeu.basGauche; taille = e.j1;}
						else {c = PanneauJeu.basDroit; taille = 5-e.j1;}
						dessineArc( c, taille,  g2);
					}
					else if(e.i1==0)
					{
						if(e.j1<3) {c = PanneauJeu.hautGauche; taille = e.j1;}
						else {c = PanneauJeu.hautDroit; taille = 5-e.j1;}
						dessineArc( c, taille,  g2);
					}

				}
		}
		result = null;
	}


	/**classe interne de gestion des clics souris*/
	class MaGestionSouris extends MouseAdapter{
		public void mouseClicked(MouseEvent e)
		{
			// on recupere les coordonnees en pixels, puis on les transforme en coordonnees i,j pour la matrice
			double xx = e.getX()-2*pw;
			double yy = e.getY()-2*ph;
			int j = (int)(xx+pw/3)/pw;
			int i = (int)(yy+ph/3)/ph;
			if (!selection && parent.gestion.isSelectionOk(i, j, tourDeJeu))
			{
				selection = true;
				indiceMessage = (indiceMessage+1)%4;
				parent.gestion.selection(i,j);
				pi = i;
				pj = j;
			}
			else
				if (selection)
				{
					if((pi==i) && (pj==j))
					{
						selection = false;
						parent.gestion.deselection(pi,pj);
						indiceMessage = (indiceMessage-1)%4;
					}
					else
						if(	parent.gestion.isDeplacementOk(pi, pj, i, j))
						{
							selection = false;
							parent.gestion.jeu(pi, pj, i, j);
							tourDeJeu = 1 + (2-tourDeJeu);
							indiceMessage = (indiceMessage+1)%4;
						}
						else
						{
							result = parent.gestion.isPriseOk(pi, pj, i, j);
							if(	 result != null && result.size()>0)
							{
								selection = false;
								parent.gestion.prise(pi, pj,i,j);
								tourDeJeu = 1 + (2-tourDeJeu);
								indiceMessage = (indiceMessage+1)%4;
								parent.gestion.verifFinJeu();
							}
						}

				}


			repaint();
		}
	}
}

