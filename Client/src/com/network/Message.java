package com.network;

import java.io.Serializable;
import java.util.*;

public class Message implements Serializable {
    private static final long serialVersionUID = 5398666302323837503L;
    public enum Status {CHECK, INIT, INVITE, UPDATEUI,
        STARTGAME, PLACE, VOTE, UPDATESCORE, POLL,
        EXIT, IDLE, ERROR, PASS, SITDOWN, INVIRESP, GAMEOVER,
        MYTURN, SUSPEND
    }
    Status status;

    private boolean nameDuplicate = false;

    // UI update region
    public enum Region {LIST, TABLE}
    public enum SitStatus{SUCESS, FULL, SITTED}
    Region region;
    SitStatus sitstatus;

    boolean responInvite;

    String userName; // used for online player list, score pane and table update.
    int userIndex = 0;

    /* for board update */
    private int row; // row number of the board
    private int col; // col number of the board
    private String letter; // the current letter on the tile
    private boolean voteChoice;
    private ArrayList<String> userNameList;
    private ArrayList<String> TableList;
    private String invitedUsername;
    private Selection selection; //list of word along with its coordinate
    private String offlineUsername; //offline user name
    private String voteWord;
    /* for score pane update */
    private int addScore; // the score to be added

    /* for vote pane update */
    private int totalNumPlayers; // total number of players
    private int actNumPlayers; // number of players who accept the voting word
    private int votedNum = 0; //number of people finish vote
    public void setUserIndex(int userIndex) {
        this.userIndex = userIndex;
    }
    public int getTotalNumPlayers() {
        return totalNumPlayers;
    }
    public void setTotalNumPlayers(int totalNumPlayers) {
        this.totalNumPlayers = totalNumPlayers;
    }
    public void setResponInvite(boolean responInvite) {
        this.responInvite = responInvite;
    }
    public boolean getResponInvite() {
        return this.responInvite;
    }
    public int getUserIndex() {
        return userIndex;
    }
    public void setOfflineUsername(String name) {
		this.offlineUsername = name;
	}
	public String getOfflineUsername() {
		return this.offlineUsername;
	}
    public int getAddScore() {
        return addScore;
    }
    public void setAddScore(int addScore) {
        this.addScore = addScore;
    }
    public void setSitStatus(SitStatus sit) {
        this.sitstatus = sit;
    }
    public SitStatus getSitStatus() {
        return this.sitstatus;
    }

    public String getInvitedUsername() {
        return invitedUsername;
    }
    public void setInvitedUsername(String invitedUsername) {
        this.invitedUsername = invitedUsername;
    }
    public ArrayList<String> getTableList() {
        return TableList;
    }
    public void setTableList(ArrayList<String> tableList) {
        TableList = tableList;
    }
    public Message(Status status) {
        this.status = status;
    }
    public Message(Status status, ArrayList<String> userNameList) {
        this.userNameList = userNameList;
        this.status = status;
    }

    //place operation required function-letter
    public void setCurrentLetter(String letter) {
        this.letter = letter;
    }
    public String getCurrentLetter() {
        return this.letter;
    }
    //place operation required function-coordinates
    public void setCurrentCoordinates(Coordinates coo) {
        this.col = coo.getX();
        this.row = coo.getY();
    }
    public int getCurrentCol() {
        return this.col;
    }
    public int getCurrentRow() {
        return this.row;
    }
    //vote operation required function for client side
    public void setSelection(Selection sel) {
        this.selection=sel;
    }
    public Selection getSelection() {
        return this.selection;
    }
    //vote operation required function for server side
    public String getVoteWord() {
        return voteWord;
    }
    public void setVoteWord(String voteWord) {
        this.voteWord = voteWord;
    }
    //poll operation required function
    public void setVoteChoice(boolean voteChoice) {
        this.voteChoice = voteChoice;
    }
    public boolean getVoteChoice() {
        return this.voteChoice;
    }

    // login message
    // update ui message
    //number of people have voted for this string
    public void setVotedNum(int votedNum) {
        this.votedNum = votedNum;
    }
    public int getVotedNum() {
        return this.votedNum;
    }
    public void setActNumPlayers(int actNumPlayers) {
        this.actNumPlayers = actNumPlayers;
    }
    public int getActNumPlayers() {
        return this.actNumPlayers;
    }

    public Status getStatus() {
        return status;
    }
    public void setUserNameList(ArrayList<String> userNameList) {
        this.userNameList = userNameList;
    }
    public ArrayList<String> getuserNameList() {
        return this.userNameList;
    }
    public void setStatus(Status status) {
        this.status = status;
    }

    public String getUserName() { return userName; }

    public void setUserName(String userName) { this.userName = userName; }

    public Region getRegion() { return region; }

    public void setRegion(Region region) { this.region = region; }

    public boolean isNameDuplicate() {
        return nameDuplicate;
    }

    public void setNameDuplicate(boolean nameDuplicate) {
        this.nameDuplicate = nameDuplicate;
    }


}