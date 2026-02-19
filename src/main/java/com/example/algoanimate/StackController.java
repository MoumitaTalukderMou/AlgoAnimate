package com.example.algoanimate;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class StackController {

    @FXML private TextField inputField;
    @FXML private VBox stackContainer;
    @FXML private ListView<String> codeListView;
    @FXML private Label lblStatus, lblTop, lblSize;

    private int stackSize = 0;
    private final int MAX_SIZE = 20; // Simulating the "Is Full" concept

    @FXML
    public void initialize() {
        loadPseudocode("idle");
    }

    private void loadPseudocode(String operation) {
        codeListView.getItems().clear();
        if (operation.equals("push")) {
            codeListView.getItems().addAll(
                    "void push(int data) {",
                    "  if (top == MAX - 1) {",
                    "    print(\"Stack Overflow\");",
                    "    return;",
                    "  }",
                    "  top++;",
                    "  stack[top] = data;",
                    "}"
            );
        } else if (operation.equals("pop")) {
            codeListView.getItems().addAll(
                    "int pop() {",
                    "  if (top == -1) {",
                    "    print(\"Stack Underflow\");",
                    "    return -1;",
                    "  }",
                    "  int data = stack[top];",
                    "  top--;",
                    "  return data;",
                    "}"
            );
        } else if (operation.equals("peek")) {
            codeListView.getItems().addAll(
                    "int peek() {",
                    "  if (top == -1) {",
                    "    return -1;",
                    "  }",
                    "  return stack[top];",
                    "}"
            );
        }
        else if (operation.equals("isEmpty")) {
            codeListView.getItems().addAll(
                    "bool isEmpty() {",
                    "  if (top == -1) {",
                    "    return true;",
                    "  }",
                    "  return false;",
                    "}"
            );
        } else if (operation.equals("isFull")) {
            codeListView.getItems().addAll(
                    "bool isFull() {",
                    "  if (top == MAX - 1) {",
                    "    return true;",
                    "  }",
                    "  return false;",
                    "}"
            );
        }
        else {
            codeListView.getItems().add("// Select an operation to see pseudocode");
        }
    }

    @FXML
    private void handlePush() {
        String input = inputField.getText();
        if (input == null || input.isEmpty()) {
            updateStatus("Please enter a value!", true);
            return;
        }

        if (stackSize >= MAX_SIZE) {
            loadPseudocode("push");
            trace(1); // Highlight overflow condition
            updateStatus("Stack Overflow! Cannot push.", true);
            return;
        }

        loadPseudocode("push");
        trace(6); // Highlight insertion logic

        // Create the visual block using your existing CSS classes
        StackPane block = new StackPane();
        block.getStyleClass().add("array-block"); // From style.css

        Text text = new Text(input);
        text.getStyleClass().add("block-text");
        block.getChildren().add(text);

        // Add to the TOP of the VBox (index 0)
        stackContainer.getChildren().add(0, block);
        stackSize++;

        // Drop animation
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), block);
        tt.setFromY(-50);
        tt.setToY(0);
        tt.play();

        inputField.clear();
        updateStats("Pushed: " + input);
    }

    @FXML
    private void handlePop() {
        if (stackSize == 0) {
            loadPseudocode("pop");
            trace(1); // Highlight underflow condition
            updateStatus("Stack Underflow! Nothing to pop.", true);
            return;
        }

        loadPseudocode("pop");
        trace(5); // Highlight pop logic

        // Get the top element (index 0)
        StackPane topBlock = (StackPane) stackContainer.getChildren().get(0);
        Text textNode = (Text) topBlock.getChildren().get(0);
        String poppedValue = textNode.getText();

        // Animate out then remove
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), topBlock);
        tt.setByY(-50);
        tt.setOnFinished(e -> {
            stackContainer.getChildren().remove(topBlock);
            stackSize--;
            updateStats("Popped: " + poppedValue);
        });
        tt.play();
    }

    @FXML
    private void handlePeek() {
        if (stackSize == 0) {
            loadPseudocode("peek");
            trace(1);
            updateStatus("Stack is empty! Cannot peek.", true);
            return;
        }

        loadPseudocode("peek");
        trace(4);

        StackPane topBlock = (StackPane) stackContainer.getChildren().get(0);
        Text textNode = (Text) topBlock.getChildren().get(0);

        // Temporarily highlight the peeked block (uses your existing block-compare class)
        topBlock.getStyleClass().add("block-compare");
        updateStatus("Peeked top element: " + textNode.getText(), false);

        // Remove highlight after 1 second
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                Platform.runLater(() -> topBlock.getStyleClass().remove("block-compare"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void handleClear() {
        stackContainer.getChildren().clear();
        stackSize = 0;
        updateStats("Stack Cleared");
        loadPseudocode("idle");
    }

    private void trace(int lineIndex) {
        Platform.runLater(() -> {
            codeListView.getSelectionModel().select(lineIndex);
        });
    }

    private void updateStats(String status) {
        updateStatus(status, false);
    }

    private void updateStatus(String statusMsg, boolean isError) {
        Platform.runLater(() -> {
            lblStatus.setText("Status: " + statusMsg);
            if (isError) {
                lblStatus.setStyle("-fx-text-fill: #ef4444;"); // Red for errors
            } else {
                lblStatus.setStyle("-fx-text-fill: #38bdf8;"); // Back to default blue
            }

            lblSize.setText("Stack Size: " + stackSize + " / " + MAX_SIZE);
            if (stackSize > 0) {
                StackPane topBlock = (StackPane) stackContainer.getChildren().get(0);
                Text textNode = (Text) topBlock.getChildren().get(0);
                lblTop.setText("Top Element: " + textNode.getText());
            } else {
                lblTop.setText("Top Element: None");
            }
        });
    }

    @FXML
    private void handleBack() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        Stage stage = (Stage) stackContainer.getScene().getWindow();
        stage.setScene(scene);
    }

    @FXML
    private void handleIsEmpty() {
        loadPseudocode("isEmpty");
        boolean empty = (stackSize == 0);

        // Highlight the return true or return false line
        trace(empty ? 2 : 4);

        if (empty) {
            updateStatus("isEmpty() returned TRUE (Stack has no elements).", false);
        } else {
            updateStatus("isEmpty() returned FALSE (Stack has elements).", false);
        }
    }

    @FXML
    private void handleIsFull() {
        loadPseudocode("isFull");
        boolean full = (stackSize >= MAX_SIZE);

        // Highlight the return true or return false line
        trace(full ? 2 : 4);

        if (full) {
            updateStatus("isFull() returned TRUE (Stack is at max capacity).", false);
        } else {
            updateStatus("isFull() returned FALSE (Stack has empty space).", false);
        }
    }
}