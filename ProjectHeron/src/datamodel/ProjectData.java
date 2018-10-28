package datamodel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ProjectData {
	private Video video;
	private List<AnimalTrack> tracks;
	private List<AnimalTrack> unassignedSegments;
	private int atFrameRecorded;
	
	public ProjectData(String videoFilePath) throws FileNotFoundException {
		video = new Video(videoFilePath);
		tracks = new ArrayList<>();
		unassignedSegments = new ArrayList<>();
		atFrameRecorded = video.getStartFrameNum();
	}

	public Video getVideo() {
		return video;
	}
	
	public int getAtFrameRecorded() {
		return atFrameRecorded;
	}
	
	public void addToAtFrameRecorded(int numFrames) {
		atFrameRecorded += numFrames;
	}
	public List<AnimalTrack> getTracks() {
		return tracks;
	}

	public List<AnimalTrack> getUnassignedSegments() {
		return unassignedSegments;
	}
	
	public boolean allHaveTimePoint(int frameNum) {
		for(AnimalTrack animal : tracks) {
			if(!animal.alreadyHasTime(frameNum)) {
				return false;
			}
		}
		return true;
	}
	
	public AnimalTrack getAnimalTrackInUnassignedSegments(String id) {
		for(AnimalTrack animal : unassignedSegments) {
			if(animal.getAnimalID().equals(id)) {
				return animal;
			}
		}
		return null;
	}
	public AnimalTrack getAnimalTrackInTracks(String id) {
		for(AnimalTrack animal : tracks) {
			if(animal.getAnimalID().equals(id) ) {
				return animal;
			}
		}
		return null;
	}
	
	public double distanceCovered(String id) {
		AnimalTrack chosenChick = getAnimalTrackInTracks(id);
		double distance = 0;
		for(int i = 0; i < chosenChick.size() - 1; i++) {
			double x1 = chosenChick.getPositionHistory().get(i).getX()/video.getXPixelsPerCm();
			double y1 = chosenChick.getPositionHistory().get(i).getY()/video.getYPixelsPerCm();
			double x2 = chosenChick.getPositionHistory().get(i + 1).getX()/video.getXPixelsPerCm();
			double y2 = chosenChick.getPositionHistory().get(i + 1).getY()/video.getYPixelsPerCm();
			double dx = (x1 - x2) * (x1 - x2);
			double dy = (y1 - y2) * (y1 - y2);
			distance += Math.sqrt(dx + dy);
		}
		return distance;
	}
	
	public double averageDistance(AnimalTrack animal1,AnimalTrack animal2) {
		AnimalTrack chosenChick = animal1.size() > animal2.size() ? animal2 : animal1;
		AnimalTrack notChosenChick = animal1.size() > animal2.size() ? animal1 : animal2;
		List<TimePoint> usedPoints = new ArrayList<TimePoint>();
		int numPointComparisons = 0;
		double distance = 0;
		for(TimePoint point : chosenChick.getPositionHistory()) {
			TimePoint closestPoint = notChosenChick.getClosestTimePoint(point.getFrameNum());
			if(Math.abs(point.getFrameNum() - closestPoint.getFrameNum()) < video.getFrameRate() + 10) {
				if(!usedPoints.contains(closestPoint)) {
					usedPoints.add(closestPoint);
					numPointComparisons++;
					double x1 = point.getX() /video.getXPixelsPerCm();
					double y1 = point.getY() /video.getYPixelsPerCm();
					double x2 = closestPoint.getX() / video.getXPixelsPerCm();
					double y2 = closestPoint.getY() / video.getYPixelsPerCm();
					double dx = (x1 - x2) * (x1 - x2);
					double dy = (y1 - y2) * (y1 - y2);
					distance += Math.sqrt(dx + dy);
				}
			}
		}
		return distance / numPointComparisons;
	}
	
	public void saveToFile(File saveFile) throws FileNotFoundException {
		String json = toJSON();
		PrintWriter out = new PrintWriter(saveFile);
		out.print(json);
		out.close();
	}
	
	public String toJSON() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();		
		return gson.toJson(this);
	}
	
	public static ProjectData loadFromFile(File loadFile) throws FileNotFoundException {
		String json = new Scanner(loadFile).useDelimiter("\\Z").next();
		return fromJSON(json);
	}
	
	public static ProjectData fromJSON(String jsonText) throws FileNotFoundException {
		Gson gson = new Gson();
		ProjectData data = gson.fromJson(jsonText, ProjectData.class);
		data.getVideo().connectVideoCapture();
		return data;
	}
	/**
	 * This method convert data to CSV file.
	 * Order of ChickID, Time, X, Y
	 * @param file
	 * @throws FileNotFoundException
	 */
	public void exportTimePointsToCSV(String fileName) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(fileName);
		DecimalFormat df = new DecimalFormat("#.##");
		writer.println("ChickID, Time(sec), X, Y");
		for(AnimalTrack chick : tracks) {
			for(TimePoint point : chick.getPositionHistory()) {
				double time = video.convertFrameNumsToSeconds(point.getFrameNum());
				double xInCm = (point.getX() - video.getCenterPoint().getX())/video.getXPixelsPerCm();
				double yInCm = (point.getY() - video.getCenterPoint().getY())/video.getYPixelsPerCm();
				
				//the format is ChickID, Time, X, Y
				writer.println(chick.getAnimalID() + ", " + df.format(time)+ ", " + df.format(xInCm) + ", " + df.format(yInCm));
			}
			writer.println("Distance Covered" + "," + df.format(distanceCovered(chick.getAnimalID())));
		}
		writer.close();
	}
	
	public void exportAverageDistances(String fileName) throws FileNotFoundException{
		if(tracks.size() > 1) {
			PrintWriter writer = new PrintWriter(fileName);
			DecimalFormat df = new DecimalFormat("#.##");
			for(int i = 0; i < this.tracks.size() - 1; i ++) {
				for (int j = i + 1; j < this.tracks.size(); j++ ) {
					writer.println("Average between " + tracks.get(i).getAnimalID() + " " + tracks.get(j).getAnimalID() + "," + df.format(averageDistance(tracks.get(i), tracks.get(j))));
				}
			}
			writer.close();
		}
	}
}