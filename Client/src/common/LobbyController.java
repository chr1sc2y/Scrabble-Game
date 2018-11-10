package common;
/**
 * @author Rongxin Zhu
 * @Student_id 938816
 */


import com.PlayerItem;
import com.jfoenix.controls.JFXButton;
import com.network.Message;
import com.player.Player;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LobbyController {
    private List<String> playersInTable = new ArrayList<>();
    private List<String> onlinePlayers = new ArrayList<>();
    private boolean[] tableOccupied = new boolean[4];

    private static LobbyController instance;

    public LobbyController() { instance = this; }

    public static LobbyController getInstance() { return instance; }

    @FXML
    Button btnStart; // start game button

    @FXML VBox playerList; // show names of online users

    // buttons for players to join a table
    @FXML JFXButton btnJoin1;
    @FXML JFXButton btnJoin2;
    @FXML JFXButton btnJoin3;
    @FXML JFXButton btnJoin4;

    /* action for join button */
    @FXML
    public void btnJoinAction() {
        // send SITDOWN message to server
        Message sitMsg = new Message(Message.Status.SITDOWN);
        sitMsg.setUserName(LoginController.getInstance().getPlayerName());
        ClientListener.getInstance().sendMsg(sitMsg);
    }

    /* action for "start" button */
    @FXML
    public void btnStartAction() {
        Message startGameMessage = new Message(Message.Status.STARTGAME);
        startGameMessage.setUserName(LoginController.getInstance().getPlayerName());
        //startGameMessage.setUserName(Thread.currentThread().getName());
        ClientListener.getInstance().sendMsg(startGameMessage);
    }

    /* show name of online player in the online list */
    public void addPlayerInfo(Player player) {
        Platform.runLater(() -> {
//            Parent playItem = FXMLLoader.
            String playerName = player.getName();
            if (!onlinePlayers.contains(playerName)) {
                onlinePlayers.add(playerName);
                playerList.getChildren().add(new PlayerItem(playerName));
            }
        });
    }

    /* show name of the player in the current game before start */
    public void addPlayerToTable(Player player) {
        Platform.runLater(() -> {
            switch (playersInTable.size()) {
                case 0:
                    if (!playersInTable.contains(player.getName())) {
                        btnJoin1.setText(player.getName());
                        btnJoin1.setStyle("-fx-background-color: #FFFF8D; -fx-text-fill: black");
                        playersInTable.add(player.getName());
                    }
                    break;
                case 1:
                    if (!playersInTable.contains(player.getName())) {
                        btnJoin2.setText(player.getName());
                        btnJoin2.setStyle("-fx-background-color: #FFFF8D; -fx-text-fill: black");
                        playersInTable.add(player.getName());
                    }
                    break;
                case 2:
                    if (!playersInTable.contains(player.getName())) {
                        btnJoin3.setText(player.getName());
                        btnJoin3.setStyle("-fx-background-color: #FFFF8D; -fx-text-fill: black");
                        playersInTable.add(player.getName());
                    }
                    break;
                case 3:
                    if (!playersInTable.contains(player.getName())) {
                        btnJoin4.setText(player.getName());
                        btnJoin4.setStyle("-fx-background-color: #FFFF8D; -fx-text-fill: black");
                    }
                    break;
                default:
                    break;
            }
        });
    }

    /* invitation confirmation dialog */
    public void showConfirmationDialog(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            Optional <ButtonType> action = alert.showAndWait();
            if (action.get() == ButtonType.OK) {
                // send accept invitation message to server
//                Message acceptInviteMsg = new Message(Message.Status.INVIRESP);
//                acceptInviteMsg.setUserName(LoginController.getInstance().getPlayerName());
//                acceptInviteMsg.setResponInvite(true);
//                ClientListener.getInstance().sendMsg(acceptInviteMsg);

                // send SITDOWN message to server
                Message sitMsg = new Message(Message.Status.SITDOWN);
                sitMsg.setSitStatus(Message.SitStatus.SUCESS);
                sitMsg.setUserName(LoginController.getInstance().getPlayerName());
                ClientListener.getInstance().sendMsg(sitMsg);
            } else if (action.get() == ButtonType.CANCEL) {
                // send reject invitation message to server
//                Message rejectInviteMsg = new Message(Message.Status.INVIRESP);
//                rejectInviteMsg.setUserName(LoginController.getInstance().getPlayerName());
//                rejectInviteMsg.setResponInvite(false);
//                ClientListener.getInstance().sendMsg(rejectInviteMsg);
            }
        });
    }

    /* Show information dialog. */
    public void showDialog(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.getDialogPane().setHeaderText(null);
            alert.showAndWait();
        });
    }

    public void showOnlinePlayers(List<String> players) {
        Platform.runLater(() -> {
            playerList.getChildren().removeAll();
            for (String playerName: players) {
                System.out.printf("> Update online player: %s\n", playerName);
                LobbyController.getInstance().addPlayerInfo(new Player(playerName));
            }
        });
    }

    /* check if the current player has joined a table */
    public boolean inTable() {
//        System.out.println("Online players:");
//        for (String p: onlinePlayers) {
//            System.out.println(p);
//        }
//        System.out.println("current player: " + LoginController.getInstance().getPlayerName());
        return playersInTable.contains(LoginController.getInstance().getPlayerName());
    }
}
