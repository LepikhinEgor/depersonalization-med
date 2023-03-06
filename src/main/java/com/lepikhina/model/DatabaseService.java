package com.lepikhina.model;

import com.lepikhina.model.data.*;
import com.lepikhina.model.exceptions.DatabaseConnectException;
import com.lepikhina.model.persitstence.ConnectionPreset;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.lepikhina.model.ConnectionUtils.connectDatabase;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DatabaseService {

    private final Integer BATCH_SIZE = 100;

    SqlQueryGenerator sqlQueryGenerator = new PostgreSqlQueryGenerator();

    public boolean isConnectionCorrect() {
        try (Connection connection = connectDatabase()) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Collection<DbTable> getDatabaseSchema(String schemaName)  {
        return sqlQueryGenerator.getDatabaseSchema(schemaName);
    }

    public <T> void fillTable(String tableName,
                              Map<DepersonalizationColumn, ScriptAnonymizer> columnsScripts,
                              Integer count) throws DatabaseConnectException, SQLException {


        int offset = 0;
        Connection connection = connectDatabase();
        connection.setAutoCommit(false);

        try {
            while (true) {
                Map<DepersonalizationColumn, List<TableRow<T>>> columnItemList = columnsScripts.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, t -> t.getValue().generate(BATCH_SIZE)));

                List<NewTableRow> newTableRows = new ArrayList<>();
                int newRowsCount = count - offset > BATCH_SIZE ? BATCH_SIZE : count - offset;
                for (int i = 0; i < newRowsCount; i++) {
                    NewTableRow newTableRow = new NewTableRow(new HashMap<>());
                    for (Map.Entry<DepersonalizationColumn, List<TableRow<T>>> columnItems : columnItemList.entrySet()) {
                        newTableRow.getColumns().put(columnItems.getKey().getName(), columnItems.getValue().get(i).getValue());
                    }

                    newTableRows.add(newTableRow);
                }

                insertRows(tableName, newTableRows, connection);

                offset += newTableRows.size();
                if (offset >= count) {
                    connection.commit();
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            connection.rollback();
        }


        connection.close();

    }

    public <T> void depersonalizeColumn(DepersonalizationColumn column,
                                        List<String> idColumns,
                                        Class<T> type, Anonymizer anonymizer) throws DatabaseConnectException, SQLException {
        int offset = 0;
        Connection connection = connectDatabase();
        connection.setAutoCommit(false);
        try {
            while (true) {

                List<TableRow<T>> oldValues = getColumnValues(column.getName(), column.getTable(), idColumns, type, offset);

                List<TableRow<T>> newValues = anonymizer.anonymize(oldValues);

                updateColumnValues(column.getName(), column.getTable(), newValues, idColumns, connection);

                offset += BATCH_SIZE;
                if (oldValues.size() < BATCH_SIZE) {
                    connection.commit();
                    column.setResult("✔");
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            connection.rollback();
            column.setResult("✖");
        }

        connection.close();
    }

    @SuppressWarnings("unchecked")
    private <T> List<TableRow<T>> getColumnValues(String columnName,
                                                  String tableName,
                                                  List<String> idColumns,
                                                  Class<T> type,
                                                  long offset) throws DatabaseConnectException, SQLException {

        String query = sqlQueryGenerator.generateSelectByIdsQuery(tableName, idColumns, offset, BATCH_SIZE);

        try (Connection connection = connectDatabase();
             PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();

            List<TableRow<T>> resultList = new ArrayList<>();
            while (resultSet.next()) {
                T value;
                if (type.equals(Double.class))
                    value = (T) (Double) (resultSet.getObject(columnName, BigDecimal.class).doubleValue());
                else
                    value = resultSet.getObject(columnName, type);
                List<Object> idValues = new ArrayList<>();
                for (String idColumn : idColumns) {
                    idValues.add(resultSet.getObject(idColumn));
                }

                resultList.add(new TableRow<>(value, idValues));
            }
            return resultList;
        }
    }

    @SneakyThrows
    private <T> void updateColumnValues(String columnName,
                                        String tableName,
                                        List<TableRow<T>> values,
                                        List<String> idColumns,
                                        Connection connection) {
        String query = sqlQueryGenerator.generateUpdateRowQuery(columnName, tableName, idColumns);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (TableRow<T> value : values) {
                statement.setObject(1, value.getValue());
                for (int i = 0; i < idColumns.size(); i++) {
                    statement.setObject(i + 2, value.getIds().get(i));
                }

                statement.addBatch();
            }

            int[] results = statement.executeBatch();

            for (int i = 0; i < results.length; i++) {
                if (results[i] == 0) {
                    String ids = values.get(i).getIds().stream()
                            .map(Object::toString)
                            .collect(Collectors.joining(","));
                    System.out.println("Row with id=" + ids + " not found");
                }
            }
        }
    }

    @SneakyThrows
    private <T> void insertRows(String tableName,
                                List<NewTableRow> newTableRows,
                                Connection connection) {
        String query = sqlQueryGenerator.generateInsertRowQuery(newTableRows.get(0), tableName);
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            for (NewTableRow row : newTableRows) {
                ArrayList<Map.Entry<String, Object>> fieldValues = new ArrayList<>(row.getColumns().entrySet());
                fieldValues.sort(Map.Entry.comparingByKey());

                for (int i = 1; i < fieldValues.size() + 1; i++) {
                    statement.setObject(i, fieldValues.get(i-1).getValue());
                }

                statement.addBatch();
            }

            int[] results = statement.executeBatch();

            System.out.println("Updated " + results.length + " rows");
        }
    }
}
