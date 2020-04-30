/**classe qui represente un etat de la solution pour prendre un pion adverse*/
public class Etat {
	static final int BOUCLE = 1;
	static final int LIGNE = 2;
	/**ligne  de depart*/
	int i0;
	/**colonne  de depart*/
	int j0;
	/**ligne  d'arrivee (sur le pion vise ou sur une boucle)*/
	int i1;
	/**colonne  d'arrivee (sur le pion vise ou sur une boucle)*/
	int j1;
	/**type d'action (boucle ou ligne)*/
	int type;

	/**constructeur complet d' un etat de la solution pour prendre un pion adverse
	 *@param _type type d'action (boucle ou ligne)
	*@param _i0 ligne de depart
	*@param _j0 colonne  de depart
	*@param _i1 ligne  d'arrivee (sur le pion vise ou sur une boucle)
	*@param _j1 colonne  d'arrivee (sur le pion vise ou sur une boucle)*/
	Etat(int _type, int _i0, int _j0, int _i1, int _j1)
	{
		type = _type;
		i0 = _i0;
		j0 = _j0;
		i1 = _i1;
		j1 = _j1;
	}
	
	public String toString()
	{
		String result = (type==1?"boucle":"ligne trouvee ") + " de " + i0 +","+j0+" a "+i1+","+j1;
		return result;
	}

}
