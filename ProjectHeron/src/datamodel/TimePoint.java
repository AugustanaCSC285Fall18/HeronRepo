package datamodel;

/**
 *@author Team Heron
 * Represents a Timepoint in the video.
 */
public class TimePoint implements Comparable<TimePoint> {

   /**
	 * This Timepoint's x value.
	 */
	private double x;
	/**
	 * This Timepoint's y value.
	 */
	private double y;
	/**
	 * This Timepoint's frame number.
	 */
	private int frameNum;
	
	/**
	 * Creates a new Timepoint with the given x,
	 * y, and frame number values.
	 * @param x This Timepoint's horizontal position.
	 * @param y This Timepoint's vertical position.
	 * @param frameNum This Timepoint's frame number.
	 */
	public TimePoint(double x, double y, int frameNum) {
		this.x = x;
		this.y = y;
		this.frameNum = frameNum;
	}
	
	/**
	 * Gets this Timepoint's horizontal position.
	 * @return this Timepoint's x value.
	 */
	public double getX() {
		return x;
	}
	
	/**
	 * Gets this Timepoint's vertical position.
	 * @return this Timepoint's y value.
	 */
	public double getY() {
		return y;
	}
	
	/**
	 * Constructs a point at this Timepoint's
	 * x and y values (OpenCV).
	 * @return the initialized Point.
	 */
	public org.opencv.core.Point getPointOpenCV() {
		return new org.opencv.core.Point(x, y);
	}
	
	/**
	 * Constructs a point at this Timepoint's 
	 * x and y values (java.awt).
	 * @return the initialized Point.
	 */
	public java.awt.Point getPointAWT() {
		return new java.awt.Point((int)x, (int)y);
	}
	
	/**
	 * Prints this Timepoint.
	 * @return this Timepoint's position and 
	 * 		   frame number.
	 */
	public String toString() {
		return String.format("%.1f,%.1f@T=%d", x, y, frameNum);
	}
	
	/**
	 * Gets the frame number of this Timepoint.
	 * @return this Timepoint's frame number.
	 */
	public int getFrameNum() {
		return frameNum;
	}
	
	/**
	 * Gets the distance between points made at different
	 * frames.
	 * @param other The Timepoint where the second point 
	 * 				is drawn.
	 * @return the distance between the two drawn points.
	 */
	public double getDistanceTo(TimePoint other) {
		double dx = other.x-x;
		double dy = other.y-y;
		return Math.sqrt(dx*dx+dy*dy);
	}
	
	/**
	 * Gets the time difference between frames.
	 * @param other The Timepoint whose frame is being
	 * 				compared.
	 * @return the number of frames between the frames
	 * 		   of the Timepoints.
	 */
	public int getTimeDiffAfter(TimePoint other) {
		return this.frameNum - other.frameNum;
	}
	
	/**
	 * Checks if the object being compared is a Timepoint.
	 * @param o The object being checked to see if it is
	 * 			a Timepoint.
	 * @return checks if all fields are equal, and returns
	 * 		   true if all fields match, and false otherwise.  
	 */
	public boolean equals(Object o) {
		if(o instanceof TimePoint) {
			TimePoint p = (TimePoint) o;
			return this.x == p.x && this.y == p.y && this.frameNum == p.frameNum;
		}
		return false;
	}
	
	/**
	 * Compare Timepoints
	 * @param other The timepoint to compare 
	 * 				with
	 * @return an integer that tells if the
	 * 		   compared Timepoint should come
	 * 		   before or after the Timepoint
	 * 		   it is compared to.
	 */
	public int compareTo(TimePoint other) {		
		return this.getTimeDiffAfter(other);
	}
}
