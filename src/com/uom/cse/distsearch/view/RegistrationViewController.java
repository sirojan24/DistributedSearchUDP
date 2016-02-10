package com.uom.cse.distsearch.view;

import java.io.IOException;
import java.net.SocketException;

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

	private Node node;

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
					final String nodeIp = txtNodeIP.getText();
					//final int nodePort = Integer.parseInt(txtNodePort.getText());
					final String username = txtUsername.getText();

					node = new Node(nodeIp, 0, username, Constant.MOVIE_FILE_NAME, nodeApp);
					
					final int nodePort = node.start();
					registrationViewStage.close();

					final Request response = Server.registerBootstrapServer(serverIp, serverPort, nodeIp, nodePort,
							username);

					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							if (response.getResponseCode() != 9999) {

								nodeApp.showInfoViewStage(node);
								node.onRequest(response);

								registrationViewStage.close();

							} else {
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
