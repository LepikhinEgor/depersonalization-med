package com.lepikhina.model.data;

import lombok.Data;

@Data
public class ScriptVariable {

    String name;

    String varName;

    VariableType type;

    Object defaultValue;
}
