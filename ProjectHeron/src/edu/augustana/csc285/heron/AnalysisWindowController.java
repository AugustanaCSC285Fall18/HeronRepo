package edu.augustana.csc285.heron;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencv.core.Mat;
import org.opencv.videoio.Videoio;

import datamodel.AnimalTrack;
import datamodel.ProjectData;
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
import javafx.scene.paint.Color;

public class AnalysisWindowController {
	
	private ProjectData project;
	@FXML private Button addChickBtn;
	@FXML private Button confirmBtn;
	@FXML private Button setBtn;
	@FXML private Button showBtn;
	@FXML private TextField chickID;
	@FXML private Canvas canvasOverVideo;
	@FXML private ChoiceBox<String> chickIDs;
	@FXML private ChoiceBox<String> paths;
	@FXML private ImageView imageView;
	@FXML private Slider videoBar;
	private int currentFrameRecord;
	private Map<String, Integer> currentFrameRecordToChick;
	
	public void setProjectData(ProjectData project) {
		this.project = project;
		for(datamodel.AnimalTrack animal : project.getUnassignedSegments()) {
			paths.getItems().add(animal.getAnimalID());
		}
		showFrameAt(project.getVideo().getStartFrameNum());
		currentFrameRecord = project.getVideo().getStartFrameNum();
		currentFrameRecordToChick = new HashMap<String, Integer>();
	}
	
	@FXML
	public void initialize() {
		
		chickID.setEditable(false);
		confirmBtn.setDisable(true); 
		GraphicsContext gc = canvasOverVideo.getGraphicsContext2D();
		
		EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() { 
			@Override 
			public void handle(MouseEvent e) {
				System.out.println("x: " + e.getX() + ", y: " + e.getY());
				if (e.getButton() == MouseButton.PRIMARY) {
					gc.setFill(Color.BLACK);
					gc.fillOval(e.getX()-5, e.getY()-5, 10, 10);
					if(project.getAnimalTrackInTracks(chickIDs.getValue()).getPositionHistory().) {
						project.getAnimalTrackInTracks(chickIDs.getValue()).add(e.getX(), e.getY(), project.getVideo().getCurrentFrameNum());
					}
				} else if (e.getButton() == MouseButton.SECONDARY) {
					gc.clearRect(e.getX()-5, e.getY()-5, 10, 10);
				}
			}
		};
		canvasOverVideo.setOnMouseClicked(eventHandler);
		ObservableList<String> items = FXCollections.observableArrayList();
		chickIDs.setItems(items);
		ObservableList<String> pathList = FXCollections.observableArrayList();
		paths.setItems(pathList);

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
						showFrameAt((int)(newValue.doubleValue() / 100 * project.getVideo().getTotalNumFrames()));
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
		currentFrameRecordToChick.put(chickID.getText(), currentFrameRecord);
		chickID.clear();
		chickID.setEditable(false);
		confirmBtn.setDisable(true);
	}
	
	@FXML
	protected void setPathtoChick(ActionEvent event) {
		if(chickIDs.getValue() != null) {
	
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
			GraphicsContext gc = canvasOverVideo.getGraphicsContext2D();
			gc.setFill(Color.RED);
			gc.clearRect(canvasOverVideo.getLayoutX(), canvasOverVideo.getLayoutY(), canvasOverVideo.getWidth(), canvasOverVideo.getHeight());
			for(datamodel.TimePoint point : project.getAnimalTrackInUnassignedSegments(paths.getValue()).getPositionHistory()) {
				gc.fillOval(point.getX(), point.getY(), 10, 10);
			}
		}
	}
}
