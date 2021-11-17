package com.lepikhina.model;

import org.springframework.stereotype.Component;

@Component
public class PostgresQueryFactoryImpl extends SqlQueryFactory {

    @Override
    String getTablesListQuery(String dbName) {
        return "SELECT * " +
                "  FROM information_schema.tables  WHERE table_type='BASE TABLE' \n" +
                "  AND table_schema='public' order by table_name";
    }

    @Override
    String getColumnsListQuery(String tableName) {
        return null;
    }
}
