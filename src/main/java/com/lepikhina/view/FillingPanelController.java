package com.lepikhina.view;

import com.lepikhina.model.DatabaseService;
import com.lepikhina.model.data.*;
import com.lepikhina.model.events.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.SneakyThrows;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class FillingPanelController implements Initializable {

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
    public TableColumn<DepersonalizationColumn, String> fillRequiredColumn;

    @FXML
    public TableView<DepersonalizationColumn> fillingTable;

    @FXML
    public TextField newRowsCountInput;

    public FillingPanelController() {
        EventBus.getInstance().addListener(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("table"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("actionsBox"));

        fillingTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)
                -> EventBus.sendEvent(new ActionChangedEvent(newValue)));

    }

    @FXML
    @SneakyThrows
    public void executeFilling(ActionEvent event) {
        ObservableList<DepersonalizationColumn> rows = fillingTable.getItems();
        DatabaseService databaseService = new DatabaseService();

        Map<DbTable, List<DepersonalizationColumn>> tableColumns = rows.stream()
                .collect(Collectors.groupingBy(column -> column.getDbColumn().getTable()));

        for (Map.Entry<DbTable, List<DepersonalizationColumn>> tableEntry : tableColumns.entrySet()) {
            DbTable table = tableEntry.getKey();
            List<DepersonalizationColumn> columns = tableEntry.getValue();

            Map<DepersonalizationColumn, ScriptAnonymizer> columnsScripts = columns.stream()
                    .collect(Collectors.toMap(column -> column,
                            column -> new ScriptAnonymizer(column.getActionsBox().getValue().getScriptPath(), column.getVariables())));

            Integer newRowsCount = Integer.valueOf(newRowsCountInput.getText());
            databaseService.fillTable(table.getName(), columnsScripts, newRowsCount);
        }
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
}
