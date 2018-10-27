package datamodel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
public void exportCSV(String fileName) throws FileNotFoundException {
	
		PrintWriter writer = new PrintWriter(fileName);
		writer.println("ChickID, Time(sec), X, Y");
		for(AnimalTrack chick : tracks) {
			for(TimePoint point : chick.getPositionHistory()) {
				int time = (int) video.convertFrameNumsToSeconds(point.getFrameNum());
				//double x = point.getX() - 
				//the format is ChickID, Time, X, Y
				writer.println(chick.getAnimalID() + "," + time + "," + point.getX() + "," + point.getY());
			}
		}
		writer.close();
		
	
	
	}
	
}