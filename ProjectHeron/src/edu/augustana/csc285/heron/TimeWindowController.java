package edu.augustana.csc285.heron;


import java.io.IOException;
import org.opencv.core.Mat;
import org.opencv.videoio.Videoio;
import datamodel.ProjectData;
import datamodel.Video;
import edu.augustana.csc285.heron.Utils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
/**
 * This class is the GUI for selecting the start and end time of what will be recorded
 * as data and for getting to the next GUI.
 * @author Jinsoo Park && Ehren Braun
 * @date 9/27/2018
 */
public class TimeWindowController {
	@FXML private Slider videoBar;
	@FXML private Button startBtn;
	@FXML private Button endBtn;
	@FXML private Button nextBtn;
	@FXML private ImageView videoView;
	private ProjectData project;
	/**
	 * This method sets the start time of the video
	 */
	@FXML
	public void selectStartTime() {
		project.getVideo().setStartFrameNum((int)(videoBar.getValue() / 1000 * (project.getVideo().getVideoCap().get(Videoio.CAP_PROP_FRAME_COUNT) - 1)));
		if(allSelected()) {
			nextBtn.setDisable(false);
		}
	}
	
	public boolean allSelected() {
		int emptyFrame = project.getVideo().getEmptyFrameNum();
		int startFrameNum = project.getVideo().getStartFrameNum();
		int endFrameNum = project.getVideo().getEndFrameNum();
		return startFrameNum != -1 && endFrameNum != -1 && startFrameNum < endFrameNum; //&& emptyFrame != -1;
	}
	
	@FXML
	public void selectEmptyFrame() {
		project.getVideo().setEmptyFrameNum((int)(videoBar.getValue() / 1000 * (project.getVideo().getVideoCap().get(Videoio.CAP_PROP_FRAME_COUNT) - 1)));
		if(allSelected()) {
			nextBtn.setDisable(false);
		}
	}
	/**
	 * This method sets the end time of the video
	 */
	@FXML
	public void selectEndTime() {
		project.getVideo().setEndFrameNum((int)(videoBar.getValue() / 1000 * (project.getVideo().getVideoCap().get(Videoio.CAP_PROP_FRAME_COUNT) - 1)));
		if(allSelected()) {
			nextBtn.setDisable(false);
		}
	}
	/**
	 * This loads the video so that the user can select the start and end time
	 * while being able to see where in the video the value is based on the slider
	 * @param video -the video that will be played
	 */
	public void setProjectData(ProjectData project) {
		this.project = project;
		Video vid = project.getVideo();		
		if (vid.getFilePath() != null) {
			try {
				// start the video capture
				vid.getVideoCap().open(vid.getFilePath());
				
				// sets the video capture to the start
				vid.getVideoCap().set(Videoio.CAP_PROP_POS_FRAMES, 0);
				
				// creates a listener for the videoBar
				videoBar.valueProperty().addListener(new ChangeListener<Number>() {

					@Override
					// this method changes the frame of video capture based on the videoBar
					public void changed(ObservableValue<? extends Number> observable, Number oldValue,
							Number newValue) {
						
						vid.getVideoCap().set(Videoio.CAP_PROP_POS_FRAMES,
								(int) (newValue.doubleValue() / 1000 * (vid.getVideoCap().get(Videoio.CAP_PROP_FRAME_COUNT) - 1)));
						Platform.runLater(new Runnable() {

							@Override
							// this method sets the frame of videoView
							public void run() {
								Mat newFrame = vid.grabFrame();
								videoView.setImage(Utils.mat2Image(newFrame));

							}

						});
					}

				});
				// sets the frame of videoView to the start
				Mat frame = vid.grabFrame();
				videoView.setImage(Utils.mat2Image(frame));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	@FXML
	public void initialize() {
		nextBtn.setDisable(true);
	}
	/**
	 * This method allows the TimeWindow to go the MainWindow
	 * @throws IOException -if there is no pane to load, then the exception is thrown
	 */
	@FXML
	public void handleNext() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("AutoTrackWindow.fxml"));
		BorderPane root = (BorderPane)loader.load();
		AutoTrackWindowController autoTrackController = loader.getController();
		autoTrackController.setProjectData(project);
		Scene timeScene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
		timeScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
		Stage primary = (Stage) nextBtn.getScene().getWindow();
		primary.setScene(timeScene);
	
	}
	@FXML
	public void wholeVideo() {
		project.getVideo().setStartFrameNum(0);
		project.getVideo().setEndFrameNum((int)(project.getVideo().getVideoCap().get(Videoio.CAP_PROP_FRAME_COUNT-1)));
		nextBtn.setDisable(false);
	}
	
}
