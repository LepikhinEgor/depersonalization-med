package com.lepikhina.model.data;

import javafx.event.EventType;
import javafx.scene.control.ComboBox;

import java.awt.event.InputEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.lepikhina.model.events.ActionChangedEvent;
import com.lepikhina.model.events.EventBus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DepersonalizationColumn {

    DbColumn dbColumn;

    ComboBox<DepersonalizationAction> actionsBox;

    HashMap<String, Object> variables;

    @Setter
    String result;

    public DepersonalizationColumn(DbColumn dbColumn, List<DepersonalizationAction> actionList) {
        this.dbColumn = dbColumn;

        actionsBox = new ComboBox<>();
        actionsBox.getItems().addAll(actionList);
        variables = new HashMap<>();
        actionsBox.setOnAction( event -> EventBus.sendEvent(new ActionChangedEvent(this)));
        if (!actionList.isEmpty()) {
            DepersonalizationAction action = actionList.get(0);
            actionsBox.setValue(action);
            action.getVariables().forEach(variable -> variables.put(variable.getVarName(), variable.defaultValue));
        }
    }

    public boolean equalByColumn(DbColumn column) {
         return Objects.equals(dbColumn.getName(), column.getName()) &&
                Objects.equals(dbColumn.getTable().getName(), column.getTable().getName());
    }

    public String getName() {
        return dbColumn.getName();
    }

    public String getTable() {
        return dbColumn.getTable().getName();
    }

    public String getType() {
        return dbColumn.getType().getType();
    }

    public DbColumnType getColumnType() {
        return dbColumn.getType();
    }

    public String getResult() {
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DepersonalizationColumn that = (DepersonalizationColumn) o;
        return Objects.equals(dbColumn.getName(), that.dbColumn.getName()) &&
                Objects.equals(dbColumn.getTable(), that.dbColumn.getTable());
    }

    @Override
    public int hashCode() {
        return Objects.hash(dbColumn.getName(), dbColumn.getTable());
    }
}
