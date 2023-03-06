package com.lepikhina.view;

import com.lepikhina.model.data.DepersonalizationAction;
import com.lepikhina.model.data.DepersonalizationColumn;
import com.lepikhina.model.data.ScriptVariable;
import com.lepikhina.model.events.ActionChangedEvent;
import com.lepikhina.model.events.EventBus;
import com.lepikhina.model.events.EventListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;

import java.net.URL;
import java.util.Objects;

public class VariablesPanelController {

    @FXML
    public VBox variablesPanel;

    public VariablesPanelController() {
        EventBus.getInstance().addListener(this);
    }

    @SneakyThrows
    @EventListener(ActionChangedEvent.class)
    public void onActionChanged(ActionChangedEvent event) {
        DepersonalizationColumn newAction = event.getNewAction();
        if (newAction != null) {
            DepersonalizationAction selectedAction = newAction.getActionsBox().getValue();

            selectedAction.getVariables()
                    .forEach(variable -> newAction.getVariables().putIfAbsent(variable.getVarName(), variable.getDefaultValue()));

            URL resource = Objects.requireNonNull(getClass().getClassLoader().getResource("variable-panel.fxml"));

            variablesPanel.getChildren().clear();
            for (ScriptVariable variable : selectedAction.getVariables()) {
                FXMLLoader loader = new FXMLLoader(resource);
                loader.load();
                variablesPanel.getChildren().add(loader.getRoot());
                VariablePanelController controller = loader.getController();
                controller.init(newAction, variable);
            }
        } else {
            variablesPanel.getChildren().clear();
        }
    }
}
