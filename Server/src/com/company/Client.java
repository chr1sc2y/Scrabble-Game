package com.company;

import java.net.*;

import com.network.Message.*;
import com.network.Message;
import java.util.*;

import java.io.*;

public class Client {
	private static String name;
	private static ObjectOutputStream output;
	private static ObjectInputStream input;
	private Socket socket;
	private Scanner scan;

	public Client() throws UnknownHostException, IOException {
		this.socket = new Socket("localhost", 55555);
		output = new ObjectOutputStream(socket.getOutputStream());
		System.out.println("Enter username: ");
		scan = new Scanner(System.in);
		name = scan.nextLine();
		Message ms = new Message(Status.INIT);
		ms.setUserName(name);
		output.writeObject(ms);
		output.flush();
	}

	public void createConnection() {
		try {
			// output = new ObjectOutputStream(socket.getOutputStream());
			ServerHandler handler = new ServerHandler();
			Thread t = new Thread(handler);
			t.start();
			String username;
			while (!(username = scan.nextLine()).equals("no")) {
				if(username.equals("pass")) {
					Message ms = new Message(Status.PASS);
					ms.setUserName(this.name);
					ms.setVoteChoice(true);
					output.writeObject(ms);
					// output.reset();
					output.flush();
				}else if(username.equals("vote")) {
					Message ms = new Message(Status.VOTE);
					ms.setVoteWord("hello");
					output.writeObject(ms);
					output.flush();
				}else if(username.equals("sit")) {
					Message ms = new Message(Status.SITDOWN);
					ms.setUserName(name);
					output.writeObject(ms);
					output.flush();
				}else if(username.equals("start")) {
					Message ms = new Message(Status.STARTGAME);
					ms.setUserName(name);
					output.writeObject(ms);
					output.flush();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class ServerHandler implements Runnable {
		@Override
		public void run() {
			Scanner scan = new Scanner(System.in);
			// TODO Auto-generated method stub
			try {
				ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
				Message ms;
				while ((ms = (Message) input.readObject()) != null) {
					System.out.println("I have got the message");
					switch (ms.getStatus()) {
					case PASS:
						System.out.println(ms.getUserName() + " decide to pass");
						break;
					case VOTE:
						System.out.println("Do u agree with word ["+ms.getVoteWord()+"] [y/n]");
						String temp = scan.next();
						System.out.println("temp = "+temp);
						Message mess = new Message(Status.POLL);
						if(temp.equals("y")) {
							System.out.println("im here!");
							mess.setVoteChoice(true);
						}else if(temp.equals("n")){
							mess.setVoteChoice(false);
						}
						output.writeObject(mess);
						output.flush();
						break;
					case POLL:
						System.out.println(ms.getActNumPlayers()+" peole accept the word is correct");
						break;
					case SITDOWN:
						switch(ms.getSitStatus()) {
						case SUCESS:
							System.out.println("i sit down success!!!");
							break;
						case FULL:
							System.out.println("WTF, TABLE IS FULL!!");
							break;
						case SITTED:
							System.out.println("I am already in the table!");
						}
						break;
					case UPDATEUI:
						switch (ms.getRegion()) {
						case LIST:
							System.out.println("--------Current online player----------");
							for (String name : ms.getuserNameList()) {
								System.out.println(name);
							}
							System.out.println("---------------------------------------");
						}
						break;
					case SUSPEND:
						System.out.println("someone is down, the game is over");
						System.out.println("--------Current online player----------");
						for (String name : ms.getuserNameList()) {
							System.out.println(name);
						}
						System.out.println("---------------------------------------");
					}
				}

			} catch (IOException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
