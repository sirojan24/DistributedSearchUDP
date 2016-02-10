package com.uom.cse.distsearch;

import java.io.IOException;

import com.uom.cse.distsearch.model.Request;
import com.uom.cse.distsearch.view.InfoViewController;
import com.uom.cse.distsearch.view.RegistrationViewController;
import com.uom.cse.distsearch.view.model.Neighbour;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class NodeApp extends Application {

	private ObservableList<Neighbour> neighbourDataList = FXCollections.observableArrayList();
	
	public ObservableList<Neighbour> getNeighbourList(){
		return this.neighbourDataList;
	}
	
	public void addNeighbour (String name, String ip){
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				Neighbour neighbour = new Neighbour(ip, name);
				neighbourDataList.add(neighbour);
			}
		});
	}
	
	public void showRegistrationViewStage() {
		try {
			// Load person overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(NodeApp.class.getResource("view/RegistrationView.fxml"));
			AnchorPane RegistrationViewPane = (AnchorPane) loader.load();
			Scene scene = new Scene(RegistrationViewPane);
			
			Stage registrationViewStage = new Stage();
			registrationViewStage.setTitle("Registration");
			registrationViewStage.setScene(scene);

			RegistrationViewController controller = loader.getController();
			controller.setDialogStage(registrationViewStage);
			controller.setNodeApp(this);

			registrationViewStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showInfoViewStage(Node node) {
		try {
			// Load person overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(NodeApp.class.getResource("view/InfoView.fxml"));
			AnchorPane RegistrationViewPane = (AnchorPane) loader.load();
			Scene scene = new Scene(RegistrationViewPane);
			
			Stage registrationViewStage = new Stage();
			registrationViewStage.setTitle("Distributed Search");
			registrationViewStage.setScene(scene);

			InfoViewController controller = loader.getController();
			controller.setDialogStage(registrationViewStage);
			controller.setNodeApp(this);
			controller.setNode(node);

			registrationViewStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		showRegistrationViewStage();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
