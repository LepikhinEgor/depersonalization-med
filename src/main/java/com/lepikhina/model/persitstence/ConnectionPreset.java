package com.lepikhina.model.persitstence;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConnectionPreset {

    String name;

    String url;

    String username;

    String password;

    String databaseName;

    String schemaName;


    @Override
    public String toString() {
        return name;
    }
}
