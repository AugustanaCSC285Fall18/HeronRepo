package edu.augustana.csc285.heron;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.Videoio;

import datamodel.AnimalTrack;
import datamodel.ProjectData;
import datamodel.TimePoint;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public class AnalysisWindowController {

	private ProjectData project;
	@FXML private Button addChickBtn;
	@FXML private Button confirmBtn;
	@FXML private Button setBtn;
	@FXML private Button showBtn;
	@FXML private Button saveBtn;
	@FXML private TextField chickID;
	@FXML private Canvas canvasOverVideo;
	@FXML private ChoiceBox<String> chickIDs;
	@FXML private ChoiceBox<String> paths;
	@FXML private ImageView imageView;
	@FXML private Slider videoBar;
	@FXML private BorderPane analysisWindow;
	@FXML private Button mBack;
	@FXML private Button mForward;
	@FXML private Button incrementSetBtn;
	@FXML private Button addIncrementBtn;
	@FXML private Button confirmIncrementBtn;
	@FXML private ChoiceBox<Double> timeChoices;
	@FXML private TextField timeIncrement;
	private GraphicsContext gc;
	private List<Color> colorChoice;
	private int colorNum;
	private ScheduledExecutorService timer;
	private double timeJump;


	public void setProjectData(ProjectData project) {
		this.project = project;
		fitVideo();
		videoBar.setMax(project.getVideo().getTotalNumFrames() - 1);
		videoBar.setValue(project.getVideo().getStartFrameNum());
		List<String> remove = new ArrayList<String>();
		for(datamodel.AnimalTrack animal : project.getUnassignedSegments()) {
			if(animal.size() < 66) {
				remove.add(animal.getAnimalID());
			} else {
				paths.getItems().add(animal.getAnimalID());
			}
		}
		for(String id : remove) {
			project.getUnassignedSegments().remove(project.getAnimalTrackInUnassignedSegments(id));
		}
	}

	@FXML
	public void initialize() {
		chickID.setEditable(false);
		confirmBtn.setDisable(true); 
		timeIncrement.setEditable(false);
		confirmIncrementBtn.setDisable(true);
		gc = canvasOverVideo.getGraphicsContext2D();

		colorChoice = new ArrayList <Color>();
		colorChoice.add(Color.BLACK);
		colorChoice.add(Color.RED);
		colorChoice.add(Color.ORANGE);
		colorChoice.add(Color.YELLOW);
		colorChoice.add(Color.GREEN);
		colorChoice.add(Color.CYAN);
		colorChoice.add(Color.DEEPSKYBLUE);
		colorChoice.add(Color.MEDIUMPURPLE);


		EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
			@Override 
			public void handle(MouseEvent e) {
				
				System.out.println("x: " + e.getX() + ", y: " + e.getY());
				if (e.getButton() == MouseButton.PRIMARY) {
					if(chickIDs.getValue() != null && project.getVideo().getCurrentFrameNum() >= project.getVideo().getStartFrameNum() && 
							project.getVideo().getCurrentFrameNum() <= project.getVideo().getEndFrameNum()) {
						if(!project.getAnimalTrackInTracks(chickIDs.getValue()).alreadyHasTime(project.getVideo().getCurrentFrameNum())) {
							project.getAnimalTrackInTracks(chickIDs.getValue()).add(e.getX()*project.getVideo().getFrameWidth()/canvasOverVideo.getWidth(),
									e.getY()*project.getVideo().getFrameHeight()/canvasOverVideo.getHeight(), project.getVideo().getCurrentFrameNum());
							gc.setFill(project.getAnimalTrackInTracks(chickIDs.getValue()).getColor());
							gc.fillOval(e.getX()-5, e.getY()-5, 10, 10);
						}
					}
				} else if (e.getButton() == MouseButton.SECONDARY) {
					if(!project.getTracks().isEmpty()) {
						project.getTracks().remove(project.getTracks().size()-1);
					}
					gc.clearRect(e.getX()-5, e.getY()-5, 20, 20);

				}
			}
		};
		canvasOverVideo.setOnMouseClicked(eventHandler);
		ObservableList<String> items = FXCollections.observableArrayList();
		chickIDs.setItems(items);
		ObservableList<String> pathList = FXCollections.observableArrayList();
		paths.setItems(pathList);
		ObservableList<Double> incrementValues = FXCollections.observableArrayList();
		timeChoices.setItems(incrementValues);

		//videoBar.valueProperty().addListener((obs, oldV, newV) -> showFrameAt(newV.intValue()));
		videoBar.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			// this method changes the frame of video capture based on the videoBar
			public void changed(ObservableValue<? extends Number> observable, Number oldValue,
					Number newValue) {


				Platform.runLater(new Runnable() {

					@Override
					// this method sets the frame of videoView
					public void run() {
						showFrameAt((int) newValue.doubleValue());
					}

				});
			}

		});

	}

	@FXML
	protected void addChick(ActionEvent event) {
		chickID.setEditable(true);
		confirmBtn.setDisable(false);
	}

	@FXML
	protected void confirmChick(ActionEvent event) {
		chickIDs.getItems().add(chickID.getText());
		project.getTracks().add(new AnimalTrack(chickID.getText()));
		project.getAnimalTrackInTracks(chickID.getText()).setColor(colorChoice.get(colorNum % colorChoice.size()));
		colorNum++;
		chickID.clear();
		chickID.setEditable(false);
		confirmBtn.setDisable(true);
	}

	@FXML
	protected void setPathtoChick(ActionEvent event) {
		if(chickIDs.getValue() != null && paths.getValue() != null) {
			if(!project.getAnimalTrackInTracks(chickIDs.getValue()).alreadyHasTime(project.getAtFrameRecorded())) {
				AnimalTrack chosenChick = project.getAnimalTrackInTracks(chickIDs.getValue());
				for(TimePoint point : project.getAnimalTrackInUnassignedSegments(paths.getValue()).getPositionHistory()) {
					chosenChick.add(point);
				}
				project.getUnassignedSegments().remove(project.getAnimalTrackInUnassignedSegments(paths.getValue()));
				paths.getItems().remove(paths.getValue());
			}
		}
	}

	public void showFrameAt(int frameNum) {
		project.getVideo().setCurrentFrameNum(frameNum);
		Image curFrame = Utils.matToJavaFXImage(project.getVideo().readFrame());
		imageView.setImage(curFrame);	
	}

	@FXML
	protected void showPath(ActionEvent event) {
		if(paths.getValue() != null) {
			System.out.println(paths.getValue());
			gc.setFill(Color.RED);
			gc.clearRect(canvasOverVideo.getLayoutX(), canvasOverVideo.getLayoutY(), canvasOverVideo.getWidth(), canvasOverVideo.getHeight());
			for(datamodel.TimePoint point : project.getAnimalTrackInUnassignedSegments(paths.getValue()).getPositionHistory()) {
				gc.fillOval(point.getX() * canvasOverVideo.getWidth() / project.getVideo().getFrameWidth(),
						point.getY() * canvasOverVideo.getHeight() / project.getVideo().getFrameHeight(), 5, 5);
			}
		}
	}

	public void showFullTrack(ActionEvent event) {
		if (chickIDs.getValue() != null) {
			gc.clearRect(canvasOverVideo.getLayoutX(), canvasOverVideo.getLayoutY(), canvasOverVideo.getWidth(), canvasOverVideo.getHeight());
			gc.setFill(project.getAnimalTrackInTracks(chickIDs.getValue()).getColor());
			for(datamodel.TimePoint point : project.getAnimalTrackInTracks(chickIDs.getValue()).getPositionHistory()) {
				gc.fillOval(point.getX() * canvasOverVideo.getWidth() / project.getVideo().getFrameWidth(),
						point.getY() * canvasOverVideo.getHeight() / project.getVideo().getFrameHeight(), 5, 5);
			}
		}
	}
	public void fitVideo() {
		double prefWidth = project.getVideo().getVideoCap().get(Videoio.CAP_PROP_FRAME_WIDTH);
		double prefHeight = project.getVideo().getVideoCap().get(Videoio.CAP_PROP_FRAME_HEIGHT);
			canvasOverVideo.setWidth(prefWidth);
			canvasOverVideo.setHeight(prefHeight);
			imageView.setFitWidth(prefWidth);
			imageView.setFitHeight(prefHeight);
//			analysisWindow.setPrefWidth(analysisWindow.getWidth());
//			analysisWindow.setPrefHeight(analysisWindow.getHeight());
		
	}
	
	@FXML
	public void setIncrement() {
		timeJump = timeChoices.getValue();
		mForward.setText(">> " + timeJump);
		mBack.setText(timeJump + " <<");
	}
	
	@FXML
	public void comfirmIncrement() {
		try {
			timeChoices.getItems().add(Double.parseDouble(timeIncrement.getText()));
			timeIncrement.clear();
			timeIncrement.setEditable(false);
			confirmIncrementBtn.setDisable(true);
		} catch (NumberFormatException e) {
			timeIncrement.clear();
			timeIncrement.setPromptText("Not a Number");
		} catch (NullPointerException e) {
			timeIncrement.clear();
			timeIncrement.setPromptText("No Value");
		}
	}
	
	@FXML
	public void addIncrement() {
		confirmIncrementBtn.setDisable(false);
		timeIncrement.setEditable(true);
	}

	@FXML
	public void moveForward() throws InterruptedException {
		if (timer != null && !timer.isShutdown()) {
			timer.shutdown();  // stop the auto-playing
			timer.awaitTermination(1000, TimeUnit.MILLISECONDS);
		}
		if(project.allHaveTimePoint(project.getVideo().getCurrentFrameNum()) || project.getVideo().getCurrentFrameNum() < project.getVideo().getStartFrameNum()) {
			int newFrameNum = (int)(project.getVideo().getCurrentFrameNum() + timeJump * project.getVideo().getFrameRate());
			if(project.getVideo().getEndFrameNum() >= newFrameNum){
				videoBar.setValue((double)newFrameNum);
			}
		}

	}

	@FXML
	public void moveBack() throws InterruptedException {
		if (timer != null && !timer.isShutdown()) {
			timer.shutdown();  // stop the auto-playing
			timer.awaitTermination(1000, TimeUnit.MILLISECONDS);
		}

		int newFrameNum = (int)(project.getVideo().getCurrentFrameNum() - timeJump * project.getVideo().getFrameRate());
		if(project.getVideo().getStartFrameNum() <= newFrameNum){
			videoBar.setValue((double)newFrameNum);
		}
	}
	
	@FXML
	public void saveProjectData() {
		
	}
}
