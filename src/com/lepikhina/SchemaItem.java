package com.lepikhina;

import javafx.event.EventType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import com.lepikhina.model.data.DbColumn;
import com.lepikhina.model.data.DbTable;
import com.lepikhina.model.events.ColumnSelectedEvent;
import com.lepikhina.model.events.EventBus;
import lombok.SneakyThrows;

public class SchemaItem extends Label {

    private DbColumn dbColumn;

    @SneakyThrows
    public SchemaItem(DbTable table) {
        super(table.getName());
        InputStream tInput = new BufferedInputStream(new FileInputStream("resources/icons/letter-t.png"));
        Image tImage = new Image(tInput);
        setGraphic(new ImageView(tImage));
    }

    @SneakyThrows
    public SchemaItem(DbColumn column) {
        super(column.getName());
        dbColumn = column;
        InputStream tInput = new BufferedInputStream(new FileInputStream("resources/icons/letter-c.png"));
        Image tImage = new Image(tInput);
        setGraphic(new ImageView(tImage));

        setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                EventBus.getInstance().sendEvent(new ColumnSelectedEvent(dbColumn));
            }
        });
    }

    @SneakyThrows
    public SchemaItem(String databaseName) {
        super(databaseName);
        InputStream tInput = new BufferedInputStream(new FileInputStream("resources/icons/letter-d.png"));
        Image tImage = new Image(tInput);
        setGraphic(new ImageView(tImage));
    }
}
