package com.lepikhina;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import com.lepikhina.model.ConnectionHolder;
import com.lepikhina.model.DatabaseService;
import com.lepikhina.model.persitstence.DatabaseProperties;
import com.lepikhina.model.events.DbConnectEvent;
import com.lepikhina.model.events.EventBus;
import com.lepikhina.model.persitstence.PersistenceManager;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DbPropertiesController {

    @FXML
    TextField urlInput;

    @FXML
    TextField dbNameInput;

    @FXML
    TextField loginInput;

    @FXML
    TextField passwordInput;

    @FXML
    public void connect(ActionEvent event) {
        DatabaseProperties connectionProperties = new DatabaseProperties();
        connectionProperties.setUrl(urlInput.getText());
        connectionProperties.setDatabaseName(dbNameInput.getText());
        connectionProperties.setUsername(loginInput.getText());
        connectionProperties.setPassword(passwordInput.getText());
        ConnectionHolder.setConnectionProperties(connectionProperties);

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
}
