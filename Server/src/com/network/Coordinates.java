package com.network;

import java.io.Serializable;

/**
* @author Li Shen
* @Student_id 1001920
* @version 
* 
*/
public class Coordinates implements Serializable{
	private static final long serialVersionUID = -2565714992068974789L;
	private final int column;
	private final int row;
	
	public Coordinates(int x, int y) {
		column = x;
		row = y;
	}
	
	//make sure hashmap put function succeed by overriding equals and hashcode function
	@Override
	public boolean equals(Object obj) {
		if(this==obj)
			return true;
		Coordinates coo = (Coordinates)obj;
		if(coo.getX() == this.column && coo.getY() == this.row) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return 157^column*137^row*19017;
	}
	
//	public String getChar() {
//		return Board.charPair.get(this);
//	}
	
	public int getX() {
		return column;
	}
	
	public int getY() {
		return row;
	}
	
}
