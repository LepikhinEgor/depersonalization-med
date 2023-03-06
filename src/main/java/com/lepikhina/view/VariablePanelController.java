package com.lepikhina.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

import com.lepikhina.model.data.DepersonalizationColumn;
import com.lepikhina.model.data.ScriptVariable;
import com.lepikhina.model.data.VariableType;

public class VariablePanelController implements Initializable {

    @FXML
    public TextField varValue;
    @FXML
    public Label varLabel;

    DepersonalizationColumn column;

    ScriptVariable scriptVariable;

    public void init(DepersonalizationColumn column, ScriptVariable scriptVariable) {
        this.scriptVariable = scriptVariable;
        this.column = column;

        varValue.setText(column.getVariables().get(scriptVariable.getVarName()).toString());
        varLabel.setText(scriptVariable.getName());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        varValue.textProperty().addListener((obs, oldText, newText) -> {
            column.getVariables().put(scriptVariable.getVarName(), convertToVarType(varValue.getText()));
        });
    }

    private Object convertToVarType(String value) {
        VariableType type = scriptVariable.getType();

        if (type.equals(VariableType.FLOAT))
            return Double.parseDouble(value);
        else if (type.equals(VariableType.NUMBER))
            return Long.parseLong(value);
        else if (type.equals(VariableType.STRING))
            return value;
        else if (type.equals(VariableType.BOOLEAN))
            return Boolean.parseBoolean(value);

        throw new RuntimeException("Incorrect var type");
    }
}
