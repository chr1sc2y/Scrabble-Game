package com.player;

import java.util.Observable;

public class Status extends Observable {
    // true if it's a real player's turn, for single-player mode
    private static boolean myTurn = false;
    private static boolean ifPlaced = false;
    private static boolean ifVoted = false;
    public static void setMyTurn(boolean val) {
        myTurn = val;
        if(myTurn == true)
        {
        	System.out.println("It's my turn.");
        }
        else {
			System.out.println("My turn is over.");
        }
    }

    public static boolean isMyTurn() {
        return myTurn;
    }
    
    public static void setIfPlaced(boolean bool) {
        ifPlaced = bool;
    }
    
    public static boolean getIfPlaced() {
        return ifPlaced;
    }
    
    public static void  setIfVoted(boolean bool) {
        ifVoted = bool;
    }
    
    public static boolean getIfVoted() {
        return ifVoted;
    }
}
