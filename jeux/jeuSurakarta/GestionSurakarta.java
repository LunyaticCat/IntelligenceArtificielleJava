import java.util.ArrayList;

import javax.swing.JOptionPane;


/**
 * Classe de gestion du jeu Surakarta <br>
 * Le jeu est stockee dans une matrice 6x6
 * @author emmanuel adam
 */
public class GestionSurakarta {
	/**matrice de jeu representant le jeu du Surakarta, une case = 0 si elle est libre, sinon, elle porte le code d'un joueur*/
	int[][]jeu;
	/**lien vers la fenetre principale de jeu*/
	JeuSuraKarta parent;

	public GestionSurakarta(JeuSuraKarta _parent)
	{
		parent = _parent;
		jeu = new int[6][6];
	}

	/** 
	 *reinitialise la matrice de jeu
	 */
	public void reInit()
	{
		for(int i=0; i<6; i++)
			for(int j=0; j<6; j++)
			{
				if (i<2) jeu[i][j] = 1;
				else if (i>=4) jeu[i][j] = 2;
				else  jeu[i][j] = 0;
			}
		parent.panneau.repaint();
	}


	/**verifie si la case en (i,j) est libre
	 * @return true si la case est libre*/
	boolean isFree(int i, int j)
	{
		return (jeu[i][j]==0);
	}

	/**verifie si la case en (i,j) n'est pas libre
	 * @return true si la case n'est pas libre*/
	boolean isSelectionOk(int i, int j)
	{
		return (jeu[i][j]!=0);
	}

	/**verifie si la case en (i,j) est du bon type
	 * @param i ligne de la case selecionnee
	 * @param j colonne de la case selecionnee
	 * @param type type de joeur attendu (1 ou 2)
	 * @return true si la case n'est pas libre*/
	boolean isSelectionOk(int i, int j, int type)
	{
		return (jeu[i][j]==type);
	}

	/**marque la case comme etant selectionne (x+10)
	       si elle ne l'etait pas deja*/
	void selection(int i, int j)
	{
		if(jeu[i][j]<10) jeu[i][j] += 10;
	}

	/**marque la case comme etant non selectionne (x-10)
	       si elle etait selectionne*/
	void deselection(int i, int j)
	{
		if(jeu[i][j]>10) jeu[i][j] -= 10;
	}

	/**verifie si un deplacement d'un pion est possible de (pi,pj) vers (i,j)*/
	boolean isDeplacementOk(int pi, int pj, int i, int j)
	{
		boolean result  = false;
		if(isFree(i,j))
		{
			int dx = Math.abs(j - pj);
			int dy = Math.abs(i - pi);
			int d = dx+dy;
			result = (d>0) && (d <= 2) && (dx<2) && (dy<2);
		}
		return result; 
	}

	/**effectue un deplacement d'un pion de (pi,pj) vers (i,j)*/
	void jeu(int pi, int pj, int i, int j)
	{
		jeu[i][j] = jeu[pi][pj] - 10;
		jeu[pi][pj] = 0;
	}

	/**verifie si une prise  d'un pion est possible de (pi,pj) vers (i,j)*/
	ArrayList<Etat> isPriseOk(int pi, int pj, int i, int j)
	{
		ArrayList<Etat> result = new ArrayList<Etat>();
		JeuSuraKarta.logger.entering(this.getClass().getName(), "isPriseOk", new Object[]{pi,  pj, i, j});
		int typeA = jeu[pi][pj] - 10;
		int typeB = jeu[i][j];
		boolean possible=true;
		//impossible de prendre a partir des coins
		if ( (pi==0 || pi==5) && (pj==0 || pj==5) ) possible = false;
		// si les pions de depart et d'arrivee sont differents
		if (possible && typeB!=0 && typeA!=typeB)
		{
//			System.out.println(" les pions de depart et d'arrivee sont differents");
			// si la ligne de depart = la ligne d'arrivee (ou son oppposee) ou la colonne d'arrivee (ou son oppposee)  
			possible = (pi==i) || (pi==j) || (i==(5-pi)) || (j==(5-pi));
			// si la colonne de depart = la ligne d'arrivee (ou son oppposee) ou la colonne d'arrivee (ou son oppposee)  
			possible = possible || (pj==j) || (pj==i) || (j==(5-pj)) || (i==(5-pj));
			// alors c'est possible,
			// il faut verifier qu'aucun pion ne se trouve entre le pion de depart et le pion d'arrivee
//			System.out.println(" jeton atteignable si rien devant");
			if (possible)
			{
				// recherche par la droite
				result = chercherLateralement(pi, pj, i, j, true);
				if (result==null) result = chercherLateralement(pi, pj, i, j, false);
				if (result==null) result = chercherVerticalement(pi, pj, i, j, true);
				if (result==null) result = chercherVerticalement(pi, pj, i, j, false);
			}
		}
		
		return result; 
	}

