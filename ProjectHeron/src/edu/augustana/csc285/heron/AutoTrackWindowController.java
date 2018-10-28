package edu.augustana.csc285.heron;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.opencv.core.Mat;

import autotracking.AutoTrackListener;
import autotracking.AutoTracker;
import datamodel.AnimalTrack;
import datamodel.ProjectData;
import datamodel.Video;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;



public class AutoTrackWindowController implements AutoTrackListener {

	@FXML private Button btnBrowse;
	@FXML private ImageView videoView;
	@FXML private Slider sliderVideoTime;
	@FXML private TextField textFieldCurFrameNum;

	@FXML private TextField textfieldStartFrame;
	@FXML private TextField textfieldEndFrame;
	@FXML private Button btnAutotrack;
	@FXML private ProgressBar progressAutoTrack;

	
	private AutoTracker autotracker;
	private ProjectData project;
	private Stage stage;
	
	/**
	 * This method will initiate the GUI for auto tracking
	 */
	@FXML public void initialize() {
				
		sliderVideoTime.valueProperty().addListener((obs, oldV, newV) -> showFrameAt(newV.intValue())); 
	}
	
	/**
	 * This method will set the project data for using
	 * @param project The project data which has been chose
	 * @throws FileNotFoundException: Exception happen when there is no project to take.
	 */
	public void setProjectData(ProjectData project) throws FileNotFoundException {
		this.project = project;
	}
	
	/**
	 * This method will make sure that the scene will fit with the video view
	 * @param stage: condition for the video change to fit with
	 */
	public void initializeWithStage(Stage stage) {
		this.stage = stage;
		
		// bind it so whenever the Scene changes width, the videoView matches it
		// (not perfect though... visual problems if the height gets too large.)
		videoView.fitWidthProperty().bind(videoView.getScene().widthProperty());  
	}
	
	/**
	 * This method will choose the browse for the video to use
	 */
	@FXML
	public void handleBrowse()  {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Video File");
		File chosenFile = fileChooser.showOpenDialog(stage);
		if (chosenFile != null) {
			loadVideo(chosenFile.getPath());
		}		
	}
	
	/**
	 * This method will load the video to use in image view
	 * @param filePath
	 */
	public void loadVideo(String filePath) {
		Video video = project.getVideo();
		sliderVideoTime.setMax(video.getTotalNumFrames()-1);
		showFrameAt(0);

	}
	
	/**
	 * This method will show the frame base on it number
	 * @param frameNum: the number of that frame
	 */
	public void showFrameAt(int frameNum) {
		if (autotracker == null || !autotracker.isRunning()) {
			project.getVideo().setCurrentFrameNum(frameNum);
			Image curFrame = Utils.matToJavaFXImage(project.getVideo().readFrame());
			videoView.setImage(curFrame);
			textFieldCurFrameNum.setText(String.format("%05d",frameNum));
			
		}		
	}
	
	/**
	 * This method will control the button start auto tracking
	 * @throws InterruptedException: Exception will happen when video has problems.
	 */
	@FXML
	public void handleStartAutotracking() throws InterruptedException {
		if (autotracker == null || !autotracker.isRunning()) {
			Video video = project.getVideo();
			loadVideo(video.getFilePath());
			System.out.println("Arena: " + video.getArenaBounds());
			autotracker = new AutoTracker();
			// Use Observer Pattern to give autotracker a reference to this object, 
			// and call back to methods in this class to update progress.
			autotracker.addAutoTrackListener(this);
			
			// this method will start a new thread to run AutoTracker in the background
			// so that we don't freeze up the main JavaFX UI thread.
			autotracker.startAnalysis(video);
			btnAutotrack.setText("CANCEL auto-tracking");
		} else {
			autotracker.cancelAnalysis();
			btnAutotrack.setText("Start auto-tracking");
		}
		 
	}

	// this method will get called repeatedly by the Autotracker after it analyzes each frame
	@Override
	public void handleTrackedFrame(Mat frame, int frameNumber, double fractionComplete) {
		Image imgFrame = Utils.matToJavaFXImage(frame);
		// this method is being run by the AutoTracker's thread, so we must
		// ask the JavaFX UI thread to update some visual properties
		Platform.runLater(() -> { 
			videoView.setImage(imgFrame);
			progressAutoTrack.setProgress(fractionComplete);
			sliderVideoTime.setValue(frameNumber);
			textFieldCurFrameNum.setText(String.format("%05d",frameNumber));
		});		
	}

	@Override
	public void trackingComplete(List<AnimalTrack> trackedSegments) {
		project.getUnassignedSegments().clear();
		project.getUnassignedSegments().addAll(trackedSegments);

		System.out.println("auto track complete");
		System.out.println("trackedsegs: " + trackedSegments.size());
		
		for (AnimalTrack track: trackedSegments) {
			System.out.println("  " + track.getAnimalID()+ " " + track.size());
			System.out.println(track);
		}
		Platform.runLater(() -> { 
			progressAutoTrack.setProgress(1.0);
			btnAutotrack.setText("Start auto-tracking");
			FXMLLoader loader = new FXMLLoader(getClass().getResource("AnalysisWindow.fxml"));
			BorderPane root = null;
			try {
				root = (BorderPane)loader.load();
			} catch (IOException e) {
				e.printStackTrace();
			}
	
			AnalysisWindowController analysisController = loader.getController();
			analysisController.setProjectData(project);
			Scene timeScene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
			timeScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			Stage primary = (Stage) btnAutotrack.getScene().getWindow();
			primary.setMinWidth(root.getPrefWidth()+10);
			primary.setMinHeight(root.getPrefHeight()+20);
			primary.setScene(timeScene);
		});	
		
	}
}