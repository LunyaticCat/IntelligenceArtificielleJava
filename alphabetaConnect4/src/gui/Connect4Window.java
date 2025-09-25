package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import model.Connect4;

/**
 * A very basic window for the Connect Four game on a 5x5 grid.
 *
 * @author emmanueladam
 */
@SuppressWarnings("serial")
public class Connect4Window extends JFrame {

    /** Panel representing the game and event captures */
    EnvironmentPanel gamePanel;

    /** The game enabling AI reasoning */
    Connect4 game;

    /** Label providing game instructions */
    JTextArea helpLabel;

    /**
     * Constructor
     *
     * @param _game the AI game attached to this window
     */
    public Connect4Window(Connect4 _game) {
        game = _game;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 500, 500);
        Container content = getContentPane();
        content.setLayout(new BorderLayout());
        gamePanel = new EnvironmentPanel(400, 400, this, game);
        content.add(gamePanel, BorderLayout.CENTER);

        helpLabel = new JTextArea("Click on the desired column...");
        helpLabel.setEditable(false);
        helpLabel.setRows(5);
        helpLabel.setBackground(Color.green);
        helpLabel.setFont(new Font("Courier", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(helpLabel);
        scrollPane.setSize(400, 20);
        content.add(scrollPane, BorderLayout.NORTH);
    }

    /**
     * Constructor
     *
     * @param _name the name of the window
     */
    Connect4Window(String _name) {
        super(_name);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        Container content = getContentPane();
        content.setLayout(new BorderLayout());
        gamePanel = new EnvironmentPanel(400, 300, this, game);
        content.add(gamePanel, BorderLayout.CENTER);
    }

    /**
     * Updates the game matrix of the graphical panel
     *
     * @param _gameMatrix game matrix deduced by the AI that needs to be updated in the panel
     * @param column the column where the AI played
     */
    public void updateGame(int[][] _gameMatrix, int column) {
        gamePanel.updateGame(_gameMatrix);
        helpLabel.setText(helpLabel.getText() + "\nThe machine played column " + column + ". Your turn...");
        repaint();
    }

    /**
     * Updates the game matrix of the graphical panel
     *
     * @param _gameMatrix game matrix deduced by the AI that needs to be updated in the panel
     * @return true if the update was successful
     */
    public boolean updateGame(int[][] _gameMatrix) {
        while (!gamePanel.updateGame(_gameMatrix))Thread.yield();
        repaint();
        return true;
    }

    /** End of the game; someone or something has won */
    public void endOfGame() {
        helpLabel.setText("End of the game...." + "\n" + "To play again, click on the grid...");
        repaint();
        gamePanel.endOfGame();
    }

    /**
     * @param helpText the text to set for the help label
     */
    public void setHelpLabelText(String helpText) {
        helpLabel.setText(helpText);
        repaint();
    }
}
