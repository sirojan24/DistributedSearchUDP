package com.uom.cse.distsearch.view;

import java.util.List;

import com.uom.cse.distsearch.Node;
import com.uom.cse.distsearch.NodeApp;
import com.uom.cse.distsearch.view.model.Movie;
import com.uom.cse.distsearch.view.model.Neighbour;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.stage.Stage;
import javafx.util.Callback;

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
	@FXML
	private TextField txtSearch;

	private Stage registrationViewStage;
	private NodeApp nodeApp;
	
	private Node node;

	@FXML
	private void initialize() {
		// Initialize neighbour table with the two columns.
		ipColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Neighbour,String>, ObservableValue<String>>() {
			
			@Override
			public ObservableValue<String> call(CellDataFeatures<Neighbour, String> param) {
				// TODO Auto-generated method stub
				return param.getValue().IPAddressProperty();
			}
		});
		
		nameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Neighbour,String>, ObservableValue<String>>() {
			
			@Override
			public ObservableValue<String> call(CellDataFeatures<Neighbour, String> param) {
				// TODO Auto-generated method stub
				return param.getValue().NameProperty();
			}
		});

		// Initialize movie table with the two columns.
		movieNameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Movie,String>, ObservableValue<String>>() {
			
			@Override
			public ObservableValue<String> call(CellDataFeatures<Movie, String> param) {
				// TODO Auto-generated method stub
				return param.getValue().NameProperty();
			}
		});

		// Add observable list data to the table
		movieTable.setItems(movieDataList);
		
	}
	
	@FXML
	private void searchAction (){
		node.search(txtSearch.getText());
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
