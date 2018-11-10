package com;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.network.Coordinates;
import com.network.Message;
import com.network.Selection;
import com.player.Player;
import com.player.Status;

import common.ClientListener;
import common.LoginController;
import common.MainLauncher;
import common.SideController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import sun.applet.Main;

/**
 * @author Li Shen
 * @Student_id 1001920
 * @version
 * 
 */
public class CharCell {
	private final int cellWidth = 26; // preference width of the tile
	private final int cellHeight = 26; // preference height of the tile
	private final Paint filledColor = Color.valueOf("#FFFFFF");// bg color of filled char
	private final Paint hiliColor = Color.valueOf("#FF9400");// bg color of highlight char
	private Board board = MainLauncher.getInstance().getBoard();

	//test flag for block other filling operation after one is done
	private int isFilledThisTurn = 0;
	
	private StackPane cellPane;
	private Label character;

	private int column; // column as x
	private int row; // row as y

	public CharCell() {
		cellPane = new StackPane();
		character = new Label();
		cellPane.setPrefSize(cellWidth, cellHeight);
		cellPane.getChildren().add(character);
		cellPane.getStyleClass().add("cell");
		character.setMouseTransparent(true);
		character.setAlignment(Pos.CENTER);
		character.setText(null);
		// listener for click
		cellPane.setOnMouseClicked(e -> {
			// get (c,r)
			column = GridPane.getColumnIndex(cellPane);
			row = GridPane.getRowIndex(cellPane);
			// the real player can only place a tile in his turn
			if (Status.isMyTurn() && !Status.getIfPlaced()) {
                popInsertDialog();
                // place tile done, change status to break the while loop for placeTile() of a player
//			    Status.setPlaceDone(true);
            }
			// current cell needs to be disabled and status needs to be transmitted to
			// server for broadcasting
		});
	}

	/* get the character of the cell */
    public String getText() {
        return this.character.getText();
    }

