package common;
/**
 * @author Kai Liu, Student Id
 * @author Rongxin Zhu, 938816
 */


import com.*;
import com.network.Message;
import com.network.Selection;
import com.player.Player;
import com.player.Status;

import javafx.application.Platform;
import javafx.geometry.Side;

import java.net.*;

import java.io.*;
import java.util.List;

public class ClientListener implements Runnable {
	private static String ip; // server ip
	private static int port; // server port
	private static ObjectOutputStream output;
	private static ObjectInputStream input;
	private static Socket socket;

	private static ClientListener instance;

	public ClientListener() { instance = this; }

	public static ClientListener getInstance() { return instance; }

	public ClientListener(String ip, int port){
		this.ip = ip;
		this.port = port;
	}

    @Override
    public void run() {}

    public void startHandler() {
        // start a network handler thread
        Thread t = new Thread(() -> networkHandler(socket));
        t.start();
    }

    public void createConnection(String ip, int port) {
        try {
            // create socket
            socket = new Socket(ip, port);
            // create input and output stream
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());
            System.out.println("> Connection established.");
        } catch (IOException e) {
            LoginController loginController = LoginController.getInstance();
            loginController.setIsServerCorrect(false);
            System.out.println(">> Failed to create connection with server.");
        }
    }

    public void endConnection() {
        try {
            socket.close();
            output.close();
            input.close();
            System.out.println("> Connection ended.");
        } catch (IOException e) {
            System.out.println(">> Failed to end connection with server.");
        }
    }

	/* update UI according to the message from server */
	private void networkHandler(Socket s) {
        try {
            Message response;
//            while (socket.isConnected()) {
            while ((response = (Message) input.readObject()) != null) {
                System.out.println("> Received a message: " + response.getStatus());
                switch (response.getStatus()) {
                    case CHECK:
                        boolean isNameDuplicate = response.isNameDuplicate();
                        System.out.println("> Duplicate: " + isNameDuplicate);
                        if (isNameDuplicate) {
                            LoginController loginController = LoginController.getInstance();
                            loginController.setIsDuplicate(true);
                            loginController.setIsCheckFinished(true);
                            loginController.duplicateHandler();
                        }
                        break;
                        case UPDATEUI: //update lobby UI
                            switch (response.getRegion()) {
                                case LIST: {
                                    LobbyController.getInstance().showOnlinePlayers(response.getuserNameList());
                                    System.out.println("-----------------------------------");
                                    if (response.getTableList() != null) {
                                        for (String player: response.getTableList()) {
                                            LobbyController.getInstance().addPlayerToTable(new Player(player));
                                        }
                                    }
                                    break;
                                }
                            }
                            break;
                        case INVITE:
                            System.out.printf("> Receive invitation from %s\n", response.getUserName());
                            LobbyController.getInstance().showConfirmationDialog("Invitation Conformation",
                                    "Do you want to join " + response.getUserName() + "'s table?");
                            break;
                        case SITDOWN: {
                            System.out.println("> receive SITDOWN message from server.");
                            switch (response.getSitStatus()) {
                                case SUCESS: {
                                    List<String> playersInTable = response.getTableList();
                                    for (String player : playersInTable) {
                                        LobbyController.getInstance().addPlayerToTable(new Player(player));
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                        case PLACE://get broadcasted place operation message
                        	int col = response.getCurrentCol();
                        	int row = response.getCurrentRow();
                        	String letter = response.getCurrentLetter();
                        	Platform.runLater(() -> {
                        		CharCell.updateBoard(letter, col, row);
                            	System.out.println("PLACE broadcast done!");
                        	});
//                            SideController.getInstance().setTextAreaProgress("Select a string to vote or pass the term");
                        	break;
                        case VOTE://get broadcasted vote request
                        	String word = response.getVoteWord();
                        	int totalNum1 = response.getTotalNumPlayers();
                        	Platform.runLater(() -> {
//                        		VotePane.title.setVisible(true);
//                            	VotePane.votingWord.setVisible(true);
//                            	VotePane.votingSituation.setVisible(true);
//                            	VotePane.btnConfirm.setVisible(true);
//                            	VotePane.btnReject.setVisible(true);
//                            	VotePane.showPotentialWord(word);
//                            	VotePane.votingSituation.setText("0/"+totalNum1);
//                            	VotePane.btnPass.setVisible(false);
                                SideController.getInstance().setVoteWord(word);
                                SideController.getInstance().voteBegin(true);
                                SideController.getInstance().setConfRejDisable(false);//enable vote button
                                SideController.getInstance().setPassIfDisable(true);
                            	System.out.println("VOTE broadcast done!");
                        	});
                            SideController.getInstance().setTextAreaProgress("Please confirm or reject the voting and waiting for others to vote.");
                        	//TODO: get the number of players and show in voting UI!!!!!!!!
                        	break;
                        case POLL://update poll info
                            //test
//                            SideController.getInstance().voteBegin(false);
                        	//TODO:update UI in voting panel, if all vote true, updatescore;
                        	int voteYesNum = response.getActNumPlayers();
                        	int votedNum = response.getVotedNum();
                        	int totalNum2 = response.getTotalNumPlayers();

                        	SideController.getInstance().setVoteResult(voteYesNum+" / " + totalNum2);
                        	if(votedNum == totalNum2)//no matter how many vote yes, leave server judge and switch turn to the next person
                    		{
                                for (int x = 0; x < Board.getMaxCol(); x++) {
                                    for (int y = 0; y < Board.getMaxRow(); y++) {
                                        if (Board.cellMatrix[x][y].getCellPane().getStyleClass().contains("hiliCell")) {
                                            Board.cellMatrix[x][y].getCellPane().getStyleClass()
                                                    .remove(Board.cellMatrix[x][y].getCellPane().getStyleClass().indexOf("hiliCell"));
                                        }
                                    }
                                }
                    			Message upScoreMessage = new Message(Message.Status.UPDATESCORE);
                    			if(Status.isMyTurn())//only the one in turn can return right score to server
                    			{
                    				Status.setMyTurn(false);//all polling done, current user switch to the next
                        			upScoreMessage.setUserName(LoginController.getInstance().getPlayerName());
                        			System.out.println(upScoreMessage.getUserName());
                        			upScoreMessage.setAddScore(Board.selection.getScore());
                        			sendMsg(upScoreMessage);
                    			}
                    		}
                    		System.out.println("POLL behavior done!");
//                            SideController.getInstance().setTextAreaProgress("Waiting for others to vote");
                        	break;
                        case PASS://pass chance of place operation 
                        	//won't receive PASS message
                            SideController.getInstance().setTextAreaProgress("Waiting for others to place a character");
                        	break;
                        case UPDATESCORE://
                        	int addScore = response.getAddScore();
                        	String addName = response.getUserName();
                            SideController.getInstance().addScoreForPlayer(addName, addScore);
                            SideController.getInstance().setTextAreaProgress("Waiting for others to place a character");
                        	break;
                        case STARTGAME:
                            // start
                            System.out.println(">> Start game!");
                            //MainLauncher.switchToGame(response.getTableList());
                            MainLauncher.switchToGame(response.getuserNameList());
                            System.out.println(">> Done!");
                            SideController.getInstance().setTextAreaProgress("Waiting for others to place a character");
                            break;
                        case MYTURN:
                            // empty route before recording selection
                    		Board.selection = new Selection();
                            Status.setMyTurn(true);
                            Status.setIfPlaced(false);
                            Status.setIfVoted(false);
                            SideController.getInstance().setPassIfDisable(false);
                            SideController.getInstance().setTextAreaProgress("Please place a character");
                            break;
                        case GAMEOVER:
                        	//TODO:if game over, print final result in progress panel;
                            SideController.getInstance().setTextAreaProgress("Game over!");
                        	
                        	break;
                        case SUSPEND:
                        	//TODO:if suspend, print final result in progress panel;
                            SideController.getInstance().setTextAreaProgress("Game end!");
                            SideController.getInstance().showWinPlayer();
                        	
                        	break;
                        default:
                            System.out.println(">> Illegal message status.");
                    }

//                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
//            System.out.println(">> Message handling failed.");
        }
    }

    /* send message to the server */
    public void sendMsg(Message msg) {
        try {
            output.writeObject(msg);
            output.flush();
            System.out.printf("> Send %s message to server.\n", msg.getStatus());
        } catch (IOException e) {
            System.out.println(">> sendMsg failed.");
        }
    }


}
