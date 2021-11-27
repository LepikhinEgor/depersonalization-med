package com.lepikhina.model.data;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class DbTable {

    String name;

    Set<String> pkColumnKeys;

    Set<DbColumn> columns;
}
