package com.example.algoanimate;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class FeaturesController {

    @FXML
    private FlowPane featuresContainer;

    @FXML
    public void initialize() {
        featuresContainer.getChildren().clear(); // আগের বক্স থাকলে ক্লিয়ার করে নেবে

        // ফ্যান্সি স্টাইলে ফিচারগুলো অ্যাড করা হচ্ছে
        addFeatureCard("1. Array", "• Create & Reset\n• Append & Insert\n• Delete (Value / Position)\n• Search & Traverse\n• Update Elements\n• Increase Capacity\n• Clear Array");

        addFeatureCard("2. Linked List", "▶ Singly, Doubly & Circular\n\n• Insert (Head, Tail, Position)\n• Delete (Value, Position)\n• Search Node\n• Traverse List\n• Update Node\n• Clear List");

        addFeatureCard("3. Stack (LIFO)", "• Push (Insert)\n• Pop (Remove)\n• Peek (Top Element)\n• isEmpty Check\n• isFull Check\n• Clear Stack");

        addFeatureCard("4. Queue (FIFO)", "• Enqueue (Insert)\n• Dequeue (Remove)\n• Peek Front\n• isEmpty Check\n• isFull Check\n• Clear Queue");

        addFeatureCard("5. Sorting Algorithms", "• Bubble Sort\n• Selection Sort\n• Insertion Sort\n• Merge Sort\n\n▶ Step-by-step Tracing\n▶ Real-time Status & Swaps");

        addFeatureCard("6. Trees (BST)", "• Insert Node\n• Delete Node\n• Search Element\n• Traversals (In, Pre, Post-order)\n• Find Predecessor\n• Find Successor");

        addFeatureCard("7. Graphs (BFS/DFS)", "• Create Custom Graph\n• Edit (Add/Delete Nodes & Edges)\n• BFS Traversal\n• DFS Traversal\n• Detect Cycles");

        addFeatureCard("8. Networking", "▶ Client-Server Architecture\n\n• Secure Login System\n• User Registration\n• Authentication\n• Remote Access Support");
    }

    // এই মেথডটিতে বক্সের সাইজ এবং ফন্ট ছোট করা হয়েছে
    private void addFeatureCard(String title, String details) {
        VBox card = new VBox(10); // স্পেসিং কমানো হয়েছে
        card.setPrefSize(300, 230); // কার্ডের সাইজ ছোট করা হয়েছে (আগে 320x260 ছিল)
        card.setAlignment(javafx.geometry.Pos.TOP_LEFT);

        // প্যাডিং 25 থেকে 20 করা হয়েছে
        card.setStyle("-fx-padding: 20; -fx-background-color: #1e293b; -fx-border-color: #38bdf8; -fx-border-width: 2; -fx-border-radius: 12; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);");

        Label titleLabel = new Label(title);
        // টাইটেলের ফন্ট সাইজ 20px করা হয়েছে
        titleLabel.setStyle("-fx-text-fill: #38bdf8; -fx-font-size: 20px; -fx-font-weight: bold;");

        Label descLabel = new Label(details);
        // ভেতরের লেখার ফন্ট 14px এবং লাইন স্পেসিং কমানো হয়েছে
        descLabel.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 14px; -fx-line-spacing: 4px;");
        descLabel.setWrapText(true);

        card.getChildren().addAll(titleLabel, descLabel);
        featuresContainer.getChildren().add(card);
    }

    @FXML
    private void handleBack() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        Stage stage = (Stage) featuresContainer.getScene().getWindow();
        stage.setScene(scene);

        // উইন্ডো সাইজ ফিক্স রাখা
        stage.setWidth(1200);
        stage.setHeight(800);
        stage.setResizable(false);
        stage.centerOnScreen();
    }
}