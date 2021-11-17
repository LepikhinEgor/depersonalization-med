package com.lepikhina;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Collection;

import com.lepikhina.model.ConnectionHolder;
import com.lepikhina.model.DatabaseService;
import com.lepikhina.model.DbConnectionProperties;
import com.lepikhina.model.data.DbTable;
import com.lepikhina.model.events.DbConnectEvent;
import com.lepikhina.model.events.EventBus;
import com.lepikhina.model.exceptions.DatabaseConnectException;
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
        DbConnectionProperties connectionProperties = new DbConnectionProperties();
        connectionProperties.setUrl(urlInput.getText());
        connectionProperties.setDatabaseName(dbNameInput.getText());
        connectionProperties.setUsername(loginInput.getText());
        connectionProperties.setPassword(passwordInput.getText());
        ConnectionHolder.setConnectionProperties(connectionProperties);

        DatabaseService databaseService = new DatabaseService();
        try {
            Collection<DbTable> databaseSchema = databaseService.getDatabaseSchema();
            if (!databaseSchema.isEmpty()) {
                EventBus.getInstance().sendEvent(new DbConnectEvent());
                closeWindow(event);
            }
        } catch (DatabaseConnectException e) {
            e.printStackTrace();
        }


    }

    @FXML
    public void closeWindow(ActionEvent event) {
        Stage stage = (Stage) urlInput.getScene().getWindow();
        stage.close();
    }
}
