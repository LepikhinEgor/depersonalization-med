package com.lepikhina.model.data;

import javafx.scene.control.ComboBox;

import java.util.Objects;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
//@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DepersonalizationColumn {

    DbColumn dbColumn;

    public String getName() {
        return dbColumn.getName();
    }

    public String getTable() {
        return dbColumn.getTableName();
    }

    public String getType() {
        return dbColumn.getType().getType();
    }

    public String getForeignKey() {
        return dbColumn.getForeignKey();
    }

    public ComboBox<DepersonalizationAction> getActionName() {
        ComboBox<DepersonalizationAction> actionsList = new ComboBox<>();
        DepersonalizationAction<String> randomString = new DepersonalizationAction<String>() {
            @Override
            public String depersonalize(String inputData) {
                return UUID.randomUUID().toString();
            }

            @Override
            public String getName() {
                return "Случайный UUID";
            }
        };
        actionsList.getItems().add(randomString);

        return actionsList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DepersonalizationColumn that = (DepersonalizationColumn) o;
        return Objects.equals(dbColumn.getName(), that.dbColumn.getName()) &&
                Objects.equals(dbColumn.getTableName(), that.dbColumn.getTableName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(dbColumn.getName(), dbColumn.getTableName());
    }
}
