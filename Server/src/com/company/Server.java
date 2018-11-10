//
//  Author: Zhengyu Chen
//  Date: 29 Sep 2018
//

package com.company;

import java.util.*;

import com.network.Message.Region;
import com.network.Message.SitStatus;
import com.network.Message.Status;
import com.network.Coordinates;
import com.network.Message;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.*;
import java.net.*;

public class Server {
    private int port;
    private ServerHandler serverHandler;
    private Thread serverThread;
    private boolean serverStarted = false;
    private static ArrayList<String> userList; //list of people in the lobby
    private static HashMap<String, ObjectOutputStream> LobbyPlayerList;
    private HashMap<String, ObjectOutputStream> DeskPlayerList;
    private ArrayList<String> deskList; //list of people on the table
    private Map<Integer, Thread> threadList;
    private boolean gameStarted;
    private int acceptVote = 0; //number of people accept vote
    private int votedNum = 0; //number of people finish vote
    private int passCounter = 0; //count how many player click pass
    private String currentPlayer;
    private int numLetter; //number of letter has been placed on board
    private final int maxLetter = 400; //max number of letter could place
    private static GameSession gameSession;

    public int getPort() {
        return port;
    }

    private void Print(String printString) {
        System.out.println(printString);
    }

    public Server(int port) {
        this.port = port;
        LobbyPlayerList = new HashMap<String, ObjectOutputStream>();
        DeskPlayerList = new HashMap<String, ObjectOutputStream>();
        userList = new ArrayList<String>();
        deskList = new ArrayList<String>();
        gameStarted = false;
        currentPlayer = null;
    }

    public void Start() {
        if (serverStarted) {
            Print("> Server has already started!");
            return;
        }
        serverStarted = true;

        threadList = new HashMap<>();

        serverHandler = new ServerHandler(this.port);
        serverThread = new Thread(serverHandler);
        serverThread.start();
    }

