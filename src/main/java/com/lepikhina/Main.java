package com.lepikhina;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        URL mainLocation = Objects.requireNonNull(getClass().getClassLoader().getResource("main-window.fxml"));
        Parent root = FXMLLoader.load(mainLocation);
        primaryStage.setTitle("Обезличивание базы данных");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
