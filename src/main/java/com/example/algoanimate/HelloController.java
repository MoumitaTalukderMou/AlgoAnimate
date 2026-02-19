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

public class HelloController implements Initializable {

    @FXML
    private BorderPane mainLayout;

    @FXML
    private FlowPane cardContainer;

    @FXML
    private Button themeToggleBtn;

    private boolean isDarkMode = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // কার্ডগুলো যোগ করা হচ্ছে
        addCard("Arrays", "Insertion, Deletion, Search", "[]");
        addCard("Linked List", "Singly, Doubly, Circular", "->");
        addCard("Stack", "LIFO Operations", "||");
        addCard("Queue", "FIFO Operations", "==");
        addCard("Sorting", "Merge, Quick, Bubble Sort", "AZ"); // এই কার্ডে ক্লিক করলে নতুন পেজ আসবে
        addCard("Trees", "BST, AVL, Traversals", "/\\");
        addCard("Graphs", "BFS, DFS, Dijkstra", "o-o");
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

        // কার্ডে ক্লিক করলে কি হবে
        card.setOnMouseClicked(event -> handleCardClick(title));

        cardContainer.getChildren().add(card);
    }

    // --- Card Click Logic (New Page Opening) ---
    private void handleCardClick(String algoName) {
        if (algoName.equals("Sorting")) {
            try {
                // ১. Sorting FXML ফাইল লোড করা
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sorting-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1100, 750);

                // ২. বর্তমান স্টেজ (উইন্ডো) খুঁজে বের করা
                Stage stage = (Stage) cardContainer.getScene().getWindow();

                // ৩. নতুন সিন সেট করা
                stage.setScene(scene);

            } catch (IOException e) {
                e.printStackTrace(); // এরর হলে কনসোলে দেখাবে
            }
        }
        else if (algoName.equals("Linked List")) {
            try {
                // ১. Sorting FXML ফাইল লোড করা
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LinkedList-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1200, 800);

                // ২. বর্তমান স্টেজ (উইন্ডো) খুঁজে বের করা
                Stage stage = (Stage) cardContainer.getScene().getWindow();

                // ৩. নতুন সিন সেট করা
                stage.setScene(scene);

            } catch (IOException e) {
                e.printStackTrace(); // এরর হলে কনসোলে দেখাবে
            }
        }

        else if (algoName.equals("Stack")) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("stack-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1100, 750);
                Stage stage = (Stage) cardContainer.getScene().getWindow();
                stage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // --- NEW: Queue Routing ---
        else if (algoName.equals("Queue")) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("queue-viw.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1100, 750);
                Stage stage = (Stage) cardContainer.getScene().getWindow();
                stage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            // বাকি কার্ডগুলোর জন্য আপাতত অ্যালার্ট
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Coming Soon");
            alert.setHeaderText(null);
            alert.setContentText(algoName + " visualizer is coming soon!");
            alert.show();
        }
    }
}