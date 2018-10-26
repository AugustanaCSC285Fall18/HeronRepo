package edu.augustana.csc285.heron;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import datamodel.ProjectData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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
	@FXML private TextField fileField;
	private ProjectData project;
	private boolean newFile;
	@FXML
	public void initialize() {
		NextBtn.setDisable(true);
		fileField.setEditable(false);
		
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
}
