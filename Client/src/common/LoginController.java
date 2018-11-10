package common;
/**
 * @author Rongxin Zhu
 * @Student_id 938816
 */

import com.ErrorHandling;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.network.Message;
import com.sun.security.ntlm.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Modality;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class LoginController {
    private static String playerName;

    private static LoginController instance;

    private static boolean isDuplicate = false;
    private static boolean isCheckFinished = false;

    private static boolean isServerCorrect = true;

    public static boolean isIsServerCorrect() {
        return isServerCorrect;
    }

    public static void setIsServerCorrect(boolean isServerCorrect) {
        LoginController.isServerCorrect = isServerCorrect;
    }

    public static boolean isIsCheckFinished() {
        return isCheckFinished;
    }

    public static void setIsCheckFinished(boolean isCheckFinished) {
        LoginController.isCheckFinished = isCheckFinished;
    }

    public static boolean isIsDuplicate() {
        return isDuplicate;
    }

    public static void setIsDuplicate(boolean isDuplicate) {
        LoginController.isDuplicate = isDuplicate;
    }

    public static LoginController getInstance() {
        return instance;
    }

    public LoginController() {
        instance = this;
    }

    @FXML
    JFXButton btnLogin;

    @FXML
    JFXTextField textFieldUsername;

    @FXML
    JFXTextField textFieldServerIP;

    @FXML
    JFXTextField textFieldServerPort;

    @FXML
    private void handleLoginButtonAction() throws InterruptedException {
        setIsDuplicate(false);
        setIsServerCorrect(true);

        // get username and server info
        String userName = textFieldUsername.getText();
        playerName = userName;
        String serverIP = textFieldServerIP.getText();
        String portString = textFieldServerPort.getText();

        ErrorHandling errorHandling = ErrorHandling.getInstance();
        boolean isNameCorrect = errorHandling.HandleName(playerName);
        if (!isNameCorrect) {
            showDialog("User name is wrong.\n" + "The length of name should be between 1 to 10 digits.\n" + "The name can only contain of 0-9 and a-z.");
            return;
        }

        boolean isIPCorrect = errorHandling.HandleIP(serverIP);
        if (!isIPCorrect) {
            showDialog("IP address is wrong.\n" + "Please input an IP between 0.0.0.0 and 255.255.255.255 or localhost.");
            return;
        }

        boolean isPortCorrect = errorHandling.HandlePort(portString);
        if (!isPortCorrect) {
            showDialog("Port number is wrong.\nPlease input an port number from 49152 to 65535.");
            return;
        }
        int serverPort = Integer.parseInt(portString);

        // connect to server
        ClientListener.getInstance().createConnection(serverIP, serverPort);
        // start the network handler
        ClientListener.getInstance().startHandler();

        if (!isIsServerCorrect()) {
            showDialog("Server is wrong.");
            return;
        }

        //send check message
//        Message checkMsg = new Message(Message.Status.CHECK);
//        checkMsg.setUserName(userName);
//        ClientListener.getInstance().sendMsg(checkMsg);

        Thread.sleep(50);

        if (isIsDuplicate())
            return;

        // send login message
        Message loginMsg = new Message(Message.Status.INIT);
        loginMsg.setUserName(userName);
        ClientListener.getInstance().sendMsg(loginMsg);
        // switch to lobby scene.
        MainLauncher.switchToLobby();
    }
//        List<String> players = new ArrayList<>();
//        players.add("111");
//        players.add("222");
//        players.add("333");
//        MainLauncher.switchToGame(players);

    public String getPlayerName() {
        return playerName;
    }

    /* Show information dialog. */
    private static void showDialog(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.getDialogPane().setHeaderText(null);
            alert.showAndWait();
        });
    }

    void duplicateHandler() {
        showDialog("The user name has already exists.\n" + "Please input another user name.");
        return;
    }
}
