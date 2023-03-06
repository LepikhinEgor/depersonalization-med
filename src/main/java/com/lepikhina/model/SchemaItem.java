package com.lepikhina.model;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import com.lepikhina.model.data.DbColumn;
import com.lepikhina.model.data.DbTable;
import com.lepikhina.view.events.ColumnSelectedEvent;
import com.lepikhina.view.events.EventBus;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

import static com.lepikhina.model.data.DbColumnType.UNKNOWN;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class SchemaItem extends Label {

    static final String DATABASE_ICON_PATH = "common/icons/letter-d.png";
    static final String TABLE_ICON_PATH = "common/icons/letter-t.png";
    static final String COLUMN_ICON_PATH = "common/icons/letter-c.png";
    static final String UNKNOWN_TYPE_COLUMN_ICON_PATH = "common/icons/letter-c-gray.png";

    public SchemaItem(DbTable table) {
        super(table.getName());

        setIcon(TABLE_ICON_PATH);
    }

    public SchemaItem(DbColumn column) {
        super(column.getName());

        String iconPath = column.getType().equals(UNKNOWN) ? UNKNOWN_TYPE_COLUMN_ICON_PATH : COLUMN_ICON_PATH;
        setIcon(iconPath);
    }

    public SchemaItem(String databaseName) {
        super(databaseName);

        setIcon(DATABASE_ICON_PATH);
    }

    @SneakyThrows
    private void setIcon(String iconPath) {
        InputStream tInput = new BufferedInputStream(new FileInputStream(iconPath));
        Image tImage = new Image(tInput);

        setGraphic(new ImageView(tImage));
    }
}
