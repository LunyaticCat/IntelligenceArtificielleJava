import java.awt.BorderLayout;
import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** 
 *classe principale du jeu du carre
 */
@SuppressWarnings("serial")
public class JeuSuraKarta extends JFrame 
{
	/** largeur de la fenetre */
	int largeur = 400;
	/** hauteur de la fenetre  */
	int hauteur = 400;
//	/** indique si le jeu doit attendre un message d'un autre jeu */
//	boolean reception = true;
	/** label sur lequel les textes vont s'afficher */
	JLabel labelTexte;
	/** paneau sur lequel les textes vont s'afficher */
	JPanel panelTexte;
	

	/**type de fin (1=serveur gagne, 2=client gagne, 3=grille remplie*/
	boolean finDeJeu;

	/**classe permettant la gestion du jeu (verification, ...)*/
	GestionSurakarta gestion;

	/**panneau sur lequel le jeu va s'afficher et s'effectuer*/
	PanneauJeu panneau;

	/**un logger pour debugger plus facilement*/
	public static Logger logger = Logger.getLogger("jeuSurakarta");

	/** constructeur classique, fait appel a init()*/
	public JeuSuraKarta(String title)
	{
		super(title);
		setBounds(0,0,largeur,hauteur);
		finDeJeu = false;
		labelTexte = new JLabel(" ");
		gestion = new GestionSurakarta(this);
		panneau = new PanneauJeu(this);
		init();
		gestion.reInit();
	}

	/** constructeur permettant d'initialiser le nom, le mode (client ou server), 
	 * le nom ou l'adresse IP du serveur et le port.
	 * fait appel a init()*/
	public JeuSuraKarta(String title, boolean mode, String _serverName, int PORT)
	{          
		super(title);
		setBounds(0,0,largeur,hauteur);
		gestion = new GestionSurakarta(this);
		panneau = new PanneauJeu(this);
	}

	/** initialise le processus de communication en fonction du mode
	 * initialise le paneau ainsi que la taille de la jframe
	 * ajoute l'ecoute d'evenements
	 */
	public void init()
	{
		getContentPane().setLayout(new BorderLayout());

		panelTexte = new JPanel();
		panelTexte.setBackground(Color.white);

		panelTexte.add(labelTexte );
		getContentPane().add(BorderLayout.NORTH, panelTexte);

		getContentPane().add(BorderLayout.CENTER, panneau);

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

	}



	/**
	 *  mise a jour du message destine a etre affiche
	 */
	public void setMessage(String message)
	{
		this.labelTexte.setText(message);
	}



	/**fonction principale, demande le mode de jeu (serveur ou client), le nom du serveur et le Port de communication */
	public static void main(String args[])
	{

		logger.setLevel(Level.INFO);
		JeuSuraKarta fe = new JeuSuraKarta("jeu surakarta");
		fe.init();
		fe.setVisible(true);

	}
}
