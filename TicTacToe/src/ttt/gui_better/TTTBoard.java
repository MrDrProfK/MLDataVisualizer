package ttt.gui_better;

import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import ttt.engine.TTTEngine;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

/**
 * Graphical Tic-Tac-Toe board.
 *
 * @author Eugene Stark
 * @version 20180211
 */
public class TTTBoard extends GridPane {

    private final TTTAppGUI gui;
    private final TTTEngine engine;
    private final int dim;

    public TTTBoard(TTTAppGUI app, TTTEngine engine) {
        this.gui = app;
        this.engine = engine;
        this.dim = engine.getDim();
        setConstraints();
        addButtons();
    }

    private void setConstraints() {
        // make board resizeable/scalable 
        for (int i = 0; i < dim; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setVgrow(Priority.ALWAYS);
            rc.setFillHeight(true);
            getRowConstraints().add(rc);
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            cc.setFillWidth(true);
            getColumnConstraints().add(cc);
        }

    }

    private void addButtons() {
        // populate board with buttons
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                Button btn = new TTTButton(gui, engine, i, j);

                add(btn, j, i);
            }
        }
    }

}
