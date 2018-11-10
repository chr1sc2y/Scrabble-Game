/**
 * @author Rongxin Zhu
 * @Student_id 938816
 */

package com;

import com.player.Player;
import com.player.Status;

/**
 * This is the game session of a single game, in which multiple players engaged in.
 * It implements the multi-player-in-turn mechanism.
 */

public class GameSession implements Runnable{
    private static Player[] players;

    public GameSession(Player[] players) {
        GameSession.players = players;
    }

    public void run() {
        // basic multiplayer in-turn mechanism
        //  based on a loop on players
//        int i = 0;
//        while (true) {
//            // the real player can only place a tile in his turn
//            Status.setTurnForRealPlayer(i == 2);
//            ScorePane.setStyleForUser(players[i].getName(), "-fx-text-fill: red");
//            // place a tile
//            players[i].placeTile();
////            players[i].poll();
//            ScorePane.setStyleForUser(players[i].getName(), "-fx-text-fill: black");
//            System.out.println(i + " done");
//            // move to the next player and go back to the first player if necessary
//            i = (i + 1) % players.length;
//        }
    }

    public static Player getPlayer(int i) {
        return players[i];
    }

    /**
     * check if game is over. the game is over if:
     * 1) a player logout in the middle of the game
     * TODO: some other rules to be added.
     * @return
     */
    private boolean gameOver() {
        return false;
    }


}

