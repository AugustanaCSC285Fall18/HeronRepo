package edu.augustana.csc285.heron;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import datamodel.ProjectData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
/**
 * This class is the GUI for selecting the video file and for getting to
 * the next GUI.
 * @author Jinsoo Park && Ehren Braun
 * @date 9/27/2018
 */
public class FileWindowController {
	@FXML private Button BrowseBtn;
	@FXML private Button NextBtn;
	@FXML private Button loadBtn;
	@FXML private Button aboutBtn;
	@FXML private TextField fileField;
	@FXML private ImageView imageView;
	private ProjectData project;
	private boolean newFile;
	@FXML
	public void initialize(){
		NextBtn.setDisable(true);
		fileField.setEditable(false);
		Image image = new Image("http://www.clipartandcrafts.com/clipart/holidays/easter/images/yellow-chick.gif");
		imageView.setImage(image);
	}
	/**
	 * This method allows the user to select a file that will be
	 * used for the video
	 * @throws FileNotFoundException 
	 */
	@FXML
	public void handleBrowse() throws FileNotFoundException {
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Video");
		Window mainWindow = BrowseBtn.getScene().getWindow();
		File chosenFile = fileChooser.showOpenDialog(mainWindow);
		if(chosenFile != null) {
			fileField.setText(chosenFile.getPath());
			newFile = true;
			project = new ProjectData(chosenFile.getAbsolutePath());
			NextBtn.setDisable(false);
		}
		
	}
	
	/**
	 * this method will load the video for tracking
	 * @throws FileNotFoundException
	 */
	@FXML
	public void handleLoad() throws FileNotFoundException {
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Project");
		Window mainWindow = loadBtn.getScene().getWindow();
		File chosenFile = fileChooser.showOpenDialog(mainWindow);
		if(chosenFile != null) {
			fileField.setText(chosenFile.getPath());
			newFile = false;
			project = datamodel.ProjectData.loadFromFile(chosenFile);
			NextBtn.setDisable(false);
		}
	}
	
	/**
	 * This method allows the FileWindow to go to the TimeWindow
	 * @throws IOException -if there is no pane to load, then the exception is thrown
	 */
	@FXML
	public void handleNext() throws IOException {
		if(newFile) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("TimeWindow.fxml"));
			AnchorPane root = (AnchorPane)loader.load();
			TimeWindowController timeController = loader.getController();
			timeController.setProjectData(project);

			Scene timeScene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
			timeScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
			Stage primary = (Stage) NextBtn.getScene().getWindow();
			primary.setMinWidth(root.getPrefWidth()+10);
			primary.setMinHeight(root.getPrefHeight()+20);
			primary.setScene(timeScene);
		} else {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("AnalysisWindow.fxml"));
			BorderPane root = (BorderPane)loader.load();
			AnalysisWindowController analysisController = loader.getController();
			analysisController.setProjectData(project);
			Scene timeScene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
			timeScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Stage primary = (Stage) loadBtn.getScene().getWindow();
			primary.setMinWidth(root.getPrefWidth()+10);
			primary.setMinHeight(root.getPrefHeight()+20);
			primary.setScene(timeScene);
		}
	}
	
	/**
	 * This method will provide information about people who did this program.
	 */
	@FXML
	public void handleAbout() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("About Dialog");
		alert.setHeaderText("Information about ChickTrack Program Of Heron Team");
		alert.setContentText("Member:\n "
				+ "\tDuc Pham\n"
				+ "\tJinsoo Park\n"
				+ "\tEhren Braun\n"
				+ "\tYafet Zeleke\n"
				+ "Advisor:\n"
				+ "\tForrest Stonedalt\n\n"
				+ "CSC 285 at Augustana College\n"
				+ "Special thanks for Math and Computer Department because they let us learn something at here.");
		alert.showAndWait();
	}
}
