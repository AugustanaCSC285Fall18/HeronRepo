package edu.augustana.csc285.heron;

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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MainWindowController {
	
	private Video vid;
	
	@FXML
	private Button start;
	@FXML
	private ImageView videoFrame;

	private VideoCapture capture = new VideoCapture();
	private ScheduledExecutorService timer;

	
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
		vid.getVideoCap().open(vid.getFile().getAbsolutePath());

		Runnable frameGrabber = new Runnable() {
			public void run() {
				// get one frame from the video
				// convert it from a opencv Mat object
				// into a JavaFX Image object
				// and then schedule an update to
				// show that image in the ImageView

				Mat frame = new Mat();
				vid.getVideoCap().read(frame);

				MatOfByte buffer = new MatOfByte();
				Imgcodecs.imencode(".png", frame, buffer);
				Image imageToShow = new Image(new ByteArrayInputStream(buffer.toArray()));
				Platform.runLater(new Runnable() {
					@Override public void run() {
						videoFrame.setImage(imageToShow); }
				});
			}
		};

		this.timer = Executors.newSingleThreadScheduledExecutor();
		this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

	}
	
	public void mouseClicked (ActionEvent e ) {
		
	}
	
	public void setVideo(Video video) {
		vid = video;
	}
	
}
