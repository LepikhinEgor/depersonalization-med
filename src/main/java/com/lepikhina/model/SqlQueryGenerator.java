package com.lepikhina.model;

import com.lepikhina.model.data.DbColumn;
import com.lepikhina.model.data.DbColumnType;
import com.lepikhina.model.data.DbTable;
import com.lepikhina.model.data.NewTableRow;

import java.sql.Connection;
import java.util.*;

public interface SqlQueryGenerator {

    Set<DbColumn> getTableColumns(DbTable table);

    Set<String> getTablePrimaryKeys(String tableName, Connection connection);

    DbColumnType getTypeFrom(String columnType);

    Collection<DbTable> getDatabaseSchema(String schemaName);

    default String generateInsertRowQuery(NewTableRow newTableRow, String tableName) {
        StringJoiner fieldsJoiner = new StringJoiner(",", "(", ")");
        StringJoiner valuesJoiner = new StringJoiner(",", "(", ")");

        ArrayList<String> fields = new ArrayList<>(newTableRow.getColumns().keySet());
        fields.sort(String::compareTo);
        for (String field : fields) {
            fieldsJoiner.add(field);
            valuesJoiner.add("?");
        }


        return "INSERT INTO " + tableName + " " + fieldsJoiner.toString() + " VALUES " + valuesJoiner.toString() + ";";
    }

    default String generateUpdateRowQuery(String columnName, String tableName, List<String> idColumns) {
        String prefix = "UPDATE " + tableName + " SET " + columnName + " = ? WHERE ";

        if (idColumns.size() == 1)
            return prefix + idColumns.get(0) + "= ?;";
        StringJoiner joiner = new StringJoiner("= ? AND ", prefix, ";");
        idColumns.forEach(joiner::add);

        return joiner.toString();
    }

    default String generateSelectByIdsQuery(String tableName, List<String> idColumns, long offset, int batchSize) {
        return "SELECT * FROM " + tableName + " ORDER BY " + String.join(",", idColumns) +
                " LIMIT " + batchSize + " OFFSET " + offset;
    }
}