	/**verifie si une prise  d'un pion est possible de (pi,pj) vers (i,j) en commencant par la droite ou la gauche
	 * @param pi ligne de depart
	 * @param pj colonne de depart
	 * @param i ligne de la cible
	 * @param j colonne de la cible
	 * @param droite true si on commence a chercher par la droite, false si on commence par la gauche*/
	ArrayList<Etat> chercherLateralement(int pi, int pj, int i, int j, boolean droite)
	{
		ArrayList<Etat> result = new ArrayList<Etat>();
		Coord suite = new Coord();
		boolean possible = false;
		//ii et jj = coordonnees du point de depart du morceau de solution
		int ii=pi;
		int jj=pj;
		//nextii et nexxtjj = coordonnees du point d'arrivee du morceau de solution
		int nextii = ii;
		int nextjj = jj;
		if(droite) {nextjj = 5; suite = trouverBoucleDroite(pi, pj);}
		else {nextjj=0; suite = trouverBoucleGauche(pi, pj);}
		if(suite.equals(Coord.falseCoord)) result = null;
		//si on a trouve une boucle a droite ou a gauche
		if(!suite.equals(Coord.falseCoord))
		{
			result.add(new Etat(Etat.BOUCLE, ii, jj, ii, nextjj));
			possible = trouverCible(suite.i, suite.j,  i, j, true);
			//si on a trouve la cible dans la colonne d'arrivee suite a la boucle
			if(possible) result.add(new Etat(Etat.LIGNE, suite.i, suite.j,  i, j));

			//sinon on cherche une boucle en bas ou en haut
			if(!possible)
			{
				nextjj = jj = suite.j;
				ii = suite.i;
				if(suite.i==0)
					{nextii = 5; suite = trouverBoucleBas(ii, jj);}
				else
					{nextii = 0; suite = trouverBoucleHaut(ii, jj);}
				if(suite.equals(Coord.falseCoord)) result = null;
				//si on a trouve une boucle en bas ou  en haut
				if(!suite.equals(Coord.falseCoord))
				{
					result.add(new Etat(Etat.BOUCLE, ii, jj, nextii, nextjj));
					possible = trouverCible(suite.i, suite.j, i, j, false);
					//si on a trouve la cible dans la ligne d'arrivee suite a la boucle bas ou haut
					if(possible) result.add(new Etat(Etat.LIGNE, suite.i, suite.j,  i, j));
					//sinon, on cherche une boucle a gauche ou a droite (dans l'autre sens)
					if(!possible)
					{
						
						nextii = ii = suite.i;
						jj = suite.j;
						if(droite) 
						{
							nextjj = 5;
							suite = trouverBoucleGauche(ii, jj);
						}
						else 
						{
							nextjj = 0;
							suite =  trouverBoucleDroite(ii, jj);
						}
						if(suite.equals(Coord.falseCoord)) result = null;
						//si on a trouve une boucle a gauche
						if(!suite.equals(Coord.falseCoord))
						{
							result.add(new Etat(Etat.BOUCLE, ii, jj, nextii, nextjj));
							possible = trouverCible(suite.i, suite.j, i, j, true);	
							//si on a trouve la cible dans la colonne d'arrivee suite a la boucle gauche
							if(possible) result.add(new Etat(Etat.LIGNE, suite.i, suite.j,  i, j));
							//sinon, on cherche une boucle en haut ou  en bas
							if(!possible)
							{
								ii = suite.i;
								nextjj = jj = suite.j;									
								if(ii == 5)
								{
									nextii = 0;
									suite = trouverBoucleHaut(ii, jj);
								}
								else								
								{
									nextii = 5;
									suite = trouverBoucleBas(ii, jj);									
								}
								if(suite.equals(Coord.falseCoord)) result = null;
								//si on a trouve une boucle en haut ou en bas
								if(!suite.equals(Coord.falseCoord))
								{
									result.add(new Etat(Etat.BOUCLE, ii, jj, nextii, nextjj));
									possible = trouverCible(suite.i, suite.j, i, j, false);
								}
								//si on a trouve la cible dans la ligne d'arrivee suite a la boucle haut
								if(possible) result.add(new Etat(Etat.LIGNE, suite.i, suite.j,  i, j));
								//sinon on arrete la car on a fait le tour...
							}
						}
					}
				}
			}
		}
		return result;
	}



