package com.lepikhina.model.data;

import javafx.scene.control.ComboBox;

import java.util.List;
import java.util.Objects;

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

    public String getForeignKey() {
        return dbColumn.getForeignKey();
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
