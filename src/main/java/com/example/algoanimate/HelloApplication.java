package com.example.algoanimate;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);  // ← changed 400,500 to 1200,800
        stage.setTitle("AlgoAnimate - Login");
        stage.setScene(scene);
        stage.setWidth(1200);   // ← add this
        stage.setHeight(800);   // ← add this
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}