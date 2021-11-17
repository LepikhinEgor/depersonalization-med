package com.lepikhina.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.lepikhina.model.data.DbColumn;
import com.lepikhina.model.data.DbColumnType;
import com.lepikhina.model.data.DbTable;
import com.lepikhina.model.exceptions.DatabaseConnectException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
//@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DatabaseService {

    private List<String> numberTypes = Arrays.asList("bigint", "integer");
    private List<String> timeTypes = Arrays.asList("timestamp without time zone");
    private List<String> booleanTypes = Arrays.asList("boolean");
    private List<String> textTypes = Arrays.asList("character varying");

    public Connection connectDatabase() throws DatabaseConnectException {
        DbConnectionProperties dbConnectionProperties = ConnectionHolder.getConnectionProperties();
        dbConnectionProperties = new DbConnectionProperties();
        dbConnectionProperties.setProperties("jdbc:postgresql://localhost:5432/", "dsa",
                "postgres", "postgres");
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

                tables.add(new DbTable(tableName, columns));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            connection.close();
        }

        return tables;
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

    private DbColumnType getTypeFrom(String columnType) {
        if (textTypes.contains(columnType))
            return DbColumnType.TEXT;
        if (timeTypes.contains(columnType))
            return DbColumnType.DATE;
        if (numberTypes.contains(columnType))
            return DbColumnType.NUMBER;

        return DbColumnType.UNKNOWN;
    }
}
