package com.lepikhina.model.data;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

@Getter
@Setter
public class DbTable {

    String name;

    String schemaName;

    Set<String> pkColumnKeys;

    Set<DbColumn> columns;
}
