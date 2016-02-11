package com.uom.cse.distsearch;

import java.io.IOException;

import com.uom.cse.distsearch.util.Settings;
import com.uom.cse.distsearch.view.InfoViewController;
import com.uom.cse.distsearch.view.RegistrationViewController;
import com.uom.cse.distsearch.view.model.Neighbour;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class NodeApp extends Application {

    private ObservableList<Neighbour> neighbourDataList = FXCollections.observableArrayList();
    private InfoViewController infoViewController;

    public ObservableList<Neighbour> getNeighbourList() {
        return this.neighbourDataList;
    }

    public void printInfo(final String info) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                if (infoViewController != null) {
                    infoViewController.printInfo(info);
                }
            }
        });
    }

    public void addNeighbour(final String name, final String ip) {
        synchronized (NodeApp.class) {
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    Neighbour neighbour = new Neighbour(ip, name);
                    neighbourDataList.add(neighbour);
                }
            });
        }
    }

    public void removeNeighbour(final String name, final String ip) {
        System.out.println("Leave command from ip : " + ip + "name : " + name);
        synchronized (NodeApp.class) {
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    int count = 0;
                    for (Neighbour neighbour : neighbourDataList) {

                        if (neighbour.getIPAddress().equals(ip) && neighbour.getName().equals(name)) {
                            neighbourDataList.remove(count);
                            break;
                        }
                        count++;
                    }
                }
            });
        }
    }

    public void showRegistrationViewStage() {
        Settings settings = Settings.getInstance();
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(NodeApp.class.getResource("/com/uom/cse/distsearch/view/RegistrationView.fxml"));
            AnchorPane RegistrationViewPane = (AnchorPane) loader.load();
            Scene scene = new Scene(RegistrationViewPane);

            Stage registrationViewStage = new Stage();
            registrationViewStage.setTitle("Registration");
            registrationViewStage.setScene(scene);
            registrationViewStage.initStyle(StageStyle.UTILITY);
            registrationViewStage.getIcons().add(new Image("icon.png"));

            RegistrationViewController controller = loader.getController();
            controller.setDialogStage(registrationViewStage);
            controller.setNodeApp(this);
            controller.setDefaults(settings.getServerIP(), settings.getServerPort(), settings.getNodeIP(),
                    settings.getNodePort(), settings.getUserName());

            registrationViewStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showInfoViewStage(Node node) {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(NodeApp.class.getResource("/com/uom/cse/distsearch/view/InfoView.fxml"));
            AnchorPane RegistrationViewPane = (AnchorPane) loader.load();
            Scene scene = new Scene(RegistrationViewPane);

            Stage registrationViewStage = new Stage();
            registrationViewStage.setTitle("Distributed Search");
            registrationViewStage.setScene(scene);
            registrationViewStage.setResizable(false);
            registrationViewStage.getIcons().add(new Image("icon.png"));

            infoViewController = loader.getController();
            infoViewController.setDialogStage(registrationViewStage);
            infoViewController.setNodeApp(this);
            infoViewController.setNode(node);

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
