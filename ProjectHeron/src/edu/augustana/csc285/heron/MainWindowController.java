package edu.augustana.csc285.heron;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.event.EventHandler;

public class MainWindowController {
	@FXML
	private Button start;
	@FXML
	private ImageView videoFrame;

	private VideoCapture capture = new VideoCapture();
	private ScheduledExecutorService timer;
	
	public Image grabFrame() {
		Mat frame = new Mat();
		capture.read(frame);
		capture.get(Videoio.CAP_PROP_FRAME_COUNT);
		System.out.println(capture.get(Videoio.CAP_PROP_FRAME_COUNT));
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
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Video File");
		Window mainWindow = videoFrame.getScene().getWindow();
		File chosenFile = fileChooser.showOpenDialog(mainWindow);
		String filename = chosenFile.getAbsolutePath();

		if (chosenFile != null) {
			try {

				videoFrame.setImage(new Image(new FileInputStream(chosenFile)));

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			capture.open(filename);
		}
		
		Runnable frameGrabber = new Runnable() {
			public void run() {
				// get one frame from the video
				// convert it from a opencv Mat object
				// into a JavaFX Image object
				// and then schedule an update to
				// show that image in the ImageView

				Mat frame = new Mat();
				capture.read(frame);

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
		
		EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() { 
	         @Override 
	         public void handle(MouseEvent e) {
	        	 System.out.println("x: " + e.getX() + ", y: " + e.getY());
	         }
		};
		
		videoFrame.setOnMouseClicked(eventHandler);
	}
			
}
