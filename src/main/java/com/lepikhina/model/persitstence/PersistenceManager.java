package com.lepikhina.model.persitstence;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class PersistenceManager {

    private static final String FOLDER_NAME = "depersonalization";
    private static final String FILE_NAME = "preferences.json";

    public static void saveDatabaseProperties(DatabaseProperties databaseProperties) {
        UserSave userPreferences = getUserPreferences();

        if (!userPreferences.getDatabaseProperties().contains(databaseProperties)) {
            userPreferences.getDatabaseProperties().add(databaseProperties);
        }

        rewritePreferencesFile(userPreferences);
    }

    @SneakyThrows
    private static void rewritePreferencesFile(UserSave userSave) {
        File preferencesFile = getPreferencesFile(true);

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.writeValue(preferencesFile, userSave);
    }

    @SneakyThrows
    private static UserSave getUserPreferences() {
        File preferencesFile = getPreferencesFile(false);

        if (!preferencesFile.exists())
            return new UserSave();

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(preferencesFile, UserSave.class);
    }

    private static File getPreferencesFile(boolean createIfNotExist) throws IOException {
        String userHome = System.getProperty("user.home");

        File preferencesFolder = new File(userHome + File.separator + FOLDER_NAME);

        if (!preferencesFolder.exists()) {
            boolean created = preferencesFolder.mkdir();
            if (!created)
                System.out.println("Не удалось создать папку с настройками пользователя");
        }

        File preferencesFile = new File(preferencesFolder, FILE_NAME);
        if (!preferencesFile.exists() && createIfNotExist) {
            try {
                boolean fileCreated = preferencesFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Не удалось создать файл с настройками пользователя");
            }
        }


        return preferencesFile;
    }
}
