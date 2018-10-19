package edu.augustana.csc285.heron;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import datamodel.ProjectData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
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
	private ProjectData project;
	@FXML
	public void initialize() {
		NextBtn.setDisable(true);
	}
	/**
	 * This method allows the user to select a file that will be
	 * used for the video
	 * @throws FileNotFoundException 
	 */
	@FXML
	public void handleBrowse() throws FileNotFoundException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Image File");
		Window mainWindow = BrowseBtn.getScene().getWindow();
		File chosenFile = fileChooser.showOpenDialog(mainWindow);
		if(chosenFile != null) {
			project = new ProjectData(chosenFile.getAbsolutePath());
			NextBtn.setDisable(false);
		}
		
	}
	/**
	 * This method allows the FileWindow to go to the TimeWindow
	 * @throws IOException -if there is no pane to load, then the exception is thrown
	 */
	@FXML
	public void handleNext() throws IOException {
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
	}
}
