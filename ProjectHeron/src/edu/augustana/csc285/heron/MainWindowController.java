package edu.augustana.csc285.heron;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.io.ByteArrayInputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
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

public class MainWindowController {
	
	private Video vid;	
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
	private VideoCapture capture = new VideoCapture();
	private ScheduledExecutorService timer;
	private int [] timeTake = new int[] {1,5};
	private int timeJump = 1;
	
	
	public Image grabFrame() {
		Mat frame = new Mat();
		capture.read(frame);		
		MatOfByte buffer = new MatOfByte();
		Imgcodecs.imencode(".png", frame, buffer);
		Image imageToShow = new Image(new ByteArrayInputStream(buffer.toArray()));

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				videoFrame.setImage(imageToShow);
			}
		});

		return imageToShow;
	}

	@FXML
	protected void startButton(ActionEvent event) {
		capture.open("S:\\CLASS\\CS\\285\\sample_videos\\sample1.mp4");

		Runnable frameGrabber = new Runnable() {
			public void run() {
				// get one frame from the video
				// convert it from a opencv Mat object
				// into a JavaFX Image object
				// and then schedule an update to
				// show that image in the ImageView
				timeChange.setItems(FXCollections.observableArrayList("Jump 1s", "Jump 5s"));			
				showNextFrame();
				
			}
		};

		this.timer = Executors.newSingleThreadScheduledExecutor();
		this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

	}
	
	public void showNextFrame() {
		Mat frame = new Mat();
		capture.read(frame);

		MatOfByte buffer = new MatOfByte();
		Imgcodecs.imencode(".png", frame, buffer);
		Image imageToShow = new Image(new ByteArrayInputStream(buffer.toArray()));
		Platform.runLater(new Runnable() {
			@Override public void run() {
				videoFrame.setImage(imageToShow); 
				pgbar.setProgress(capture.get(Videoio.CAP_PROP_POS_FRAMES)/capture.get(Videoio.CV_CAP_PROP_FRAME_COUNT));
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
		timer.shutdown();  // stop the auto-playing
		timer.awaitTermination(1000, TimeUnit.MILLISECONDS);
		int newFrameNum = (int)(capture.get(Videoio.CAP_PROP_POS_FRAMES) + timeJump * capture.get(Videoio.CAP_PROP_FPS));
		capture.set(Videoio.CAP_PROP_POS_FRAMES, newFrameNum);
		showNextFrame();
		
	}
	


	public void moveBack() throws InterruptedException {
		timer.shutdown();  // stop the auto-playing
		timer.awaitTermination(1000, TimeUnit.MILLISECONDS);
		int newFrameNum = (int)(capture.get(Videoio.CAP_PROP_POS_FRAMES) - timeJump * capture.get(Videoio.CAP_PROP_FPS));
		capture.set(Videoio.CAP_PROP_POS_FRAMES, newFrameNum);
		showNextFrame();
	}

	

	public void setVideo(Video video) {
		vid = video;
	}

	
}
