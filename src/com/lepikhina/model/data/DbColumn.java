package com.lepikhina.model.data;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class DbColumn {

    String name;

    String tableName;

    DbColumnType type;

    String foreignKey;

    boolean isNullable;


}
