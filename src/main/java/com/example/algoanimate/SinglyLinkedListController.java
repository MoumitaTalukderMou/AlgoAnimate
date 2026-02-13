package com.example.algoanimate;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.util.Duration;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;
import javafx.scene.control.TextInputDialog;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


import java.io.IOException;

public class SinglyLinkedListController {

    @FXML
    private Button btnBack;

    @FXML
    private ChoiceBox<String> SLLChoiceBox;

    @FXML
    private AnchorPane animationPane;  // left panel, visual nodes দেখাবে

    @FXML
    private ListView<String> actionListView; // right panel

    @FXML
    private void onBackClick(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LinkedList-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Head tracking variable
    private HBox headHBox;  // actual HBox head track

     //Head কে visually mark করার method

    private void markHeadVisually() {
        // প্রথমে সব node থেকে HEAD marker সরান
        for (Node node : animationPane.getChildren()) {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                // যদি ৩টির বেশি child থাকে (মানে HEAD marker আছে)
                while (hbox.getChildren().size() > 2) {
                    hbox.getChildren().remove(2); // HEAD marker remove
                    // Node কে normal color এ ফিরিয়ে দিন
                    StackPane stack = (StackPane) hbox.getChildren().get(0);
                    Rectangle rect = (Rectangle) stack.getChildren().get(0);
                    rect.setFill(Color.LIGHTYELLOW);
                }
            }
        }

        // Add head Marker at new Head
        if (headHBox != null  && animationPane.getChildren().contains(headHBox)) {
            Text headText = new Text("HEAD");
            headText.setFont(Font.font(15));
            headText.setFill(Color.GREENYELLOW);
            headText.setTranslateY(-30); // node এর উপরে দেখানোর জন্য

            // Head node কে slightly different color দিন
            StackPane stack = (StackPane) headHBox.getChildren().get(0);
            Rectangle rect = (Rectangle) stack.getChildren().get(0);
            rect.setFill(Color.DARKGRAY);  // head আলাদা color
            headHBox.getChildren().add(headText);

        }
    }

    // Head update method
    private void updateHead(HBox newHead) {
        this.headHBox = newHead;
        //markHeadVisually(); // Head update করার সাথে সাথে visually mark করুন
    }

    // Node create function
    private HBox createNodeView(int data , boolean isLast) {
        //Rectangle
        Rectangle rect = new Rectangle(60, 40, Color.LIGHTYELLOW);
        rect.setArcWidth(10);
        rect.setArcHeight(10);

        // Text
        Text text;

            text = new Text(String.valueOf(data));
            text.setFont(Font.font(20));
            text.setFill(Color.DARKBLUE);// text color


        // StackPane to center text on rectangle
        StackPane stack = new StackPane();
        stack.getChildren().addAll(rect, text);
        stack.setPrefSize(rect.getWidth(), rect.getHeight());

        HBox hbox = new HBox();
        hbox.setSpacing(0);
        hbox.setLayoutY(50);

            // Arrow line
            javafx.scene.shape.Line line = new javafx.scene.shape.Line(0, 0, 20, 0);
            line.setStroke(Color.WHITESMOKE);
            line.setStrokeWidth(2);

            // Arrow head (triangle)
            javafx.scene.shape.Polygon arrowHead = new javafx.scene.shape.Polygon();
            arrowHead.getPoints().addAll(
                    50.0, 0.0,   // tip
                    40.0, -5.0,   // top
                    40.0, 5.0    // bottom
            );
            arrowHead.setFill(Color.WHITESMOKE);

            // Use HBox to left-align line + arrow
            HBox arrowPane = new HBox(line, arrowHead);
            arrowPane.setAlignment(Pos.CENTER_LEFT); // left theke start
            arrowPane.setPrefHeight(rect.getHeight());
            arrowPane.setSpacing(0); // line ar arrow head er spacing 0

            // Combine rectangle + arrow
            hbox.getChildren().addAll(stack, arrowPane);
        if (isLast) {

            //Rectangle
            Rectangle nullTextRect = new Rectangle(60, 40, Color.LIGHTYELLOW);
            nullTextRect.setArcWidth(10);
            nullTextRect.setArcHeight(10);

            // Last node → show "null" instead of arrow
            Text nullText = new Text("NULL");
            nullText.setFont(Font.font(15));
            nullText.setFill(Color.DARKBLUE);
            nullText.setTranslateY(0); // vertical alignment

            // StackPane to center text on rectangle
            StackPane NullStack = new StackPane();
            NullStack.getChildren().addAll(nullTextRect, nullText);
            NullStack.setPrefSize(nullTextRect.getWidth(), nullTextRect.getHeight());

            hbox.getChildren().add(NullStack);
        }


        return hbox;
    }

