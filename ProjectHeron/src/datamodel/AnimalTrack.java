package datamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.scene.paint.Color;
/**
 * Description: This class is to track a particular animal
 * 
 * @author Team Heron
 * @date 9/18/2018
 *
 */
public class AnimalTrack {
	
	private List<TimePoint> positionHistory;
	private String animalID;
	private Color color;
	
	/**
	 * This constructs the AnimalTrack given the ID of the animal
	 * and the initial time point
	 * @param chickName -the animal ID that will be assigned
	 */
	public AnimalTrack(String animalName) {
		positionHistory = new ArrayList<TimePoint>();
		animalID = animalName;
	}
	
	/**
	 * This adds a data point to the arrayList of points given
	 * the coordinates of the animals and the time of the video
	 * @param  -the time in the recording
	 * @param x -the x coordinate of the animal's location
	 * @param y -the y coordinate of the animal's location
	 */
	public void add(double x, double y, int time) {
		positionHistory.add(new TimePoint(x, y, time));
		Collections.sort(positionHistory);
	}
	
	/**
	 * This adds a data point to the arrayList of points given
	 * a TimePoint object
	 * @param xy -the TimePosition of the chick
	 */
	public void add(TimePoint xy) {
		positionHistory.add(xy);
		Collections.sort(positionHistory);
	}
	
	/**
	 * This method gives the TimePoint based on the index
	 * @param index -the location in the array
	 * @return -TimePoint at the index of the array
	 */
	public TimePoint TimePointAtIndex(int index) {
		return positionHistory.get(index);
	}
	
	/**
	 * This method gives the TimePoint based on the frame number
	 * if there is no TimePoint for that frame
	 * @param frameNum -the frame number that is being looked for
	 * @return -the TimePoint with the correct frame number, returns null if not found
	 */
	public TimePoint getTimePointAtTime(int frameNum) {
		for(TimePoint point : positionHistory) {
			if(point.getFrameNum() == frameNum) {
				return point;
			}
		}
		return null;
	}
	
	/**
	 * This gives the ID of the animal that was given to the constructor
	 * @return -the ID given to the animal
	 */
	public String getAnimalID() {
		return animalID;
	}
	
	/**
	 * This gives a string representation of an AnimalTrack showing the id, the number
	 * of points, the start frame, and the end frame.
	 */
	public String toString() {
		int startFrame = positionHistory.get(0).getFrameNum();
		int endFrame = getFinalTimePoint().getFrameNum();
		return "AnimalTrack[id=" + animalID + ",numPts=" + size() + " startFrame=" + startFrame + " endFrame=" + endFrame + "]";
	}
	
	/**
	 * This method tells whether there is a TimePoint with the frame
	 * number already in the AnimalTrack. If there is not, then it is
	 * false
	 * @param frameNum -the frame number that is being looked for
	 * @return -whether there is a TimePoint with the frame number
	 */
	public boolean alreadyHasTime(int frameNum) {
		for(TimePoint point : positionHistory) {
			if(point.getFrameNum() == frameNum) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method gives the closest TimePoint to the frame number passed
	 * in. If there no TimePoints to look through, it returns null.
	 * @param frameNum -the frame number that is being asked for
	 * @return -the TimePoint with the closest frame number to the parameter
	 */
	public TimePoint getClosestTimePoint(int frameNum) {
		TimePoint closest = null;
		int closestTime = Integer.MAX_VALUE;
		for(TimePoint point : positionHistory) {
			if(Math.abs(frameNum - point.getFrameNum()) < closestTime) {
				closestTime = Math.abs(frameNum - point.getFrameNum());
				closest = point;
			}
		}
		return closest;
	}
	
	/**
	 * This method gives the last TimePoint in the array
	 * @return -the last TimePoint in the array
	 */
	public TimePoint getFinalTimePoint() {
		return positionHistory.get(positionHistory.size() - 1);
	}
	
	/**
	 * This method tells how many TimePoints there are in the array
	 * @return the number of TimePoints
	 */
	public int size() {
		return positionHistory.size();
	}
	
	/**
	 * This method gives a list of TimePoints that fit within the two times from the
	 * AnimalTrack
	 * @param startFrameNum -the start frame number that looks for TimePoints
	 * @param endFrameNum -the end frame number that looks for TimePoints
	 * @return -a list of TimePoints that fit within the start and end time
	 */
	public List<TimePoint> getTimePointsWithinInterval(int startFrameNum, int endFrameNum) {
		List<TimePoint> pointsInInterval = new ArrayList<>();
		for (TimePoint pt : positionHistory) {
			if (pt.getFrameNum() >= startFrameNum && pt.getFrameNum() <= endFrameNum) {
				pointsInInterval.add(pt);
			}
		}
		return pointsInInterval;
	}
	
	/**
	 * This gives the history of the TimePositions of the the chick
	 * @return
	 */
	public List<TimePoint> getPositionHistory(){
		return positionHistory;
	}
	
	/**
	 * This gives the color of the AnimalTrack so it can be drawn with the correct color
	 * @return the color associated with the AnimalTrack
	 */
	public Color getColor() {
		return this.color;
	}
	
	/**
	 * This method allows the color to be set for the AnimalTrack
	 * @param newColor -the color that will be associated with the AnimalTrack
	 */
	public void setColor(Color newColor) {
		color = newColor;
	}
}
