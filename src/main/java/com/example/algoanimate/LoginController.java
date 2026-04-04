package com.example.algoanimate;

// JavaFX Core
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

// JavaFX Controls
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

// JavaFX Scene & Stage
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// JavaFX Event
import javafx.event.ActionEvent;

// JavaFX Application Thread (for network calls)
import javafx.application.Platform;

// Java Networking
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.Socket;

// Java IO
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;

// Java Util
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Runs when FXML loads
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        // Disable button to prevent double-click
        loginButton.setDisable(true);

        // Run network call on background thread
        new Thread(() -> {
            boolean success = authenticate(username, password);

            // Update UI back on JavaFX thread
            Platform.runLater(() -> {
                if (success) {
                    loadMainScene(username);
                } else {
                    showError("Invalid username or password.");
                    loginButton.setDisable(false);
                }
            });
        }).start();
    }

    private boolean authenticate(String username, String password) {
        // TODO: Replace with your actual server IP and port
        try (Socket socket = new Socket("192.168.0.104", 5000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

//            out.println(username + ":" + password);
            String message = "LOGIN:" + username + ":" + password;  // ← LOGIN: prefix must be here
            System.out.println("Sending: " + message);
            out.println(message);

            String response = in.readLine();
            System.out.println("Response: " + response);

            return "SUCCESS".equals(response);

        } catch (IOException e) {
            System.out.println("CONNECTION FAILED: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void loadMainScene(String username) {
        try {
            // This loads YOUR existing main animation screen
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
            Parent root = loader.load();

            // Pass username to HelloController
            HelloController controller = loader.getController();
            controller.setUsername(username);

            // Get the current window (login window)
            Stage stage = (Stage) loginButton.getScene().getWindow();

            // Replace login scene with main animation scene
            stage.setTitle("AlgoAnimate - DSA Visualizer");
            stage.setScene(new Scene(root, 1200, 800));
            stage.setWidth(1200);
            stage.setHeight(800);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load main screen.");
        }
    }


    @FXML
    private void handleRegister(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields to register.");
            return;
        }

        loginButton.setDisable(true);

        new Thread(() -> {
            String result = register(username, password);

            Platform.runLater(() -> {
                switch (result) {
                    case "SUCCESS"     -> showSuccess("Account created! You can now log in.");
                    case "USER_EXISTS" -> showError("Username already taken. Try another.");
                    default            -> showError("Registration failed. Server error.");
                }
                loginButton.setDisable(false);
            });
        }).start();
    }

    private String register(String username, String password) {
        try (Socket socket = new Socket("192.168.0.104", 5000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("REGISTER:" + username + ":" + password);
            return in.readLine();

        } catch (IOException e) {
            e.printStackTrace();
            return "FAIL";
        }
    }

    private void showSuccess(String message) {
        errorLabel.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 12px;");
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void showError(String message) {
        errorLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;");
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}