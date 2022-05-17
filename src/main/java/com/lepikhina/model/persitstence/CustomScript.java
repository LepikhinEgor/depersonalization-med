package com.lepikhina.model.persitstence;

import com.lepikhina.model.data.DbColumnType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomScript {

    String name;

    String content;

    DbColumnType targetType;

}
