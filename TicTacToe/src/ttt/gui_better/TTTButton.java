package ttt.gui_better;

import ttt.engine.TTTEngine;
import javafx.scene.control.Button;

/**
 * Button for graphical Tic-Tac-Toe board.
 *
 * @author Eugene Stark
 * @version 20180211
 */
public class TTTButton extends Button {

    private static final int PREF_SIZE = 50;

    private final int row, col;
    private final TTTAppGUI gui;
    private final TTTEngine engine;

    public TTTButton(TTTAppGUI app, TTTEngine engine, int row, int col) {
        this.gui = app;
        this.engine = engine;
        this.row = row;
        this.col = col;
        
        // set preferred button dimensions
        setPrefWidth(PREF_SIZE);
        setPrefHeight(PREF_SIZE);
        
        // for max button dimensions, so buttons can resize/scale
        setMaxWidth(Double.MAX_VALUE);
        setMaxHeight(Double.MAX_VALUE);
        
        setListener();
    }

    private void setListener() {
        // configure button listener and actions for button press
        setOnAction(e -> {
            try {
                setText(engine.getPlayerToMove()
                        == TTTEngine.X_PLAYER ? "X" : "O");
                engine.makeMove(row, col);
                if (engine.gameOver()) {
                    gui.alertGameOver();
                }
            } catch (TTTEngine.IllegalMoveException x) {
                gui.alertIllegalMove();
            }
        });
    }

}
