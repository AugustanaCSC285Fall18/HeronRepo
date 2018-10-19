package datamodel;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

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
	
}