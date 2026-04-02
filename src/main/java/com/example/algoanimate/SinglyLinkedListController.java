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
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.scene.control.Dialog;        // Dialog
import javafx.scene.control.ButtonBar;     // ButtonType,s position
import javafx.scene.control.Label;         // to show "Value:" / "Position:"
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.util.Pair;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.geometry.Pos;





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
    private Label lblStatus;
    @FXML
    private Label lblSize;


    private SinglyLinkedListOperation list = new SinglyLinkedListOperation();
    private Map<SinglyLinkedListOperation.Node, HBox> nodeMap = new HashMap<>();
    private HBox headHBox;  // actual HBox head track
    private HBox tailHBox;

    @FXML
    private void onBackClick(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LinkedList-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(scene);

            stage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Head tracking variable

    //Head ke visually mark korar method
    private void markHeadVisually() {

        for(Node node : animationPane.getChildren()) {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;

                // node color reset
                StackPane stack = (StackPane) hbox.getChildren().get(0);
                Rectangle rect = (Rectangle) stack.getChildren().get(0);
                if(rect.getFill() == Color.DARKGRAY) {
                    rect.setFill(Color.LIGHTYELLOW);
                }

            }
        }

        // Add head Marker at new Head
        if (headHBox != null  && animationPane.getChildren().contains(headHBox)) {
            // Head node কে slightly different color দিন
            StackPane stack = (StackPane) headHBox.getChildren().get(0);
            Rectangle rect = (Rectangle) stack.getChildren().get(0);
            rect.setFill(Color.DARKGRAY);  // head আলাদা color
        }
    }
    // Head update method
    private void updateHead(HBox newHead) {
        this.headHBox = newHead;
        markHeadVisually(); // Head update & visually mark
    }

    private void markTailVisually() {

        for(Node node : animationPane.getChildren()) {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                // node color reset
                StackPane stack = (StackPane) hbox.getChildren().get(0);
                Rectangle rect = (Rectangle) stack.getChildren().get(0);
                if(rect.getFill() == Color.GHOSTWHITE) {
                    rect.setFill(Color.LIGHTYELLOW);
                }

            }
        }

        // Add head Marker at new Head
        if (tailHBox != null  && animationPane.getChildren().contains(tailHBox)) {
            // Head node কে slightly different color দিন
            StackPane stack = (StackPane) tailHBox.getChildren().get(0);
            Rectangle rect = (Rectangle) stack.getChildren().get(0);
            rect.setFill(Color.GHOSTWHITE);  // tail আলাদা color
        }
    }
    // Tail update method
    private void updateTail(HBox newTail) {
        this.tailHBox = newTail;
        markTailVisually(); // Head update করার সাথে সাথে visually mark করুন
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


    private HBox insertAtHeadAnimation(int data) {

        boolean isLast = animationPane.getChildren().isEmpty();

        // Create the new node first (but don't add yet)
        HBox newNode = createNodeView(data, isLast);

        newNode.setLayoutY(50);
        newNode.setLayoutX(50); // final head position

        // Shift old nodes right first
        if (!animationPane.getChildren().isEmpty()) {
            shiftNodesRightFromIndexFixed(0, () -> {
                // After shifting, add the new node at head
                animationPane.getChildren().add(0, newNode);

                // Now update head so marking works
                updateHead(newNode);
                double requiredWidth = 50 + (animationPane.getChildren().size() * 100) + 200;
                if (requiredWidth > animationPane.getPrefWidth()) {
                    animationPane.setPrefWidth(requiredWidth);
                }

                // Fade in animation if you want
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), newNode);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();
                updateSize(list.getSize());
            });
        } else {
            // List is empty, just add directly
            animationPane.getChildren().add(newNode);

            // Update head and tail
            updateHead(newNode);
            if (isLast) {
                updateTail(newNode);
            }
        }

        // Update head/tail references
        if (isLast && !animationPane.getChildren().isEmpty()) {
            updateTail(newNode);
        }


        return newNode;
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
                int insertValue = Integer.parseInt(value);
                SinglyLinkedListOperation.Node realNode = list.insertAtHead(insertValue); // LinkedList Logic
                HBox uiNode = insertAtHeadAnimation(insertValue);
                nodeMap.put(realNode, uiNode);

            } catch (NumberFormatException e) {
                actionListView.getItems().add("Please enter a valid number!");
            }
        });
    }


    // Insert at tail animation
    private HBox insertAtTailAnimation(int data) {

        boolean isLast = true; // last node
        HBox newNode = createNodeView(data, isLast);


        // If list not empty → remove NULL from previous last node
        if (animationPane.getChildren().isEmpty()) {
            // First Node so Head
            updateHead(newNode);
        }
        if (!animationPane.getChildren().isEmpty()) {
            HBox lastNode = (HBox) animationPane.getChildren().get(animationPane.getChildren().size() - 1);

            // lastNode এর children: [stack(data), arrowPane, NULLstack]
            if (lastNode.getChildren().size() > 2) {
                lastNode.getChildren().remove(2); // remove NULL box
            }
        }

        double x = 140; // start head position
        for (Node node : animationPane.getChildren()) {
            x += 95; // spacing
        }
        // After calculating x position
        if (x + 200 > animationPane.getPrefWidth()) {
            animationPane.setPrefWidth(x + 200);  // expand pane as nodes grow
        }

        newNode.setLayoutX(-100); // start outside pane
        animationPane.getChildren().add(newNode);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(1), newNode);
        tt.setToX(x);
        updateSize(list.getSize());
        tt.play();

        updateTail(newNode);
        return newNode;
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
                int insertValue = Integer.parseInt(value);
                SinglyLinkedListOperation.Node realNode = list.insertAtTail(insertValue); //LinkedList Logic Updated
                HBox uiNode = insertAtTailAnimation(insertValue);
                nodeMap.put(realNode, uiNode);

            } catch (NumberFormatException e) {
                actionListView.getItems().add("Please enter a valid number!");
            }
        });
    }

    private void searchAnimation(int searchValue) {
        if (animationPane.getChildren().isEmpty()) {
            actionListView.getItems().add("List is empty! Nothing to search.");
            return;
        }

        actionListView.getItems().add("Searching for value: " + searchValue);

        SinglyLinkedListOperation.Node headNode = list.getHead();
        AtomicBoolean foundFlag = new AtomicBoolean(false); // mutable flag
        searchNext(headNode, 0, searchValue , foundFlag);
    }

    // Recursive helper method for sequential search animation
    private void searchNext(SinglyLinkedListOperation.Node current, int index, int searchValue, AtomicBoolean foundFlag) {
        if (current == null) {
            if(!foundFlag.get()) {
                actionListView.getItems().add("✗ Value " + searchValue + " not found in the list");
            }
            actionListView.getItems().add("Search Operation successful!");
            return;
        }


        HBox uiNode = nodeMap.get(current);
        StackPane stackPane = (StackPane) uiNode.getChildren().get(0);
        Rectangle rect = (Rectangle) stackPane.getChildren().get(0);
        int value = current.data;

        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(e -> {
            rect.setFill(Color.ORANGE);
            actionListView.getItems().add("Checking node with value: " + value);

            PauseTransition revert = new PauseTransition(Duration.seconds(0.5));
            revert.setOnFinished(ev -> {
//                final boolean foundValue = found;

                if (value == searchValue) {
                    foundFlag.set(true); // mark as found
                    rect.setFill(Color.LIGHTGREEN);
                    actionListView.getItems().add("✓ Found value " + searchValue + " at position " + index);

                    // blinking effect
                    Timeline blink = new Timeline(
                            new KeyFrame(Duration.millis(300), evt ->
                                    rect.setFill(rect.getFill() == Color.LIGHTGREEN ? Color.YELLOW : Color.LIGHTGREEN)
                            )
                    );
                    blink.setCycleCount(6);
                    blink.setOnFinished(b -> {
                        // reset head/tail color after blink
                        resetNodeColor(uiNode);
                        searchNext(current.next, index + 1, searchValue, foundFlag);
                    });
                    blink.play();
                } else {
                    resetNodeColor(uiNode);
                    searchNext(current.next, index + 1, searchValue, foundFlag);
                }
            });
            revert.play();
        });
        pause.play();
    }

    // Helper method: reset node color based on head/tail
    private void resetNodeColor(HBox uiNode) {
        StackPane stack = (StackPane) uiNode.getChildren().get(0);
        Rectangle rect = (Rectangle) stack.getChildren().get(0);

        if (uiNode == headHBox) {
            rect.setFill(Color.DARKGRAY);
        } else if (uiNode == tailHBox) {
            rect.setFill(Color.GHOSTWHITE);
        } else {
            rect.setFill(Color.LIGHTYELLOW);
        }
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

    private void traverseAnimation() {
        if (animationPane.getChildren().isEmpty()) {
            actionListView.getItems().add("List is empty! Nothing to search.");
            return;
        }

        SinglyLinkedListOperation.Node current = list.getHead();
        int index = 0;
        actionListView.getItems().add("Traversing the List...\n");
        int listSize = animationPane.getChildren().size();

        traverseNext(current, index);
    }

    private void traverseNext(SinglyLinkedListOperation.Node current, int index) {

        if(current == null) {
            actionListView.getItems().add("Total Nodes: " + index + "\n");
            actionListView.getItems().add("Traversal Complete Successfully!");
            return;
        }


        HBox uiNode = nodeMap.get(current);
        StackPane stackPane = (StackPane) uiNode.getChildren().get(0);
        Rectangle rect = (Rectangle) stackPane.getChildren().get(0);
        int value = current.data;
        final int idx = index;
        final SinglyLinkedListOperation.Node curr = current;


        PauseTransition pause = new PauseTransition(Duration.seconds( 0.5));

        pause.setOnFinished(e -> {
            rect.setFill(Color.ORANGE);
            actionListView.getItems().add("Position " + idx +" : " + value);


            PauseTransition revert = new PauseTransition(Duration.seconds(0.3));
            revert.setOnFinished(ev ->{
                if(uiNode == headHBox) {
                    rect.setFill(Color.DARKGRAY);
                }
                else if(uiNode == tailHBox) {

                    rect.setFill(Color.GHOSTWHITE);
                }
                else {
                    rect.setFill(Color.LIGHTYELLOW);
                }
                traverseNext(curr.next, idx + 1); // next node

            });
            revert.play();

        });
        pause.play();


    }

    private HBox insertAtPosAnimation(int val, int pos) {

        if (animationPane.getChildren().isEmpty() && pos != 0) {
            actionListView.getItems().add("List is empty! position " + pos + " not found.\n");
            return null;
        }

        actionListView.getItems().add("Inserting value " + val + " at position: " + pos);

        // Find previous UI node
        SinglyLinkedListOperation.Node current = list.getHead();
        for (int i = 0; i < pos - 1; i++) {
            current = current.next;
        }

        HBox prevUI = nodeMap.get(current);

        if (prevUI == null) {
            actionListView.getItems().add("Error: Previous node not found in UI!");
            return null;
        }

        int insertIndex = animationPane.getChildren().indexOf(prevUI) + 1;

        // Middle insert → never last
        HBox newNode = createNodeView(val, false);

        newNode.setLayoutY(50);

        double prevActualX = prevUI.getLayoutX() + prevUI.getTranslateX();
        double newX = prevActualX + 95;

        shiftNodesRightFromIndexFixed(insertIndex, () -> {

            animationPane.getChildren().add(insertIndex, newNode);

            newNode.setLayoutX(newX);
            newNode.setTranslateX(0);

            double requiredWidth = 50 + (animationPane.getChildren().size() * 100) + 200;
            if (requiredWidth > animationPane.getPrefWidth()) {
                animationPane.setPrefWidth(requiredWidth);
            }

            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), newNode);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

            actionListView.getItems().add("✓ Inserted " + val + " at position " + pos);
            updateSize(list.getSize());
        });

        return newNode;
    }


    // Fixed shift method - only one version
    private void shiftNodesRightFromIndexFixed(int startIndex, Runnable onComplete) {
        int count = animationPane.getChildren().size();
        AtomicInteger finishedCount = new AtomicInteger(0);
        int nodesToShift = count - startIndex;

        if (nodesToShift == 0) {
            onComplete.run();
            return;
        }

        for (int i = startIndex; i < count; i++) {
            Node node = animationPane.getChildren().get(i);

            // Get current position including any ongoing translation
            double currentActualX = node.getLayoutX() + node.getTranslateX();

            TranslateTransition shift = new TranslateTransition(Duration.seconds(0.5), node);
            shift.setByX(100);

            final int index = i;
            final double finalCurrentX = currentActualX;

            shift.setOnFinished(e -> {
                // IMPORTANT: Update layoutX to the new permanent position
                // and reset translateX to 0
                node.setLayoutX(finalCurrentX + 100);
                node.setTranslateX(0);

                if (finishedCount.incrementAndGet() == nodesToShift) {
                    onComplete.run();
                }
            });
            shift.play();
        }
    }

    private void showInsertAtPosDialog() {
        // Creating a custom dialog
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Insert at Position");
        dialog.setHeaderText("Enter Value and Position to Insert:");

        // Fixing Button type (OK & Cancel)
        ButtonType insertButtonType = new ButtonType("Insert", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(insertButtonType, ButtonType.CANCEL);

        // Grid Plan
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField valueField = new TextField();
        valueField.setPromptText("Value");
        TextField posField = new TextField();
        posField.setPromptText("Position(0-based)");

        grid.add(new Label("Value:"), 0, 0);
        grid.add(valueField, 1, 0);
        grid.add(new Label("Position:"), 0, 1);
        grid.add(posField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        //Result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == insertButtonType) {
                return new Pair<>(valueField.getText(), posField.getText());
            }
            return null;
        });

        // Dialog show and Result handling
        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(pair -> {
            try {
                int value = Integer.parseInt(pair.getKey());
                int position = Integer.parseInt(pair.getValue());
                // calling Insertion Method
                HBox uiNode ;
                SinglyLinkedListOperation.Node realNode;

                int currentSize = list.getSize(); // capture current size BEFORE insert

                if(position == 0)
                {
                    realNode = list.insertAtHead(value);
                    uiNode = insertAtHeadAnimation(value);

                } else if (position == currentSize)
                {
                    realNode = list.insertAtTail(value);
                    uiNode = insertAtTailAnimation(value);
                }
                else if (position > 0 && position < currentSize){
                    realNode = list.insertAtPosition(value, position);
                    uiNode = insertAtPosAnimation(value, position);
                } else {
                    actionListView.getItems().add("Error: Invalid position!");
                    return;
                }
                nodeMap.put(realNode, uiNode);


            } catch (NumberFormatException e) {
                actionListView.getItems().add("Error: Please enter valid integers!");
            }
        });
    }
    @FXML


    // Fixed shift method - only one version
    private void shiftNodesLeftFromIndexFixed(int startIndex, Runnable onComplete) {
        int count = animationPane.getChildren().size();
        AtomicInteger finishedCount = new AtomicInteger(0);
        int nodesToShift = count - startIndex;

        if (nodesToShift == 0) {
            onComplete.run();
            return;
        }

        for (int i = startIndex; i < count; i++) {
            Node node = animationPane.getChildren().get(i);

            // Get current position including any ongoing translation
            double currentActualX = node.getLayoutX() + node.getTranslateX();

            TranslateTransition shift = new TranslateTransition(Duration.seconds(0.5), node);
            shift.setByX(-95);

            final int index = i;
            final double finalCurrentX = currentActualX;

            shift.setOnFinished(e -> {
                // IMPORTANT: Update layoutX to the new permanent position
                // and reset translateX to 0
                node.setLayoutX(finalCurrentX - 95);
                node.setTranslateX(0);

                if (finishedCount.incrementAndGet() == nodesToShift) {
                    onComplete.run();
                }
            });
            shift.play();
        }
    }

 //Dialog for Deleting

    private void showDeleteValueDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Value");
        dialog.setHeaderText("Enter value to Delete:");
        dialog.setContentText("Value:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(value -> {
            try {
                int delValue = Integer.parseInt(value);

                // First, delete from the real linked list and get count
                int deletedCount = list.deleteAllOccurrences(delValue);

                if (deletedCount == 0) {
                    actionListView.getItems().add("Value " + delValue + " not found!");
                    return;
                }

                actionListView.getItems().add("Deleting " + deletedCount + " occurrence(s) of " + delValue);

                // Now delete all occurrences from UI
                deleteAllOccurrencesFromUI(delValue);

                updateSize(list.getSize());

            } catch (NumberFormatException e) {
                actionListView.getItems().add("Please enter a valid number!");
            }
        });
    }
    private void deleteAllOccurrencesFromUI(int delValue) {
        // Collect all positions with the matching value (from end to start to avoid index issues)
        ArrayList<Integer> positionsToDelete = new ArrayList<>();
        for (int i = 0; i < animationPane.getChildren().size(); i++) {
            Node node = animationPane.getChildren().get(i);
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                StackPane stack = (StackPane) hbox.getChildren().get(0);
                Text text = (Text) stack.getChildren().get(1);
                if (Integer.parseInt(text.getText()) == delValue) {
                    positionsToDelete.add(i);
                }
            }
        }

        // Delete from the end to avoid index shifting issues
        deleteUINodesSequentially(positionsToDelete, positionsToDelete.size() - 1);
    }

    private void deleteUINodesSequentially(ArrayList<Integer> positions, int index) {
        if (index < 0) {
            actionListView.getItems().add("All occurrences deleted from display!");
            return;
        }

        int pos = positions.get(index);

        // Delete this node from UI
        Node nodeToDelete = animationPane.getChildren().get(pos);

        // Fade out animation
        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), nodeToDelete);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);

        final int currentPos = pos;
        fade.setOnFinished(e -> {
            // Remove node from pane
            animationPane.getChildren().remove(currentPos);

            // Update head if we deleted the first node
            if (currentPos == 0 && !animationPane.getChildren().isEmpty()) {
                updateHead((HBox) animationPane.getChildren().get(0));
            }

            // Update tail if we deleted the last node
            if (currentPos == animationPane.getChildren().size()) {
                if (!animationPane.getChildren().isEmpty()) {
                    HBox newTail = (HBox) animationPane.getChildren().get(animationPane.getChildren().size() - 1);

                    // Remove any existing arrow/NULL at the end
                    if (newTail.getChildren().size() > 2) {
                        newTail.getChildren().remove(2);
                    }

                    // Create and add NULL box for the new tail
                    Rectangle nullTextRect = new Rectangle(60, 40, Color.LIGHTYELLOW);
                    nullTextRect.setArcWidth(10);
                    nullTextRect.setArcHeight(10);

                    Text nullText = new Text("NULL");
                    nullText.setFont(Font.font(15));
                    nullText.setFill(Color.DARKBLUE);

                    StackPane NullStack = new StackPane();
                    NullStack.getChildren().addAll(nullTextRect, nullText);
                    NullStack.setPrefSize(nullTextRect.getWidth(), nullTextRect.getHeight());

                    newTail.getChildren().add(NullStack);
                    updateTail(newTail);
                } else {
                    tailHBox = null;
                    headHBox = null;
                }
            }

            // Shift remaining nodes left
            shiftNodesLeftFromIndexFixed(currentPos, () -> {
                double requiredWidth = 50 + (animationPane.getChildren().size() * 100) + 200;
                animationPane.setPrefWidth(Math.max(740, requiredWidth));

                // Delete next node
                deleteUINodesSequentially(positions, index - 1);
            });
        });

        fade.play();
    }


    private void showDeletePosDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Position");
        dialog.setHeaderText("Enter position to Delete(0-Based):");
        dialog.setContentText("Position:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(value -> {
            try {
                int pos = Integer.parseInt(value);
                deleteAnimation(pos);

            } catch (NumberFormatException e) {
                actionListView.getItems().add("Please enter a valid Position!");
            }
        });
    }
    // beng

    private void deleteAnimation(int pos){
        // Delete Node at pos

        int count = animationPane.getChildren().size();

        if (pos < 0 || pos >= count) return;

        Node nodeToDelete = animationPane.getChildren().get(pos);

        // 1️⃣ Fade out animation
        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), nodeToDelete);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);

        fade.setOnFinished(e -> {

            // 2️⃣ Remove node from pane AFTER fade completes
            animationPane.getChildren().remove(pos);

            // 🔹 Delete from real linked list
            list.deleteNodeAtPosition(pos);

            // 🔹 Update head/tail if needed
            if (pos == 0) { // deleted head
                if (!animationPane.getChildren().isEmpty()) {
                    updateHead((HBox) animationPane.getChildren().get(0));
                } else {
                    headHBox = null;
                }
            }

            if (pos == count - 1) { // deleted tail
                        if (!animationPane.getChildren().isEmpty()) {
                            // Get the new tail (last node)
                            HBox newTail = (HBox) animationPane.getChildren().get(animationPane.getChildren().size() - 1);

                            // Remove any existing arrow/NULL at the end
                            if (newTail.getChildren().size() > 2) {
                                newTail.getChildren().remove(2);
                            }

                            // Create and add NULL box for the new tail
                            Rectangle nullTextRect = new Rectangle(60, 40, Color.LIGHTYELLOW);
                            nullTextRect.setArcWidth(10);
                            nullTextRect.setArcHeight(10);

                            Text nullText = new Text("NULL");
                            nullText.setFont(Font.font(15));
                            nullText.setFill(Color.DARKBLUE);

                            StackPane NullStack = new StackPane();
                            NullStack.getChildren().addAll(nullTextRect, nullText);
                            NullStack.setPrefSize(nullTextRect.getWidth(), nullTextRect.getHeight());

                            newTail.getChildren().add(NullStack);

                            updateTail(newTail);
                } else {
                    tailHBox = null;
                }
            }

            //  CHECK if deleted node was TAIL
            boolean deletedTail = (pos == count - 1);

            // 3️⃣ Shift remaining nodes LEFT starting from same position
            shiftNodesLeftFromIndexFixed(pos, () -> {
                double requiredWidth = 50 + (animationPane.getChildren().size() * 100) + 200;
                animationPane.setPrefWidth(Math.max(740, requiredWidth));
                actionListView.getItems().add("Deleted node at position " + pos);
                updateSize(list.getSize());
            });
        });

        fade.play();
    }

    private void updateAnimation(int position, int newValue) {
        if (animationPane.getChildren().isEmpty()) {
            actionListView.getItems().add("List is empty! Cannot update.");
            return;
        }

        if (position < 0 || position >= animationPane.getChildren().size()) {
            actionListView.getItems().add("Error: Invalid position! Please enter position between 0 and " + (animationPane.getChildren().size() - 1));
            return;
        }

        actionListView.getItems().add("Updating value at position " + position + " to " + newValue);

        // Get the node at the specified position
        HBox nodeToUpdate = (HBox) animationPane.getChildren().get(position);
        StackPane stackPane = (StackPane) nodeToUpdate.getChildren().get(0);
        Text text = (Text) stackPane.getChildren().get(1); // The text showing the value

        int oldValue = Integer.parseInt(text.getText());

        // Highlight the node being updated
        Rectangle rect = (Rectangle) stackPane.getChildren().get(0);

        // Animation sequence
        PauseTransition pause = new PauseTransition(Duration.seconds(0.3));
        pause.setOnFinished(e -> {
            // Change color to indicate update in progress
            rect.setFill(Color.ORANGE);

            PauseTransition pause2 = new PauseTransition(Duration.seconds(0.3));
            pause2.setOnFinished(ev -> {
                // Update the text with new value
                text.setText(String.valueOf(newValue));

                // Blink effect to show update completed
                rect.setFill(Color.LIGHTGREEN);

                Timeline blink = new Timeline(
                        new KeyFrame(Duration.millis(200), evt ->
                                rect.setFill(rect.getFill() == Color.LIGHTGREEN ? Color.YELLOW : Color.LIGHTGREEN)
                        )
                );
                blink.setCycleCount(4);
                blink.setOnFinished(b -> {
                    // Restore original color based on head/tail status
                    resetNodeColor(nodeToUpdate);
                    actionListView.getItems().add("✓ Updated position " + position + " from " + oldValue + " to " + newValue);
                });
                blink.play();
            });
            pause2.play();
        });
        pause.play();

        // Update the real linked list
        boolean success = updateNodeInList(position, newValue);
        if (!success) {
            actionListView.getItems().add("Error: Failed to update in linked list!");
        }
    }
    private boolean updateNodeInList(int position, int newValue) {
        if (position < 0 || position >= list.getSize()) {
            return false;
        }

        SinglyLinkedListOperation.Node current = list.getHead();
        for (int i = 0; i < position; i++) {
            current = current.next;
        }

        current.data = newValue;
        return true;
    }
    private void showUpdateDialog() {
        // Creating a custom dialog
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Update at Position");
        dialog.setHeaderText("Enter New Value and Position to Update:");

        // Fixing Button type (OK & Cancel)
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Grid Plan
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField valueField = new TextField();
        valueField.setPromptText("New Value");
        TextField posField = new TextField();
        posField.setPromptText("Position (0-based)");

        grid.add(new Label("New Value:"), 0, 0);
        grid.add(valueField, 1, 0);
        grid.add(new Label("Position:"), 0, 1);
        grid.add(posField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                return new Pair<>(valueField.getText(), posField.getText());
            }
            return null;
        });

        // Dialog show and Result handling
        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(pair -> {
            try {
                int newValue = Integer.parseInt(pair.getKey());
                int position = Integer.parseInt(pair.getValue());

                // Validate position
                int currentSize = list.getSize();

                if (currentSize == 0) {
                    actionListView.getItems().add("Error: List is empty! Nothing to update.");
                    return;
                }

                if (position < 0 || position >= currentSize) {
                    actionListView.getItems().add("Error: Invalid position! Please enter position between 0 and " + (currentSize - 1));
                    return;
                }

                // Call update animation
                updateAnimation(position, newValue);

            } catch (NumberFormatException e) {
                actionListView.getItems().add("Error: Please enter valid integers!");
            }
        });
    }

    /**
     * Update status label
     */
    private void updateStatus(String status) {
        lblStatus.setText("Status: " + status);
    }

    /**
     * Update size label
     */
    private void updateSize(int size) {
        lblSize.setText("Size: " + size);
    }


    public void initialize() {
        SLLChoiceBox.getItems().addAll(
                "Create",
                "Insert at Head",
                "Insert at Tail",
                "Insert at Position" ,
                "Delete Value",
                "Delete Position",
                "Search",
                "Traverse",
                "Update",
                "Clear"
        );

        SLLChoiceBox.setOnAction(e -> handleChoiceSelection());

        animationPane.getChildren().clear();
        actionListView.getItems().clear();
        headHBox = null;  // head reset

    }



    private void handleChoiceSelection() {
        String selected = SLLChoiceBox.getValue();

        if (selected == null) return;
        actionListView.getItems().clear();

        switch (selected) {
            case "Create":
                actionListView.getItems().add("Creating new Linked List...");
                updateStatus("Creating new Linked List...");
                list = new SinglyLinkedListOperation();  // reset real list
                nodeMap.clear();                         // reset mapping
                animationPane.getChildren().clear();     // reset UI
                headHBox = null;
                for(int i = 0 ; i < 4 ; i++)
                {
                    int value = (int)(Math.random() * 100);
                    SinglyLinkedListOperation.Node realNode = list.insertAtTail(value); //LinkedList Logic Updated
                    HBox uiNode = insertAtTailAnimation(value);
                    nodeMap.put(realNode, uiNode);
                }
                updateSize(4);
                break;

            case "Insert at Head":
                actionListView.getItems().add("Insert at Head operation...");
                updateStatus("Insert at Head operation");
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
                updateStatus("Insert at Tail operation");
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

            case "Insert at Position" :
                updateStatus("Insert at Tail operation");
                showInsertAtPosDialog();

                break;
            case "Delete Value":
                actionListView.getItems().add("Delete operation...");
                updateStatus("Delete Value");
                showDeleteValueDialog();
                break;
            case "Delete Position":
                actionListView.getItems().add("Delete operation...");
                updateStatus("Delete Value at Position");
                showDeletePosDialog();
                break;

            case "Search":
                actionListView.getItems().add("Search operation...");
                updateStatus("Searching...");
                showSearchDialog();
                break;

            case "Traverse":
                actionListView.getItems().add("Traverse operation...");
                updateStatus("Traversing...");
                traverseAnimation();
                break;
            case "Update":
                actionListView.getItems().add("Update operation...");
                updateStatus("Updating...");
                showUpdateDialog();
                break;
            case "Clear":
                // Clear main animation pane
                actionListView.getItems().add("Cleared");
                updateStatus("Clearing...");
                animationPane.getChildren().clear();  // ← Clears the pane
                updateSize(0);
                break;
        }
        SLLChoiceBox.setValue(null);
    }
}