	/**verifie si une prise  d'un pion est possible de (pi,pj) vers (i,j) en commencant par la droite ou la gauche
	 * @param pi ligne de depart
	 * @param pj colonne de depart
	 * @param i ligne de la cible
	 * @param j colonne de la cible
	 * @param haut true si on commence a chercher par le haut, false si on commence par le bas*/
	ArrayList<Etat> chercherVerticalement(int pi, int pj, int i, int j, boolean haut)
	{
		ArrayList<Etat> result = new ArrayList<Etat>();
		Coord suite = new Coord();
		boolean possible = false;
		//ii et jj = coordonnees du point de depart du morceau de solution
		int ii=pi;
		int jj=pj;
		//nextii et nexxtjj = coordonnees du point d'arrivee du morceau de solution
		int nextii = ii;
		int nextjj = jj;
		if(haut) {nextii=0; suite = trouverBoucleHaut(pi, pj);}
		else  {nextii=5; suite = trouverBoucleBas(pi, pj);}
		if(suite.equals(Coord.falseCoord)) result=null;
		//si on a trouve une boucle en haut ou en bas
		if(!suite.equals(Coord.falseCoord))
		{
			result.add(new Etat(Etat.BOUCLE, ii, jj, nextii, nextjj));
			possible = trouverCible(suite.i, suite.j,  i, j, false);
			//si on a trouve la cible dans la colonne d'arrivee suite a la boucle
			if(possible)  result.add(new Etat(Etat.LIGNE, suite.i, suite.j,  i, j));

			//sinon on cherche une boucle en bas ou en haut
			if(!possible)
			{
				nextii = ii = suite.i;
				jj = suite.j;
				if(jj==0)
				{
					nextjj = 5;
					suite = trouverBoucleDroite(ii, jj);
				}
				else
				{
					nextjj = 0;
					suite = trouverBoucleGauche(ii, jj);
				}
				if(suite.equals(Coord.falseCoord)) result=null;
				//si on a trouve une boucle a gauche ou a droite
				if(!suite.equals(Coord.falseCoord))
				{
					result.add(new Etat(Etat.BOUCLE, ii, jj, nextii, nextjj));
					possible = trouverCible(suite.i, suite.j, i, j, true);
					//si on a trouve la cible dans la ligne d'arrivee suite a la boucle  gauche ou a droite
					if(possible)  result.add(new Etat(Etat.LIGNE, suite.i, suite.j,  i, j));
					//sinon, on cherche une boucle en bas ou en haut (cote opose)
					if(!possible)
					{
						ii = suite.i;
						jj = nextjj = suite.j;
						if(haut) 
						{
							nextii = 5;
							suite = trouverBoucleBas(suite.i, suite.j);
						}
						else
						{
							nextii = 0;
							suite = trouverBoucleHaut(pi, pj);
						}
						if(suite.equals(Coord.falseCoord)) result=null;
						//si on a trouve une boucle en bas ou  en haut
						if(!suite.equals(Coord.falseCoord))
						{
							possible = trouverCible(suite.i, suite.j, i, j, false);	
							//si on a trouve la cible dans la colonne d'arrivee suite a la boucle bas ou haut
							if(possible)  result.add(new Etat(Etat.LIGNE, suite.i, suite.j,  i, j));
							//sinon, on cherche une boucle a gauche ou droite
							if(!possible)
							{
								ii = nextii = suite.i;
								jj = suite.j;
								if(jj == 5)
								{
									nextjj = 0;
									suite = trouverBoucleGauche(suite.i, 5);
								}
								else
								{
									nextjj = 5;
									suite = trouverBoucleDroite(suite.i, 0);
								}
								if(suite.equals(Coord.falseCoord)) result=null;
								//si on a trouve une boucle a gauche ou droite
								if(!suite.equals(Coord.falseCoord))
								{
									result.add(new Etat(Etat.BOUCLE, ii, jj, nextii, nextjj));
									possible = trouverCible(suite.i, suite.j, i, j, true);
								}
								//si on a trouve la cible dans la ligne d'arrivee suite a la boucle a gauche ou droite
								if(possible)  result.add(new Etat(Etat.LIGNE, suite.i, suite.j,  i, j));
								//sinon on arrete la car on a fait le tour...
							}
						}
					}
				}
			}
		}
		return result;
	}
	
	
	
	
	/**verifie si (pi, pj) est aligne sur (i,j) et si les cases sont vides entre ces points
	 * @param pi colonne de la position de depart
	 * @param pj ligne de la position de depart
	 * @param i colonne de la cible
	 * @param j ligne de la cible
	 * @param colonne true -> teste alignement de colonne, false -> teste alignement de ligne*/
	boolean trouverCible(int pi, int pj, int i, int j, boolean colonne)
	{
		JeuSuraKarta.logger.entering(this.getClass().getName(), "trouverCible", new Object[]{pi,  pj,  i,  j,  colonne});

//		System.err.println("trouverCible ("+pi+","+pj+"," + i+"," +  j+"," + colonne+")");
		boolean result = false;
		if(colonne) 
		{
			result = (pj==j);
			if(result)
			{
				if(pi>i)
					for(int k=pi; k>i && result; k--)
						result = result && (jeu[k][j] == 0 || jeu[k][j]>10);
				else
					for(int k=pi; k<i && result; k++)
						result = result && (jeu[k][j] == 0 || jeu[k][j]>10);
			}
		}	    	  	
		else
		{
			result = (pi==i);
			if(result)
			{
				if(pj>j)
					for(int k=pj; k>j && result; k--)
						result = result && (jeu[i][k] == 0 || jeu[i][k]>10);
				else
					for(int k=pj; k<j && result; k++)
						result = result && (jeu[i][k] == 0 || jeu[i][k]>10);
			}	    	  		
		} 
		JeuSuraKarta.logger.exiting(this.getClass().getName(), "trouverCible", result);

		return result;
	}


