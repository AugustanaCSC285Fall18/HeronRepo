package edu.augustana.csc285.heron;

public class TimePosition {
	
	private int x;
	private int y;
	private int time;
	
	public TimePosition(int time, int x, int y) {
		this.x = x;
		this.y = y;
		this.time = time;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getTime() {
		return time;
	}
}