    // Insert at head animation
    private void insertAtHeadAnimation(int data) {
        boolean isLast = animationPane.getChildren().isEmpty();

        HBox newNode = createNodeView(data, isLast);
        newNode.setLayoutX(-10); // start outside pane
        animationPane.getChildren().add(newNode);

        // Animate: move to first position
        TranslateTransition tt = new TranslateTransition(Duration.seconds(1), newNode);
        tt.setToX(50); // head position
        tt.play();

        // shift old nodes
        tt.setOnFinished(e -> shiftNodesRight(newNode));

        updateHead(newNode);
       // markHeadVisually();


    }

    // Insert at head with input dialog
    private void showInsertAtHeadDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Insert At Head Value");
        dialog.setHeaderText("Enter value to insert at Head:");
        dialog.setContentText("Value:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(value -> {
            try {
                int searchValue = Integer.parseInt(value);
                insertAtHeadAnimation(searchValue);
            } catch (NumberFormatException e) {
                actionListView.getItems().add("Please enter a valid number!");
            }
        });
    }
    // Existing nodes right shift
    private void shiftNodesRight(HBox newNode) {
        for (Node node : animationPane.getChildren()) {
            if (node != newNode) {
                TranslateTransition shift = new TranslateTransition(Duration.seconds(0.5), node);
                shift.setByX(95); // spacing
                shift.play();
            }
        }
    }

    // Insert at tail animation
    private void insertAtTailAnimation(int data) {
        // If list not empty → remove NULL from previous last node
        if (!animationPane.getChildren().isEmpty()) {
            HBox lastNode = (HBox) animationPane.getChildren().get(animationPane.getChildren().size() - 1);

            // lastNode এর children: [stack(data), arrowPane, NULLstack]
            if (lastNode.getChildren().size() > 2) {
                lastNode.getChildren().remove(2); // remove NULL box
            }
        }

        boolean isLast = true; // last node
        HBox newNode = createNodeView(data, isLast);


        double x = 140; // start head position
        for (Node node : animationPane.getChildren()) {
            x += 95; // spacing
        }

        newNode.setLayoutX(-100); // start outside pane
        animationPane.getChildren().add(newNode);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(1), newNode);
        tt.setToX(x);
        tt.play();

        //if it is first Node then update head
        tt.setOnFinished(e -> {
            if (animationPane.getChildren().size() == 1) {
                // First Node so Head
                updateHead(newNode);

            }
        });

    }

    // Insert at tail with input dialog
    private void showInsertAtTailDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Insert At tail Value");
        dialog.setHeaderText("Enter value to insert at tail:");
        dialog.setContentText("Value:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(value -> {
            try {
                int searchValue = Integer.parseInt(value);
                insertAtTailAnimation(searchValue);
            } catch (NumberFormatException e) {
                actionListView.getItems().add("Please enter a valid number!");
            }
        });
    }

    // Search animation method
    private void searchAnimation(int searchValue) {
        if (animationPane.getChildren().isEmpty()) {
            actionListView.getItems().add("List is empty! Nothing to search.");
            return;
        }

        actionListView.getItems().add("Searching for value: " + searchValue);
        int listSize = animationPane.getChildren().size();
        // Sequential search animation
        for (int i = 0; i < listSize; i++) {
            final int currentIndex = i;  // final variable for lambda
            Node node = animationPane.getChildren().get(i);

            if (node instanceof HBox) {
                HBox nodeBox = (HBox) node;

                // Find the StackPane inside HBox which contains the rectangle and text
                StackPane stackPane = (StackPane) nodeBox.getChildren().get(0);
                Rectangle rect = (Rectangle) stackPane.getChildren().get(0);
                Text text = (Text) stackPane.getChildren().get(1);

                int nodeValue = Integer.parseInt(text.getText());

                // Create a delay for sequential animation
                Duration delay = Duration.seconds(i * 1.0);

                // Highlight current node being checked
                PauseTransition pause = new PauseTransition(delay);
                pause.setOnFinished(e -> {
                    // Change color to show we're checking this node
                    rect.setFill(Color.ORANGE);
                    actionListView.getItems().add("Checking node with value: " + nodeValue);

                    // Check if this is the value we're looking for
                    if (nodeValue == searchValue) {
                        // Found! Highlight in green
                        rect.setFill(Color.LIGHTGREEN);
                        actionListView.getItems().add("✓ Found value " + searchValue + " at position " + currentIndex);

                        // Add a blinking effect
                        Timeline blink = new Timeline(
                                new KeyFrame(Duration.millis(300), event -> {
                                    rect.setFill(rect.getFill() == Color.LIGHTGREEN ? Color.YELLOW : Color.LIGHTGREEN);
                                })
                        );
                        blink.setCycleCount(6); // blink 3 times (300ms * 6 = 1800ms)
                        blink.play();
                    } else {
                        // Not found, revert color after a moment
                        PauseTransition revertPause = new PauseTransition(Duration.seconds(0.5));
                        revertPause.setOnFinished(event -> {
                            rect.setFill(Color.LIGHTYELLOW);
                        });
                        revertPause.play();
                    }
                });
                pause.play();
            }
        }

        // Check if value exists at all after animation completes
        PauseTransition finalCheck = new PauseTransition(
                Duration.seconds(animationPane.getChildren().size() * 1.0 + 0.5)
        );
        finalCheck.setOnFinished(e -> {
            boolean found = false;
            for (Node node : animationPane.getChildren()) {
                if (node instanceof HBox) {
                    HBox nodeBox = (HBox) node;
                    StackPane stackPane = (StackPane) nodeBox.getChildren().get(0);
                    Text text = (Text) stackPane.getChildren().get(1);
                    if (Integer.parseInt(text.getText()) == searchValue) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                actionListView.getItems().add("✗ Value " + searchValue + " not found in the list");
            }
        });
        finalCheck.play();
    }

    // Search with input dialog
    private void showSearchDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Value");
        dialog.setHeaderText("Enter value to search:");
        dialog.setContentText("Value:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(value -> {
            try {
                int searchValue = Integer.parseInt(value);
                searchAnimation(searchValue);
            } catch (NumberFormatException e) {
                actionListView.getItems().add("Please enter a valid number!");
            }
        });
    }



    @FXML
    public void initialize() {
        SLLChoiceBox.getItems().addAll(
                "Create",
                "Insert at Head",
                "Insert at Tail",
                "Delete",
                "Search",
                "Traverse"
        );

        SLLChoiceBox.setOnAction(e -> handleChoiceSelection());

        animationPane.getChildren().clear();
        actionListView.getItems().clear();
        headHBox = null;  // head reset

    }

    private void handleChoiceSelection() {
        String selected = SLLChoiceBox.getValue();
        actionListView.getItems().clear();

        switch (selected) {
            case "Create":
                actionListView.getItems().add("Creating new Linked List...");
                animationPane.getChildren().clear();
                for(int i = 0 ; i < 4 ; i++)
                {

                        insertAtTailAnimation((int)(Math.random() * 100));

                }
                break;

            case "Insert at Head":
                actionListView.getItems().add("Insert at Head operation...");
                actionListView.getItems().add(
                        "    private static class Node {\n" +
                        "        int data;\n" +
                        "        Node next;\n" +
                        "\n" +
                        "        Node(int data) {\n" +
                        "            this.data = data;\n" +
                        "            this.next = null;\n" +
                        "        }\n" +
                        "    }\n" +
                        "\n" +
                        "    private Node head;" +
                        "\n" +
                        "    public void insertAtHead(int newData) {\n" +
                        "        Node newNode = new Node(newData);\n" +
                        "\n" +
                        "        newNode.next = head;\n" +
                        "\n" +
                        "        head = newNode;\n" +
                        "    }");
                showInsertAtHeadDialog();
                break;

            case "Insert at Tail":
                actionListView.getItems().add("Insert at Tail operation...");
                actionListView.getItems().add("private static class Node {\n" +
                        "        int data;\n" +
                        "        Node next;\n" +
                        "\n" +
                        "        Node(int data) {\n" +
                        "            this.data = data;\n" +
                        "            this.next = null; " +
                        "        }\n" +
                        "    }\n" +
                        "\n" +
                        "    private Node head;\n" +
                        "\n" +
                        "    public void insertAtTail(int data) {\n" +
                        "        Node newNode = new Node(data);\n" +
                        "\n" +
                        "        if (head == null) {\n" +
                        "            head = newNode;\n" +
                        "            return;\n" +
                        "        }");
                showInsertAtTailDialog();
                break;

            case "Delete":
                actionListView.getItems().add("Delete operation (not animated yet)");
                break;

            case "Search":
                actionListView.getItems().add("Search operation (pseudo code)");
                showSearchDialog();
                break;

            case "Traverse":
                actionListView.getItems().add("Traverse operation (pseudo code)");
                break;
        }
    }
}