	/**recherche une boucle sur la droite de (i,j)  dans la matrice de jeu
	 * @param i no de ligne de la case 
	 * @param j no de colonne de la case 
	 * @return le point d'arrivee si les cases a droite de (i,j) sont libres jusque (i,5), sinon le point (-1, -1)<br>
	 * le point d'arrivee est (0, 5-i) si i<3, sinon il est (5, i)*/
	Coord trouverBoucleDroite(int i, int j)
	{
		JeuSuraKarta.logger.entering(this.getClass().getName(), "trouverBoucleDroite", new Object[]{i,j});
		Coord result = Coord.falseCoord;
		boolean ok = true;
		for(int k=j+1; k<6 && ok; k++)
			ok = ok && (jeu[i][k]!=1 && jeu[i][k]!=2);
		if(ok) 
			result = (i<3?new Coord(0, 5-i):new Coord(5, i));
		JeuSuraKarta.logger.exiting(this.getClass().getName(), "trouverBoucleDroite", result);
		return result;
	}

	/**recherche une boucle sur la gauche de (i,j) dans la matrice de jeu
	 * @param i no de ligne de la case 
	 * @param j no de colonne de la case 
	 * @return le point d'arrivee si les cases a gauche de (i,j) sont libres jusque (i,0), sinon le point (-1, -1)<br>
	 * le point d'arrivee est (0, i) si i<3, sinon il est (5, 5-i)*/
	Coord trouverBoucleGauche(int i, int j)
	{
		JeuSuraKarta.logger.entering(this.getClass().getName(), "trouverBoucleGauche", new Object[]{i,j});
		Coord result = Coord.falseCoord;
		boolean ok = true;
		for(int k=j-1; k>=0 && ok; k--)
			ok = ok && (jeu[i][k]!=1 && jeu[i][k]!=2);
		if(ok) 
			result = (i<3?new Coord(0, i):new Coord(5, 5-i));
		JeuSuraKarta.logger.exiting(this.getClass().getName(), "trouverBoucleGauche", result);
		return result;
	}

