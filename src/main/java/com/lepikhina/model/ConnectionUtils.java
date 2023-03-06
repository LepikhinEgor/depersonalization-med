package com.lepikhina.model;

import com.lepikhina.model.data.ConnectionsHolder;
import com.lepikhina.model.exceptions.DatabaseConnectException;
import com.lepikhina.model.persitstence.ConnectionPreset;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionUtils {

    public static Connection connectDatabase() throws DatabaseConnectException {
        ConnectionPreset connectionPreset = ConnectionsHolder.getInstance().getCurrentPreset();

        try {
            return DriverManager.getConnection(
                    connectionPreset.getUrl() + "/" + connectionPreset.getDatabaseName(),
                    connectionPreset.getUsername(),
                    connectionPreset.getPassword()
            );
        } catch (Exception e) {
            throw new DatabaseConnectException();
        }
    }
}
