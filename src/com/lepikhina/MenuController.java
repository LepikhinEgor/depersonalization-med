package com.lepikhina;

import com.lepikhina.model.data.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

import com.lepikhina.model.ConnectionHolder;
import com.lepikhina.model.DatabaseService;
import com.lepikhina.model.events.ColumnSelectedEvent;
import com.lepikhina.model.events.DbConnectEvent;
import com.lepikhina.model.events.EventBus;
import com.lepikhina.model.events.EventListener;
import lombok.SneakyThrows;

public class MenuController implements Initializable {

    @FXML
    public TreeView<SchemaItem> schemaTree;

    @FXML
    public TableView<DepersonalizationColumn> actionsTable;

    @FXML
    public TableColumn<DepersonalizationColumn, String> nameColumn;
    @FXML
    public TableColumn<DepersonalizationColumn, String> tableColumn;
    @FXML
    public TableColumn<DepersonalizationColumn, String> typeColumn;
    @FXML
    public TableColumn<DepersonalizationColumn, String> foreignKeyColumn;
    @FXML
    public TableColumn<DepersonalizationColumn, String> actionColumn;

    @FXML
    public Button executeBtn;

    public MenuController() {
        EventBus.getInstance().addListener(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("table"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        foreignKeyColumn.setCellValueFactory(new PropertyValueFactory<>("foreignKey"));
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("actionsBox"));
    }

    @FXML
    @SneakyThrows
    private void openConnectionWindow(ActionEvent event) {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(
                MenuController.class.getResource("connection-db-window.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("My modal window");
        stage.initModality(Modality.WINDOW_MODAL);
//        stage.initOwner(((MenuItem)event.getSource()).get().getWindow() );
        stage.show();
    }

    @SneakyThrows
    @EventListener(DbConnectEvent.class)
    public void onDbConnect(DbConnectEvent event) {
        DatabaseService service = new DatabaseService();

        Collection<DbTable> databaseSchema = service.getDatabaseSchema();

        TreeItem<SchemaItem> tableNode = new TreeItem<>(new SchemaItem(ConnectionHolder.getConnectionProperties().getDatabaseName()));
        tableNode.getChildren().addAll(databaseSchema.stream()
                .map(this::tableAsNode)
                .collect(Collectors.toList()));
        schemaTree.setRoot(tableNode);
    }

    @SneakyThrows
    @EventListener(ColumnSelectedEvent.class)
    public void onColumnSelected(ColumnSelectedEvent event) {
        List<DepersonalizationAction> allActions = ActionsHolder.getInstance().getAllActions();
        DepersonalizationColumn newRow = new DepersonalizationColumn(event.getDbColumn(), allActions);
        if (!actionsTable.getItems().contains(newRow))
            actionsTable.getItems().addAll(newRow);
    }

    private TreeItem<SchemaItem> tableAsNode(DbTable table) {
        TreeItem<SchemaItem> tableNode = new TreeItem<>(new SchemaItem(table));
        tableNode.getChildren().addAll(
                table.getColumns().stream()
                        .map(column -> new TreeItem<>(new SchemaItem(column)))
                        .collect(Collectors.toList())
        );

        return tableNode;
    }

    @FXML
    @SneakyThrows
    public void executeDepersonalize(ActionEvent event) {
        ObservableList<DepersonalizationColumn> rows = actionsTable.getItems();
        DatabaseService databaseService = new DatabaseService();

        for (DepersonalizationColumn row : rows) {
            Class<?> columnType = getTypeFrom(row.getColumnType());
            DepersonalizationAction action = row.getActionsBox().getSelected();
            ScriptAnonymizer scriptAnonymizer = new ScriptAnonymizer(action.getScriptPath());

            List<String> pkColumnKeys = new ArrayList<>(row.getDbColumn().getTable().getPkColumnKeys());
            databaseService.depersonalizeColumn(row.getName(), row.getTable(), pkColumnKeys, columnType, scriptAnonymizer);
        }
    }

    private Class<?> getTypeFrom(DbColumnType columnType) {
        if (columnType.equals(DbColumnType.BOOLEAN))
            return Boolean.class;
        if (columnType.equals(DbColumnType.TEXT))
            return String.class;
        if (columnType.equals(DbColumnType.DATE))
            return Date.class;
        if (columnType.equals(DbColumnType.NUMBER))
            return Long.class;
        if (columnType.equals(DbColumnType.DECIMAL))
            return Double.class;

        return Object.class;
    }
}
