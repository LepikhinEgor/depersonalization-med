package com.lepikhina.view;

import com.lepikhina.view.events.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.*;

import lombok.SneakyThrows;

public class MainWindowController {

    @FXML
    public VBox variablesPanel;

    public MainWindowController() {
        EventBus.getInstance().addListener(this);
    }

    @FXML
    public void sendDBDisconnectedEvent(ActionEvent event) {
        EventBus.sendEvent(new DBDisconnectEvent());
    }


    @FXML
    @SneakyThrows
    private void openConnectionWindow(ActionEvent event) {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(
                Objects.requireNonNull(getClass().getClassLoader().getResource("connection-preset-window.fxml")));
        stage.setScene(new Scene(root));
        stage.setTitle("Подключение");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.show();
    }
}