	/**recherche une boucle sur le bas de (i,j) dans la matrice de jeu
	 * @param i no de ligne de la case 
	 * @param j no de colonne de la case 
	 * @return le point d'arrivee si les cases dessous de (i,j) sont libres jusque (5,j), sinon le point (-1, -1)<br>
	 * le point d'arrivee est (5-j, 0) si j<3, sinon il est (j, 5)*/
	Coord trouverBoucleBas(int i, int j)
	{
		JeuSuraKarta.logger.entering(this.getClass().getName(), "trouverBoucleBas", new Object[]{i,j});
		Coord result = Coord.falseCoord;
		boolean ok = true;
		for(int k=i+1; k<6 && ok; k++)
			ok = ok && (jeu[k][j]!=1 && jeu[k][j]!=2 );
		if(ok) 
			result = (j<3?new Coord(5-j, 0):new Coord(j, 5));
		JeuSuraKarta.logger.exiting(this.getClass().getName(), "trouverBoucleBas", result);
		return result;
	}

	/**recherche une boucle sur le haut de (i,j) dans la matrice de jeu
	 * @param i no de ligne de la case 
	 * @param j no de colonne de la case 
	 * @return le point d'arrivee si les cases dessus de (i,j) sont libres jusque (0,j), sinon le point (-1, -1)<br>
	 * le point d'arrivee est (j, 0) si j<3, sinon il est (5-j, 5)*/
	Coord trouverBoucleHaut(int i, int j)
	{
		JeuSuraKarta.logger.entering(this.getClass().getName(), "trouverBoucleHaut", new Object[]{i,j});
		Coord result = Coord.falseCoord;
		boolean ok = true;
		for(int k=i-1; k>=0 && ok; k--)
			ok = ok && (jeu[k][j]!=1 && jeu[k][j]!=2 ); 	  	
		if(ok) 
			result = (j<3?new Coord(j, 0):new Coord(5-j, 5));
		JeuSuraKarta.logger.exiting(this.getClass().getName(), "trouverBoucleHaut", result);
		return result;
	}

	/**effectue une prise de (i,j) par (pi, pj)  
	 * @param pi colonne de la position de depart
	 * @param pj ligne de la position de depart
	 * @param i colonne de la cible
	 * @param j ligne de la cible*/
	void prise(int pi, int pj, int i, int j)
	{
		jeu[i][j] = jeu[pi][pj]-10;
		jeu[pi][pj] = 0;
	}


	/**verifie la fon du jeu (plus de jetons d'une des deux couleurs)
	 * */
	void verifFinJeu()
	{
		int nbBleus = 0;
		int nbRouges = 0;
		for(int i=0; i<6; i++)
			for(int j=0; j<6; j++)
				if(jeu[i][j]==1) nbRouges++;
				else if(jeu[i][j]==2) nbBleus++;
		if(nbRouges == 0|| nbBleus == 0)
		{
			String msg = "";
			if(nbBleus==0) msg = "Le joueur rouge gagne par " + nbRouges + " points !";
			if(nbRouges==0) msg = "Le joueur bleu gagne par " + nbBleus + " points !";
//			afficheMatrice();
			parent.labelTexte.setText(msg);
			parent.repaint();
			JOptionPane.showMessageDialog(parent, msg, "Fin du jeu", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	/**fonction affichant le contenu de la matrice de jeu sur la console*/
	 void afficheMatrice()
	{
		String ligne = "";
		for(int i=0; i<6; i++)
		{
			for(int j=0; j<6; j++)
				ligne = ligne+jeu[i][j] + "  ";
			System.out.println(ligne);
			ligne="";
		}
	}
	
}
