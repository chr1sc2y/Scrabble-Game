/**
 * @author Rongxin Zhu
 * @Student_id 938816
 */

package com;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import com.network.Selection;

public class Board extends GridPane{
    private static final int numCols = 20; // number of columns of the board
    private static final int numRows = 20; // number of rows of the board
    private final int prefWidth = 600; // preference width of the board
    private final int prefHeight = 600; // preference height of the board

    //store info about current string
	public static Selection selection;
	//public static Map<Coordinates, String> charPair= new HashMap();
	public static CharCell[][] cellMatrix = new CharCell[numCols][numRows];
	public static boolean[][] occupied = new boolean[numCols][numRows];
	
    public Board() {
        createBoard();
    }

    private void createBoard() {
        this.setPadding(new Insets(1));
        this.setPrefSize(prefWidth, prefHeight);

        /* set style for the board */
        this.getStyleClass().add("board");

        /* resize constraints for columns and rows */
        for (int x = 0; x < numCols; x++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setFillWidth(true);
            cc.setHgrow(Priority.ALWAYS);
            this.getColumnConstraints().add(cc);
        }

        for (int y = 0; y < numRows; y++) {
            RowConstraints rc = new RowConstraints();
            rc.setFillHeight(true);
            rc.setVgrow(Priority.ALWAYS);
            this.getRowConstraints().add(rc);
        }

        /* create cell for every position on the board */
        for (int x = 0; x < numCols; x++) {
            for (int y = 0; y < numRows; y++) {
            	cellMatrix[x][y] = new CharCell();
            	this.add(cellMatrix[x][y].getCellPane(), x, y);
                //this.add(createCell(switches[x][y]), x, y);
            }
        }
    }
    
    public static int getMaxCol() {
    	return numCols;
    }
    
    public static int getMaxRow() {
    	return numRows;
    }

    /* place a tile on the board */
    public static void placeTile(int row, int col, String letter) {
        Platform.runLater(() -> {
            if (!hasTile(row, col)) {
                CharCell cell = cellMatrix[row][col];
                Label character = cell.getLabel();
                StackPane cellPane = cell.getCellPane();
                character.setText(letter.toUpperCase());
                cellPane.getStyleClass().remove(cellPane.getStyleClass().indexOf("cell"));
                cellPane.getStyleClass().add("filledCell");
                cellPane.getStyleClass().add("hiliCell");
                occupied[row][col] = true;
            } else {
                System.out.printf("There is already a tile for this position (%d, %d)\n", row, col);
            }
        });
    }

    /* check if a postion on the board is occupied by a tile */
    public static boolean hasTile(int row, int col) {
        return occupied[row][col];
    }
    
}
