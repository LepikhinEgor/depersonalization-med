package com.lepikhina.model.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActionsHolder {

    static ActionsHolder instance;

    List<DepersonalizationAction> actions;

    @SneakyThrows
    private ActionsHolder() {
        actions = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<List<DepersonalizationAction>> jsonType = new TypeReference<List<DepersonalizationAction>>() {
        };
        File configFile = new File("resources/actionsConfig.json");
        List<DepersonalizationAction> commonActions = objectMapper.readValue(configFile, jsonType);
        actions.addAll(commonActions);
    }

    public static ActionsHolder getInstance() {
        if (instance == null)
           instance = new ActionsHolder();

        return instance;
    }

    public List<DepersonalizationAction> getAllActions() {
        return actions;
    }


}
