package datamodel;

public class TimePoint {
	
	private double x;
	private double y;
	private int frameNum;
	
	public TimePoint(double x, double y, int frameNum) {
		this.x = x;
		this.y = y;
		this.frameNum = frameNum;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public org.opencv.core.Point getPointOpenCV() {
		return new org.opencv.core.Point(x, y);
	}
	
	public java.awt.Point getPointAWT() {
		return new java.awt.Point((int)x, (int)y);
	}
	
	public String toString() {
		return String.format("%.1f,%.1f@T=%d", x, y, frameNum);
	}
	
	public int getFrameNum() {
		return frameNum;
	}
	
	public double getDistanceTo(TimePoint other) {
		double dx = other.x-x;
		double dy = other.y-y;
		return Math.sqrt(dx*dx+dy*dy);
	}
	
	public int getTimeDiffAfter(TimePoint other) {
		return this.frameNum - other.frameNum;
	}
	
	public int compareTo(TimePoint other) {		
		return this.getTimeDiffAfter(other);
	}
}
