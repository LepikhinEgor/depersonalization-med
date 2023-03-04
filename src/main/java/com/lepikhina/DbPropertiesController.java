package com.lepikhina;

import com.lepikhina.model.DatabaseService;
import com.lepikhina.model.data.ConnectionsHolder;
import com.lepikhina.model.events.DbConnectEvent;
import com.lepikhina.model.events.EventBus;
import com.lepikhina.model.persitstence.ConnectionPreset;
import com.lepikhina.model.persitstence.PersistenceManager;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Setter
@Getter
public class DbPropertiesController implements Initializable {

    @FXML
    ChoiceBox<ConnectionPreset> presetInput;

    @FXML
    TextField presetNameInput;

    @FXML
    TextField urlInput;

    @FXML
    TextField dbNameInput;

    @FXML
    TextField dbSchemaInput;

    @FXML
    TextField loginInput;

    @FXML
    TextField passwordInput;

    @FXML
    public void connect(ActionEvent event) {
        ConnectionPreset connectionProperties = new ConnectionPreset();
        connectionProperties.setName(presetNameInput.getText());
        connectionProperties.setUrl(urlInput.getText());
        connectionProperties.setDatabaseName(dbNameInput.getText());
        connectionProperties.setSchemaName(dbSchemaInput.getText());
        connectionProperties.setUsername(loginInput.getText());
        connectionProperties.setPassword(passwordInput.getText());

        ConnectionsHolder.getInstance().setCurrentPreset(connectionProperties);

        DatabaseService databaseService = new DatabaseService();

        if (databaseService.isConnectionCorrect()) {
            EventBus.sendEvent(new DbConnectEvent());
            PersistenceManager.saveDatabaseProperties(connectionProperties);
            closeWindow(event);
        }
    }

    @FXML
    public void closeWindow(ActionEvent event) {
        Stage stage = (Stage) urlInput.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        presetInput.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            presetNameInput.setText(newValue.getName());
            urlInput.setText(newValue.getUrl());
            dbNameInput.setText(newValue.getDatabaseName());
            dbSchemaInput.setText(newValue.getSchemaName());
            loginInput.setText(newValue.getUsername());
            passwordInput.setText(newValue.getPassword());
        });

        List<ConnectionPreset> connectionsProperties = ConnectionsHolder.getInstance().getAllPresets();
        presetInput.setItems(new ObservableListWrapper<>(connectionsProperties));

        presetInput.setValue(connectionsProperties.get(0));
    }
}
