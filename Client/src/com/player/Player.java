package com.player;

public class Player {
    private String name;
    private int score;

    public Player() {
        setName("Player");
        setScore(0);
        Thread t = new Thread(() -> getNofificationAndOperate());
        t.start();
    }

    public Player(String name) {
        setName(name);
        setScore(0);
    }

    public Player(String name, int score) {
        setName(name);
        setScore(score);
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setName(String name) {
        this.name = name;
    }

    private void getNofificationAndOperate() {

    }
}
