package com.lepikhina.model;

import com.lepikhina.model.persitstence.DatabaseProperties;

public class ConnectionHolder {

    private static DatabaseProperties databaseProperties;

    public static void setConnectionProperties(DatabaseProperties properties) {
        databaseProperties = properties;
    }

    public static DatabaseProperties getConnectionProperties() {
        return databaseProperties;
    }

}
