package com.lepikhina;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
public class ScriptsHolder {

    static String RANDOM_UUID = "src/com/lepikhina/model/scripts/randomUuid.groovy";
}
