package edu.augustana.csc285.heron;

import org.opencv.core.Mat;
import org.opencv.videoio.Videoio;

import datamodel.ProjectData;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
	
	public void setProjectData(ProjectData project) {
		this.project = project;
		for(datamodel.AnimalTrack animal : project.getUnassignedSegments()) {
			paths.getItems().add(animal.getAnimalID());
		}
		showFrameAt(project.getVideo().getStartFrameNum());
		System.out.println(project.getVideo().getStartFrameNum());
		System.out.println(project.getVideo().getEndFrameNum());
	}
	@FXML
	public void intialize() {
		chickID.setEditable(false);
		confirmBtn.setDisable(true);
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
						System.out.println("?");
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
		chickID.clear();
		chickID.setEditable(false);
		confirmBtn.setDisable(true);
	}
	
	@FXML
	protected void setPathtoChick(ActionEvent event) {
		System.out.println(chickIDs.getValue());
		System.out.println(paths.getValue());
		if(chickIDs.getValue() != null && paths.getValue() != null) {
			System.out.println("neither are null");
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
			for(datamodel.TimePoint point : project.getAnimalTrack(paths.getValue()).getPositionHistory()) {
				gc.fillOval(point.getX(), point.getY(), 10, 10);
			}
		}
	}
}
