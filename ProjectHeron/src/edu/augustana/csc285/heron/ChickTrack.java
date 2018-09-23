package edu.augustana.csc285.heron;

import java.util.ArrayList;
import java.util.List;
/**
 * Description: This class is to track a particular chick
 * 
 * @author Ehren Braun
 * @date 9/18/2018
 *
 */
public class ChickTrack {
	
	private List<TimePosition> positionHistory;
	private String chickID;
	
	/**
	 * This constructs the ChickTrack given the ID of the chick
	 * and the initial time position
	 * @param chickName -the chick ID that will be assigned
	 * @param spot -the initial location at the time the chick is constructed
	 * 
	 */
	public ChickTrack(String chickName, TimePosition spot) {
		positionHistory = new ArrayList<TimePosition>();
		positionHistory.add(spot);
		chickID = chickName;
	}
	
	/**
	 * This adds a data point to the arrayList of points given
	 * the coordinates of the chick and the time of the video
	 * @param time -the time in the recording
	 * @param x -the x coordinate of the chick's location
	 * @param y -the y coordinate of the chick's location
	 */
	public void addPosition(int time, int x, int y) {
		positionHistory.add(new TimePosition(time, x, y));
	}
	
	/**
	 * This adds a data point to the arrayList of points given
	 * a TimePosition object
	 * @param xy -the TimePosition of the chick
	 */
	public void addPosition(TimePosition xy) {
		positionHistory.add(xy);
	}
	
	/**
	 * This gives the ID of the chick that was given to the constructor
	 * @return -the ID given to the chick
	 */
	public String getChickID() {
		return chickID;
	}
	
	/**
	 * This gives the history of the TimePositions of the the chick
	 * @return
	 */
	public List<TimePosition> getPositionHistory(){
		return positionHistory;
	}
	
}
