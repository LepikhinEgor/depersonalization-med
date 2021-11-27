package com.lepikhina.model;

import com.lepikhina.model.data.DbColumn;
import com.lepikhina.model.data.DbColumnType;
import com.lepikhina.model.data.DbTable;
import com.lepikhina.model.data.TableRow;
import com.lepikhina.model.exceptions.DatabaseConnectException;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;

@Service
//@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DatabaseService {

    private List<String> numberTypes = Arrays.asList("bigint", "integer");
    private List<String> decimalTypes = Collections.singletonList("numeric");
    private List<String> timeTypes = Arrays.asList("timestamp with time zone", "timestamp without time zone", "date");
    private List<String> booleanTypes = Collections.singletonList("boolean");
    private List<String> textTypes = Arrays.asList("character varying", "text");

    private static final String SELECT_PK_QUERRY = "SELECT pg_attribute.attname \n" +
            "FROM pg_index, pg_class, pg_attribute, pg_namespace \n" +
            "WHERE \n" +
            "  pg_class.oid = ?::regclass AND \n" +
            "  indrelid = pg_class.oid AND \n" +
            "  nspname = 'public' AND \n" +
            "  pg_class.relnamespace = pg_namespace.oid AND \n" +
            "  pg_attribute.attrelid = pg_class.oid AND \n" +
            "  pg_attribute.attnum = any(pg_index.indkey)\n" +
            " AND indisprimary";

    public Connection connectDatabase() throws DatabaseConnectException {
        DbConnectionProperties dbConnectionProperties = ConnectionHolder.getConnectionProperties();

        try {
            return DriverManager.getConnection(
                    dbConnectionProperties.getUrl() + dbConnectionProperties.getDatabaseName(),
                    dbConnectionProperties.getUsername(),
                    dbConnectionProperties.getPassword()
            );
        } catch (Exception e) {
            throw new DatabaseConnectException();
        }
    }

    @SneakyThrows
    public Collection<DbTable> getDatabaseSchema() throws DatabaseConnectException {

        String getTablesQuery = "SELECT *" +
                "  FROM information_schema.tables  WHERE table_type='BASE TABLE' " +
                "  AND table_schema='public' ORDER BY table_name";

        Set<DbTable> tables = new HashSet<>();
        Connection connection = connectDatabase();
        try (PreparedStatement preparedStatement = connection.prepareStatement(getTablesQuery)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String tableName = resultSet.getString("table_name");
                Set<DbColumn> columns = getTableColumns(tableName);

                Set<String> primaryKeys = getTablePrimaryKeys(tableName, connection);

                tables.add(new DbTable(tableName, primaryKeys, columns));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            connection.close();
        }

        return tables;
    }

    public <T> List<TableRow<T>> getColumnValues(String columnName,
                                       String tableName,
                                       List<String> idColumns,
                                       Class<T> type,
                                       int pageNum) throws DatabaseConnectException, SQLException {
        Connection connection = connectDatabase();

        try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + tableName)) {
            ResultSet resultSet = statement.executeQuery();

            List<TableRow<T>> resultList = new ArrayList<>();
            while (resultSet.next()) {
                T value = resultSet.getObject(columnName, type);
                List<Object> idValues = new ArrayList<>();
                for (String idColumn : idColumns) {
                    idValues.add(resultSet.getObject(idColumn));
                }

                resultList.add(new TableRow<>(value, idValues));
            }
            return resultList;
        }
    }

    public <T> void updateColumnValues(String columnName,
                                                 String tableName,
                                   List<TableRow<T>> values,
                                                 List<String> idColumns) throws DatabaseConnectException, SQLException {
        Connection connection = connectDatabase();

        String query = generateUpdateRowQuery(columnName, tableName, idColumns);
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            for (int i = 0; i < idColumns.size(); i++) {
                statement.setObject(i + 1, values.get(i));
            }
            long i = statement.executeUpdate();

            if (i == 0)
                throw new RuntimeException();
        }
    }

    private String generateUpdateRowQuery(String columnName, String tableName, List<String> idColumns) {
        String prefix =  "UPDATE " + tableName + "SET " + columnName + " = ? WHERE ";

        StringJoiner joiner = new StringJoiner( "= ? AND ", prefix, ";");
        idColumns.forEach(joiner::add);

        return joiner.toString();
    }

    private Set<DbColumn> getTableColumns(String tableName) throws SQLException, DatabaseConnectException {
        String getColumnsQuery = "SELECT * FROM information_schema.columns " +
                "WHERE table_schema = 'public' AND table_name  = ?";

        Set<DbColumn> columns = new HashSet<>();
        Connection connection = connectDatabase();
        try (PreparedStatement preparedStatement = connection.prepareStatement(getColumnsQuery)) {
            preparedStatement.setString(1, tableName);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String columnName = resultSet.getString("column_name");
                String columnType = resultSet.getString("data_type"); //udt_name

                columns.add(new DbColumn(columnName, tableName, getTypeFrom(columnType),"", true));
            }
        }

        return columns;
    }


    private Set<String> getTablePrimaryKeys(String tableName, Connection connection) throws SQLException {
        Set<String> pkColumnNames = new HashSet<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PK_QUERRY)) {
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
