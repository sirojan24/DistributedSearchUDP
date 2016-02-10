package com.uom.cse.distsearch.view;

import java.io.IOException;

import com.uom.cse.distsearch.Node;
import com.uom.cse.distsearch.NodeApp;
import com.uom.cse.distsearch.Server;
import com.uom.cse.distsearch.model.Request;
import com.uom.cse.distsearch.util.Constant;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegistrationViewController {
	@FXML
	private TextField txtServerIp;
	@FXML
	private TextField txtServerPort;
	@FXML
	private TextField txtNodeIP;
	@FXML
	private TextField txtNodePort;
	@FXML
	private TextField txtUsername;
	@FXML
	private Label lblErrorMsg;
	@FXML
	private Button btnRegister;
	private NodeApp nodeApp;
	private Stage registrationViewStage;

	@FXML
	private void registerAction() {
		lblErrorMsg.setText("");
		
		setDisableElements(true);
		
		new Thread() {
			@Override
			public void run() {
				try {
					String serverIp = txtServerIp.getText();
					int serverPort = Integer.parseInt(txtServerPort.getText());
					String nodeIp = txtNodeIP.getText();
					int nodePort = Integer.parseInt(txtNodePort.getText());
					String username = txtUsername.getText();

					Request response = Server.registerBootstrapServer(serverIp, serverPort, nodeIp, 
							nodePort, username);
					
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							if (response.getResponseCode() != 9999){
								
								Node node = new Node(nodeIp, nodePort, username, 
										Constant.MOVIE_FILE_NAME, nodeApp);
								
								node.startReceiving();
								node.onRequest(response);
								
								nodeApp.showInfoViewStage(node);
								
								registrationViewStage.close();
							}else{
								lblErrorMsg.setText("This IP has already been registered in the server!!!");
								setDisableElements(false);
							}
						}
					});
					
				} catch (NumberFormatException numEx) {
					
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							lblErrorMsg.setText("Please Check Your Input!!!");
							setDisableElements(false);
						}
					});
					
				} catch (IOException e) {
					
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							setDisableElements(false);
							lblErrorMsg.setText("Unable to connect to the server!!!");
						}
					});
					
				}
			}
		}.start();
	}

	private void setDisableElements(boolean flag) {
		// disable all element
		txtServerIp.setDisable(flag);
		txtServerPort.setDisable(flag);
		txtNodeIP.setDisable(flag);
		txtNodePort.setDisable(flag);
		txtUsername.setDisable(flag);
		btnRegister.setDisable(flag);
	}

	public void setDialogStage(Stage registrationViewStage) {
		this.registrationViewStage = registrationViewStage;
	}

	public void setNodeApp(NodeApp nodeApp) {
		this.nodeApp = nodeApp;
	}
}
