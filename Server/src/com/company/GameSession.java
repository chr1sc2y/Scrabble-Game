package com.company;

import com.network.Message;

import java.util.ArrayList;

/**
 * multi-player in turn mechanism controller
 */

public class GameSession {

    private int userIndex;

    private ArrayList<String> userList;
    private int maxUserNumber;

    public GameSession(ArrayList<String> userList) {
        this.userIndex = 0;
        this.userList = userList;
        this.maxUserNumber = userList.size();
        for (String user: userList) {
            System.out.println(user);
        }
    }

    public void cycle(){
        Message turnMsg = new Message(Message.Status.MYTURN);
        Server.SendToUser(turnMsg, userList.get(userIndex));
        ++userIndex;
        if(userIndex == maxUserNumber)
            userIndex = 0;
    }

    public int getUserIndex() {
        return userIndex;
    }

    public void setUserIndex(int userIndex) {
        this.userIndex = userIndex;
    }

    public ArrayList<String> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<String> userList) {
        this.userList = userList;
    }
}

