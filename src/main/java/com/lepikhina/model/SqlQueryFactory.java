package com.lepikhina.model;

public abstract class SqlQueryFactory {

    abstract String getTablesListQuery(String dbName);

    abstract String getColumnsListQuery(String tableName);

}
