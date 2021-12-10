package com.lepikhina.model.persitstence;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSave {

    List<DatabaseProperties> databaseProperties;

    List<CustomScript> scripts;

    public UserSave() {
        this.databaseProperties = new ArrayList<>();
        this.scripts =  new ArrayList<>();
    }
}
