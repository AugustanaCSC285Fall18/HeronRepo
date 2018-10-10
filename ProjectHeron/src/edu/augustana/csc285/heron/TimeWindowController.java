package edu.augustana.csc285.heron;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import datamodel.Video;
import edu.augustana.csc285.heron.Utils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
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
	@FXML private Canvas canvasOverVideo;
	@FXML private TextField lengthOfLineText;

	private Video vid;
	private ArrayList<Point2D> calibrationPoints;
	
	
	/**
	 * This method sets the start time of the video
	 */
	@FXML
	public void selectStartTime() {
		vid.setStartFrameNum((int)(videoBar.getValue() / 1000 * (vid.getVideoCap().get(Videoio.CAP_PROP_FRAME_COUNT) - 1)));
	}
	
	/**
	 * This method sets the end time of the video
	 */
	@FXML
	public void selectEndTime() {
		vid.setEndFrameNum((int)(videoBar.getValue() / 1000 * (vid.getVideoCap().get(Videoio.CAP_PROP_FRAME_COUNT) - 1)));
	}
	/**
	 * This loads the video so that the user can select the start and end time
	 * while being able to see where in the video the value is based on the slider
	 * @param video -the video that will be played
	 */
	public void setVideo(Video video) {
		vid = video;		
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
		GraphicsContext gc = canvasOverVideo.getGraphicsContext2D();
		calibrationPoints = new ArrayList<>();
		EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() { 
			@Override 
			public void handle(MouseEvent e) {
				
				System.out.println("x: " + e.getX() + ", y: " + e.getY());
				if (calibrationPoints.size() < 2 && e.getButton() == MouseButton.PRIMARY) {
					gc.setFill(Color.RED);
					gc.fillOval(e.getX()-5, e.getY()-5, 10, 10);
					calibrationPoints.add(new Point2D(e.getX(),e.getY()));
				};
				if(calibrationPoints.size() == 2) {
					gc.setLineWidth(3);
					gc.setStroke(Color.RED);
					gc.strokeLine(calibrationPoints.get(0).getX(), calibrationPoints.get(0).getY(), 
							calibrationPoints.get(1).getX(), calibrationPoints.get(1).getY());
				}
				
	}};
	canvasOverVideo.setOnMouseClicked(eventHandler);
		}
	/**
	 * This method allows the TimeWindow to go the MainWindow
	 * @throws IOException -if there is no pane to load, then the exception is thrown
	 */
	@FXML
	public void handleNext() throws IOException {
		if(vid.getStartFrameNum() < vid.getEndFrameNum()) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("AutoTrackWindow.fxml"));
		BorderPane root = (BorderPane)loader.load();
		AutoTrackWindowController autoTrackController = loader.getController();
		autoTrackController.setProjectData(vid);
		Scene timeScene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
		timeScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
		Stage primary = (Stage) nextBtn.getScene().getWindow();
		primary.setScene(timeScene);
		System.out.println(handleCaliberation());
		}
	}
	@FXML
	public void wholeVideo() {
		vid.setStartFrameNum(0);
		vid.setEndFrameNum((int)(vid.getVideoCap().get(Videoio.CAP_PROP_FRAME_COUNT-1)));
	}
	
	public double handleCaliberation() {
		double ratioBetImaAndRel = Math.max(vid.getWidth()/videoView.getFitWidth(),vid.getHeight()/videoView.getFitHeight());
		double screenDistance = calibrationPoints.get(0).distance(calibrationPoints.get(1));		
		return Double.parseDouble(lengthOfLineText.getText()) / screenDistance *ratioBetImaAndRel;
	}
}
