package gui;
import javax.swing.*;
import model.TicTacToe;
import java.awt.*;
import java.awt.event.*;

/**
 * Very simple panel for displaying and placing a token
 * @author emmanueladam
 */
@SuppressWarnings("serial")
class PanelTTT extends JPanel implements ActionListener
{

    /** the game containing the AI */
    TicTacToe jeu;
    /** display of tokens as buttons */
    JButton[][] matriceJButtons;
    /** the displayed game matrix */
    int[][] matriceJeu;

    /** constructor
     * @param _jeu the AI game attached to this window */
    PanelTTT(TicTacToe _jeu)
    {
        jeu = _jeu;
        this.setLayout(new GridLayout(3, 3));
        matriceJButtons = new JButton[3][3];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
            {
                matriceJButtons[i][j] = new JButton(".");
                matriceJButtons[i][j].setActionCommand("" + i + "," + j);
                matriceJButtons[i][j].addActionListener(this);
                this.add(matriceJButtons[i][j]);
            }
    }

    /** updates the display based on the received game matrix
     * @param _matriceJeu game matrix to display */
    void updateJeu(int[][] _matriceJeu)
    {
        matriceJeu = _matriceJeu;
        for (int i = 0; i < TicTacToe.HEIGHT; i++)
            for (int j = 0; j < TicTacToe.WIDTH; j++)
            {
                if (matriceJeu[i][j] == 1) matriceJButtons[i][j].setText("O");
                if (matriceJeu[i][j] == 2) matriceJButtons[i][j].setText("X");
            }
    }

    /**
     * reaction to the user clicking a button -> adds a token in the chosen column
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        JButton button = (JButton) e.getSource();
        button.setText("O");
        String cmd = e.getActionCommand();
        String[] strCoor = cmd.split(",");
        int i = Integer.parseInt(strCoor[0]);
        int j = Integer.parseInt(strCoor[1]);
        jeu.tourDeJeu(i, j);
    }
}
