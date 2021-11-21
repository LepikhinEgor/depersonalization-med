package com.lepikhina.model.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DepersonalizationAction {

    String name;

    String scriptPath;

    DbColumnType suitableType;

    @Override
    public String toString() {
        return name;
    }
}
