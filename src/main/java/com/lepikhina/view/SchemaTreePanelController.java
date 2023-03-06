package com.lepikhina.view;

import com.lepikhina.model.SchemaItem;
import com.lepikhina.model.DatabaseService;
import com.lepikhina.model.data.ConnectionsHolder;
import com.lepikhina.model.data.DbColumn;
import com.lepikhina.model.data.DbTable;
import com.lepikhina.model.events.*;
import com.lepikhina.model.persitstence.ConnectionPreset;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

public class SchemaTreePanelController {


    @FXML
    public TreeView<SchemaItem> schemaTree;

    @FXML
    public VBox variablesPanel;

    public SchemaTreePanelController() {
        EventBus.getInstance().addListener(this);
    }

    @SneakyThrows
    @EventListener(DbConnectEvent.class)
    public void onDbConnect(DbConnectEvent event) {
        DatabaseService service = new DatabaseService();

        ConnectionPreset connectionProperties = ConnectionsHolder.getInstance().getCurrentPreset();
        Collection<DbTable> databaseSchema = service.getDatabaseSchema(connectionProperties.getSchemaName());

        TreeItem<SchemaItem> tableNode = new TreeItem<>(new SchemaItem(connectionProperties.getDatabaseName()));
        tableNode.getChildren().addAll(databaseSchema.stream()
                .sorted(Comparator.comparing(DbTable::getName))
                .map(this::getTableAsNode)
                .collect(Collectors.toList()));
        tableNode.setExpanded(true);
        schemaTree.setRoot(tableNode);
    }

    @EventListener(DBDisconnectEvent.class)
    public void onDbDisconnected(DBDisconnectEvent event) {
        schemaTree.setRoot(null);
    }

    private TreeItem<SchemaItem> getTableAsNode(DbTable table) {
        TreeItem<SchemaItem> tableNode = new TreeItem<>(new SchemaItem(table));
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addAction = new MenuItem("Добавить");
        MenuItem removeAction = new MenuItem("Удалить");
        addAction.setOnAction(event -> EventBus.sendEvent(new TableSelectedEvent(table)));
        removeAction.setOnAction(event -> EventBus.sendEvent(new TableRemoveEvent(table)));

        contextMenu.getItems().addAll(addAction, removeAction);

        tableNode.getValue().setContextMenu(contextMenu);

        tableNode.getChildren().addAll(
                table.getColumns().stream()
                        .sorted(Comparator.comparing(DbColumn::getName))
                        .map(this::getColumnAsNode)
                        .collect(Collectors.toList())
        );

        return tableNode;
    }

    private TreeItem<SchemaItem> getColumnAsNode(DbColumn column) {
        SchemaItem schemaItem = new SchemaItem(column);
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addAction = new MenuItem("Добавить");
        MenuItem removeAction = new MenuItem("Удалить");
        contextMenu.getItems().addAll(addAction, removeAction);
        addAction.setOnAction(event -> EventBus.sendEvent(new ColumnSelectedEvent(column)));
        removeAction.setOnAction(event -> EventBus.sendEvent(new ColumnRemoveEvent(column)));
        schemaItem.setContextMenu(contextMenu);

        return new TreeItem<>(schemaItem);
    }
}
