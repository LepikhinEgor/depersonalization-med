package com.lepikhina.view;

import com.lepikhina.model.DatabaseService;
import com.lepikhina.model.data.*;
import com.lepikhina.view.events.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.SneakyThrows;

import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AnonymizePanelController implements Initializable {

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

    public AnonymizePanelController() {
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

    @EventListener(ColumnSelectedEvent.class)
    public void onColumnSelected(ColumnSelectedEvent event) {
        if (event.getDbColumn().getType().equals(DbColumnType.UNKNOWN) && !event.getDbColumn().isNullable())
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

    @EventListener(DBDisconnectEvent.class)
    public void onDbDisconnected(DBDisconnectEvent event) {
        actionsTable.getItems().clear();
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

    private ContextMenu createTableContextMenu() {
        MenuItem removeItemMenu = new MenuItem("Удалить");
        removeItemMenu.setOnAction((ActionEvent event) -> {
            EventBus.sendEvent(new ColumnRemoveEvent(actionsTable.getSelectionModel().selectedItemProperty().get().getDbColumn()));
        });

        ContextMenu menu = new ContextMenu();
        menu.getItems().add(removeItemMenu);
        return menu;
    }

}
