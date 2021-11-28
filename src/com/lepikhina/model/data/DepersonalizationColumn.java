package com.lepikhina.model.data;

import javafx.scene.control.ComboBox;

import java.util.List;

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

    public DepersonalizationColumn(DbColumn dbColumn, List<DepersonalizationAction> actionList) {
        this.dbColumn = dbColumn;

        actionsBox = new ComboBox<>();
        actionsBox.getItems().addAll(actionList);
        actionsBox.setValue(actionList.get(0));
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

    public String getForeignKey() {
        return dbColumn.getForeignKey();
    }
}
