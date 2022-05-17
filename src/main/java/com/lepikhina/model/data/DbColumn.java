package com.lepikhina.model.data;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class DbColumn {

    String name;

    DbTable table;

    DbColumnType type;

    String foreignKey;

    boolean isNullable;


}
