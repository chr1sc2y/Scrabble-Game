package com;

import com.network.Coordinates;

/**
 * @author Li Shen
 * @Student_id 1001920
 * @version
 * 
 */

public class CharPoint {
	private Coordinates coo;
	private char character;

	public CharPoint() {

	}

	public CharPoint(int x, int y) {
		coo = new Coordinates(x, y);
	}

	public CharPoint(int x, int y, char ch) {
		coo = new Coordinates(x, y);
		character = ch;
	}

	public CharPoint(Coordinates c, char ch) {
		coo = c;
		character = ch;
	}
	
	public int getX() {
		return this.coo.getX();
	}

	public int getY() {
		return this.coo.getY();
	}

	public char getChar() {
		return this.character;
	}
	
	public void setChar(char c) {
		this.character = c;
	}

}
