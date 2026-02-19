package com.example.algoanimate;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class QueueController {

    @FXML private TextField inputField;
    @FXML private HBox queueContainer;
    @FXML private ListView<String> codeListView;
    @FXML private Label lblStatus, lblFront, lblRear, lblSize;

    private int queueSize = 0;
    private final int MAX_SIZE = 20;

    @FXML
    public void initialize() {
        loadPseudocode("idle");
    }

    private void loadPseudocode(String operation) {
        codeListView.getItems().clear();
        if (operation.equals("enqueue")) {
            codeListView.getItems().addAll(
                    "void enqueue(int data) {",
                    "  if (rear == MAX_SIZE) {",
                    "    print(\"Queue Overflow\");",
                    "    return;",
                    "  }",
                    "  queue[rear] = data;",
                    "  rear++;",
                    "}"
            );
        } else if (operation.equals("dequeue")) {
            codeListView.getItems().addAll(
                    "int dequeue() {",
                    "  if (front == rear) {",
                    "    print(\"Queue Underflow\");",
                    "    return -1;",
                    "  }",
                    "  int data = queue[front];",
                    "  front++;",
                    "  return data;",
                    "}"
            );
        } else if (operation.equals("peek")) {
            codeListView.getItems().addAll(
                    "int peekFront() {",
                    "  if (front == rear) {",
                    "    return -1;",
                    "  }",
                    "  return queue[front];",
                    "}"
            );
        } else if (operation.equals("isEmpty")) {
            codeListView.getItems().addAll(
                    "bool isEmpty() {",
                    "  if (front == rear) {",
                    "    return true;",
                    "  }",
                    "  return false;",
                    "}"
            );
        } else if (operation.equals("isFull")) {
            codeListView.getItems().addAll(
                    "bool isFull() {",
                    "  if (rear == MAX_SIZE) {",
                    "    return true;",
                    "  }",
                    "  return false;",
                    "}"
            );
        } else {
            codeListView.getItems().add("// Select an operation to see pseudocode");
        }
    }

    @FXML
    private void handleEnqueue() {
        String input = inputField.getText();
        if (input == null || input.isEmpty()) {
            updateStatus("Please enter a value!", true);
            return;
        }

        if (queueSize >= MAX_SIZE) {
            loadPseudocode("enqueue");
            trace(1);
            updateStatus("Queue Overflow! Cannot enqueue.", true);
            return;
        }

        loadPseudocode("enqueue");
        trace(4);

        // Create the block
        StackPane block = new StackPane();
        block.getStyleClass().add("array-block");
        Text text = new Text(input);
        text.getStyleClass().add("block-text");
        block.getChildren().add(text);

        // Create a label to go under the block (for Front/Rear tags)
        Label posLabel = new Label("");
        posLabel.setStyle("-fx-text-fill: #38bdf8; -fx-font-weight: bold;");

        // Combine them in a VBox
        VBox itemBox = new VBox(5, block, posLabel);
        itemBox.setAlignment(javafx.geometry.Pos.CENTER);

        // Add to the REAR of the HBox (end of list)
        queueContainer.getChildren().add(itemBox);
        queueSize++;

        // Animation sliding in from the right
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), itemBox);
        tt.setFromX(50);
        tt.setToX(0);
        tt.play();

        inputField.clear();
        updateLabelsAndStats("Enqueued: " + input);
    }

    @FXML
    private void handleDequeue() {
        if (queueSize == 0) {
            loadPseudocode("dequeue");
            trace(1);
            updateStatus("Queue Underflow! Nothing to dequeue.", true);
            return;
        }

        loadPseudocode("dequeue");
        trace(5);

        // Get the front element (index 0)
        VBox frontItemBox = (VBox) queueContainer.getChildren().get(0);
        StackPane topBlock = (StackPane) frontItemBox.getChildren().get(0);
        Text textNode = (Text) topBlock.getChildren().get(0);
        String dequeuedValue = textNode.getText();

        // Animate out (moving up and fading out)
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), frontItemBox);
        tt.setByY(-50);
        tt.setOnFinished(e -> {
            queueContainer.getChildren().remove(frontItemBox);
            queueSize--;
            updateLabelsAndStats("Dequeued: " + dequeuedValue);
        });
        tt.play();
    }

    @FXML
    private void handlePeek() {
        if (queueSize == 0) {
            loadPseudocode("peek");
            trace(1);
            updateStatus("Queue is empty! Cannot peek.", true);
            return;
        }

        loadPseudocode("peek");
        trace(4);

        VBox frontItemBox = (VBox) queueContainer.getChildren().get(0);
        StackPane frontBlock = (StackPane) frontItemBox.getChildren().get(0);
        Text textNode = (Text) frontBlock.getChildren().get(0);

        frontBlock.getStyleClass().add("block-compare");
        updateStatus("Peeked front element: " + textNode.getText(), false);

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                Platform.runLater(() -> frontBlock.getStyleClass().remove("block-compare"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void handleIsEmpty() {
        loadPseudocode("isEmpty");
        boolean empty = (queueSize == 0);
        trace(empty ? 2 : 4);
        updateStatus("isEmpty() returned " + String.valueOf(empty).toUpperCase(), false);
    }

    @FXML
    private void handleIsFull() {
        loadPseudocode("isFull");
        boolean full = (queueSize >= MAX_SIZE);
        trace(full ? 2 : 4);
        updateStatus("isFull() returned " + String.valueOf(full).toUpperCase(), false);
    }

    @FXML
    private void handleClear() {
        queueContainer.getChildren().clear();
        queueSize = 0;
        updateLabelsAndStats("Queue Cleared");
        loadPseudocode("idle");
    }

    private void trace(int lineIndex) {
        Platform.runLater(() -> codeListView.getSelectionModel().select(lineIndex));
    }

    // This method handles the logic of updating the "Front" and "Rear" text under the blocks
    private void updateLabelsAndStats(String statusMsg) {
        Platform.runLater(() -> {
            updateStatus(statusMsg, false);
            lblSize.setText("Queue Size: " + queueSize + " / " + MAX_SIZE);

            if (queueSize > 0) {
                // Update text indicators under the blocks
                for (int i = 0; i < queueSize; i++) {
                    VBox itemBox = (VBox) queueContainer.getChildren().get(i);
                    Label posLabel = (Label) itemBox.getChildren().get(1);

                    if (i == 0 && i == queueSize - 1) posLabel.setText("Front / Rear");
                    else if (i == 0) posLabel.setText("Front");
                    else if (i == queueSize - 1) posLabel.setText("Rear");
                    else posLabel.setText("");
                }

                // Update Stats Panel
                VBox frontBox = (VBox) queueContainer.getChildren().get(0);
                Text frontText = (Text) ((StackPane) frontBox.getChildren().get(0)).getChildren().get(0);
                lblFront.setText("Front Element: " + frontText.getText());

                VBox rearBox = (VBox) queueContainer.getChildren().get(queueSize - 1);
                Text rearText = (Text) ((StackPane) rearBox.getChildren().get(0)).getChildren().get(0);
                lblRear.setText("Rear Element: " + rearText.getText());
            } else {
                lblFront.setText("Front Element: None");
                lblRear.setText("Rear Element: None");
            }
        });
    }

    private void updateStatus(String statusMsg, boolean isError) {
        Platform.runLater(() -> {
            lblStatus.setText("Status: " + statusMsg);
            lblStatus.setStyle(isError ? "-fx-text-fill: #ef4444;" : "-fx-text-fill: #38bdf8;");
        });
    }

    @FXML
    private void handleBack() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        Stage stage = (Stage) queueContainer.getScene().getWindow();
        stage.setScene(scene);
    }
}