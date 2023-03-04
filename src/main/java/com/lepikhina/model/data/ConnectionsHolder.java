package com.lepikhina.model.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lepikhina.model.persitstence.ConnectionPreset;
import lombok.SneakyThrows;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class ConnectionsHolder {

    private static ConnectionsHolder instance;
    private final List<ConnectionPreset> presets;

    private ConnectionPreset currentPreset;

    @SneakyThrows
    private ConnectionsHolder() {
        ObjectMapper objectMapper = new ObjectMapper();
        File configFile = new File("common/UserConfig.json");
        UserConfig userConfig = objectMapper.readValue(configFile, UserConfig.class);

        presets = Optional.ofNullable(userConfig.connectionPresets).orElseGet(ArrayList::new);
    }

    public ConnectionPreset getCurrentPreset() {
        return currentPreset;
    }

    public List<ConnectionPreset> getAllPresets() {
        return presets;
    }

    @SneakyThrows
    public void setCurrentPreset(ConnectionPreset preset) {
        this.currentPreset = preset;

        HashSet<ConnectionPreset> presetsSet = new HashSet<>(presets);
        if (!presetsSet.contains(preset)) {
            presets.add(preset);
            UserConfig userConfig = new UserConfig(presets);
            File configFile = new File("common/UserConfig.json");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(configFile, userConfig);
        }
    }

    public static ConnectionsHolder getInstance() {
        if (instance == null)
            instance = new ConnectionsHolder();

        return instance;
    }
}
