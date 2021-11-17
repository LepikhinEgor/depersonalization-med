package com.lepikhina.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DbConnectionProperties {

    String url;

    String username;

    String password;

    String databaseName;

    public void setProperties(String url, String databaseName, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.databaseName = databaseName;
    }

}
