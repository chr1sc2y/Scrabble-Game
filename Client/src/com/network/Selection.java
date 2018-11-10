package com.network;

import java.io.Serializable;

import com.Board;

/**
* @author Li Shen
* @Student_id 1001920
* @version 
* 
*/

	
public class Selection implements Serializable{
	private static final long serialVersionUID = -3906716126750882253L;
	//point filled
	private Coordinates filledPoint;
	private Coordinates startPoint;
	private Coordinates endPoint;
	private String word;

	//private int score;
	public Selection(){
		filledPoint = null;
		startPoint = null;
		endPoint = null;
		word = null;
	}

	public Selection(Coordinates startPoint, Coordinates endPoint) {
	    this.startPoint = startPoint;
	    this.endPoint = endPoint;
    }

	public int getLength() {
		switch(this.getOrientation()) {
		case "RIGHTWARD":
			return this.endPoint.getX() - this.startPoint.getX() + 1;
		case "DOWNWARD":
			return this.endPoint.getY() - this.startPoint.getY() + 1;
		case "SINGLE":
			return 1;
		}
		return 0;
	}
	
	public void setFilledPoint(Coordinates p) {
		this.filledPoint = p;
	}
	
	public void setStartPoint(Coordinates p) {
		this.startPoint = p;
	}
	
	public void setEndPoint(Coordinates p) {
		this.endPoint = p;
	}
	
	public void setPotentialWord() {
		switch(this.getOrientation()) {
		case "RIGHTWARD":
			StringBuilder strBuilder1 = new StringBuilder();
			int x1 = startPoint.getX();
			int y1 = startPoint.getY();
			for(int i = 0;i<this.getLength();i++)
			{
                strBuilder1.append(Board.cellMatrix[x1+i][y1].getText());
			}
			this.word = strBuilder1.toString();
			break;
		case "DOWNWARD":
			StringBuilder strBuilder2 = new StringBuilder();
			int x2 = startPoint.getX();
			int y2 = startPoint.getY();
			for(int i = 0;i<this.getLength();i++)
			{
                strBuilder2.append(Board.cellMatrix[x2][y2+i].getText());
			}
			this.word = strBuilder2.toString();
			break;
		case "SINGLE":
			this.word = Board.cellMatrix[filledPoint.getX()][filledPoint.getY()].getText();
			break;
		}
	}
	
	public String getPotentialWord() {
		setPotentialWord();
		return this.word;
	}
	
	public int getScore() {
		return this.getLength();
	}

	public Coordinates getFilledPoint() {
		return this.filledPoint;
	}
	
	public Coordinates getStartPoint() {
		return this.startPoint;
	}
	
	public Coordinates getEndPoint() {
		return this.endPoint;
	}
	
	public String getOrientation() {
		if(this.startPoint == null || this.endPoint == null) {
			return null;
		}
		if(this.startPoint.getX() < this.endPoint.getX()) {
			return "RIGHTWARD"; //left-to-right
		}
		else if(this.startPoint.getY() < this.endPoint.getY()){
			return "DOWNWARD"; //up-to-down
		}
		else
			return "SINGLE";
	}

}

