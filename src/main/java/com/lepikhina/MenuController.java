package com.lepikhina;

import com.lepikhina.model.data.*;

import com.lepikhina.model.events.*;
import com.lepikhina.model.events.EventListener;
import com.lepikhina.model.persitstence.ConnectionPreset;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

import com.lepikhina.model.DatabaseService;
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
    public TableColumn<DepersonalizationColumn, String> actionColumn;
    @FXML
    public TableColumn<DepersonalizationColumn, String> resultColumn;

    @FXML
    public Button executeBtn;
    public VBox variablesPanel;

    @FXML
    public TableView<DepersonalizationColumn> fillingTable;
    public TableColumn<DepersonalizationColumn, String> fillRequiredColumn;

    public MenuController() {
        EventBus.getInstance().addListener(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("table"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("actionsBox"));
        resultColumn.setCellValueFactory(new PropertyValueFactory<>("result"));

        actionsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)
                -> EventBus.sendEvent(new ActionChangedEvent(newValue)));
        fillingTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)
                -> EventBus.sendEvent(new ActionChangedEvent(newValue)));

        ContextMenu menu = createTableContextMenu();
        actionsTable.setContextMenu(menu);
        nameColumn.prefWidthProperty().bind(actionsTable.widthProperty().multiply(0.2));
        tableColumn.prefWidthProperty().bind(actionsTable.widthProperty().multiply(0.2));
        typeColumn.prefWidthProperty().bind(actionsTable.widthProperty().multiply(0.2));
        actionColumn.prefWidthProperty().bind(actionsTable.widthProperty().multiply(0.3));
        resultColumn.prefWidthProperty().bind(actionsTable.widthProperty().multiply(0.1));
        nameColumn.setResizable(false);
        tableColumn.setResizable(false);
        typeColumn.setResizable(false);
        actionColumn.setResizable(false);
        resultColumn.setResizable(false);
    }

    private ContextMenu createTableContextMenu() {
        MenuItem removeItemMenu = new MenuItem("Удалить");
        removeItemMenu.setOnAction((ActionEvent event) -> {
            EventBus.sendEvent(new ColumnRemoveEvent(actionsTable.getSelectionModel().selectedItemProperty().get().getDbColumn()));
        });

        ContextMenu menu = new ContextMenu();
        menu.getItems().add(removeItemMenu);
        return menu;
    }

    @FXML
    @SneakyThrows
    private void openConnectionWindow(ActionEvent event) {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(
                Objects.requireNonNull(getClass().getClassLoader().getResource("connection-db-window.fxml")));
        stage.setScene(new Scene(root));
        stage.setTitle("Подключение");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.show();
    }

    @FXML
    public void sendDBDisconnectedEvent(ActionEvent event) {
        EventBus.sendEvent(new DBDisconnectEvent());
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

    @EventListener(ColumnSelectedEvent.class)
    public void onColumnSelected(ColumnSelectedEvent event) {
        if (event.getDbColumn().getType().equals(DbColumnType.UNKNOWN))
            return;

        List<DepersonalizationAction> allActions = ActionsHolder.getInstance().getTypeActions(event.getDbColumn().getType());
        DepersonalizationColumn newRow = new DepersonalizationColumn(event.getDbColumn(), allActions);

        if (!actionsTable.getItems().contains(newRow))
            actionsTable.getItems().addAll(newRow);
    }

    @EventListener(ColumnRemoveEvent.class)
    public void onColumnRemove(ColumnRemoveEvent event) {
        actionsTable.getItems().removeIf(row -> row.equalByColumn(event.getDbColumn()));
    }

    @EventListener(TableSelectedEvent.class)
    public void onTableSelected(TableSelectedEvent event) {

        Set<DbColumn> columns = event.getDbTable().getColumns();

        for (DbColumn column : columns) {
            List<DepersonalizationAction> allActions = ActionsHolder.getInstance().getTypeActions(column.getType());
            DepersonalizationColumn newRow = new DepersonalizationColumn(column, allActions);

            if (!fillingTable.getItems().contains(newRow))
                fillingTable.getItems().addAll(newRow);
        }
    }

    @EventListener(TableRemoveEvent.class)
    public void onTableRemove(TableRemoveEvent event) {
        Set<DbColumn> columns = event.getDbTable().getColumns();
        for (DbColumn column : columns) {
            fillingTable.getItems().removeIf(row -> row.equalByColumn(column));
        }
    }

    @EventListener(DBDisconnectEvent.class)
    public void onDbDisconnected(DBDisconnectEvent event) {
        actionsTable.getItems().clear();
        schemaTree.setRoot(null);
    }


    @SneakyThrows
    @EventListener(ActionChangedEvent.class)
    public void onActionChanged(ActionChangedEvent event) {
        DepersonalizationColumn newAction = event.getNewAction();
        if (newAction != null) {
            DepersonalizationAction selectedAction = newAction.getActionsBox().getValue();

            selectedAction.getVariables()
                    .forEach(variable -> newAction.getVariables().putIfAbsent(variable.getVarName(), variable.getDefaultValue()));

            URL resource = Objects.requireNonNull(getClass().getClassLoader().getResource("variable-panel.fxml"));

            variablesPanel.getChildren().clear();
            for (ScriptVariable variable : selectedAction.getVariables()) {
                FXMLLoader loader = new FXMLLoader(resource);
                loader.load();
                variablesPanel.getChildren().add(loader.getRoot());
                VariablePanelController controller = loader.getController();
                controller.init(newAction, variable);
            }
        } else {
            variablesPanel.getChildren().clear();
        }
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

    @FXML
    @SneakyThrows
    public void executeDepersonalize(ActionEvent event) {
        ObservableList<DepersonalizationColumn> rows = actionsTable.getItems();
        DatabaseService databaseService = new DatabaseService();

        for (DepersonalizationColumn row : rows) {
            Class<?> columnType = getTypeFrom(row.getColumnType());
            DepersonalizationAction action = row.getActionsBox().getValue();
            ScriptAnonymizer scriptAnonymizer = new ScriptAnonymizer(action.getScriptPath(), row.getVariables());

            List<String> pkColumnKeys = new ArrayList<>(row.getDbColumn().getTable().getPkColumnKeys());
            databaseService.depersonalizeColumn(row, pkColumnKeys, columnType, scriptAnonymizer);
        }

        actionsTable.refresh();
    }

    @FXML
    @SneakyThrows
    public void executeFilling(ActionEvent event) {
        ObservableList<DepersonalizationColumn> rows = actionsTable.getItems();
        DatabaseService databaseService = new DatabaseService();

        Map<DbTable, List<DepersonalizationColumn>> tableColumns = rows.stream()
                .collect(Collectors.groupingBy(column -> column.getDbColumn().getTable()));

        for (Map.Entry<DbTable, List<DepersonalizationColumn>> tableEntry : tableColumns.entrySet()) {
            DbTable table = tableEntry.getKey();
            List<DepersonalizationColumn> columns = tableEntry.getValue();
            List<String> pkColumnKeys = new ArrayList<>(table.getPkColumnKeys());

            Map<DepersonalizationColumn, ScriptAnonymizer> columnsScripts = columns.stream()
                    .collect(Collectors.toMap(column -> column,
                            column -> new ScriptAnonymizer(column.getActionsBox().getValue().getScriptPath(), column.getVariables())));

            databaseService.fillTable(columnsScripts, pkColumnKeys);
        }

        actionsTable.refresh();
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
