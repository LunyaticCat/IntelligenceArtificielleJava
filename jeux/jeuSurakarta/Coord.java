
/**
 * classe representant les coordonnees i (no ligne) et j (no colonne) d'une matrice
 * @author emmanueladam*/
public class Coord {
	/**no de ligne*/
	public int i;
	/**no de colonne*/
	public int j;
	
	/**coordonnee fixee a (-1, -1)*/
	public static Coord falseCoord = new Coord(-1, -1);
	
	/**constructeur par defaut, intialise i et j a 0*/
	public Coord(){i=j=0;}
	/**Constructeur initialisant i et j*/
	public Coord(int _i, int _j)
	{
		i = _i;
		j = _j;
	}

	/**deux objets Coord  sont egaux si leurs coordonnes i et j sont egales
	 * @param o autre coordonnee a laquelle se comparer
	 * @return true si la coordonnee egale la coordonnee passe en parametre*/
	public boolean equals(Object o)
	{
		boolean result = false;
		if(o instanceof Coord)
		{
			Coord oc = (Coord)o;
			result = ( (i==oc.i) && (j==oc.j) );
		}
		return result;
	}

}
