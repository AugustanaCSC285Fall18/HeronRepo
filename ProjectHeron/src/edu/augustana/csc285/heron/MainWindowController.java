package edu.augustana.csc285.heron;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Window;


public class MainWindowController {
	
	@FXML
	private Button button;
	@FXML
	private ImageView videoFrame;
	@FXML
	private ProgressBar pgbar ;
	@FXML
	private Button mBack;
	@FXML
	private Button mForward;
	@FXML
	private ChoiceBox timeChange;
	private ScheduledExecutorService timer;
	private int [] timeTake = new int[] {1,5};
	private int timeJump = 1;
	private Video vid;

	
	@FXML
	protected void startButton(ActionEvent event) {
		System.out.println("vid path: " + vid.getFile().getAbsolutePath());
		vid.getVideoCap().open(vid.getFile().getAbsolutePath());
		// sets the video capture to the start
		System.out.println("vid start: " + vid.getStartTime());
		vid.getVideoCap().set(Videoio.CAP_PROP_POS_FRAMES, vid.getStartTime());
		timeChange.setItems(FXCollections.observableArrayList("Jump 1s", "Jumps 5s"));
		EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() { 
	         @Override 
	         public void handle(MouseEvent e) {
	        	 System.out.println("x: " + e.getX() + ", y: " + e.getY());
	         }
		};
		
		videoFrame.setOnMouseClicked(eventHandler);
		showNextFrame();
	}
	
	
	public void showNextFrame() {
		Mat frame = new Mat();
		vid.getVideoCap().read(frame);

		MatOfByte buffer = new MatOfByte();
		Imgcodecs.imencode(".png", frame, buffer);
		Image imageToShow = new Image(new ByteArrayInputStream(buffer.toArray()));
		Platform.runLater(new Runnable() {
			@Override public void run() {
				videoFrame.setImage(imageToShow); 
				pgbar.setProgress(vid.getVideoCap().get(Videoio.CAP_PROP_POS_FRAMES)/vid.getVideoCap().get(Videoio.CV_CAP_PROP_FRAME_COUNT));

				timeChange.getSelectionModel().selectedIndexProperty()
		        .addListener(new ChangeListener<Number>() {
		            public void changed(ObservableValue ov, Number value, Number new_value) {
		              timeJump = timeTake[new_value.intValue()];
		            }
		          });
			}
		});		
		
	}
	
	
	public void moveForward() throws InterruptedException {
		if (timer != null && !timer.isShutdown()) {
			timer.shutdown();  // stop the auto-playing
			timer.awaitTermination(1000, TimeUnit.MILLISECONDS);
		}
		int newFrameNum = (int)(vid.getVideoCap().get(Videoio.CAP_PROP_POS_FRAMES) + timeJump * vid.getVideoCap().get(Videoio.CAP_PROP_FPS));
		if(vid.getEndTime() >= newFrameNum){
			vid.getVideoCap().set(Videoio.CAP_PROP_POS_FRAMES, newFrameNum);
			showNextFrame();
		}
		
	}
	


	public void moveBack() throws InterruptedException {
		if (timer != null && !timer.isShutdown()) {
			timer.shutdown();  // stop the auto-playing
			timer.awaitTermination(1000, TimeUnit.MILLISECONDS);
		}
		
		int newFrameNum = (int)(vid.getVideoCap().get(Videoio.CAP_PROP_POS_FRAMES) - timeJump * vid.getVideoCap().get(Videoio.CAP_PROP_FPS));
		if(vid.getStartTime() <= newFrameNum){
			vid.getVideoCap().set(Videoio.CAP_PROP_POS_FRAMES, newFrameNum);
			showNextFrame();
		}
		
	}

	

	public void setVideo(Video video) {
		vid = video;
	}
}