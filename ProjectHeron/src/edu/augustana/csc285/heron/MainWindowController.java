package edu.augustana.csc285.heron;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import datamodel.ProjectData;
import datamodel.Video;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
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
	private ChoiceBox<String> timeChange;
	@FXML
	private ChoiceBox<String> chickList;
	@FXML
	private Group group;
	@FXML
	private Canvas canvasOverVideo;
	@FXML
	private Button addChick;
	@FXML
	private TextField chickID;
	@FXML
	private Button confirmBtn;


	private ScheduledExecutorService timer;
	private int [] timeTake = new int[] {1,5};
	private int timeJump = 1;
	private ProjectData project;

	@FXML public void initialize() {
		chickID.setEditable(false);
		confirmBtn.setDisable(true);
		System.out.println("init called");
		GraphicsContext gc = canvasOverVideo.getGraphicsContext2D();
		EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() { 
			@Override 
			public void handle(MouseEvent e) {
				System.out.println("x: " + e.getX() + ", y: " + e.getY());
				if (e.getButton() == MouseButton.PRIMARY) {
					gc.setFill(Color.BLACK);
					gc.fillOval(e.getX()-5, e.getY()-5, 10, 10);
				} else if (e.getButton() == MouseButton.SECONDARY) {
					gc.clearRect(e.getX()-5, e.getY()-5, 10, 10);
				}
			}
		};
		canvasOverVideo.setOnMouseClicked(eventHandler);
		
		ObservableList<String> items = FXCollections.observableArrayList();
		chickList.setItems(items);
	}

	@FXML
	protected void startButton(ActionEvent event) {
		System.out.println("vid path: " + project.getVideo().getFilePath());
		project.getVideo().getVideoCap().open(project.getVideo().getFilePath());
		// sets the video capture to the start
		System.out.println("vid start: " + project.getVideo().getStartFrameNum());
		project.getVideo().getVideoCap().set(Videoio.CAP_PROP_POS_FRAMES, project.getVideo().getStartFrameNum());
		timeChange.setItems(FXCollections.observableArrayList("Jump 1s", "Jumps 5s"));		
		showNextFrame();
	}


	public void showNextFrame() {
		Mat frame = new Mat();
		project.getVideo().getVideoCap().read(frame);

		MatOfByte buffer = new MatOfByte();
		Imgcodecs.imencode(".png", frame, buffer);
		Image imageToShow = new Image(new ByteArrayInputStream(buffer.toArray()));
		Platform.runLater(new Runnable() {
			@Override public void run() {
				videoFrame.setImage(imageToShow); 
				pgbar.setProgress(project.getVideo().getVideoCap().get(Videoio.CAP_PROP_POS_FRAMES)/project.getVideo().getVideoCap().get(Videoio.CV_CAP_PROP_FRAME_COUNT));

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
		int newFrameNum = (int)(project.getVideo().getVideoCap().get(Videoio.CAP_PROP_POS_FRAMES) + timeJump * project.getVideo().getVideoCap().get(Videoio.CAP_PROP_FPS));
		if(project.getVideo().getEndFrameNum() >= newFrameNum){
			project.getVideo().getVideoCap().set(Videoio.CAP_PROP_POS_FRAMES, newFrameNum);
			showNextFrame();
		}

	}



	public void moveBack() throws InterruptedException {
		if (timer != null && !timer.isShutdown()) {
			timer.shutdown();  // stop the auto-playing
			timer.awaitTermination(1000, TimeUnit.MILLISECONDS);
		}

		int newFrameNum = (int)(project.getVideo().getVideoCap().get(Videoio.CAP_PROP_POS_FRAMES) - timeJump * project.getVideo().getVideoCap().get(Videoio.CAP_PROP_FPS));
		if(project.getVideo().getStartFrameNum() <= newFrameNum){
			project.getVideo().getVideoCap().set(Videoio.CAP_PROP_POS_FRAMES, newFrameNum);
			showNextFrame();
		}

	}
	

	
	public void setProjectData(ProjectData project) {
		this.project = project;
	}
	
	
	@FXML
	protected void addChickButton(ActionEvent event) {
		chickID.setEditable(true);
		confirmBtn.setDisable(false);
	}
	
	@FXML
	protected void confirmBtn(ActionEvent event) {
		chickList.getItems().add(chickID.getText());
		chickID.clear();
		chickID.setEditable(false);
		confirmBtn.setDisable(true);
	}
	

}

