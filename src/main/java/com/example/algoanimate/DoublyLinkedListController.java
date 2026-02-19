package com.example.algoanimate;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.animation.KeyFrame;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.util.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DoublyLinkedListController {

    @FXML
    private Button btnBack;

    @FXML
    private ChoiceBox<String> DLLChoiceBox;

    @FXML
    private AnchorPane animationPane;  // left panel, visual nodes দেখাবে

    @FXML
    private ListView<String> actionListView; // right panel

    private DoublyLinkedListOperation list = new DoublyLinkedListOperation();
    private Map<DoublyLinkedListOperation.Node, HBox> nodeMap = new HashMap<>();
    private HBox headHBox;
    private HBox tailHBox;

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

    // Head tracking and visual marking
    private void markHeadVisually() {
        for (Node node : animationPane.getChildren()) {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                StackPane stack = (StackPane) hbox.getChildren().get(1); // For DLL: [prevArrow, stack, nextArrow]
                Rectangle rect = (Rectangle) stack.getChildren().get(0);
                if (rect.getFill() == Color.DARKGRAY) {
                    rect.setFill(Color.LIGHTYELLOW);
                }
            }
        }

        if (headHBox != null && animationPane.getChildren().contains(headHBox)) {
            StackPane stack = (StackPane) headHBox.getChildren().get(1);
            Rectangle rect = (Rectangle) stack.getChildren().get(0);
            rect.setFill(Color.DARKGRAY); // head আলাদা color
        }
    }

    private void updateHead(HBox newHead) {
        this.headHBox = newHead;
        markHeadVisually();
    }

    private void markTailVisually() {
        for (Node node : animationPane.getChildren()) {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                StackPane stack = (StackPane) hbox.getChildren().get(1);
                Rectangle rect = (Rectangle) stack.getChildren().get(0);
                if (rect.getFill() == Color.GHOSTWHITE) {
                    rect.setFill(Color.LIGHTYELLOW);
                }
            }
        }

        if (tailHBox != null && animationPane.getChildren().contains(tailHBox)) {
            StackPane stack = (StackPane) tailHBox.getChildren().get(1);
            Rectangle rect = (Rectangle) stack.getChildren().get(0);
            rect.setFill(Color.GHOSTWHITE); // tail আলাদা color
        }
    }

    private void updateTail(HBox newTail) {
        this.tailHBox = newTail;
        markTailVisually();
    }

    // Node create function for Doubly Linked List
    private HBox createNodeView(int data, boolean isFirst, boolean isLast) {
        // Main rectangle for data
        Rectangle rect = new Rectangle(60, 40, Color.LIGHTYELLOW);
        rect.setArcWidth(10);
        rect.setArcHeight(10);

        // Text for data
        Text text = new Text(String.valueOf(data));
        text.setFont(Font.font(20));
        text.setFill(Color.DARKBLUE);

        // StackPane to center text on rectangle
        StackPane stack = new StackPane();
        stack.getChildren().addAll(rect, text);
        stack.setPrefSize(rect.getWidth(), rect.getHeight());

        HBox hbox = new HBox();
        hbox.setSpacing(0);
        hbox.setLayoutY(50);

        // Previous arrow (left side) - for doubly linked list
        HBox prevArrowPane = createPrevArrow();

        // Next arrow (right side)
        HBox nextArrowPane = createNextArrow();

        if (isFirst && isLast) {
            // Only node - both arrows point to null
            HBox prevNullBox = createNullBox();
            HBox nextNullBox = createNullBox();
            hbox.getChildren().addAll(prevNullBox, stack, nextNullBox);
        } else if (isFirst) {
            // First node - prev points to null, next points to next node
            HBox prevNullBox = createNullBox();
            hbox.getChildren().addAll(prevNullBox, stack, nextArrowPane);
        } else if (isLast) {
            // Last node - prev points to previous node, next points to null
            HBox nextNullBox = createNullBox();
            hbox.getChildren().addAll(prevArrowPane, stack, nextNullBox);
        } else {
            // Middle node - both arrows point to adjacent nodes
            hbox.getChildren().addAll(prevArrowPane, stack, nextArrowPane);
        }

        return hbox;
    }

    private HBox createPrevArrow() {
        // Left-pointing arrow for previous
        Line line = new Line(20, 0, 0, 0);
        line.setStroke(Color.WHITESMOKE);
        line.setStrokeWidth(2);

        Polygon arrowHead = new Polygon();
        arrowHead.getPoints().addAll(
                0.0, 0.0,    // tip (left)
                10.0, -5.0,   // top
                10.0, 5.0     // bottom
        );
        arrowHead.setFill(Color.WHITESMOKE);

        HBox arrowPane = new HBox(arrowHead, line);
        arrowPane.setAlignment(Pos.CENTER_LEFT);
        arrowPane.setPrefHeight(40);
        arrowPane.setSpacing(0);
        arrowPane.setMinWidth(30);
        return arrowPane;
    }

    private HBox createNextArrow() {
        // Right-pointing arrow for next
        Line line = new Line(0, 0, 20, 0);
        line.setStroke(Color.WHITESMOKE);
        line.setStrokeWidth(2);

        Polygon arrowHead = new Polygon();
        arrowHead.getPoints().addAll(
                50.0, 0.0,    // tip
                40.0, -5.0,    // top
                40.0, 5.0      // bottom
        );
        arrowHead.setFill(Color.WHITESMOKE);

        HBox arrowPane = new HBox(line, arrowHead);
        arrowPane.setAlignment(Pos.CENTER_LEFT);
        arrowPane.setPrefHeight(40);
        arrowPane.setSpacing(0);
        arrowPane.setMinWidth(30);
        return arrowPane;
    }

    private HBox createNullBox() {
        Rectangle nullRect = new Rectangle(30, 40, Color.LIGHTYELLOW);
        nullRect.setArcWidth(10);
        nullRect.setArcHeight(10);

        Text nullText = new Text("null");
        nullText.setFont(Font.font(12));
        nullText.setFill(Color.DARKBLUE);

        StackPane nullStack = new StackPane();
        nullStack.getChildren().addAll(nullRect, nullText);
        nullStack.setPrefSize(30, 40);

        HBox nullBox = new HBox(nullStack);
        nullBox.setAlignment(Pos.CENTER);
        nullBox.setMinWidth(30);
        return nullBox;
    }

    // Insert at Head Animation
    private HBox insertAtHeadAnimation(int data) {
        boolean isFirst = animationPane.getChildren().isEmpty();
        boolean isLast = isFirst;

        HBox newNode = createNodeView(data, true, isLast);

        newNode.setLayoutY(50);
        newNode.setLayoutX(50);

        if (!animationPane.getChildren().isEmpty()) {
            shiftNodesRightFromIndexFixed(0, () -> {
                animationPane.getChildren().add(0, newNode);
                updateHead(newNode);

                // Update arrows for adjacent nodes
                if (animationPane.getChildren().size() > 1) {
                    updateAdjacentArrows(1);
                }

                FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), newNode);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();
            });
        } else {
            animationPane.getChildren().add(newNode);
            updateHead(newNode);
            updateTail(newNode);
        }

        return newNode;
    }


    // Insert at Tail Animation
    private HBox insertAtTailAnimation(int data) {
        boolean isLast = true;
        boolean isFirst = animationPane.getChildren().isEmpty();

        HBox newNode = createNodeView(data, isFirst, isLast);

        if (!animationPane.getChildren().isEmpty()) {
            // Update the previous last node's next arrow
            int lastIndex = animationPane.getChildren().size() - 1;
            HBox oldLastNode = (HBox) animationPane.getChildren().get(lastIndex);
            replaceNullWithArrow(oldLastNode, false); // false = next arrow side
        }

        double x = 140;
        for (Node node : animationPane.getChildren()) {
            x += 95;
        }

        newNode.setLayoutX(-100);
        animationPane.getChildren().add(newNode);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(1), newNode);
        tt.setToX(x);
        tt.setOnFinished(e -> {
            updateTail(newNode);

            // Update previous node's next arrow if needed
            if (animationPane.getChildren().size() > 1) {
                updateAdjacentArrows(animationPane.getChildren().size() - 2);
            }
        });
        tt.play();

        return newNode;
    }

    private void replaceNullWithArrow(HBox node, boolean isPrevSide) {
        if (node.getChildren().size() < 3) return;

        int index = isPrevSide ? 0 : 2; // 0 = prev arrow, 2 = next arrow

        if (node.getChildren().get(index) instanceof HBox) {
            HBox currentBox = (HBox) node.getChildren().get(index);
            if (currentBox.getChildren().get(0) instanceof StackPane) {
                // It's a null box, replace with arrow
                HBox newArrow = isPrevSide ? createPrevArrow() : createNextArrow();
                node.getChildren().set(index, newArrow);
            }
        }
    }

    private void updateAdjacentArrows(int nodeIndex) {
        if (nodeIndex < 0 || nodeIndex >= animationPane.getChildren().size()) return;

        HBox currentNode = (HBox) animationPane.getChildren().get(nodeIndex);

        // Update prev arrow (index 0)
        if (nodeIndex > 0) {
            if (!(currentNode.getChildren().get(0) instanceof HBox) ||
                    !(((HBox) currentNode.getChildren().get(0)).getChildren().get(0) instanceof Line)) {
                currentNode.getChildren().set(0, createPrevArrow());
            }
        }

        // Update next arrow (index 2)
        if (nodeIndex < animationPane.getChildren().size() - 1) {
            if (!(currentNode.getChildren().get(2) instanceof HBox) ||
                    !(((HBox) currentNode.getChildren().get(2)).getChildren().get(0) instanceof Line)) {
                currentNode.getChildren().set(2, createNextArrow());
            }
        }
    }

    // Insert at Position Animation


    private HBox insertAtPosAnimation(int val, int pos) {
        int size = animationPane.getChildren().size();

        // Handle edge cases
        if (pos == 0) {
            return insertAtHeadAnimation(val);
        } else if (pos == size) {
            return insertAtTailAnimation(val);
        } else if (pos > 0 && pos < size) {
            actionListView.getItems().add("Inserting value " + val + " at position: " + pos);

            // ====== Middle insertion ======
            // 1️⃣ Find the previous node in DLL
            DoublyLinkedListOperation.Node prevNode = list.getHead();
            for (int i = 0; i < pos - 1; i++) {
                prevNode = prevNode.next;
            }

            // 2️⃣ Get its corresponding HBox in UI
            HBox prevHBox = nodeMap.get(prevNode);
            if (prevHBox == null) {
                actionListView.getItems().add("Error: Previous node not found in UI!");
                return null;
            }

            // 3️⃣ Calculate positions
            int insertIndex = animationPane.getChildren().indexOf(prevHBox) + 1;

            // Get the current actual X position of previous node
            double prevActualX = prevHBox.getLayoutX() + prevHBox.getTranslateX();
            double spacing = 95; // Match the spacing used in shift methods

            // 4️⃣ Create the new node (not first, not last)
            HBox newNode = createNodeView(val, false, false);
            newNode.setLayoutY(50);
            newNode.setLayoutX(prevActualX + spacing); // Set initial position after previous node

            // 5️⃣ First shift existing nodes to the right
            shiftNodesRightFromIndexFixed(insertIndex, () -> {
                // After shifting, add the new node
                animationPane.getChildren().add(insertIndex, newNode);

                // Update its position to be exactly after previous node
                newNode.setLayoutX(prevActualX + spacing);
                newNode.setTranslateX(0);

                // Fade in animation
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), newNode);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();

                // 6️⃣ Update adjacent arrows
                if (pos - 1 >= 0) updateAdjacentArrows(pos - 1);
                updateAdjacentArrows(pos);
                if (pos + 1 < animationPane.getChildren().size()) updateAdjacentArrows(pos + 1);

                actionListView.getItems().add("✓ Inserted " + val + " at position " + pos);
            });

            return newNode;
        } else {
            actionListView.getItems().add("Error: Invalid position!");
            return null;
        }
    }


    // Shift methods
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
            double currentActualX = node.getLayoutX() + node.getTranslateX();

            TranslateTransition shift = new TranslateTransition(Duration.seconds(0.5), node);
            shift.setByX(95);

            final int index = i;
            final double finalCurrentX = currentActualX;

            shift.setOnFinished(e -> {
                node.setLayoutX(finalCurrentX + 95);
                node.setTranslateX(0);

                if (finishedCount.incrementAndGet() == nodesToShift) {
                    onComplete.run();
                }
            });
            shift.play();
        }
    }

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
            double currentActualX = node.getLayoutX() + node.getTranslateX();

            TranslateTransition shift = new TranslateTransition(Duration.seconds(0.5), node);
            shift.setByX(-95);

            final int index = i;
            final double finalCurrentX = currentActualX;

            shift.setOnFinished(e -> {
                node.setLayoutX(finalCurrentX - 95);
                node.setTranslateX(0);

                if (finishedCount.incrementAndGet() == nodesToShift) {
                    onComplete.run();
                }
            });
            shift.play();
        }
    }

    // Dialog methods
    private void showInsertAtHeadDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Insert At Head Value");
        dialog.setHeaderText("Enter value to insert at Head:");
        dialog.setContentText("Value:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(value -> {
            try {
                int insertValue = Integer.parseInt(value);
                DoublyLinkedListOperation.Node realNode = list.insertAtHead(insertValue);
                HBox uiNode = insertAtHeadAnimation(insertValue);
                nodeMap.put(realNode, uiNode);
            } catch (NumberFormatException e) {
                actionListView.getItems().add("Please enter a valid number!");
            }
        });
    }

    private void showInsertAtTailDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Insert At Tail Value");
        dialog.setHeaderText("Enter value to insert at Tail:");
        dialog.setContentText("Value:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(value -> {
            try {
                int insertValue = Integer.parseInt(value);
                DoublyLinkedListOperation.Node realNode = list.insertAtTail(insertValue);
                HBox uiNode = insertAtTailAnimation(insertValue);
                nodeMap.put(realNode, uiNode);
            } catch (NumberFormatException e) {
                actionListView.getItems().add("Please enter a valid number!");
            }
        });
    }

    private void showInsertAtPosDialog() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Insert at Position");
        dialog.setHeaderText("Enter Value and Position to Insert (0-based):");

        ButtonType insertButtonType = new ButtonType("Insert", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(insertButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField valueField = new TextField();
        valueField.setPromptText("Value");
        TextField posField = new TextField();
        posField.setPromptText("Position");

        grid.add(new Label("Value:"), 0, 0);
        grid.add(valueField, 1, 0);
        grid.add(new Label("Position:"), 0, 1);
        grid.add(posField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == insertButtonType) {
                return new Pair<>(valueField.getText(), posField.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(pair -> {
            try {
                int value = Integer.parseInt(pair.getKey());
                int position = Integer.parseInt(pair.getValue());
                HBox uiNode;
                DoublyLinkedListOperation.Node realNode;

                int currentSize = list.getSize();

                if (position == 0) {
                    realNode = list.insertAtHead(value);
                    uiNode = insertAtHeadAnimation(value);
                } else if (position == currentSize) {
                    realNode = list.insertAtTail(value);
                    uiNode = insertAtTailAnimation(value);
                } else if (position > 0 && position < currentSize) {
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

    // Search Animation
    private void searchAnimation(int searchValue) {
        if (animationPane.getChildren().isEmpty()) {
            actionListView.getItems().add("List is empty! Nothing to search.");
            return;
        }

        actionListView.getItems().add("Searching for value: " + searchValue);

        DoublyLinkedListOperation.Node headNode = list.getHead();
        AtomicBoolean foundFlag = new AtomicBoolean(false);
        searchNext(headNode, 0, searchValue, foundFlag);
    }

    private void searchNext(DoublyLinkedListOperation.Node current, int index, int searchValue, AtomicBoolean foundFlag) {
        if (current == null) {
            if (!foundFlag.get()) {
                actionListView.getItems().add("✗ Value " + searchValue + " not found in the list");
            }
            actionListView.getItems().add("Search Operation successful!");
            return;
        }

        HBox uiNode = nodeMap.get(current);
        StackPane stackPane = (StackPane) uiNode.getChildren().get(1);
        Rectangle rect = (Rectangle) stackPane.getChildren().get(0);
        int value = current.data;

        PauseTransition pause = new PauseTransition(Duration.seconds(1.0));
        pause.setOnFinished(e -> {
            rect.setFill(Color.ORANGE);
            actionListView.getItems().add("Checking node with value: " + value);

            PauseTransition revert = new PauseTransition(Duration.seconds(0.5));
            revert.setOnFinished(ev -> {
                if (value == searchValue) {
                    foundFlag.set(true);
                    rect.setFill(Color.LIGHTGREEN);
                    actionListView.getItems().add("✓ Found value " + searchValue + " at position " + index);

                    Timeline blink = new Timeline(
                            new KeyFrame(Duration.millis(300), evt ->
                                    rect.setFill(rect.getFill() == Color.LIGHTGREEN ? Color.YELLOW : Color.LIGHTGREEN)
                            )
                    );
                    blink.setCycleCount(6);
                    blink.setOnFinished(b -> {
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

    private void resetNodeColor(HBox uiNode) {
        StackPane stack = (StackPane) uiNode.getChildren().get(1);
        Rectangle rect = (Rectangle) stack.getChildren().get(0);

        if (uiNode == headHBox) {
            rect.setFill(Color.DARKGRAY);
        } else if (uiNode == tailHBox) {
            rect.setFill(Color.GHOSTWHITE);
        } else {
            rect.setFill(Color.LIGHTYELLOW);
        }
    }

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

    // Traverse Animation
    private void traverseAnimation() {
        if (animationPane.getChildren().isEmpty()) {
            actionListView.getItems().add("List is empty! Nothing to traverse.");
            return;
        }

        DoublyLinkedListOperation.Node current = list.getHead();
        actionListView.getItems().add("Traversing the List Forward...\n");
        traverseNext(current, 0);
    }

    private void traverseNext(DoublyLinkedListOperation.Node current, int index) {
        if (current == null) {
            actionListView.getItems().add("Total Nodes: " + index + "\n");
            actionListView.getItems().add("Traversal Complete Successfully!");
            return;
        }

        HBox uiNode = nodeMap.get(current);
        StackPane stackPane = (StackPane) uiNode.getChildren().get(1);
        Rectangle rect = (Rectangle) stackPane.getChildren().get(0);
        int value = current.data;
        final int idx = index;
        final DoublyLinkedListOperation.Node curr = current;

        PauseTransition pause = new PauseTransition(Duration.seconds(1.0));
        pause.setOnFinished(e -> {
            rect.setFill(Color.ORANGE);
            actionListView.getItems().add("Position " + idx + " : " + value);

            PauseTransition revert = new PauseTransition(Duration.seconds(0.5));
            revert.setOnFinished(ev -> {
                resetNodeColor(uiNode);
                traverseNext(curr.next, idx + 1);
            });
            revert.play();
        });
        pause.play();
    }

    // Delete methods
    private void showDeleteValueDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Value");
        dialog.setHeaderText("Enter value to Delete:");
        dialog.setContentText("Value:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(value -> {
            try {
                int delValue = Integer.parseInt(value);
                int pos = findFirstOccurrence(delValue);
                if (pos != -1) {
                    deleteAnimation(pos);
                } else {
                    actionListView.getItems().add("Value " + delValue + " not found in the list!");
                }
            } catch (NumberFormatException e) {
                actionListView.getItems().add("Please enter a valid number!");
            }
        });
    }

    private void showDeletePosDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Position");
        dialog.setHeaderText("Enter position to Delete (0-based):");
        dialog.setContentText("Position:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(value -> {
            try {
                int pos = Integer.parseInt(value);
                if (pos >= 0 && pos < animationPane.getChildren().size()) {
                    deleteAnimation(pos);
                } else {
                    actionListView.getItems().add("Invalid position!");
                }
            } catch (NumberFormatException e) {
                actionListView.getItems().add("Please enter a valid Position!");
            }
        });
    }

    private int findFirstOccurrence(int value) {
        for (int i = 0; i < animationPane.getChildren().size(); i++) {
            Node node = animationPane.getChildren().get(i);
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                StackPane stack = (StackPane) hbox.getChildren().get(1);
                Text text = (Text) stack.getChildren().get(1);
                if (Integer.parseInt(text.getText()) == value) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void deleteAnimation(int pos) {
        int count = animationPane.getChildren().size();
        if (pos < 0 || pos >= count) return;

        Node nodeToDelete = animationPane.getChildren().get(pos);

        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), nodeToDelete);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);

        fade.setOnFinished(e -> {
            animationPane.getChildren().remove(pos);
            list.deleteNodeAtPosition(pos);

            // Update arrows for adjacent nodes
            if (pos > 0) updateAdjacentArrows(pos - 1);
            if (pos < animationPane.getChildren().size()) updateAdjacentArrows(pos);

            if (pos == 0) { // deleted head
                if (!animationPane.getChildren().isEmpty()) {
                    // New head should have NULL on prev side
                    HBox newHead = (HBox) animationPane.getChildren().get(0);
                    if (newHead.getChildren().size() > 0) {
                        newHead.getChildren().set(0, createNullBox()); // Set prev to NULL
                    }
                    updateHead(newHead);
                } else {
                    headHBox = null;
                    tailHBox = null;
                }
            }

            if (pos == count - 1) { // deleted tail
                if (!animationPane.getChildren().isEmpty()) {
                    updateTail((HBox) animationPane.getChildren().get(animationPane.getChildren().size() - 1));
                } else {
                    tailHBox = null;
                }
            }

            boolean deletedTail = (pos == count - 1);

            shiftNodesLeftFromIndexFixed(pos, () -> {
                if (deletedTail && !animationPane.getChildren().isEmpty()) {
                    // Add null at the end of new tail
                    HBox newTail = (HBox) animationPane.getChildren().get(animationPane.getChildren().size() - 1);
                    if (newTail.getChildren().size() > 2) {
                        newTail.getChildren().set(2, createNullBox());
                    }
                }
                actionListView.getItems().add("Deleted node at position " + pos);
            });
        });
        fade.play();
    }

    // Initialize method
    public void initialize() {
        DLLChoiceBox.getItems().addAll(
                "Create",
                "Insert at Head",
                "Insert at Tail",
                "Insert at Position",
                "Delete Value",
                "Delete Position",
                "Search",
                "Traverse"
        );

        DLLChoiceBox.setOnAction(e -> handleChoiceSelection());

        animationPane.getChildren().clear();
        actionListView.getItems().clear();
        headHBox = null;
        tailHBox = null;
    }

    private void handleChoiceSelection() {
        String selected = DLLChoiceBox.getValue();
        actionListView.getItems().clear();

        switch (selected) {
            case "Create":
                actionListView.getItems().add("Creating new Doubly Linked List...");
                list = new DoublyLinkedListOperation();
                nodeMap.clear();
                animationPane.getChildren().clear();
                headHBox = null;
                tailHBox = null;
                for (int i = 0; i < 4; i++) {
                    int value = (int) (Math.random() * 100);
                    DoublyLinkedListOperation.Node realNode = list.insertAtTail(value);
                    HBox uiNode = insertAtTailAnimation(value);
                    nodeMap.put(realNode, uiNode);
                }
                break;

            case "Insert at Head":
                actionListView.getItems().add("Insert at Head operation...");
                actionListView.getItems().add(
                        "private static class Node {\n" +
                                "    int data;\n" +
                                "    Node prev;\n" +
                                "    Node next;\n" +
                                "\n" +
                                "    Node(int data) {\n" +
                                "        this.data = data;\n" +
                                "        this.prev = null;\n" +
                                "        this.next = null;\n" +
                                "    }\n" +
                                "}\n" +
                                "\n" +
                                "private Node head;\n" +
                                "private Node tail;\n" +
                                "\n" +
                                "public void insertAtHead(int data) {\n" +
                                "    Node newNode = new Node(data);\n" +
                                "    if (head == null) {\n" +
                                "        head = tail = newNode;\n" +
                                "    } else {\n" +
                                "        newNode.next = head;\n" +
                                "        head.prev = newNode;\n" +
                                "        head = newNode;\n" +
                                "    }\n" +
                                "}");
                showInsertAtHeadDialog();
                break;

            case "Insert at Tail":
                actionListView.getItems().add("Insert at Tail operation...");
                actionListView.getItems().add(
                        "public void insertAtTail(int data) {\n" +
                                "    Node newNode = new Node(data);\n" +
                                "    if (head == null) {\n" +
                                "        head = tail = newNode;\n" +
                                "    } else {\n" +
                                "        tail.next = newNode;\n" +
                                "        newNode.prev = tail;\n" +
                                "        tail = newNode;\n" +
                                "    }\n" +
                                "}");
                showInsertAtTailDialog();
                break;

            case "Insert at Position":
                showInsertAtPosDialog();
                break;

            case "Delete Value":
                actionListView.getItems().add("Delete operation by value");
                showDeleteValueDialog();
                break;

            case "Delete Position":
                actionListView.getItems().add("Delete operation by position");
                showDeletePosDialog();
                break;

            case "Search":
                actionListView.getItems().add("Search operation");
                showSearchDialog();
                break;

            case "Traverse":
                actionListView.getItems().add("Traverse operation");
                traverseAnimation();
                break;
        }
    }
}