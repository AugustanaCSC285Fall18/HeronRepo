package edu.augustana.csc285.heron;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
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
import javafx.stage.FileChooser;

public class AnalysisWindowController {

	private ProjectData project;
	@FXML private Button addChickBtn;
	@FXML private Button confirmBtn;
	@FXML private Button setBtn;
	@FXML private Button showBtn;
	@FXML private Button saveBtn;
	@FXML private TextField chickID;
	@FXML private Canvas canvasOverVideo;
	@FXML private Label startTime;
	@FXML private Label endTime;
	@FXML private ChoiceBox<String> chickIDs;
	@FXML private ChoiceBox<String> paths;
	@FXML private ImageView imageView;
	@FXML private Slider videoBar;
	@FXML private BorderPane analysisWindow;
	@FXML private Label timeLabel;
	@FXML private Button mBack;
	@FXML private Button mForward;
	@FXML private Button incrementSetBtn;
	@FXML private Button addIncrementBtn;
	@FXML private Button confirmIncrementBtn;
	@FXML private ChoiceBox<Double> timeChoices;
	@FXML private TextField timeIncrement;
	@FXML private CheckBox hasTrackBox;
	private GraphicsContext gc;
	private List<Color> colorChoice;
	private int colorNum;
	private ScheduledExecutorService timer;
	private double timeJump;


	public void setProjectData(ProjectData project) {
		this.project = project;
		int startTimeNumSeconds = ((int)project.getVideo().convertFrameNumsToSeconds(project.getVideo().getStartFrameNum()) % 60);
		int startTimeNumMinutes = (int)project.getVideo().convertFrameNumsToSeconds(project.getVideo().getStartFrameNum()) / 60;
		int endTimeNumSeconds = ((int)project.getVideo().convertFrameNumsToSeconds(project.getVideo().getEndFrameNum()) % 60);
		int endTimeNumMinutes = (int)project.getVideo().convertFrameNumsToSeconds(project.getVideo().getEndFrameNum()) / 60;
		startTime.setText("Start Time: " + startTimeNumMinutes + ":" +(startTimeNumSeconds < 10 ? ("0" + startTimeNumSeconds) : (""+ startTimeNumSeconds)));
		endTime.setText("End Time: " + endTimeNumMinutes + ":" +(endTimeNumSeconds < 10 ? ("0" + endTimeNumSeconds) : (""+ endTimeNumSeconds)));
		timeJump = project.getVideo().getFrameRate();
		fitVideo();
		videoBar.setMax(project.getVideo().getTotalNumFrames() - 1);
		videoBar.setValue(project.getVideo().getStartFrameNum());
		List<String> remove = new ArrayList<String>();
		for(datamodel.AnimalTrack animal : project.getUnassignedSegments()) {
			if(animal.size() < project.getVideo().getFrameRate() * 2) {
				remove.add(animal.getAnimalID());
			} else if(!animal.getTimePointsWithinInterval(project.getVideo().getStartFrameNum(), project.getVideo().getStartFrameNum() + (int) project.getVideo().getFrameRate() * 2).isEmpty()){
				paths.getItems().add(animal.getAnimalID());
			}
		}
		for(String id : remove) {
			project.getUnassignedSegments().remove(project.getAnimalTrackInUnassignedSegments(id));
		}
	}

