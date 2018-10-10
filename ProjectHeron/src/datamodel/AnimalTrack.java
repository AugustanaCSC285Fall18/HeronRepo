package datamodel;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;
/**
 * Description: This class is to track a particular animal
 * 
 * @author Ehren Braun
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
	}
	
	/**
	 * This adds a data point to the arrayList of points given
	 * a TimePoint object
	 * @param xy -the TimePosition of the chick
	 */
	public void add(TimePoint xy) {
		positionHistory.add(xy);
	}
	
	public TimePoint TimePointAtIndex(int index) {
		return positionHistory.get(index);
	}
	
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
	
	public String toString() {
		int startFrame = positionHistory.get(0).getFrameNum();
		int endFrame = getFinalTimePoint().getFrameNum();
		return "AnimalTrack[id=" + animalID + ",numPts=" + size() + " startFrame=" + startFrame + " endFrame=" + endFrame + "]";
	}
	
	public boolean alreadyHasTime(int frameNum) {
		for(TimePoint point : positionHistory) {
			if(point.getFrameNum() == frameNum) {
				return true;
			}
		}
		return false;
	}
	public TimePoint getFinalTimePoint() {
		return positionHistory.get(positionHistory.size() - 1);
	}
	
	public int size() {
		return positionHistory.size();
	}
	/**
	 * This gives the history of the TimePositions of the the chick
	 * @return
	 */
	public List<TimePoint> getPositionHistory(){
		return positionHistory;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public void setColor(Color newColor) {
		color = newColor;
	}
}
