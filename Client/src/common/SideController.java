package common;

import com.Board;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.network.Message;
import com.player.Status;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SideController {
    private int numPlayers;

    @FXML
    JFXTextArea textAreaProgress;
    @FXML
    Label labelUser1;
    @FXML
    Label labelUser2;
    @FXML
    Label labelUser3;
    @FXML
    Label labelUser4;
    @FXML
    JFXTextField textFieldScore1;
    @FXML
    JFXTextField textFieldScore2;
    @FXML
    JFXTextField textFieldScore3;
    @FXML
    JFXTextField textFieldScore4;
    @FXML
    JFXTextField textFieldVoteWord;
    @FXML
    JFXButton btnConfirm;
    @FXML
    JFXButton btnReject;
    @FXML
    JFXButton btnPass;
    @FXML
    Label labelResult;
    @FXML
    JFXTextField textFieldResult;

    // singleton
    public static SideController instance;

    public SideController () {
        // no player at the beginning
        numPlayers = 0;
        instance = this;
    }

    public static SideController getInstance() { return instance; }

    @FXML
    private void handleConfirmAction() {
//        addPlayer(String.valueOf(numPlayers));
        //send confirm pollMessage to server
        Message pollMessage = new Message(Message.Status.POLL);
        pollMessage.setUserName(LoginController.getInstance().getPlayerName());
        pollMessage.setVoteChoice(true);
        ClientListener.getInstance().sendMsg(pollMessage);
        voteBegin(false);
        setConfRejDisable(true);
    }

    @FXML
    private void handleRejectAction() {
//        addPlayer(String.valueOf(numPlayers));
            //send confirm pollMessage to server
        Message pollMessage = new Message(Message.Status.POLL);
        pollMessage.setUserName(LoginController.getInstance().getPlayerName());
        pollMessage.setVoteChoice(false);
        ClientListener.getInstance().sendMsg(pollMessage);
        voteBegin(false);
        setConfRejDisable(true);
    }

    @FXML
    private void handlePassAction() {
        //send passMessage to server
        Message passMessage = new Message(Message.Status.PASS);
        passMessage.setUserName(LoginController.getInstance().getPlayerName());
        ClientListener.getInstance().sendMsg(passMessage);
        for (int x = 0; x < Board.getMaxCol(); x++) {
			for (int y = 0; y < Board.getMaxRow(); y++) {
				if (Board.cellMatrix[x][y].getCellPane().getStyleClass().contains("hiliCell")) {
					Board.cellMatrix[x][y].getCellPane().getStyleClass()
							.remove(Board.cellMatrix[x][y].getCellPane().getStyleClass().indexOf("hiliCell"));
				}
			}
		}
        Status.setMyTurn(false);
        setPassIfDisable(true);
    }

    public void addPlayer(String playerName) {
        System.out.println("add " + playerName);
        Platform.runLater(() -> {
            if (numPlayers == 0) {
                labelUser1.setVisible(true);
                labelUser1.setText(playerName);
                textFieldScore1.setVisible(true);
                textFieldScore1.setText("0");
                numPlayers++;
            } else if (numPlayers == 1) {
                labelUser2.setVisible(true);
                labelUser2.setText(playerName);
                textFieldScore2.setVisible(true);
                textFieldScore2.setText("0");
                numPlayers++;
            } else if (numPlayers == 2) {
                labelUser3.setVisible(true);
                labelUser3.setText(playerName);
                textFieldScore3.setVisible(true);
                textFieldScore3.setText("0");
                numPlayers++;
            } else if (numPlayers == 3) {
                labelUser4.setVisible(true);
                labelUser4.setText(playerName);
                textFieldScore4.setVisible(true);
                textFieldScore4.setText("0");
                numPlayers++;
            }
        });
    }

    public void addScoreForPlayer(String playerName, int score) {
        int newScore;
        if (playerName.equals(labelUser1.getText())) {
            newScore = Integer.valueOf(textFieldScore1.getText()) + score;
            textFieldScore1.setText(String.valueOf(newScore));
        } else if (playerName.equals(labelUser2.getText())) {
            newScore = Integer.valueOf(textFieldScore2.getText()) + score;
            textFieldScore2.setText(String.valueOf(newScore));
        } else if (playerName.equals(labelUser3.getText())) {
            newScore = Integer.valueOf(textFieldScore3.getText()) + score;
            textFieldScore3.setText(String.valueOf(newScore));
        } else if (playerName.equals(labelUser4.getText())) {
            newScore = Integer.valueOf(textFieldScore4.getText()) + score;
            textFieldScore4.setText(String.valueOf(newScore));
        }
    }

    public void setVoteWord(String word) {
        Platform.runLater(() -> {
            textFieldVoteWord.setText(word.toLowerCase());
        });
    }

    public void setResultStatus(boolean status) {
        Platform.runLater(() -> {
            labelResult.setVisible(status);
            textFieldResult.setVisible(status);
        });
    }

    public void setButtonStatus(boolean status) {
        Platform.runLater(() -> {
            btnConfirm.setVisible(status);
            btnReject.setVisible(status);
        });
    }

    public void voteBegin(boolean status) {
        // if status is true,  hide voting result,
        // show confirm and reject bottons
        Platform.runLater(() -> {
            setResultStatus(!status);
            setButtonStatus(status);
        });
    }
    
    public void setConfRejDisable(boolean status){
        Platform.runLater(() -> {
            btnConfirm.setDisable(status);
            btnReject.setDisable(status);
        });
    }

    public void setPassIfDisable(boolean status){
        Platform.runLater(() -> {
            btnPass.setDisable(status);
        });
    }

    public void setVoteResult(String result) {
        Platform.runLater(() -> {
            textFieldResult.setText(result);
        });
    }

    public JFXTextArea getTextAreaProgress() {
        return textAreaProgress;
    }

    public void setTextAreaProgress(String progress) {
        Platform.runLater(() -> {
            textAreaProgress.setText(progress);
        });
    }

    public void addTextAreaProgress(String progress) {
        Platform.runLater(() -> {
           textAreaProgress.appendText("\n"+progress);
        });
    }

    public void showWinPlayer() {
        Platform.runLater(() -> {
            int[] scores = new int[numPlayers];
            if (numPlayers >= 1) {
                scores[0] = Integer.valueOf(textFieldScore1.getText());
            }
            if (numPlayers >= 2) {
                scores[1] = Integer.valueOf(textFieldScore2.getText());
            }
            if (numPlayers >= 3) {
                scores[2] = Integer.valueOf(textFieldScore3.getText());
            }
            if (numPlayers >= 4) {
                scores[3] = Integer.valueOf(textFieldScore4.getText());
            }
            // game result
            boolean tie = true;
            int maxScore = scores[0];
            int maxIndex = 0;
            String winPlayer = "";
            for (int i = 1; i < numPlayers; i++) {
                if (scores[i] != scores[i-1]) {
                    tie = false;
                }
                if (scores[i] > maxScore) {
                    maxScore = scores[i];
                    maxIndex = i;
                }
            }
            if (maxIndex == 0) {
                winPlayer = labelUser1.getText();
            } else if (maxIndex == 1) {
                winPlayer = labelUser2.getText();
            } else if (maxIndex == 2) {
                winPlayer = labelUser3.getText();
            } else if (maxIndex == 3) {
                winPlayer = labelUser4.getText();
            }

            // game draw
            if (tie) {
                addTextAreaProgress("Game draw.");
            } else {
                // someone win
                addTextAreaProgress(winPlayer + " wins.");

            }
        });
    }
}
