package com.lepikhina.model.data;

import lombok.Getter;

@Getter
public enum DbColumnType {
    UNKNOWN("Неизвестный"),
    TEXT("Текст"),
    DATE("Дата"),
    NUMBER("Целое число"),
    DECIMAL("Дробное число"),
    BOOLEAN("Булево значение");

    String type;

    DbColumnType(String type) {
        this.type = type;
    }
}
