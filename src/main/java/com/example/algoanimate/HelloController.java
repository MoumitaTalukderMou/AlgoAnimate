package com.example.algoanimate;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.application.Platform;

public class HelloController implements Initializable {

    @FXML
    private BorderPane mainLayout;

    @FXML
    private FlowPane cardContainer;

    @FXML
    private Button themeToggleBtn;

    @FXML private Label loggedInUser;
    @FXML private Button logoutBtn;

    private boolean isDarkMode = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        addCard("Arrays", "Insertion, Deletion, Search", "[]");
        addCard("Linked List", "Singly, Doubly, Circular", "->");
        addCard("Stack", "LIFO Operations", "||");
        addCard("Queue", "FIFO Operations", "==");
        addCard("Sorting", "Merge, selection,insertion, Bubble Sort", "AZ");
        addCard("Trees", "BST,Traversals", "/\\");
        addCard("Graphs", "BFS, DFS", "o-o");
    }

    // --- Theme Toggle (Dark/Light Mode) ---
    @FXML
    private void handleThemeToggle() {
        isDarkMode = !isDarkMode;
        if (isDarkMode) {
            mainLayout.getStyleClass().remove("light-theme");
            themeToggleBtn.setText("☀");
        } else {
            mainLayout.getStyleClass().add("light-theme");
            themeToggleBtn.setText("🌙");
        }
    }

    // --- Card Create Helper ---
    private void addCard(String title, String desc, String iconText) {
        VBox card = new VBox(15);
        card.getStyleClass().add("algo-card");
        card.setPrefSize(220, 170);
        card.setAlignment(javafx.geometry.Pos.CENTER);

        Label icon = new Label(iconText);
        icon.getStyleClass().add("icon-badge");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-title");

        Label descLabel = new Label(desc);
        descLabel.getStyleClass().add("card-desc");
        descLabel.setWrapText(true);
        descLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        card.getChildren().addAll(icon, titleLabel, descLabel);

        card.setOnMouseClicked(event -> handleCardClick(title));

        cardContainer.getChildren().add(card);
    }

    // --- Card Click Logic (New Page Opening) ---
    private void handleCardClick(String algoName) {
        if (algoName.equals("Sorting")) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sorting-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
                Stage stage = (Stage) cardContainer.getScene().getWindow();
                stage.setScene(scene);

                stage.setResizable(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (algoName.equals("Linked List")) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LinkedList-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
                Stage stage = (Stage) cardContainer.getScene().getWindow();
                stage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (algoName.equals("Stack")) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("stack-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
                Stage stage = (Stage) cardContainer.getScene().getWindow();
                stage.setScene(scene);
                stage.setResizable(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // --- NEW: Queue Routing ---
        else if (algoName.equals("Queue")) {
            try {
                // FIXED THE TYPO HERE: queue-view.fxml
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("queue-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
                Stage stage = (Stage) cardContainer.getScene().getWindow();
                stage.setScene(scene);
                stage.setResizable(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (algoName.equals("Trees")) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("bst-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
                Stage stage = (Stage) cardContainer.getScene().getWindow();
                stage.setScene(scene);
                stage.setResizable(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // --- NEW: Graph ---
        else if (algoName.equals("Graphs")) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Graph-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
                Stage stage = (Stage) cardContainer.getScene().getWindow();
                stage.setScene(scene);
                stage.setResizable(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // --- NEW: Array ---
        else if (algoName.equals("Arrays")) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Array-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
                Stage stage = (Stage) cardContainer.getScene().getWindow();
                stage.setScene(scene);
                stage.setResizable(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Coming Soon");
            alert.setHeaderText(null);
            alert.setContentText(algoName + " visualizer is coming soon!");
            alert.show();
        }
    }

    public void setUsername(String username) {
        loggedInUser.setText("Welcome, " + username);
    }
    @FXML
    private void handleFeaturesClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("features-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        Stage stage = (Stage) mainLayout.getScene().getWindow(); // অথবা cardContainer
        stage.setScene(scene);

        stage.setWidth(1200);
        stage.setHeight(800);
        stage.setResizable(false);
        stage.centerOnScreen();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("Login-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) logoutBtn.getScene().getWindow();
            stage.setTitle("AlgoAnimate - Login");
            stage.setScene(new Scene(root, 1200, 800));
            stage.setWidth(1200);
            stage.setHeight(800);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}