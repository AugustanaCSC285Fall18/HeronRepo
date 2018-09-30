package datamodel;

import edu.augustana.csc285.heron.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
/**
 * Description: This class allows for the tracking of multiple chicks
 * 				from a selected video to be saved and stored
 * @author Ehren Braun
 * @date 9/22/2018
 *
 */
public class MyProjectData {
	
	private List<AnimalTrack> chickList;
	private File file;
	private Video video;
	
	/**
	 * This constructs the ProjectData given the file to where the
	 * information is going to be stored/read from and the video
	 * that the information will be taken from
	 * @param currentRecord -the file that the information will be saved to/read from
	 * @param recording -the video that will be used to gather the information
	 */
	public MyProjectData(File currentRecord, Video recording){
		chickList = new ArrayList<AnimalTrack>();
		video = recording;
		file = currentRecord;
		//this is where the file is going to be read if there is any information stored
		Scanner scanner = null;
		try {
			scanner = new Scanner(currentRecord);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//since the information is "spaced" by a ','
		//this changes the cursor of the iterator to use ',' instead of ' ' 
		scanner.useDelimiter(",");
		scanner.nextLine();
		//this is where the information is being read
		while(scanner.hasNext()) {
			String chickID = scanner.next();
			//if there is no chickID in the array, then it adds a new ChickTrack, otherwise it adds the point to the existing chickID
			if(!contains(chickID)) {
				chickList.add(new AnimalTrack(chickID, new TimePoint(Integer.parseInt(scanner.next().trim()), Integer.parseInt(scanner.next().trim()), Integer.parseInt(scanner.nextLine().trim().substring(1)))));
			} else {
				getChickTrack(chickID).addPosition(Integer.parseInt(scanner.next().trim()), Double.parseDouble(scanner.next().trim()), Double.parseDouble(scanner.nextLine().trim().substring(1)));
			}
		}
		scanner.close();
	}
	
	/**
	 * This method checks to see if the chickID is in the arrayList
	 * @param chickID -the ID that is being searched for
	 * @return -whether or not the ID is in the arrayList
	 */
	private boolean contains(String chickID) {
		for(AnimalTrack chick : chickList) {
			if(chick.getChickID().equals(chickID)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method returns the ChickTrack given the ID, if it is not
	 * in the arrayList, then it returns null
	 * @param chickID -the ChickTrack that is being asked for
	 * @return -the ChickTrack that is being asked for
	 */
	public AnimalTrack getChickTrack(String chickID) {
		for(AnimalTrack chick : chickList) {
			if(chick.getChickID().equals(chickID)) {
				return chick;
			}
		}
		return null;
	}
	
	/**
	 * This method allows the addition of a new ChickTrack
	 * @param newChick -the new ChickTrack that is going to be added
	 */
	public void addChickTrack(AnimalTrack newChick) {
		chickList.add(newChick);
	}
	
	/**
	 * This method saves the the information of the chicks in the order
	 * that they were added to the Project and the TimePositions are in
	 * chronological order
	 */
	public void saveData() {
		PrintWriter writer = null;
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			writer = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//this writes the information into the csv file
		writer.println("ChickID, Time, X, Y");
		for(AnimalTrack chick : chickList) {
			for(TimePoint point : chick.getPositionHistory()) {
				//the format is ChickID, Time, X, Y
				writer.println(chick.getChickID() + "," + point.getTime() + "," + point.getX() + "," + point.getY());
			}
		}
		writer.close();
	}
}

