package com.lepikhina.model.data;

import javafx.scene.control.ComboBox;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DepersonalizationColumn {

    String name;

    String table;

    String type;

    DbColumnType columnType;

    String foreignKey;

    ComboBox<DepersonalizationAction> actionsBox;

    public DepersonalizationColumn(DbColumn dbColumn, List<DepersonalizationAction> actionList) {
        this.name = dbColumn.getName();
        this.table = dbColumn.getTableName();
        this.type = dbColumn.getType().getType();
        this.foreignKey = dbColumn.getForeignKey();
        this.columnType = dbColumn.getType();

        actionsBox = new ComboBox<>();
        actionsBox.getItems().addAll(actionList);
        actionsBox.setValue(actionList.get(0));
    }

}