	@FXML
	public void initialize() {
		hasTrackBox.setDisable(true);
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
					if(project.getVideo().inRectangle(e.getX(), e.getSceneY())){
						if(chickIDs.getValue() != null && project.getVideo().getCurrentFrameNum() >= project.getVideo().getStartFrameNum() && 
								project.getVideo().getCurrentFrameNum() <= project.getVideo().getEndFrameNum()) {
							if(!project.getAnimalTrackInTracks(chickIDs.getValue()).alreadyHasTime(project.getVideo().getCurrentFrameNum())) {
								project.getAnimalTrackInTracks(chickIDs.getValue()).add(e.getX()*project.getVideo().getFrameWidth()/canvasOverVideo.getWidth(),
										e.getY()*project.getVideo().getFrameHeight()/canvasOverVideo.getHeight(), project.getVideo().getCurrentFrameNum());
								gc.setFill(project.getAnimalTrackInTracks(chickIDs.getValue()).getColor());
							gc.fillOval(e.getX()-3, e.getY()-3, 6, 6);
							} //else {
								//project.getAnimalTrackInTracks(chickIDs.getValue()).getPositionHistory().remove(project.getAnimalTrackInTracks(chickIDs.getValue()).getTimePointAtTime(project.getVideo().getCurrentFrameNum()));
								//project.getAnimalTrackInTracks(chickIDs.getValue()).add(e.getX()*project.getVideo().getFrameWidth()/canvasOverVideo.getWidth(),
										//e.getY()*project.getVideo().getFrameHeight()/canvasOverVideo.getHeight(), project.getVideo().getCurrentFrameNum());
							//}							
							int newFrameNum = (int)(project.getVideo().getCurrentFrameNum() + timeJump * project.getVideo().getFrameRate());
							if(project.getVideo().getEndFrameNum() >= newFrameNum){
								videoBar.setValue((double)newFrameNum);
							}
						}
						
					}
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
						int numSeconds = ((int)project.getVideo().convertFrameNumsToSeconds(project.getVideo().getCurrentFrameNum()) % 60);
						int numMinutes = (int)project.getVideo().convertFrameNumsToSeconds(project.getVideo().getCurrentFrameNum()) / 60;
						timeLabel.setText(numMinutes + ":" + (numSeconds < 10 ? ("0" + numSeconds) : (""+numSeconds)));
						int beforeTime = project.getVideo().getCurrentFrameNum() - (int)project.getVideo().getFrameRate();
						int afterTime = project.getVideo().getCurrentFrameNum() + (int)project.getVideo().getFrameRate();
						for(AnimalTrack animal : project.getUnassignedSegments()) {
							if(!paths.getItems().contains(animal.getAnimalID())) {
								if(!animal.getTimePointsWithinInterval(beforeTime < 0 ? 0 : beforeTime , afterTime > project.getVideo().getEndFrameNum() ? project.getVideo().getEndFrameNum() : afterTime).isEmpty()) {
									paths.getItems().add(animal.getAnimalID());
								}
							} else if(animal.getTimePointsWithinInterval(beforeTime < 0 ? 0 : beforeTime , afterTime > project.getVideo().getEndFrameNum() ? project.getVideo().getEndFrameNum() : afterTime).isEmpty()) {
								paths.getItems().remove(animal.getAnimalID());
							}
						}
						if(!paths.getItems().isEmpty()) {
							hasTrackBox.setSelected(true);
						} else {
							hasTrackBox.setSelected(false);
						}
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
				AnimalTrack chosenPath = project.getAnimalTrackInUnassignedSegments(paths.getValue());
				for(int numPoints = 0; numPoints < chosenPath.size() / (int) project.getVideo().getFrameRate(); numPoints++) {
					chosenChick.add(chosenPath.getPositionHistory().get(numPoints * (int) project.getVideo().getFrameRate()));
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
			gc.clearRect(0, 0, canvasOverVideo.getWidth(), canvasOverVideo.getHeight());
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
		int newFrameNum = (int)(project.getVideo().getCurrentFrameNum() + timeJump * project.getVideo().getFrameRate());
		if(project.getVideo().getEndFrameNum() >= newFrameNum){
			videoBar.setValue((double)newFrameNum);
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
	public void saveProjectData() throws FileNotFoundException {
		FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showSaveDialog(showBtn.getScene().getWindow());
		if(file != null) {
			project.saveToFile(file);
		}
		
	}
	/*
	 * Exporting CSV file.
	 * Suggested file name as name of the video.
	 */
@FXML
	public void exportCSVData() throws FileNotFoundException{
		
		String filePath = project.getVideo().getFilePath();
		System.out.println("original video path: "+ filePath);

		int pos = filePath.lastIndexOf(".");
		if (pos > 0) {
		    filePath = filePath.substring(0, pos);
		}
		
		//filePath = filePath + ".csv";
		
		String fileName = new File(filePath).getName();
		System.out.println("suggested filename: "+ fileName);

		FileChooser fileChooser = new FileChooser();
		
		fileChooser.setInitialFileName(fileName);
		File file = fileChooser.showSaveDialog(showBtn.getScene().getWindow());
		String userFileName = file + ".csv";
		
		if(file != null) {
			System.out.println("user chose: "+userFileName);

			project.exportCSV(userFileName);
		}
		
		
		
	}

}
