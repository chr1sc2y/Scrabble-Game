/**
 * @author Rongxin Zhu
 * @Student_id 938816
 */

package com;
import com.player.Player;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;

import java.io.IOException;

/* the layout of the main game window */
public class GameWindow extends BorderPane {
    private final int prefWidth = 900; // preference width of the game window
    private final int prefHeight = 600; // preference height of the game window
    public static Board board; // board to place letters
    
    public GameWindow(Player[] players, Parent sideRoot) {
        // set window size
        this.setPrefSize(prefWidth, prefHeight);

        // add board to the window
        board = new Board();
        this.setCenter(board);

        // add score pane to the window
//        scorePane = new ScorePane(players);
        this.setRight(sideRoot);
//        try {
//            Parent sidePane = FXMLLoader.load(getClass().getResource("SideView.fxml"));
//            this.setRight(sidePane);
//        } catch (IOException e) {
//            System.out.println("Failed to load SideView.fxml");
//        }

        // set controller
//        this.setStyle("-fx-controller");
    }

    /* Show information dialog. */
    public static void showDialog(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.getDialogPane().setHeaderText(null);
            alert.showAndWait();
        });
    }
}
