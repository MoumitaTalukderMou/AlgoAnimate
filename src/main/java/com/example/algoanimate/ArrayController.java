package com.example.algoanimate;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class ArrayController {
    @FXML
    private Button btnBack;
    @FXML
    private ChoiceBox<String> ArrayChoiceBox;
    @FXML
    private Pane animationPane;
    @FXML
    private ListView<String> actionListView;
    @FXML
    private ListView<String> nodeListView;
    @FXML
    private Label lblStatus;
    @FXML
    private Label lblSize;

    // Data structure to store array elements
    private ArrayList<Integer> arrayList = new ArrayList<>();
    private int arrayCapacity = 0;
    private HBox arrayContainer;
    private boolean isSearching = false;
    private boolean isTraversing = false;

    @FXML
    private void onBackClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(scene);

            stage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        ArrayChoiceBox.getItems().addAll(
                "Create",
                "Append",
                "Insert",
                "Delete Value",
                "Delete Position",
                "Search",
                "Traverse",
                "Update",
                "Increase Capacity",
                "Reset Array",
                "Clear"
        );



        ArrayChoiceBox.setOnAction(e -> handleChoiceSelection());

        // Initialize array container
        arrayContainer = new HBox(10); // 10px spacing between boxes
        arrayContainer.setAlignment(Pos.CENTER);
        arrayContainer.setLayoutX(50);
        arrayContainer.setLayoutY(200);
        animationPane.getChildren().add(arrayContainer);
        updateSize();
    }

    private void handleChoiceSelection() {
        String selected = ArrayChoiceBox.getValue();
        if (selected == null) return;
        actionListView.getItems().clear();

        switch (selected) {
            case "Create":
                actionListView.getItems().add("Creating new Array...");
                updateStatus("Creating new Array");
                showCreateArrayDialog();
                break;
            case "Append":
                actionListView.getItems().add("Appending Value...");
                updateStatus("Append");
                showAppendDialog();
                break;
            case "Insert":
                actionListView.getItems().add("Inserting Value at Position...");
                updateStatus("Insert");
                showInsertDialog();
                break;
            case "Delete Value":
                actionListView.getItems().add("Deleting Value...");
                updateStatus("Delete Value");
                showDeleteValueDialog();
                break;
            case "Delete Position":
                actionListView.getItems().add("Deleting Value at Position...");
                updateStatus("Delete Position");
                showDeletePositionDialog();
                break;
            case "Search":
                actionListView.getItems().add("Searching...");
                updateStatus("Searching");
                showSearchDialog();
                break;
            case "Traverse":
                actionListView.getItems().add("Traversing...");
                updateStatus("Traversing");
                traverseArray();
                break;
            case "Update":
                actionListView.getItems().add("Updating value at a certain position...");
                updateStatus("Updating");
                showUpdateDialog();
                break;
            case "Increase Capacity":
                actionListView.getItems().add("Increasing capacity...");
                updateStatus("Increasing capacity");
                increaseArrayVisualization();
                break;
            case "Reset Array":
                actionListView.getItems().add("Array Reseting to null...");
                updateStatus("Reset Array");
                clearArray();
                break;
            case "Clear":
                actionListView.getItems().add("Cleared");
                updateStatus("Cleared");
                animationPane.getChildren().clear();
                break;
        }
        ArrayChoiceBox.setValue(null);
    }

    private void showCreateArrayDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Array");
        dialog.setHeaderText("Enter the Array Capacity:");
        dialog.setContentText("Capacity:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(value -> {
            try {
                int capacity = Integer.parseInt(value);
                if (capacity <= 0) {
                    actionListView.getItems().add("Please enter a positive number!");
                    return;
                }
                if (capacity > 500) {
                    actionListView.getItems().add("Maximum capacity is 20!");
                    return;
                }
                arrayCapacity = capacity;
                arrayList.clear(); // Clear existing data
                createArrayVisualization();
                actionListView.getItems().add("Array created with capacity: " + capacity);
                updateSize();
            } catch (NumberFormatException e) {
                actionListView.getItems().add("Please enter a valid number!");
            }
        });
    }

    private void createArrayVisualization() {
        // Clear existing boxes
        arrayContainer.getChildren().clear();

        // Create boxes for each position
        for (int i = 0; i < arrayCapacity; i++) {
            VBox cell = createArrayCell(i);
            arrayContainer.getChildren().add(cell);
        }
        double requiredWidth = 50 + (arrayCapacity * 50) + 200;
        if (requiredWidth > animationPane.getPrefWidth()) {
            animationPane.setPrefWidth(requiredWidth);
        }
    }

    private void increaseArrayVisualization() {
        if (arrayCapacity == 0) {
            actionListView.getItems().add("Please create an array first!");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(String.valueOf(arrayCapacity));
        dialog.setTitle("Increase Capacity");
        dialog.setHeaderText("Current capacity: " + arrayCapacity);
        dialog.setContentText("Enter new capacity:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(value -> {
            try {
                int newCapacity = Integer.parseInt(value);

                if (newCapacity <= arrayCapacity) {
                    actionListView.getItems().add("New capacity must be greater than current capacity!");
                    return;
                }
                if (newCapacity > 500) {
                    actionListView.getItems().add("Maximum capacity is 20!");
                    return;
                }

                // Store the old capacity
                int oldCapacity = arrayCapacity;

                // Add new empty boxes to the existing visualization
                for (int i = oldCapacity; i < newCapacity; i++) {
                    VBox cell = createArrayCell(i);

                    // Highlight new cells briefly
                    cell.setStyle("-fx-border-color: green; -fx-border-width: 3; -fx-padding: 10; -fx-background-color: lightgreen;");

                    arrayContainer.getChildren().add(cell);

                    // Reset after a short delay
                    final int index = i;
                    new Thread(() -> {
                        try {
                            Thread.sleep(300);
                            javafx.application.Platform.runLater(() -> {
                                if (index < arrayContainer.getChildren().size()) {
                                    VBox addedCell = (VBox) arrayContainer.getChildren().get(index);
                                    addedCell.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 10; -fx-background-color: lightyellow;");
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }

                // Update the capacity
                arrayCapacity = newCapacity;
                double requiredWidth = 50 + (arrayCapacity * 50) + 200;
                if (requiredWidth > animationPane.getPrefWidth()) {
                    animationPane.setPrefWidth(requiredWidth);
                }


                actionListView.getItems().add("Array capacity increased from " + oldCapacity + " to: " + arrayCapacity);
                actionListView.getItems().add("Added " + (newCapacity - oldCapacity) + " new empty positions");

            } catch (NumberFormatException e) {
                actionListView.getItems().add("Please enter a valid number!");
            }
        });
    }

    private VBox createArrayCell(int index) {
        VBox cell = new VBox(5);
        cell.setAlignment(Pos.CENTER);
        cell.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 10; -fx-background-color: lightyellow;");
        cell.setPrefWidth(30);
        cell.setPrefHeight(60);

        // Index label
        Text indexText = new Text("[" + index + "]");
        indexText.setStyle("-fx-font-weight: bold;");

        // Value display (empty initially)
        Text valueText = new Text("null");
        valueText.setStyle("-fx-fill: gray;");

        cell.getChildren().addAll(indexText, valueText);

        // Add hover effect
        cell.setOnMouseEntered(e ->
                cell.setStyle("-fx-border-color: blue; -fx-border-width: 3; -fx-padding: 10; -fx-background-color: lightblue;"));
        cell.setOnMouseExited(e ->
                cell.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 10; -fx-background-color: lightyellow;"));

        return cell;
    }

    private void showAppendDialog() {
        if (arrayCapacity == 0) {
            actionListView.getItems().add("Please create an array first!");
            return;
        }

        if (arrayList.size() >= arrayCapacity) {
            actionListView.getItems().add("Array is full! Cannot append.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Append Value");
        dialog.setHeaderText("Enter value to append:");
        dialog.setContentText("Value:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(value -> {
            try {
                int intValue = Integer.parseInt(value);
                int position = arrayList.size();
                arrayList.add(intValue);
                updateCellValue(position, intValue);
                highlightCellWithAnimation(position, "green", 1000);
                actionListView.getItems().add("Appended value " + intValue + " at position " + position);
                updateSize();
            } catch (NumberFormatException e) {
                actionListView.getItems().add("Please enter a valid number!");
            }
        });
    }

    private void showInsertDialog() {
        if (arrayCapacity == 0) {
            actionListView.getItems().add("Please create an array first!");
            return;
        }

        if (arrayList.size() >= arrayCapacity) {
            actionListView.getItems().add("Array is full! Cannot insert.");
            return;
        }

        // First dialog for position
        TextInputDialog posDialog = new TextInputDialog();
        posDialog.setTitle("Insert Value");
        posDialog.setHeaderText("Enter position to insert (0 to " + arrayList.size() + "):");
        posDialog.setContentText("Position:");

        Optional<String> posResult = posDialog.showAndWait();

        posResult.ifPresent(posValue -> {
            try {
                int position = Integer.parseInt(posValue);
                if (position < 0 || position > arrayList.size()) {
                    actionListView.getItems().add("Invalid position! Must be between 0 and " + arrayList.size());
                    return;
                }

                // Second dialog for value
                TextInputDialog valDialog = new TextInputDialog();
                valDialog.setTitle("Insert Value");
                valDialog.setHeaderText("Enter value to insert at position " + position + ":");
                valDialog.setContentText("Value:");

                Optional<String> valResult = valDialog.showAndWait();

                valResult.ifPresent(valValue -> {
                    try {
                        int intValue = Integer.parseInt(valValue);

                        // First, animate right shift to create empty space
                        animateShiftRightForInsert(position, intValue);


                    } catch (NumberFormatException e) {
                        actionListView.getItems().add("Please enter a valid number!");
                    }
                });

            } catch (NumberFormatException e) {
                actionListView.getItems().add("Please enter a valid position!");
            }
        });

    }

    private void showDeleteValueDialog() {
        if (arrayList.isEmpty()) {
            actionListView.getItems().add("Array is empty!");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Value");
        dialog.setHeaderText("Enter value to delete:");
        dialog.setContentText("Value:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(value -> {
            try {
                int intValue = Integer.parseInt(value);

                // Find all occurrences
                ArrayList<Integer> positions = new ArrayList<>();
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i) == intValue) {
                        positions.add(i);
                    }
                }

                if (positions.isEmpty()) {
                    actionListView.getItems().add("Value " + intValue + " not found in array!");
                    return;
                }

                // Delete from last position to first to avoid index shifting issues
                for (int i = positions.size() - 1; i >= 0; i--) {
                    int pos = positions.get(i);

                    // Animate deletion for this position
                    animateDeleteWithLeftShift(pos, intValue);
                }

            } catch (NumberFormatException e) {
                actionListView.getItems().add("Please enter a valid number!");
            }
        });

    }

    private void showDeletePositionDialog() {
        if (arrayList.isEmpty()) {
            actionListView.getItems().add("Array is empty!");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete at Position");
        dialog.setHeaderText("Enter position to delete (0 to " + (arrayList.size() - 1) + "):");
        dialog.setContentText("Position:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(value -> {
            try {
                int position = Integer.parseInt(value);
                if (position < 0 || position >= arrayList.size()) {
                    actionListView.getItems().add("Invalid position! Must be between 0 and " + (arrayList.size() - 1));
                    return;
                }

                // Animate deletion for this position
                animateDeleteWithLeftShift(position, arrayList.get(position));

            } catch (NumberFormatException e) {
                actionListView.getItems().add("Please enter a valid number!");
            }
        });
        updateSize();
    }

    private void animateDeleteWithLeftShift(int deletePos, int deletedValue) {
        // First, highlight the cell to be deleted and make it empty
        if (deletePos < arrayContainer.getChildren().size()) {
            VBox cellToDelete = (VBox) arrayContainer.getChildren().get(deletePos);

            // Highlight in red
            cellToDelete.setStyle("-fx-border-color: red; -fx-border-width: 3; -fx-padding: 10; -fx-background-color: lightcoral;");

            // Make the cell appear empty
            Text valueText = (Text) cellToDelete.getChildren().get(1);
            String oldValue = valueText.getText();
            valueText.setText("null");
            valueText.setStyle("-fx-fill: gray;");

            actionListView.getItems().add("Position " + deletePos + " emptied");
        }

        // Create a sequential animation for shifting elements left one by one
        SequentialTransition shiftAnimation = new SequentialTransition();

        // For each element after the deleted position, shift it left
        for (int i = deletePos + 1; i < arrayList.size(); i++) {
            final int currentIndex = i;
            final int targetIndex = i - 1;

            if (currentIndex < arrayContainer.getChildren().size()) {
                VBox movingCell = (VBox) arrayContainer.getChildren().get(currentIndex);
                VBox targetCell = (VBox) arrayContainer.getChildren().get(targetIndex);

                // Get the value from the moving cell
                Text movingText = (Text) movingCell.getChildren().get(1);
                String movingValue = movingText.getText();

                // Create animation for this shift
                ParallelTransition shiftStep = new ParallelTransition();

                // Fade out the moving cell slightly
                FadeTransition fadeOut = new FadeTransition(Duration.millis(200), movingCell);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.3);

                // Move the cell left
                TranslateTransition moveLeft = new TranslateTransition(Duration.millis(300), movingCell);
                moveLeft.setByX(-40); // Move left by cell width + spacing

                // After moving, update the target cell's value
                moveLeft.setOnFinished(e -> {
                    javafx.application.Platform.runLater(() -> {
                        // Update the target cell with the moved value
                        Text targetText = (Text) targetCell.getChildren().get(1);
                        targetText.setText(movingValue);
                        targetText.setStyle("-fx-fill: black; -fx-font-weight: bold;");

                        // Reset the moving cell's position and make it null
                        movingCell.setTranslateX(0);
                        movingText.setText("null");
                        movingText.setStyle("-fx-fill: gray;");

                        // Fade in the moving cell
                        FadeTransition fadeIn = new FadeTransition(Duration.millis(100), movingCell);
                        fadeIn.setFromValue(0.3);
                        fadeIn.setToValue(1.0);
                        fadeIn.play();
                    });
                });

                shiftStep.getChildren().addAll(fadeOut, moveLeft);
                shiftAnimation.getChildren().add(shiftStep);
            }
        }

        // After all shifts are complete, update the data structure and refresh
        shiftAnimation.setOnFinished(e -> {
            javafx.application.Platform.runLater(() -> {
                // Remove the value from arrayList
                arrayList.remove(deletePos);

                // Refresh the visualization to ensure everything is in place
                refreshArrayVisualization();

                actionListView.getItems().add("Deleted value " + deletedValue + " from position " + deletePos);
                updateSize();
            });
        });

        shiftAnimation.play();
    }

    private void animateShiftRightForInsert(int insertPos, int valueToInsert) {
        if (arrayList.size() >= arrayCapacity) {
            actionListView.getItems().add("Array is full! Cannot insert.");
            return;
        }

        // Create a sequential animation for shifting elements right one by one
        SequentialTransition shiftAnimation = new SequentialTransition();

        // Shift elements from the end to the insert position
        for (int i = arrayList.size() - 1; i >= insertPos; i--) {
            final int currentIndex = i;
            final int targetIndex = i + 1;

            if (currentIndex < arrayContainer.getChildren().size() && targetIndex < arrayContainer.getChildren().size()) {
                VBox movingCell = (VBox) arrayContainer.getChildren().get(currentIndex);
                VBox targetCell = (VBox) arrayContainer.getChildren().get(targetIndex);

                // Get the value from the moving cell
                Text movingText = (Text) movingCell.getChildren().get(1);
                String movingValue = movingText.getText();

                // Create animation for this shift
                ParallelTransition shiftStep = new ParallelTransition();

                // Fade out the moving cell slightly
                FadeTransition fadeOut = new FadeTransition(Duration.millis(200), movingCell);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.3);

                // Move the cell right
                TranslateTransition moveRight = new TranslateTransition(Duration.millis(300), movingCell);
                moveRight.setByX(40); // Move right by cell width + spacing

                // After moving, update the target cell's value
                moveRight.setOnFinished(e -> {
                    javafx.application.Platform.runLater(() -> {
                        // Update the target cell with the moved value
                        if (!movingValue.equals("null")) {
                            Text targetText = (Text) targetCell.getChildren().get(1);
                            targetText.setText(movingValue);
                            targetText.setStyle("-fx-fill: black; -fx-font-weight: bold;");
                        }

                        // Reset the moving cell's position
                        movingCell.setTranslateX(0);

                        // Fade in the moving cell
                        FadeTransition fadeIn = new FadeTransition(Duration.millis(100), movingCell);
                        fadeIn.setFromValue(0.3);
                        fadeIn.setToValue(1.0);
                        fadeIn.play();
                    });
                });

                shiftStep.getChildren().addAll(fadeOut, moveRight);
                shiftAnimation.getChildren().add(shiftStep);
            }
        }

        // After all shifts are complete, insert the new value
        shiftAnimation.setOnFinished(e -> {
            javafx.application.Platform.runLater(() -> {
                // Now insert the new value into arrayList
                arrayList.add(insertPos, valueToInsert);

                // Refresh to show the new value at the insert position
                refreshArrayVisualization();

                // Highlight the newly inserted cell
                highlightCellWithAnimation(insertPos, "orange", 1000);

                actionListView.getItems().add("Inserted value " + valueToInsert + " at position " + insertPos);
                updateSize();
            });
        });

        shiftAnimation.play();
    }

    private void showSearchDialog() {
        if (arrayList.isEmpty()) {
            actionListView.getItems().add("Array is empty!");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Value");
        dialog.setHeaderText("Enter value to search:");
        dialog.setContentText("Value:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(value -> {
            try {
                int searchValue = Integer.parseInt(value);
                isSearching = true;

                // Clear previous items and add header
                actionListView.getItems().clear();
                actionListView.getItems().add("🔍 Searching for value: " + searchValue);

                // Store all found positions
                ArrayList<Integer> foundPositions = new ArrayList<>();

                // Create a sequential animation
                SequentialTransition searchAnimation = new SequentialTransition();

                for (int i = 0; i < arrayList.size(); i++) {
                    final int currentIndex = i;
                    int currentValue = arrayList.get(i);

                    // Create pause between checks (500ms)
                    PauseTransition pause = new PauseTransition(Duration.millis(500));

                    // Create animation for this element
                    ParallelTransition checkAnimation = new ParallelTransition();

                    // Highlight the current cell
                    Timeline highlightTimeline = new Timeline(
                            new KeyFrame(Duration.millis(0), e -> {
                                resetAllCellsToDefault();
                                if (currentIndex < arrayContainer.getChildren().size()) {
                                    VBox cell = (VBox) arrayContainer.getChildren().get(currentIndex);
                                    cell.setStyle("-fx-border-color: blue; -fx-border-width: 3; -fx-padding: 10; -fx-background-color: lightblue;");
                                }
                            })
                    );

                    // Check if value matches
                    Timeline checkResultTimeline = new Timeline(
                            new KeyFrame(Duration.millis(300), e -> {
                                if (currentValue == searchValue) {
                                    // Found match
                                    foundPositions.add(currentIndex);

                                    // Green flash for found
                                    if (currentIndex < arrayContainer.getChildren().size()) {
                                        VBox cell = (VBox) arrayContainer.getChildren().get(currentIndex);

                                        Timeline foundFlash = new Timeline(
                                                new KeyFrame(Duration.millis(0), ev ->
                                                        cell.setStyle("-fx-border-color: green; -fx-border-width: 4; -fx-padding: 10; -fx-background-color: lightgreen;")),
                                                new KeyFrame(Duration.millis(300), ev ->
                                                        cell.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 10; -fx-background-color: lightyellow;"))
                                        );
                                        foundFlash.play();
                                    }

                                    actionListView.getItems().add("  ✓ Position " + currentIndex + ": " + currentValue + " = " + searchValue + " [FOUND]");
                                } else {
                                    // Red flash for not found
                                    if (currentIndex < arrayContainer.getChildren().size()) {
                                        VBox cell = (VBox) arrayContainer.getChildren().get(currentIndex);

                                        Timeline notFoundFlash = new Timeline(
                                                new KeyFrame(Duration.millis(0), ev ->
                                                        cell.setStyle("-fx-border-color: red; -fx-border-width: 3; -fx-padding: 10; -fx-background-color: lightcoral;")),
                                                new KeyFrame(Duration.millis(200), ev ->
                                                        cell.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 10; -fx-background-color: lightyellow;"))
                                        );
                                        notFoundFlash.play();
                                    }

                                    actionListView.getItems().add("  → Position " + currentIndex + ": " + currentValue + " ≠ " + searchValue);
                                }
                            })
                    );

                    checkAnimation.getChildren().addAll(highlightTimeline, checkResultTimeline);

                    // Add pause and check to sequential animation
                    searchAnimation.getChildren().add(pause);
                    searchAnimation.getChildren().add(checkAnimation);
                }

                // After all elements are checked
                searchAnimation.setOnFinished(e -> {
                    if (foundPositions.isEmpty()) {
                        actionListView.getItems().add("❌ Value " + searchValue + " not found anywhere in the array!");
                    } else {
                        actionListView.getItems().add("✅ Search complete! Value " + searchValue + " found at position(s): " + foundPositions);

                        // Highlight all found positions one by one
                        highlightFoundPositions(foundPositions);
                    }
                    isSearching = false;
                    resetAllCellsToDefault();
                });

                searchAnimation.play();

            } catch (NumberFormatException e) {
                actionListView.getItems().add("Please enter a valid number!");
            }
        });
    }

    // Helper method to highlight all found positions
    private void highlightFoundPositions(ArrayList<Integer> positions) {
        if (positions.isEmpty()) return;

        actionListView.getItems().add(" Highlighting all found positions:");

        SequentialTransition highlightSequence = new SequentialTransition();

        for (int i = 0; i < positions.size(); i++) {
            final int pos = positions.get(i);

            PauseTransition pause = new PauseTransition(Duration.millis(400));

            ParallelTransition highlightAnimation = new ParallelTransition();

            Timeline flashTimeline = new Timeline(
                    new KeyFrame(Duration.millis(0), e -> {
                        if (pos < arrayContainer.getChildren().size()) {
                            VBox cell = (VBox) arrayContainer.getChildren().get(pos);
                            cell.setStyle("-fx-border-color: gold; -fx-border-width: 4; -fx-padding: 10; -fx-background-color: lightyellow;");
                        }
                    }),
                    new KeyFrame(Duration.millis(200), e -> {
                        if (pos < arrayContainer.getChildren().size()) {
                            VBox cell = (VBox) arrayContainer.getChildren().get(pos);
                            cell.setStyle("-fx-border-color: green; -fx-border-width: 4; -fx-padding: 10; -fx-background-color: lightgreen;");
                        }
                    }),
                    new KeyFrame(Duration.millis(400), e -> {
                        if (pos < arrayContainer.getChildren().size()) {
                            VBox cell = (VBox) arrayContainer.getChildren().get(pos);
                            cell.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 10; -fx-background-color: lightyellow;");
                        }
                    })
            );

            highlightAnimation.getChildren().add(flashTimeline);

            highlightSequence.getChildren().add(pause);
            highlightSequence.getChildren().add(highlightAnimation);
        }

        highlightSequence.play();
    }

    private void traverseArray() {
        if (arrayList.isEmpty()) {
            actionListView.getItems().add("Array is empty!");
            return;
        }

        isTraversing = true;
        actionListView.getItems().add("Traversing array:");

        // Create a timeline for sequential traversal
        Timeline timeline = new Timeline();

        for (int i = 0; i < arrayList.size(); i++) {
            final int currentIndex = i;
            int currentValue = arrayList.get(i);

            // Add keyframe for each position
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(i * 500), e -> {
                        // Reset all cells to default first
                        resetAllCellsToDefault();

                        // Highlight current cell
                        if (currentIndex < arrayContainer.getChildren().size()) {
                            VBox cell = (VBox) arrayContainer.getChildren().get(currentIndex);
                            cell.setStyle("-fx-border-color: green; -fx-border-width: 3; -fx-padding: 10; -fx-background-color: lightgreen;");
                        }

                        actionListView.getItems().add("  Position " + currentIndex + ": " + currentValue);

                        // If this is the last element
                        if (currentIndex == arrayList.size() - 1) {
                            isTraversing = false;

                            // Reset colors after traversal
                            new Timeline(new KeyFrame(Duration.millis(500), ev -> resetAllCellsToDefault())).play();
                            actionListView.getItems().add("Traversal complete");
                            actionListView.getItems().add("Array Size : " + arrayList.size());
                        }
                    })
            );
        }

        timeline.setCycleCount(1);
        timeline.play();
    }

    private void showUpdateDialog() {
        if (arrayList.isEmpty()) {
            actionListView.getItems().add("Array is empty!");
            return;
        }

        // First dialog for position
        TextInputDialog posDialog = new TextInputDialog();
        posDialog.setTitle("Update Value");
        posDialog.setHeaderText("Enter position to update (0 to " + (arrayList.size() - 1) + "):");
        posDialog.setContentText("Position:");

        Optional<String> posResult = posDialog.showAndWait();

        posResult.ifPresent(posValue -> {
            try {
                int position = Integer.parseInt(posValue);
                if (position < 0 || position >= arrayList.size()) {
                    actionListView.getItems().add("Invalid position! Must be between 0 and " + (arrayList.size() - 1));
                    return;
                }

                // Second dialog for new value
                TextInputDialog valDialog = new TextInputDialog();
                valDialog.setTitle("Update Value");
                valDialog.setHeaderText("Enter new value for position " + position + " (current: " + arrayList.get(position) + "):");
                valDialog.setContentText("New value:");

                Optional<String> valResult = valDialog.showAndWait();

                valResult.ifPresent(valValue -> {
                    try {
                        int intValue = Integer.parseInt(valValue);
                        arrayList.set(position, intValue);
                        updateCellValue(position, intValue);
                        highlightCellWithAnimation(position, "purple", 1000);
                        actionListView.getItems().add("Updated position " + position + " to value " + intValue);
                    } catch (NumberFormatException e) {
                        actionListView.getItems().add("Please enter a valid number!");
                    }
                });

            } catch (NumberFormatException e) {
                actionListView.getItems().add("Please enter a valid position!");
            }
        });
    }

    private void clearArray() {
        arrayList.clear();
        refreshArrayVisualization();
        actionListView.getItems().add("Array cleared");
        updateSize();
    }

    private void updateCellValue(int index, int value) {
        if (index >= 0 && index < arrayContainer.getChildren().size()) {
            VBox cell = (VBox) arrayContainer.getChildren().get(index);
            Text valueText = (Text) cell.getChildren().get(1);
            valueText.setText(String.valueOf(value));
            valueText.setStyle("-fx-fill: black; -fx-font-weight: bold;");
        }
    }

    private void highlightCellWithAnimation(int index, String color, int duration) {
        if (index >= 0 && index < arrayContainer.getChildren().size()) {
            VBox cell = (VBox) arrayContainer.getChildren().get(index);

            // Apply highlight color
            switch (color) {
                case "green":
                    cell.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 10; -fx-background-color: lightgreen;");
                    break;
                case "red":
                    cell.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 10; -fx-background-color: lightcoral;");
                    break;
                case "yellow":
                    cell.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 10; -fx-background-color: #FFFF00;");
                    break;
                case "orange":
                    cell.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 10; -fx-background-color: orange;");
                    break;
                case "purple":
                    cell.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 10; -fx-background-color: lavender;");
                    break;
                case "lightgreen":
                    cell.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 10; -fx-background-color: #90EE90;");
                    break;
            }

            // Reset after delay
            new Timeline(new KeyFrame(Duration.millis(duration), e ->
                    cell.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 10; -fx-background-color: lightyellow;")
            )).play();
        }
    }

    private void resetAllCellsToDefault() {
        for (int i = 0; i < arrayContainer.getChildren().size(); i++) {
            VBox cell = (VBox) arrayContainer.getChildren().get(i);
            cell.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 10; -fx-background-color: lightyellow;");
        }
    }

    private void refreshArrayVisualization() {
        // Clear and recreate all cells with current values
        arrayContainer.getChildren().clear();

        for (int i = 0; i < arrayCapacity; i++) {
            VBox cell = createArrayCell(i);

            // If there's a value at this index, display it
            if (i < arrayList.size()) {
                Text valueText = (Text) cell.getChildren().get(1);
                valueText.setText(String.valueOf(arrayList.get(i)));
                valueText.setStyle("-fx-fill: black; -fx-font-weight: bold;");
            } else {
                Text valueText = (Text) cell.getChildren().get(1);
                valueText.setText("null");
                valueText.setStyle("-fx-fill: gray;");
            }

            arrayContainer.getChildren().add(cell);
        }

        double requiredWidth = 50 + (arrayCapacity * 50) + 200;
        animationPane.setPrefWidth(Math.max(740, requiredWidth));
    }

    private void updateStatus(String status) {
        lblStatus.setText("Status: " + status);
    }

    /**
     * Update size label
     */
    private void updateSize() {
        lblSize.setText("Size: " +  arrayList.size());
    }
}