package gui;

import javax.swing.*;
import model.Connect4;
import model.PlayerType;
import java.awt.*;
import java.awt.event.*;

/**
 * A very simple panel for displaying and placing a token.
 * @author emmanueladam
 */
@SuppressWarnings("serial")
class GamePanel extends JPanel implements ActionListener {

    /** The game containing the AI */
    Connect4 game;

    /** Display of tokens as labels */
    JLabel[][] tokenLabels;

    /** Buttons to choose the column to play */
    JButton[] columnButtons;

    /** The displayed game matrix */
    int[][] gameMatrix;

    /**
     * Constructor
     * @param _game the AI game attached to this window
     */
    GamePanel(Connect4 _game) {
        game = _game;
        tokenLabels = new JLabel[Connect4.HEIGHT][Connect4.WIDTH];
        columnButtons = new JButton[Connect4.WIDTH];

        GridBagLayout gridBagLayout = new GridBagLayout();
        this.setLayout(gridBagLayout);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.CENTER;
        constraints.gridy = 0;

        for (int i = 0; i < Connect4.WIDTH; i++) {
            columnButtons[i] = new JButton("" + i);
            columnButtons[i].addActionListener(this);
            constraints.gridx = i;
            gridBagLayout.setConstraints(columnButtons[i], constraints);
            add(columnButtons[i], constraints);
        }

        for (int i = 0; i < Connect4.HEIGHT; i++) {
            for (int j = 0; j < Connect4.WIDTH; j++) {
                tokenLabels[i][j] = new JLabel("-");
            }
        }

        for (int i = 0; i < Connect4.HEIGHT; i++) {
            constraints.gridy = i + 1;
            for (int j = 0; j < Connect4.WIDTH; j++) {
                constraints.gridx = j;
                gridBagLayout.setConstraints(tokenLabels[Connect4.HEIGHT - 1 - i][j], constraints);
                add(tokenLabels[Connect4.HEIGHT - 1 - i][j], constraints);
            }
        }
    }

    /**
     * Constructor
     * @param _gameMatrix the game matrix to display
     */
    GamePanel(int[][] _gameMatrix) {
        tokenLabels = new JLabel[Connect4.HEIGHT][Connect4.WIDTH];
        this.setLayout(new GridLayout(Connect4.HEIGHT, Connect4.WIDTH - 1));

        for (int i = 0; i < Connect4.HEIGHT; i++) {
            for (int j = 0; j < Connect4.WIDTH; j++) {
                tokenLabels[i][j] = new JLabel("" + _gameMatrix[i][j]);
            }
        }

        for (int i = 0; i < Connect4.HEIGHT; i++) {
            for (int j = 0; j < Connect4.WIDTH; j++) {
                add(tokenLabels[Connect4.HEIGHT - 1 - i][j]);
            }
        }
    }

    /**
     * Updates the display based on the received game matrix
     * @param _gameMatrix game matrix to display
     */
    void updateGame(int[][] _gameMatrix) {
        gameMatrix = _gameMatrix;
        for (int i = 0; i < Connect4.HEIGHT; i++) {
            for (int j = 0; j < Connect4.WIDTH; j++) {
                int playerType = gameMatrix[i][j];
                if (playerType == 0) {
                    tokenLabels[i][j].setText("-");
                } else if (playerType == PlayerType.PLAYER.getType()) {
                    tokenLabels[i][j].setText("O");
                } else if (playerType == PlayerType.MACHINE.getType()) {
                    tokenLabels[i][j].setText("X");
                }
            }
        }
    }

    /** End of the game; someone or something has won. Buttons are disabled. */
    void endOfGame() {
        for (JButton columnButton : columnButtons) {
            columnButton.setEnabled(false);
        }
    }

    /**
     * Reaction to the user clicking a button -> adds a token in the chosen column
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        int columnNumber = Integer.parseInt(command);
        game.roundGame(columnNumber);
    }
}
