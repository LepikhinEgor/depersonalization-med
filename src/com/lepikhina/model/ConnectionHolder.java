package com.lepikhina.model;

public class ConnectionHolder {

    private static DbConnectionProperties dbConnectionProperties;

    public static void setConnectionProperties(DbConnectionProperties properties) {
        dbConnectionProperties = properties;
    }

    public static DbConnectionProperties getConnectionProperties() {
        return dbConnectionProperties;
    }

}
