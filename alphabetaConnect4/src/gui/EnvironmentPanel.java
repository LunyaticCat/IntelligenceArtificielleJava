package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import model.Connect4;
import model.PlayerType;

/**
 * Panel on which the game environment is displayed.<br>
 * The user can select a column to play by clicking with the mouse.
 * @author emmanuel adam
 */
@SuppressWarnings("serial")
public class EnvironmentPanel extends JPanel {

    /** The game containing the AI */
    Connect4 game;

    /** Display of tokens as labels */
    JLabel[][] labelMatrix;

    /** Buttons to choose the column to play */
    JButton[] buttonArray;

    /** The displayed game matrix */
    int[][] gameMatrix;

    /** Width of a cell in pixels */
    int cellWidth;

    /** Height of a cell in pixels */
    int cellHeight;

    /** End of game flag */
    boolean endOfGame;

    /** Window containing the panel */
    Connect4Window parent;

    /**
     * Defines the panel and mouse behavior.
     * @param _width width of the panel
     * @param _height height of the panel
     * @param _parent parent window
     * @param _game the game instance
     */
    EnvironmentPanel(int _width, int _height, Connect4Window _parent, Connect4 _game) {
        parent = _parent;
        setSize(_width, _height);
        game = _game;
        gameMatrix = game.getGameMatrix();

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (!endOfGame) {
                    int column = e.getX() / cellWidth;
                    parent.setHelpLabelText("The machine is thinking...");
                    validate();
                    game.roundGame(column);
                } else {
                    endOfGame = false;
                    game.init();
                    gameMatrix = game.getGameMatrix();
                    repaint();
                }
            }
        });
    }

    /** Paints the game environment */
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // To have a good rendering
        RenderingHints qualityHints = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(qualityHints);
        g2d.setColor(Color.BLUE);
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

        // Compute the size of a cell in pixels
        cellWidth = (int) (((double) getWidth()) / ((double) Connect4.WIDTH));
        cellHeight = (int) (((double) getHeight()) / ((double) Connect4.HEIGHT));

        BasicStroke thickStroke = new BasicStroke(2.0f);
        BasicStroke thinStroke = new BasicStroke(1.0f);

        int row = Connect4.HEIGHT;
        for (int i = 0; i < Connect4.HEIGHT; i++) {
            row--;
            for (int j = 0; j < Connect4.WIDTH; j++) {
                g2d.setColor(Color.BLACK);
                g2d.fillOval(j * cellWidth + 2, i * cellHeight + 2, cellWidth - 4, cellHeight - 4);

                g2d.setColor(Color.DARK_GRAY);
                g2d.drawArc(j * cellWidth + 2, i * cellHeight + 2, cellWidth - 4, cellHeight - 4, -135, 180);

                if (gameMatrix[row][j] == 0 || gameMatrix[row][j] == -1) {
                    g2d.setColor(Color.GRAY);
                    g2d.fillOval(j * cellWidth + 4, i * cellHeight + 4, cellWidth - 8, cellHeight - 8);
                }

                if (gameMatrix[row][j] == PlayerType.PLAYER.getType()) {
                    g2d.setColor(Color.YELLOW);
                    g2d.fillOval(j * cellWidth + 4, i * cellHeight + 4, cellWidth - 8, cellHeight - 8);
                    g2d.setStroke(thickStroke);
                    g2d.setColor(Color.WHITE);
                    g2d.drawArc(j * cellWidth + 4, i * cellHeight + 4, cellWidth - 8, cellHeight - 8, 45, 180);
                    g2d.setColor(Color.ORANGE);
                    g2d.drawArc(j * cellWidth + 4, i * cellHeight + 4, cellWidth - 8, cellHeight - 8, -135, 180);
                    g2d.setStroke(thinStroke);
                }

                if (gameMatrix[row][j] == PlayerType.MACHINE.getType()) {
                    g2d.setColor(Color.RED);
                    g2d.fillOval(j * cellWidth + 4, i * cellHeight + 4, cellWidth - 8, cellHeight - 8);
                    g2d.setStroke(thickStroke);
                    g2d.setColor(Color.PINK);
                    g2d.drawArc(j * cellWidth + 4, i * cellHeight + 4, cellWidth - 8, cellHeight - 8, 45, 180);
                    g2d.setColor(Color.MAGENTA);
                    g2d.drawArc(j * cellWidth + 4, i * cellHeight + 4, cellWidth - 8, cellHeight - 8, -135, 180);
                    g2d.setStroke(thinStroke);
                }
            }
        }
    }

    /**
     * Updates the display based on the received game matrix.
     * @param _gameMatrix game matrix to display
     * @return true if the update was successful
     */
    boolean updateGame(int[][] _gameMatrix) {
        gameMatrix = _gameMatrix;
        repaint();
        try {
            Thread.sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /** End of the game; someone or something has won */
    void endOfGame() {
        endOfGame = true;
    }
}
