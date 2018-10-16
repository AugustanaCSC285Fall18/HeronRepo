package edu.augustana.csc285.heron;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
	@FXML private Button showTrack;
	@FXML private TextField chickID;
	@FXML private Canvas canvasOverVideo;
	@FXML private ChoiceBox<String> chickIDs;
	@FXML private ChoiceBox<String> paths;
	@FXML private ImageView imageView;
	@FXML private Slider videoBar;
	@FXML private BorderPane analysisWindow;
	private int currentFrameRecord;
	private int colorNum;
	private ArrayList<Integer> currentFrameNum;
	private ArrayList<Color> colorChoice;
	private Map<String, Integer> currentFrameRecordToChick;
	private GraphicsContext gc;

	public void setProjectData(ProjectData project) {
		currentFrameNum = new ArrayList<Integer>();
		this.project = project;
		fitVideo();
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
					if(chickIDs.getValue() != null) {
						if(!project.getAnimalTrackInTracks(chickIDs.getValue()).alreadyHasTime(project.getVideo().getCurrentFrameNum())) {
							project.getAnimalTrackInTracks(chickIDs.getValue()).add(e.getX(), e.getY(), project.getVideo().getCurrentFrameNum());
							gc.setFill(project.getAnimalTrackInTracks(chickIDs.getValue()).getColor());
							gc.fillOval(e.getX()-5, e.getY()-5, 10, 10);
						}
					}
					currentFrameNum.add(project.getVideo().getCurrentFrameNum());
				} else if (e.getButton() == MouseButton.SECONDARY) {
					if(!project.getTracks().isEmpty()) {
						project.getTracks().remove(project.getTracks().size()-1);
					}
					gc.clearRect(e.getX()-5, e.getY()-5, 20, 20);

				}
				currentFrameNum.add(project.getVideo().getCurrentFrameNum());
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
						showFrameAt((int)(newValue.doubleValue() / 100 * (project.getVideo().getTotalNumFrames()-1)));
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
		project.getAnimalTrackInTracks(chickID.getText()).setColor(colorChoice.get(colorNum%colorChoice.size()));
		colorNum++;
		currentFrameRecordToChick.put(chickID.getText(), currentFrameRecord);
		chickID.clear();
		chickID.setEditable(false);
		confirmBtn.setDisable(true);
	}

	@FXML
	protected void setPathtoChick(ActionEvent event) {
		if(chickIDs.getValue() != null && paths.getValue() != null) {
			AnimalTrack chosenChick = project.getAnimalTrackInTracks(chickIDs.getValue());
			for (TimePoint point : project.getAnimalTrackInUnassignedSegments(paths.getValue()).getPositionHistory()) {
				chosenChick.add(point);
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
			gc.setFill(Color.RED);
			gc.clearRect(canvasOverVideo.getLayoutX(), canvasOverVideo.getLayoutY(), canvasOverVideo.getWidth(), canvasOverVideo.getHeight());
			for(datamodel.TimePoint point : project.getAnimalTrackInUnassignedSegments(paths.getValue()).getPositionHistory()) {
				gc.fillOval(point.getX() * canvasOverVideo.getWidth() / project.getVideo().getFrameWidth(), point.getY() * canvasOverVideo.getHeight() / project.getVideo().getFrameHeight(), 5, 5);
			}
		}
	}

	public void showFullTrack(ActionEvent event) {
		if (chickIDs.getValue() != null) {
			gc.clearRect(canvasOverVideo.getLayoutX(), canvasOverVideo.getLayoutY(), canvasOverVideo.getWidth(), canvasOverVideo.getHeight());
			gc.setFill(project.getAnimalTrackInTracks(chickIDs.getValue()).getColor());
			for(datamodel.TimePoint point : project.getAnimalTrackInTracks(chickIDs.getValue()).getPositionHistory()) {
				gc.fillOval(point.getX() * canvasOverVideo.getWidth() / project.getVideo().getFrameWidth(), point.getY() * canvasOverVideo.getHeight() / project.getVideo().getFrameHeight(), 5, 5);
			}
		}
	}

	public boolean exist(ArrayList<Integer> list, int num) {
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i) == num) {
				return true;
			}
		}
		return false;
	}


	public void fitVideo() {
		double prefWidth = project.getVideo().getVideoCap().get(Videoio.CAP_PROP_FRAME_WIDTH);
		double prefHeight = project.getVideo().getVideoCap().get(Videoio.CAP_PROP_FRAME_HEIGHT);
		if (prefWidth > imageView.getFitWidth() || prefHeight > imageView.getFitHeight()) {
			canvasOverVideo.setWidth(prefWidth/2);
			canvasOverVideo.setHeight(prefHeight/2);
			imageView.setFitWidth(prefWidth/2);
			imageView.setFitHeight(prefHeight/2);
		} else {
			canvasOverVideo.setWidth(prefWidth);
			canvasOverVideo.setHeight(prefHeight);
			imageView.setFitWidth(prefWidth);
			imageView.setFitHeight(prefHeight);
			analysisWindow.setPrefWidth(analysisWindow.getWidth()/2);
			analysisWindow.setPrefHeight(analysisWindow.getHeight()/2);
		}
	}
}
