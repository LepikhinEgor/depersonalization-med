package com.lepikhina.model;

import com.lepikhina.model.data.DbColumn;
import com.lepikhina.model.data.DbColumnType;
import com.lepikhina.model.data.DbTable;
import com.lepikhina.model.data.NewTableRow;
import com.lepikhina.model.exceptions.DatabaseConnectException;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import static com.lepikhina.model.ConnectionUtils.connectDatabase;

public class PostgreSqlQueryGenerator implements SqlQueryGenerator {

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

    private final List<String> numberTypes = Arrays.asList("bigint", "integer");
    private final List<String> decimalTypes = Collections.singletonList("numeric");
    private final List<String> timeTypes = Arrays.asList("timestamp with time zone", "timestamp without time zone", "date");
    private final List<String> booleanTypes = Collections.singletonList("boolean");
    private final List<String> textTypes = Arrays.asList("character varying", "text");

    @Override
    @SneakyThrows
    public Set<DbColumn> getTableColumns(DbTable table)  {
        String getColumnsQuery = "SELECT * FROM information_schema.columns " +
                "WHERE table_schema = ? AND table_name  = ?";

        Set<DbColumn> columns = new HashSet<>();
        try (Connection connection = ConnectionUtils.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(getColumnsQuery)) {
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


    @Override
    @SneakyThrows
    public Set<String> getTablePrimaryKeys(String tableName, Connection connection) {
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

    @Override
    public DbColumnType getTypeFrom(String columnType) {
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

    @Override
    @SneakyThrows
    public Collection<DbTable> getDatabaseSchema(String schemaName)  {

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
}