	public void popInsertDialog() {
		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle("Insert");
		dialog.setHeaderText("You should type one single character.");

		// set the button types
		ButtonType insertType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(insertType, ButtonType.CANCEL);

		// add text field character
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));
		TextField charText = new TextField();
		charText.setPromptText("Enter a character");
		charText.setPrefWidth(250);

		// set conditions for input character to be valid
		Node insertButton = dialog.getDialogPane().lookupButton(insertType);
		insertButton.setDisable(true);
		charText.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.split(" ").length != 1 || newValue.length() != 1
					|| !((newValue.charAt(0) >= 'a' && newValue.charAt(0) <= 'z')
							|| (newValue.charAt(0) >= 'A' && newValue.charAt(0) <= 'Z'))) {
				insertButton.setDisable(true);
			} else {
				insertButton.setDisable(false);
			}
		});

		grid.add(new Label("Insert:"), 0, 0);
		grid.add(charText, 1, 0);
		dialog.getDialogPane().setContent(grid);
		charText.requestFocus();

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == insertType) {
				return charText.getText();
			}
			return null;
		});

		Optional<String> result = dialog.showAndWait();
		result.ifPresent(insertChar -> {
			Status.setIfPlaced(true);
			updateBoard(insertChar.toUpperCase(),column,row);//update current tile and its listener
			//send placeMessage to server
			Message placeMessage = new Message(Message.Status.PLACE);
			placeMessage.setUserName(LoginController.getInstance().getPlayerName());
			placeMessage.setCurrentLetter(Board.cellMatrix[column][row].character.getText());
			placeMessage.setCurrentCoordinates(new Coordinates(column, row));
			ClientListener.getInstance().sendMsg(placeMessage);
			SideController.getInstance().setTextAreaProgress("Please choose a word to vote or pass your term.");
		});
	}
	
	public static void updateBoard(String currChar,int col,int row) {
		// empty route before recording selection
		Board.selection = new Selection();
		//Board.charPair.put(new Coordinates(column, row), insertChar.toLowerCase());
		Board.selection.setFilledPoint(new Coordinates(col, row));
		
		// Set highlight for current cell, which cannot be cancelled until the whole
		// operation of this user is done.
		Board.cellMatrix[col][row].character.setText(currChar);
		Board.cellMatrix[col][row].cellPane.getStyleClass().remove(Board.cellMatrix[col][row].cellPane.getStyleClass().indexOf("cell"));
		Board.cellMatrix[col][row].cellPane.getStyleClass().add("filledCell");
		Board.cellMatrix[col][row].cellPane.getStyleClass().add("hiliCell");
					
        // clear hilight style of all cells
        //clearHighlight();

		// find potential words based on the current cell
//        List<Word> potentialWords = findPotentialWords(row, column);

        
		// setup new listener
		Board.cellMatrix[col][row].cellPane.setOnMouseEntered(e -> {
			if (Status.isMyTurn() && Board.selection.getFilledPoint() != null && !Status.getIfVoted()) {
				if (new Coordinates(col, row).equals(Board.selection.getFilledPoint())) { // single char
					Board.selection.setStartPoint(new Coordinates(col, row));
					Board.selection.setEndPoint(new Coordinates(col, row));
					return;
				}
				int i;
				if (col	 == Board.selection.getFilledPoint().getX()) { // select highlight in vertical direction
					//remove highlight of other nodes
					for (int x = 0; x < Board.getMaxCol(); x++) {
						// jump current cell if it is the filled cell
						if (new Coordinates(x, Board.selection.getFilledPoint().getY()).equals(Board.selection.getFilledPoint())) {
							continue;
						}
						if (Board.cellMatrix[x][Board.selection.getFilledPoint().getY()].cellPane.getStyleClass().contains("hiliCell")) {
							Board.cellMatrix[x][Board.selection.getFilledPoint().getY()].cellPane.getStyleClass()
									.remove(Board.cellMatrix[x][Board.selection.getFilledPoint().getY()].cellPane.getStyleClass().indexOf("hiliCell"));
						}
					}
					for (i = Board.selection.getFilledPoint().getY(); i >= 0; i--) { // filledPoint to start
						if (Board.cellMatrix[col][i].getText() != null		
								&& Board.cellMatrix[col][i].getText().toLowerCase().charAt(0) >= 'a'
								&& Board.cellMatrix[col][i].getText().toLowerCase().charAt(0) <= 'z') {
							
							// show highlight
							if (Board.cellMatrix[col][i].cellPane.getStyleClass().contains("hiliCell")) {
								Board.cellMatrix[col][i].cellPane.getStyleClass().remove(
										Board.cellMatrix[col][i].cellPane.getStyleClass().indexOf("hiliCell"));
							}
							Board.cellMatrix[col][i].cellPane.getStyleClass().add("hiliCell");
						} else {
							// char chain break here, record start point
							Board.selection.setStartPoint(new Coordinates(col, i + 1));
							//System.out.println("up-to-down start set");
							break;
						}
					}
					for (i = Board.selection.getFilledPoint().getY(); i <= Board.getMaxRow() - 1; i++) { // filledPoint to end
						if (Board.cellMatrix[col][i].getText() != null
								&& Board.cellMatrix[col][i].getText().toLowerCase().charAt(0) >= 'a'
								&& Board.cellMatrix[col][i].getText().toLowerCase().charAt(0) <= 'z') {
							// show highlight
							if (Board.cellMatrix[col][i].cellPane.getStyleClass().contains("hiliCell")) {
								Board.cellMatrix[col][i].cellPane.getStyleClass().remove(
										Board.cellMatrix[col][i].cellPane.getStyleClass().indexOf("hiliCell"));
							}
							Board.cellMatrix[col][i].cellPane.getStyleClass().add("hiliCell");
						} else { // char chain break here, record end point
							Board.selection.setEndPoint(new Coordinates(col, i - 1));
							//System.out.println("up-to-down end set");
							break;
						}
					}
				} else if (row == Board.selection.getFilledPoint().getY()) { // select highlight in horizontal direction
					//remove highlight of column
					for (int y = 0; y < Board.getMaxRow(); y++) {
						// jump current cell if it is the filled cell
						if (new Coordinates(col, y).equals(Board.selection.getFilledPoint())) {
							continue;
						}
						if (Board.cellMatrix[Board.selection.getFilledPoint().getX()][y].cellPane.getStyleClass().contains("hiliCell")) {
							Board.cellMatrix[Board.selection.getFilledPoint().getX()][y].cellPane.getStyleClass()
									.remove(Board.cellMatrix[Board.selection.getFilledPoint().getX()][y].cellPane.getStyleClass().indexOf("hiliCell"));
						}
					}
					for (i = Board.selection.getFilledPoint().getX(); i >= 0; i--) { // filledPoint to start
						if (Board.cellMatrix[i][row].getText() != null
								&& Board.cellMatrix[i][row].getText().toLowerCase().charAt(0) >= 'a'
								&& Board.cellMatrix[i][row].getText().toLowerCase().charAt(0) <= 'z') {
							// show highlight
							if (Board.cellMatrix[i][row].cellPane.getStyleClass().contains("hiliCell")) {
								Board.cellMatrix[i][row].cellPane.getStyleClass()
										.remove(Board.cellMatrix[i][row].cellPane.getStyleClass().indexOf("hiliCell"));
							}
							Board.cellMatrix[i][row].cellPane.getStyleClass().add("hiliCell");
						} else {
							// char chain break here, record start point
							Board.selection.setStartPoint(new Coordinates(i + 1, row));
							//System.out.println("left-to-right start set");
							break;
						}
					}
					for (i = Board.selection.getFilledPoint().getX(); i <= Board.getMaxCol() - 1; i++) { // filled Point to end
						if (Board.cellMatrix[i][row].getText() != null
								&& Board.cellMatrix[i][row].getText().toLowerCase().charAt(0) >= 'a'
								&& Board.cellMatrix[i][row].getText().toLowerCase().charAt(0) <= 'z') {
							// show highlight
							if (Board.cellMatrix[i][row].cellPane.getStyleClass().contains("hiliCell")) {
								Board.cellMatrix[i][row].cellPane.getStyleClass()
										.remove(Board.cellMatrix[i][row].cellPane.getStyleClass().indexOf("hiliCell"));
							}
							Board.cellMatrix[i][row].cellPane.getStyleClass().add("hiliCell");
						} else { // char chain break here, record end point
							Board.selection.setEndPoint(new Coordinates(i - 1, row));
							//System.out.println("left-to-right end set");
							break;
						}
					}
				} else { // other grid, recover changed grid color
					for (int x = 0; x < Board.getMaxCol(); x++) {
						for (int y = 0; y < Board.getMaxRow(); y++) {
							if (new Coordinates(x, y).equals(Board.selection.getFilledPoint())) { // single char
								continue;
							}
							if (Board.cellMatrix[x][y].cellPane.getStyleClass().contains("hiliCell")) {
								Board.cellMatrix[x][y].cellPane.getStyleClass()
										.remove(Board.cellMatrix[x][y].cellPane.getStyleClass().indexOf("hiliCell"));
							}
						}
					}
				}
            }
		});
		Board.cellMatrix[col][row].cellPane.setOnMouseClicked(e -> {
			// rule: for start point: x or y should be the same as inserted point;
			// for end point: either x or y should be the same as both start point and
			// inserted point
			if (Status.isMyTurn() && Board.selection.getFilledPoint() != null && !Status.getIfVoted()) {
				for (int x = 0; x < Board.getMaxCol(); x++) {
					for (int y = 0; y < Board.getMaxRow(); y++) {
						if (new Coordinates(x, y).equals(Board.selection.getFilledPoint())) {
							continue;
						}
						if (Board.cellMatrix[x][y].getCellPane().getStyleClass().contains("hiliCell")) {
							Board.cellMatrix[x][y].getCellPane().getStyleClass()
									.remove(Board.cellMatrix[x][y].getCellPane().getStyleClass().indexOf("hiliCell"));
						}
					}
				}
				if (Board.selection.getFilledPoint().equals(new Coordinates(col, row))) {
					if(col == 0 && row > 0 && row < 19
							&& (Board.cellMatrix[col+1][row].getText() != null
							|| Board.cellMatrix[col][row-1].getText() != null
							|| Board.cellMatrix[col][row+1].getText() != null)) {
						
					}else if(col == 19 && row > 0 && row < 19
							&& (Board.cellMatrix[col-1][row].getText() != null
							|| Board.cellMatrix[col][row-1].getText() != null
							|| Board.cellMatrix[col][row+1].getText() != null)) {
						
					}else if(row == 0 && col > 0 && col < 19
							&& (Board.cellMatrix[col-1][row].getText() != null
							|| Board.cellMatrix[col+1][row].getText() != null
							|| Board.cellMatrix[col][row+1].getText() != null)) {
						
					}else if(row == 19 && col > 0 && col < 19
							&& (Board.cellMatrix[col-1][row].getText() != null
							|| Board.cellMatrix[col+1][row].getText() != null
							|| Board.cellMatrix[col][row-1].getText() != null)) {
						
					} else if (col == 0 && row == 0 && (Board.cellMatrix[col + 1][row].getText() != null
							|| Board.cellMatrix[col][row + 1].getText() != null)) {

					} else if (col == 19 && row == 0 && (Board.cellMatrix[col - 1][row].getText() != null
							|| Board.cellMatrix[col][row + 1].getText() != null)) {

					} else if (col == 19 && row == 19 && (Board.cellMatrix[col - 1][row].getText() != null
							|| Board.cellMatrix[col][row - 1].getText() != null)) {

					} else if (col == 0 && row == 19 && (Board.cellMatrix[col][row - 1].getText() != null
							|| Board.cellMatrix[col + 1][row].getText() != null)) {

					} else if (Board.cellMatrix[col - 1][row].getText() != null
							|| Board.cellMatrix[col + 1][row].getText() != null
							|| Board.cellMatrix[col][row - 1].getText() != null
							|| Board.cellMatrix[col][row + 1].getText() != null) {

					}
					else { // single-char word
						Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
						alert.setTitle("Comfirmation");
						alert.setHeaderText("Confirm to select the string "+Board.selection.getPotentialWord()+" as your word?");
						alert.setContentText("String: " + Board.selection.getPotentialWord()+", Potential score: "+ Board.selection.getScore());
						Optional ifConfirm = alert.showAndWait();
						if(ifConfirm.get() == ButtonType.OK) { // selected word confirmed
							Status.setIfVoted(true);// vote operation is done for current user
							//select potential word and send voteMessage to server
							Message voteMessage = new Message(Message.Status.VOTE);
							voteMessage.setUserName(LoginController.getInstance().getPlayerName());
							voteMessage.setSelection(Board.selection);
							voteMessage.setVoteWord(Board.selection.getPotentialWord());
							ClientListener.getInstance().sendMsg(voteMessage);
						}
					}
				}
				else { // multi-char word, a row or a column
					if(col != Board.selection.getFilledPoint().getX() //click cell not in either route
							&& row != Board.selection.getFilledPoint().getY()) {
						
					}
					else {
						Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
						alert.setTitle("Confirmation");
						alert.setHeaderText("Confirm to select the string "+Board.selection.getPotentialWord()+" as your word?");
						alert.setContentText("String: " + Board.selection.getPotentialWord()+", Potential score: "+Board.selection.getScore());
						Optional ifConfirm = alert.showAndWait();
						if(ifConfirm.get() == ButtonType.OK) { // selected word confirmed
							Status.setIfVoted(true);// vote operation is done for current user
							//select potential word and send voteMessage to server
							Message voteMessage = new Message(Message.Status.VOTE);
							voteMessage.setUserName(LoginController.getInstance().getPlayerName());
							voteMessage.setSelection(Board.selection);
							voteMessage.setVoteWord(Board.selection.getPotentialWord());
							ClientListener.getInstance().sendMsg(voteMessage);
						}
					}
				}
			}
		});
	}

	public StackPane getCellPane() {
		return this.cellPane;
	}

	public Label getLabel() {
		return this.character;
	}

	public int getColumn() {
		return this.column;
	}

	public int getRow() {
		return this.row;
	}

}
