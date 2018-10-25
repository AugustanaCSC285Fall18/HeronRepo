package edu.augustana.csc285.heron;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;




import org.opencv.core.Mat;
import org.opencv.videoio.Videoio;
import datamodel.ProjectData;
import datamodel.Video;
import edu.augustana.csc285.heron.Utils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.Light.Point;
import javafx.scene.image.ImageView;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;


import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * This class is the GUI for selecting the start and end time of what will be
 * recorded as data and for getting to the next GUI.
 * 
 * @author Jinsoo Park && Ehren Braun
 * @date 9/27/2018
 * @update by Duc Pham 10/17/2018
 */
public class TimeWindowController {
	@FXML private Slider videoBar;
	@FXML private Button startBtn;
	@FXML private Button endBtn;
	@FXML private Button nextBtn;
	@FXML private Button wholeBtn;
	@FXML private Button emptyFrameBtn;
	@FXML private ImageView videoView;
	@FXML private Label timeLabel;
	@FXML private TextField startTime;
	@FXML private TextField endTime;
	@FXML
	private Canvas canvasOverVideo;
	private Video vid;
	private ArrayList<Point2D> calibrationPoints;
	private int getWidthLength;
	private int getHeightLength;
	private ProjectData project;
	private Point2D center;
	/**
	 * This method sets the start time of the video
	 */
	@FXML
	public void selectStartTime() {
		project.getVideo().setStartFrameNum((int)(videoBar.getValue() / 1000 * (project.getVideo().getVideoCap().get(Videoio.CAP_PROP_FRAME_COUNT) - 1)));
		int numSeconds = ((int)project.getVideo().convertFrameNumsToSeconds(project.getVideo().getCurrentFrameNum()) % 60);
		int numMinutes = (int)project.getVideo().convertFrameNumsToSeconds(project.getVideo().getCurrentFrameNum()) / 60;
		startTime.setText(numMinutes + ":" + (numSeconds < 10 ? ("0" + numSeconds) : (""+numSeconds)));
		if(allSelected()) {
			nextBtn.setDisable(false);
		}
	}

	public boolean allSelected() {
		int emptyFrame = project.getVideo().getEmptyFrameNum();
		int startFrameNum = project.getVideo().getStartFrameNum();
		int endFrameNum = project.getVideo().getEndFrameNum();
		return startFrameNum != -1 && endFrameNum != -1 && startFrameNum < endFrameNum; //&& emptyFrame != -1;
	}

	@FXML
	public void selectEmptyFrame() {
		project.getVideo().setEmptyFrameNum((int)(videoBar.getValue() / 1000 * (project.getVideo().getVideoCap().get(Videoio.CAP_PROP_FRAME_COUNT) - 1)));
		if(allSelected()) {
			nextBtn.setDisable(false);
		}
	}

	/**
	 *  This method sets the whole video
	 */
	@FXML
	public void wholeVideo() {
		project.getVideo().setStartFrameNum(0);
		project.getVideo().setEndFrameNum((int)(project.getVideo().getVideoCap().get(Videoio.CAP_PROP_FRAME_COUNT)-1));
		nextBtn.setDisable(false);
	}
	/**
	 * This method sets the end time of the video
	 */
	@FXML
	public void selectEndTime() {
		project.getVideo().setEndFrameNum((int)(videoBar.getValue() / 1000 * (project.getVideo().getVideoCap().get(Videoio.CAP_PROP_FRAME_COUNT) - 1)));
		int numSeconds = ((int)project.getVideo().convertFrameNumsToSeconds(project.getVideo().getCurrentFrameNum()) % 60);
		int numMinutes = (int)project.getVideo().convertFrameNumsToSeconds(project.getVideo().getCurrentFrameNum()) / 60;
		endTime.setText(numMinutes + ":" + (numSeconds < 10 ? ("0" + numSeconds) : (""+numSeconds)));
		if(allSelected()) {
			nextBtn.setDisable(false);
		}
	}

	/**
	 * This loads the video so that the user can select the start and end time
	 * while being able to see where in the video the value is based on the slider
	 * @param video -the video that will be played
	 */

