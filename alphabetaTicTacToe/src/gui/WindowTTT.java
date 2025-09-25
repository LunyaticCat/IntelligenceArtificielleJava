package gui;
import javax.swing.*;
import model.TicTacToe;
import java.awt.*;

/**
 * Very basic window for the Tic-Tac-Toe game on a 5x5 grid
 * @author emmanueladam
 */
@SuppressWarnings("serial")
public class WindowTTT extends JFrame
{

    /** panel representing the game and event captures */
    PanelTTT p;
    /** the game enabling AI reasoning */
    TicTacToe jeu;

    /** constructor
     * @param _jeu the AI game attached to this window */
    public WindowTTT(TicTacToe _jeu)
    {
        jeu = _jeu;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Tic-Tac-Toe");
        //size(300, 300);
        setMinimumSize(new Dimension(300, 300));
        Container content = getContentPane();
        content.setLayout(new BorderLayout());
        p = new PanelTTT(jeu);
        content.add(p, BorderLayout.CENTER);
        pack();
    }

    /** updates the game matrix of the graphical panel
     * @param _matriceJeu game matrix deduced by the AI that needs to be updated in the panel */
    public void updateJeu(int[][] _matriceJeu)
    {
        p.updateJeu(_matriceJeu);
        repaint();
    }
}
