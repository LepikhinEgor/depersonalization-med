package com.lepikhina.model;

import com.lepikhina.model.data.*;
import com.lepikhina.model.exceptions.DatabaseConnectException;
import com.lepikhina.model.persitstence.ConnectionPreset;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class DatabaseService {

    private static final String SELECT_PK_QUERY = "SELECT pg_attribute.attname \n" +
            "FROM pg_index, pg_class, pg_attribute, pg_namespace \n" +
            "WHERE \n" +
            "  pg_class.oid = ?::regclass AND \n" +
            "  indrelid = pg_class.oid AND \n" +
            "  nspname = 'public' AND \n" +
            "  pg_class.relnamespace = pg_namespace.oid AND \n" +
            "  pg_attribute.attrelid = pg_class.oid AND \n" +
            "  pg_attribute.attnum = any(pg_index.indkey)\n" +
            " AND indisprimary";
    private final Integer BATCH_SIZE = 100;
    private final List<String> numberTypes = Arrays.asList("bigint", "integer");
    private final List<String> decimalTypes = Collections.singletonList("numeric");
    private final List<String> timeTypes = Arrays.asList("timestamp with time zone", "timestamp without time zone", "date");
    private final List<String> booleanTypes = Collections.singletonList("boolean");
    private final List<String> textTypes = Arrays.asList("character varying", "text");

    public boolean isConnectionCorrect() {
        try (Connection connection = connectDatabase()) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Connection connectDatabase() throws DatabaseConnectException {
        ConnectionPreset connectionPreset = ConnectionsHolder.getInstance().getCurrentPreset();

        try {
            return DriverManager.getConnection(
                    connectionPreset.getUrl() + "/" + connectionPreset.getDatabaseName(),
                    connectionPreset.getUsername(),
                    connectionPreset.getPassword()
            );
        } catch (Exception e) {
            throw new DatabaseConnectException();
        }
    }

    @SneakyThrows
    public Collection<DbTable> getDatabaseSchema(String schemaName) throws DatabaseConnectException {

        String getTablesQuery = "SELECT *" +
                "  FROM information_schema.tables  WHERE table_type='BASE TABLE' " +
                "  AND table_schema = ? ORDER BY table_name";

        Set<DbTable> tables = new HashSet<>();
        try (Connection connection = connectDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(getTablesQuery)) {
            preparedStatement.setString(1, schemaName);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                DbTable table = new DbTable();
                table.setSchemaName(schemaName);
                String tableName = resultSet.getString("table_name");

                Set<String> primaryKeys = getTablePrimaryKeys(tableName, connection);

                table.setName(tableName);
                table.setPkColumnKeys(primaryKeys);
                table.setColumns(getTableColumns(table));

                tables.add(table);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return tables;
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

    private <T> List<TableRow<T>> getColumnValues(String columnName,
                                                  String tableName,
                                                  List<String> idColumns,
                                                  Class<T> type,
                                                  long offset) throws DatabaseConnectException, SQLException {

        String query = "SELECT * FROM " + tableName +
                " ORDER BY " + String.join(",", idColumns) +
                " LIMIT " + BATCH_SIZE + " OFFSET " + offset;
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

    private <T> void updateColumnValues(String columnName,
                                        String tableName,
                                        List<TableRow<T>> values,
                                        List<String> idColumns,
                                        Connection connection) throws DatabaseConnectException, SQLException {
        String query = generateUpdateRowQuery(columnName, tableName, idColumns);
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

    private String generateUpdateRowQuery(String columnName, String tableName, List<String> idColumns) {
        String prefix = "UPDATE " + tableName + " SET " + columnName + " = ? WHERE ";

        if (idColumns.size() == 1)
            return prefix + idColumns.get(0) + "= ?;";
        StringJoiner joiner = new StringJoiner("= ? AND ", prefix, ";");
        idColumns.forEach(joiner::add);

        return joiner.toString();
    }

    private Set<DbColumn> getTableColumns(DbTable table) throws SQLException, DatabaseConnectException {
        String getColumnsQuery = "SELECT * FROM information_schema.columns " +
                "WHERE table_schema = ? AND table_name  = ?";

        Set<DbColumn> columns = new HashSet<>();
        Connection connection = connectDatabase();
        try (PreparedStatement preparedStatement = connection.prepareStatement(getColumnsQuery)) {
            preparedStatement.setString(1, table.getSchemaName());
            preparedStatement.setString(2, table.getName());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String columnName = resultSet.getString("column_name");
                String columnType = resultSet.getString("data_type"); //udt_name

                columns.add(new DbColumn(columnName, table, getTypeFrom(columnType), "", true));
            }
        }

        return columns;
    }


    private Set<String> getTablePrimaryKeys(String tableName, Connection connection) throws SQLException {
        Set<String> pkColumnNames = new HashSet<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PK_QUERY)) {
            preparedStatement.setString(1, tableName);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String columnName = resultSet.getString("attname");
                pkColumnNames.add(columnName);
            }
        }

        return pkColumnNames;
    }

    private DbColumnType getTypeFrom(String columnType) {
        if (textTypes.contains(columnType))
            return DbColumnType.TEXT;
        if (timeTypes.contains(columnType))
            return DbColumnType.DATE;
        if (numberTypes.contains(columnType))
            return DbColumnType.NUMBER;
        if (decimalTypes.contains(columnType))
            return DbColumnType.DECIMAL;
        if (booleanTypes.contains(columnType))
            return DbColumnType.BOOLEAN;

        return DbColumnType.UNKNOWN;
    }
}
