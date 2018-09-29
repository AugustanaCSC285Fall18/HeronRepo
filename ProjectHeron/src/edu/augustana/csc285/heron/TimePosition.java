package edu.augustana.csc285.heron;

public class TimePosition {
	
	private double x;
	private double y;
	private int time;
	
	public TimePosition(int time, double x, double y) {
		this.x = x;
		this.y = y;
		this.time = time;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public int getTime() {
		return time;
	}
}