	public void setVideo(Video video) {
		vid = video;
	}
	public void setProjectData(ProjectData project) {
		this.project = project;
		fitVideo();
		Video vid = project.getVideo();		
		if (vid.getFilePath() != null) {
			try {
				// start the video capture
				vid.getVideoCap().open(vid.getFilePath());
				
				// sets the video capture to the start
				vid.getVideoCap().set(Videoio.CAP_PROP_POS_FRAMES, 0);
				
				// creates a listener for the videoBar
				videoBar.valueProperty().addListener(new ChangeListener<Number>() {

					@Override
					// this method changes the frame of video capture based on the videoBar
					public void changed(ObservableValue<? extends Number> observable, Number oldValue,
							Number newValue) {
						
						vid.getVideoCap().set(Videoio.CAP_PROP_POS_FRAMES,
								(int) (newValue.doubleValue() / 1000 * (vid.getVideoCap().get(Videoio.CAP_PROP_FRAME_COUNT) - 1)));
						Platform.runLater(new Runnable() {

							@Override
							// this method sets the frame of videoView
							public void run() {
								Mat newFrame = vid.grabFrame();
								videoView.setImage(Utils.mat2Image(newFrame));
								int numSeconds = ((int)project.getVideo().convertFrameNumsToSeconds(project.getVideo().getCurrentFrameNum()) % 60);
								int numMinutes = (int)project.getVideo().convertFrameNumsToSeconds(project.getVideo().getCurrentFrameNum()) / 60;
								timeLabel.setText(numMinutes + ":" + (numSeconds < 10 ? ("0" + numSeconds) : (""+numSeconds)));

							}
						});
					}
				});
				// sets the frame of videoView to the start
				Mat frame = vid.grabFrame();
				videoView.setImage(Utils.mat2Image(frame));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	@FXML
	public void initialize() {
		startTime.setDisable(true);
		endTime.setDisable(true);
		GraphicsContext gc = canvasOverVideo.getGraphicsContext2D();
		calibrationPoints = new ArrayList<>();
		canvasOverVideo.setOnMouseClicked(event -> {
			gc.setStroke(Color.RED);
			if (calibrationPoints.size() == 1) {
				gc.fillOval(event.getX() - 3, event.getY() - 3, 6, 6);
				center = new Point2D(event.getX(), event.getY());
			}
		});
		canvasOverVideo.setOnMousePressed(event -> {
			calibrationPoints.add(new Point2D(event.getX(), event.getY()));
		});
		canvasOverVideo.setOnMouseDragged(event -> {
			Point2D startPoint = calibrationPoints.get(calibrationPoints.size()-1);		
			double minX = Math.min(startPoint.getX(), event.getX());
			double minY = Math.min(startPoint.getY(), event.getY());
			double width = Math.abs(event.getX() - startPoint.getX());
			double height = Math.abs(event.getY() - startPoint.getY());
			Rectangle2D arenaRect = new Rectangle2D(minX,minY,minX+width,minY+height); 
			project.getVideo().setArenaBounds(arenaRect);
			
			gc.setStroke(Color.RED);
			gc.clearRect(0, 0, canvasOverVideo.getWidth(), canvasOverVideo.getHeight());
			gc.strokeRect(arenaRect.getMinX(), arenaRect.getMinY(), arenaRect.getWidth(), arenaRect.getHeight());

		});

		canvasOverVideo.setOnMouseReleased(event -> {
			// Do what you want with selection's properties here
			//System.out.println(project.getVideo().getArenaBounds().getX() + project.getVideo().getArenaBounds().getY() + project.getVideo().getArenaBounds().getWidth() + project.getVideo().getArenaBounds().getHeight());
			if (calibrationPoints.size() > 2 ) {
				createDialog();
			}
		});
		nextBtn.setDisable(true);

	}

	/**
	 * This method allows the TimeWindow to go the MainWindow
	 * 
	 * @throws IOException -if there is no pane to load, then the exception is
	 *                     thrown
	 */
	@FXML
	public void handleNext() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("AutoTrackWindow.fxml"));
		BorderPane root = (BorderPane)loader.load();
		AutoTrackWindowController autoTrackController = loader.getController();
		autoTrackController.setProjectData(project);
		Scene timeScene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
		timeScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
		Stage primary = (Stage) nextBtn.getScene().getWindow();
		primary.setMinWidth(root.getPrefWidth()+10);
		primary.setMinHeight(root.getPrefHeight()+20);
		primary.setScene(timeScene);
	
	}

	public double handleCaliberationWidth() {
		return getWidthLength * 1.0 / project.getVideo().getArenaBounds().getWidth();
	}

	public double handleCaliberationHeight() {
		return getHeightLength * 1.0 /project.getVideo().getArenaBounds().getHeight();
	}

	public void createDialog() {
		// Create the custom dialog.
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Ratio Dialog");
		dialog.setHeaderText("This Dialog is For Ratio");

		// Set the button types.
		ButtonType accpetButtonType = new ButtonType("Accept", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(accpetButtonType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField widthLength = new TextField();
		widthLength.setPromptText("Width Length in centimeter.");
		TextField heightLength = new TextField();
		heightLength.setPromptText("Height Length in centimeter.");

		grid.add(new Label("Width Length:"), 0, 0);
		grid.add(widthLength, 1, 0);
		grid.add(new Label("Height Length:"), 0, 1);
		grid.add(heightLength, 1, 1);

		// Enable/Disable
		Node accpetButton = dialog.getDialogPane().lookupButton(accpetButtonType);
		accpetButton.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		widthLength.textProperty().addListener((observable, oldValue, newValue) -> {
			accpetButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.getDialogPane().setContent(grid);

		// Request focus
		Platform.runLater(() -> widthLength.requestFocus());

		// Convert the result
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == accpetButtonType) {
				return new Pair<>(widthLength.getText(), heightLength.getText());
			}
			return null;
		});
		Optional<Pair<String, String>> result = dialog.showAndWait();
		if (result.isPresent()) {
			getWidthLength = Integer.parseInt(widthLength.getText());
			getHeightLength = Integer.parseInt(heightLength.getText());
			project.getVideo().setStartFrameNum(0);
			project.getVideo().setEndFrameNum((int)(project.getVideo().getVideoCap().get(Videoio.CAP_PROP_FRAME_COUNT-1)));
		//	nextBtn.setDisable(false);
		}
	}
	
	public void fitVideo() {
		double prefWidth = project.getVideo().getVideoCap().get(Videoio.CAP_PROP_FRAME_WIDTH);
		double prefHeight = project.getVideo().getVideoCap().get(Videoio.CAP_PROP_FRAME_HEIGHT);
		canvasOverVideo.setWidth(prefWidth);
		canvasOverVideo.setHeight(prefHeight);
		videoView.setFitWidth(prefWidth);
		videoView.setFitHeight(prefHeight);
		}
	}

