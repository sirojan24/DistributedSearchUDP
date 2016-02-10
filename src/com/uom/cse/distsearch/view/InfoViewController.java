package com.uom.cse.distsearch.view;

import java.util.List;

import com.uom.cse.distsearch.Node;
import com.uom.cse.distsearch.NodeApp;
import com.uom.cse.distsearch.view.model.Movie;
import com.uom.cse.distsearch.view.model.Neighbour;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class InfoViewController {

	@FXML
	public TableView<Neighbour> neighbourTable;
	@FXML
	private TableColumn<Neighbour, String> ipColumn;
	@FXML
	private TableColumn<Neighbour, String> nameColumn;

	@FXML
	public TableView<Movie> movieTable;
	@FXML
	private TableColumn<Movie, String> movieNameColumn;

	private ObservableList<Movie> movieDataList = FXCollections.observableArrayList();
	
	@FXML
	private Button btnSearch;
	@FXML
	private TextArea txtAreaInfo;

	private Stage registrationViewStage;
	private NodeApp nodeApp;
	
	private Node node;

	@FXML
	private void initialize() {
		// Initialize neighbour table with the two columns.
		ipColumn.setCellValueFactory(cellData -> cellData.getValue().IPAddressProperty());
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().NameProperty());

		// Initialize movie table with the two columns.
		movieNameColumn.setCellValueFactory(cellData -> cellData.getValue().NameProperty());

		// Add observable list data to the table
		movieTable.setItems(movieDataList);
		
	}
	
	public void setDialogStage(Stage registrationViewStage) {
		this.registrationViewStage = registrationViewStage;
	}

	public void setNodeApp(NodeApp nodeApp) {
		this.nodeApp = nodeApp;

		// Add observable list data to neighbour table
		neighbourTable.setItems(nodeApp.getNeighbourList());
	}

	public void setNode(Node node) {
		this.node = node;
		
		List<String> movieList = this.node.getMovies();
		
		for (String movieName : movieList) {
			movieDataList.add(new Movie(movieName));
		}
	}

	public void printInfo(String info) {
		txtAreaInfo.setText(txtAreaInfo.getText() + "\n" + info);
	}
}
