package com.lepikhina;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.lepikhina.model.ConnectionHolder;
import com.lepikhina.model.DatabaseService;
import com.lepikhina.model.data.DbTable;
import com.lepikhina.model.data.DepersonalizationColumn;
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

    public MenuController() {
        EventBus.getInstance().addListener(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("table"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        foreignKeyColumn.setCellValueFactory(new PropertyValueFactory<>("foreignKey"));
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("actionName"));
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
        DepersonalizationColumn newRow = new DepersonalizationColumn(event.getDbColumn());
        actionsTable.getItems().addAll(newRow);
    }

    private TreeItem<SchemaItem> tableAsNode(DbTable table) {
        TreeItem<SchemaItem> tableNode = new TreeItem<>(new SchemaItem(table));
        tableNode.getChildren().addAll(
                table.getColumns().stream()
                        .map(column -> new TreeItem<>(new SchemaItem(column)))
                        .collect(Collectors.toList())
        );

//        TableColumn emailCol = new TableColumn("Email");
//        emailCol.setMinWidth(200);
//        emailCol.setCellValueFactory(
//                new PropertyValueFactory<Person,String>("email")
//        );
        return tableNode;
    }
}