    public static void SendToUser(Message ms, String username) {
        for (String name : userList) {
            try {
                if (name.equals(username)) {
                    LobbyPlayerList.get(name).writeObject(ms);
                    LobbyPlayerList.get(name).flush();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                continue;
            }
        }
    }

    public class ServerHandler implements Runnable {
        private ServerSocket serversocket;
        int port;

        ServerHandler(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            try {
                serversocket = new ServerSocket(this.port);
                Print("> Server port number is " + port + ".");
                Print("> Server thread starts!");
                Print("> Waiting for clients to connect...");
                while (true) {
                    Socket socket = serversocket.accept();
                    Print("> Client " + socket.getPort() + " has connected!");
                    ClientHandler clientHandler = new ClientHandler(socket);
                    Thread clientThread = new Thread(clientHandler);
                    threadList.put(socket.getPort(), clientThread);
                    clientThread.start();
                }
            } catch (SocketException e) {
                Print("> Server thread closed!");
                Print("> Server Exit.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void Close() {
            try {
                int size = threadList.size();
                if (size > 0) {
                    if (size > 1)
                        Print("> " + size + " clients are still running!");
                    else
                        Print("> " + "1 clinet is still running!");
                    Print("> Please close clients first!");
                    return;
                }
                serverStarted = false;
                serversocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void ServerClose(boolean buttonClose) {
            if (buttonClose) {
                if (!serverStarted) {
                    Print("> Server has not started yet.");
                    Print("> Please start the server first.");
                    return;
                }
            }
            serverHandler.Close();
        }
    }

    public class ClientHandler implements Runnable {
        String userName;
        Socket socket;
        ObjectOutputStream objectOutputStream;
        ObjectInputStream objectInputStream;

        public void sendMsg(Message messageObj, boolean self,
                            ArrayList<String> list, HashMap<String, ObjectOutputStream> playerList) {
            // self = true: send to all user including self
            // self = false: send to all user not including self
            Thread t = Thread.currentThread();
            messageObj.setUserNameList(list);
            for (String name : list) {
                try {
                    if (!self) {
                        if (!t.getName().equals(name)) {
                            playerList.get(name).writeObject(messageObj);
                            playerList.get(name).flush();
                            playerList.get(name).reset();
                        }
                    } else {
                        playerList.get(name).writeObject(messageObj);
                        playerList.get(name).flush();
                        playerList.get(name).reset();
                    }
                } catch (Exception e) { // TODO Auto-generated catch block
                    System.out.printf("> Failed to broadcase message: %s\n", messageObj.getStatus());
                    e.printStackTrace();
                }
            }
            System.out.printf("> Broadcast message: %s.\n", messageObj.getStatus());
        }

        public void SendToUserName(Message ms, String username) {
            for (String name : userList) {
                try {
                    if (name.equals(username)) {
                        LobbyPlayerList.get(name).writeObject(ms);
                        LobbyPlayerList.get(name).flush();
                        System.out.printf("> Send to %s: %s\n", username, ms.getStatus());
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    continue;
                }
            }

        }

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                // TODO Auto-generated catch block
//                e.printStackTrace();
                System.out.println("> Failed to get in/out stream of client socket.");
            }
        }

        @Override
        public void run() {
            boolean close = false;
            try {
                Message messageReceived;
                while ((messageReceived = (Message) objectInputStream.readObject()) != null) {
                    this.userName = messageReceived.getUserName();
                    Print("> Client " + this.userName + " send an operation: " + messageReceived.getStatus());
                    switch (messageReceived.getStatus()) {
                        case CHECK:
                            Message duplicateMessage = new Message(Status.CHECK);
                            System.out.println("> User name: " + userName);
                            if (userList.contains(userName)) {
                                System.out.println("> User name duplicate!");
                                duplicateMessage.setNameDuplicate(true);
                            } else {
                                System.out.println("> User name doesn't exist!");
                                userList.add(userName);
                                LobbyPlayerList.put(this.userName, objectOutputStream);
                            }
                            objectOutputStream.writeObject(duplicateMessage);
                            objectOutputStream.flush();
                            break;
                        case INIT: // login
                            Thread initThread = Thread.currentThread();
                            initThread.setName(userName);
                            userList.add(userName);
                            LobbyPlayerList.put(this.userName, objectOutputStream);
                            Message allPlayer = new Message(Status.UPDATEUI);
                            allPlayer.setRegion(Region.LIST);
                            allPlayer.setUserName(userName);
                            allPlayer.setUserNameList(userList);
                            allPlayer.setTableList(deskList);
                            sendMsg(allPlayer, true, userList, LobbyPlayerList);
                            break;
                        case INVITE:
                            Message inviteMessage = new Message(Status.INVITE);
                            if (DeskPlayerList.containsKey(messageReceived.getInvitedUsername())) {
                                inviteMessage.setSitStatus(SitStatus.SITTED);
                                break;
                            }
                            if (DeskPlayerList.size() >= 4) {
                                inviteMessage.setSitStatus(SitStatus.FULL);
                                System.out.println(this.userName + " are invited, but table is full");
                                SendToUserName(inviteMessage, this.userName);
                            } else if (DeskPlayerList.size() < 4) {
                                System.out.println("Third branch of INVITE");
                                inviteMessage.setUserName(messageReceived.getUserName());
                                SendToUserName(inviteMessage, messageReceived.getInvitedUsername());
                                break;
                            }
                            break;
                        case INVIRESP:
                            Message InviteResponse = new Message(Status.INVIRESP);
                            if (messageReceived.getResponInvite()) {
                                DeskPlayerList.put(this.userName, objectOutputStream);
                                deskList.add(this.userName);
                            } else {
                                System.out.println("> INVIRESP do nothing");
                            }
                            break;
                        case STARTGAME:
                            //Thread startGameThread = Thread.currentThread();
                            //startGameThread.setName(userName);
                            //LobbyPlayerList.put(this.userName, objectOutputStream);
                            numLetter = 0;
                            Message startGameMessage = new Message(Status.STARTGAME);
                            startGameMessage.setUserNameList(deskList);
                            gameSession = new GameSession(deskList);
                            startGameMessage.setUserName(startGameMessage.getuserNameList().get(gameSession.getUserIndex()));
                            gameStarted = true;
                            sendMsg(startGameMessage, true, deskList, DeskPlayerList);
                            gameSession.cycle();
                            break;
                        case PLACE:
                            Message placeMessage = new Message(Message.Status.PLACE);
                            placeMessage.setCurrentLetter(messageReceived.getCurrentLetter());
                            //number of letter has been placed
                            numLetter++;
                            if (numLetter == maxLetter) {
                                Message over = new Message(Status.GAMEOVER);
                                sendMsg(over, true, deskList, DeskPlayerList);
                            }

                            int x = messageReceived.getCurrentCol();
                            int y = messageReceived.getCurrentRow();
                            Coordinates coo = new Coordinates(x, y);

                            placeMessage.setCurrentCoordinates(coo);
                            sendMsg(placeMessage, false, deskList, DeskPlayerList);
                            // to do
                            break;

                        //Current round user start vote
                        case VOTE:
                            votedNum = 0;
                            acceptVote = 0;
                            currentPlayer = this.userName;
                            Message voteMessage = new Message(Message.Status.VOTE);
                            voteMessage.setVoteWord(messageReceived.getVoteWord());
                            voteMessage.setTotalNumPlayers(DeskPlayerList.size());
                            sendMsg(voteMessage, true, deskList, DeskPlayerList);
                            break;
                        //recive rest of user's voting
                        case POLL:
                            Message pollMessage = new Message(Message.Status.POLL);
                            // to do
                            if (messageReceived.getVoteChoice()) {
                                acceptVote++;
                            }
                            votedNum++;
                            pollMessage.setVotedNum(votedNum);
                            pollMessage.setActNumPlayers(acceptVote);
                            pollMessage.setTotalNumPlayers(DeskPlayerList.size());
                            sendMsg(pollMessage, true, deskList, DeskPlayerList);
                            break;
                        case PASS:
                            //if everyone have passed, game is over;
                            passCounter++;
                            if (passCounter == DeskPlayerList.size()) {
                                Message over = new Message(Status.GAMEOVER);
                                sendMsg(over, true, deskList, DeskPlayerList);
                            }
                            gameSession.cycle();
                            break;
                        case SITDOWN:
                            Message sitdown = new Message(Status.SITDOWN);
                            if (DeskPlayerList.containsKey(this.userName)) {
                                System.out.println(this.userName + " is seated.");
                                sitdown.setSitStatus(SitStatus.SITTED);
                                SendToUserName(sitdown, this.userName);
                                break;
                            }
                            if (DeskPlayerList.size() >= 4) {
                                sitdown.setSitStatus(SitStatus.FULL);
                                System.out.println(this.userName + " wants to sitdown! but table is full");
                                SendToUserName(sitdown, this.userName);
                            } else if (DeskPlayerList.size() < 4) {
                                DeskPlayerList.put(this.userName, objectOutputStream);
                                deskList.add(this.userName);
                                sitdown.setSitStatus(SitStatus.SUCESS);
                                sitdown.setTableList(deskList);
                                System.out.println(this.userName + " wants to sitdown!");
                                sendMsg(sitdown, true, userList, LobbyPlayerList);
                            }
                            break;
                        case UPDATESCORE:
                            Message updateScoreMessage = new Message(Message.Status.UPDATESCORE);
                            //if all players accept, then get points
                            updateScoreMessage.setUserName(messageReceived.getUserName());
                            if (acceptVote == (DeskPlayerList.size())) {
                                updateScoreMessage.setAddScore(messageReceived.getAddScore());//change to current Selection.getScore();
                            } else {
                                updateScoreMessage.setAddScore(0);
                            }
                            sendMsg(updateScoreMessage, true, deskList, DeskPlayerList);
                            gameSession.cycle();
                            break;
                        case UPDATEUI:
                            break;
                        case EXIT:
                            Print("> Client exiting...");
                            // to do

                        default:
                            messageReceived.setStatus(Message.Status.IDLE);
                            Message IdleMessage = new Message(Message.Status.IDLE);
                            // to do
                    }
                    if (close) {
                        Print("> Client " + socket.getPort() + " closed.");
                        break;
                    }
                    // objectOutputStream.writeObject(message);
                    // objectOutputStream.flush();
                    // objectOutputStream.reset();
                }
            } catch (Exception e) {
                //e.printStackTrace();
                //dealing with user disconnected
                //dealing with user disconnected
                userList.remove(this.userName);
                LobbyPlayerList.remove(this.userName);
                if (DeskPlayerList.containsKey(this.userName)) {
                    DeskPlayerList.remove(this.userName);
                    deskList.remove(this.userName);
                }
                //before game, someone exit from lobby, send new name list to other user
                Message ms = new Message(Status.UPDATEUI);
                ms.setUserNameList(userList);
                ms.setUserName(this.userName);
                ms.setRegion(Region.LIST);
                //if the game started, someone down, then send other user suspend and game is over
//                }
                if (gameStarted == true) {
                    ms.setStatus(Status.SUSPEND);
                    ms.setTableList(deskList);
                    ms.setOfflineUsername(this.userName);
                    sendMsg(ms, false, deskList, DeskPlayerList);
                } else {
                    sendMsg(ms, false, userList, LobbyPlayerList);
                }
                Close();
            }
        }

        public void Close() {
            try {
                threadList.remove(socket.getPort());
                // objectInputStream.close();
                // objectOutputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public Thread[] getAllthread() {
            ThreadGroup group = Thread.currentThread().getThreadGroup();
            ThreadGroup topGroup = group;
            while (group != null) {
                topGroup = group;
                group = group.getParent();
            }
            int estimatedSize = topGroup.activeCount() * 2;
            Thread[] slackList = new Thread[estimatedSize];
            int actualSize = topGroup.enumerate(slackList);
            Thread[] Threadlist = new Thread[actualSize];
            System.arraycopy(slackList, 0, Threadlist, 0, actualSize);
            System.out.println("Thread list size == " + Threadlist.length);
            // for (Thread thread : Threadlist) { System.out.println(thread.getName()); }
            return Threadlist;
        }
    }
}
