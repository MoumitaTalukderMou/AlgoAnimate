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
    @FXML private Button btnPause;



    // ─── Dark Theme Style Constants ───────────────────────────────────────────
    private static final String CELL_DEFAULT =
            "-fx-border-color: #38bdf8; -fx-border-width: 2; -fx-padding: 10; " +
                    "-fx-background-color: #0f172a; -fx-border-radius: 6; -fx-background-radius: 6;";

    private static final String CELL_HOVER =
            "-fx-border-color: #7dd3fc; -fx-border-width: 2.5; -fx-padding: 10; " +
                    "-fx-background-color: #1e293b; -fx-border-radius: 6; -fx-background-radius: 6;";

    private static final String CELL_GREEN =
            "-fx-border-color: #22c55e; -fx-border-width: 3; -fx-padding: 10; " +
                    "-fx-background-color: #052e16; -fx-border-radius: 6; -fx-background-radius: 6;";

    private static final String CELL_RED =
            "-fx-border-color: #ef4444; -fx-border-width: 3; -fx-padding: 10; " +
                    "-fx-background-color: #450a0a; -fx-border-radius: 6; -fx-background-radius: 6;";

    private static final String CELL_ORANGE =
            "-fx-border-color: #f97316; -fx-border-width: 3; -fx-padding: 10; " +
                    "-fx-background-color: #431407; -fx-border-radius: 6; -fx-background-radius: 6;";

    private static final String CELL_PURPLE =
            "-fx-border-color: #a78bfa; -fx-border-width: 3; -fx-padding: 10; " +
                    "-fx-background-color: #2e1065; -fx-border-radius: 6; -fx-background-radius: 6;";

    private static final String CELL_BLUE =
            "-fx-border-color: #60a5fa; -fx-border-width: 3; -fx-padding: 10; " +
                    "-fx-background-color: #172554; -fx-border-radius: 6; -fx-background-radius: 6;";

    private static final String CELL_GOLD =
            "-fx-border-color: #facc15; -fx-border-width: 3; -fx-padding: 10; " +
                    "-fx-background-color: #1c1400; -fx-border-radius: 6; -fx-background-radius: 6;";

    // ─── Text Style Constants ─────────────────────────────────────────────────
    private static final String TEXT_INDEX =
            "-fx-font-size: 12px; -fx-fill: #38bdf8; -fx-font-weight: bold;";

    private static final String TEXT_VALUE =
            "-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: #f1f5f9;";

    private static final String TEXT_NULL =
            "-fx-font-size: 13px; -fx-font-weight: normal; -fx-fill: #334155;";

    // ─── Cell Dimensions ──────────────────────────────────────────────────────
    private static final double CELL_WIDTH = 65;
    private static final double CELL_HEIGHT = 75;
    private static final double CELL_SPACING = 12;

    // ─── State ────────────────────────────────────────────────────────────────
    private ArrayList<Integer> arrayList = new ArrayList<>();
    private int arrayCapacity = 0;
    private HBox arrayContainer;
    private boolean isSearching = false;
    private boolean isTraversing = false;
    // ADD THESE FOR PAUSE/RESUME
    private Timeline currentTimeline = null;
    private SequentialTransition currentSequential = null;
    private boolean isPaused = false;

    // ─────────────────────────────────────────────────────────────────────────
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
                "Create", "Append", "Insert", "Delete Value", "Delete Position",
                "Search", "Traverse", "Update", "Increase Capacity", "Decrease Capacity", "Reset Array", "Clear"
        );
        ArrayChoiceBox.setOnAction(e -> handleChoiceSelection());

        arrayContainer = new HBox(CELL_SPACING);
        arrayContainer.setAlignment(Pos.CENTER_LEFT);
        arrayContainer.setLayoutX(50);
        arrayContainer.setLayoutY(180);
        animationPane.getChildren().add(arrayContainer);

    }

    // ─── Choice Handler ───────────────────────────────────────────────────────
    private void handleChoiceSelection() {
        String selected = ArrayChoiceBox.getValue();
        if (selected == null) return;
        actionListView.getItems().clear();

        switch (selected) {
            case "Create":
                actionListView.getItems().add("Creating new Array...");
                updateStatus("Creating");
                showCreateArrayDialog();
                break;
            case "Append":
                actionListView.getItems().add("Appending value...");
                updateStatus("Append");
                showAppendDialog();
                break;
            case "Insert":
                actionListView.getItems().add("Inserting value...");
                updateStatus("Insert");
                showInsertDialog();
                break;
            case "Delete Value":
                actionListView.getItems().add("Deleting value...");
                updateStatus("Delete Value");
                showDeleteValueDialog();
                break;
            case "Delete Position":
                actionListView.getItems().add("Deleting position...");
                updateStatus("Delete Position");
                showDeletePositionDialog();
                break;
            case "Search":
                actionListView.getItems().add("Searching...");
                updateStatus("Search");
                showSearchDialog();
                break;
            case "Traverse":
                actionListView.getItems().add("Traversing...");
                updateStatus("Traverse");
                traverseArray();
                break;
            case "Update":
                actionListView.getItems().add("Updating...");
                updateStatus("Update");
                showUpdateDialog();
                break;
            case "Increase Capacity":
                actionListView.getItems().add("Increasing capacity...");
                updateStatus("Increase Capacity");
                increaseArrayVisualization();
                break;
            case "Decrease Capacity":
                actionListView.getItems().add("Decreasing capacity...");
                updateStatus("Decrease Capacity");
                showDecreaseCapacityDialog();
                break;
            case "Reset Array":
                actionListView.getItems().add("Resetting array...");
                updateStatus("Reset");
                clearArray();
                break;
            case "Clear":
                actionListView.getItems().add("Cleared.");
                updateStatus("Cleared");
                animationPane.getChildren().clear();
                arrayContainer = new HBox(CELL_SPACING);
                arrayContainer.setAlignment(Pos.CENTER_LEFT);
                arrayContainer.setLayoutX(50);
                arrayContainer.setLayoutY(180);
                animationPane.getChildren().add(arrayContainer);
                arrayList.clear();
                arrayCapacity = 0;
                updateSize();
                break;
        }
        ArrayChoiceBox.setValue(null);
    }

    // ─── Create Cell ──────────────────────────────────────────────────────────
    private VBox createArrayCell(int index) {
        VBox cell = new VBox(4);
        cell.setAlignment(Pos.CENTER);
        cell.setStyle(CELL_DEFAULT);
        cell.setPrefWidth(CELL_WIDTH);
        cell.setPrefHeight(CELL_HEIGHT);
        cell.setMinWidth(CELL_WIDTH);
        cell.setMaxWidth(CELL_WIDTH);
        cell.setMinHeight(CELL_HEIGHT);
        cell.setMaxHeight(CELL_HEIGHT);

        Text indexText = new Text("[" + index + "]");
        indexText.setStyle(TEXT_INDEX);

        Text valueText = new Text("null");
        valueText.setStyle(TEXT_NULL);

        cell.getChildren().addAll(indexText, valueText);

        cell.setOnMouseEntered(e -> cell.setStyle(CELL_HOVER));
        cell.setOnMouseExited(e -> {
            // restore correct base style based on whether it has a value
            boolean hasValue = index < arrayList.size();
            cell.setStyle(CELL_DEFAULT);
            valueText.setStyle(hasValue ? TEXT_VALUE : TEXT_NULL);
        });

        return cell;
    }

    // ─── Create / Increase Array ──────────────────────────────────────────────
    private void showCreateArrayDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Array");
        dialog.setHeaderText("Enter the Array Capacity:");
        dialog.setContentText("Capacity:");

        dialog.showAndWait().ifPresent(value -> {
            try {
                int capacity = Integer.parseInt(value.trim());
                if (capacity <= 0) {
                    actionListView.getItems().add("Please enter a positive number!");
                    return;
                }
                if (capacity > 500) {
                    actionListView.getItems().add("Maximum capacity is 500!");
                    return;
                }
                arrayCapacity = capacity;
                arrayList.clear();
                createArrayVisualization();
                actionListView.getItems().add("Array created with capacity: " + capacity);
                updateSize();
            } catch (NumberFormatException e) {
                actionListView.getItems().add("Please enter a valid number!");
            }
        });
    }

    private void createArrayVisualization() {
        arrayContainer.getChildren().clear();
        for (int i = 0; i < arrayCapacity; i++) {
            arrayContainer.getChildren().add(createArrayCell(i));
        }
        updatePaneWidth();
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

        dialog.showAndWait().ifPresent(value -> {
            try {
                int newCapacity = Integer.parseInt(value.trim());
                if (newCapacity <= arrayCapacity) {
                    actionListView.getItems().add("New capacity must be greater than current!");
                    return;
                }
                if (newCapacity > 500) {
                    actionListView.getItems().add("Maximum capacity is 500!");
                    return;
                }

                int oldCapacity = arrayCapacity;

                // Update capacity FIRST so createArrayCell hover logic is correct
                arrayCapacity = newCapacity;

                // Step 1: append new cells with green flash
                for (int i = oldCapacity; i < newCapacity; i++) {
                    VBox cell = createArrayCell(i);
                    cell.setStyle(CELL_GREEN);
                    ((Text) cell.getChildren().get(1)).setStyle(TEXT_NULL);
                    arrayContainer.getChildren().add(cell);
                }
                updatePaneWidth();

                // Step 2: after flash delay, rebuild cleanly so indices/styles are 100% in sync
                PauseTransition flashDelay = new PauseTransition(Duration.millis(500));
                flashDelay.setOnFinished(e -> {
                    refreshArrayVisualization();   // ← this is the key fix
                    actionListView.getItems().add("Capacity increased from " + oldCapacity + " → " + arrayCapacity);
                    actionListView.getItems().add("Added " + (newCapacity - oldCapacity) + " new empty positions.");
                });
                flashDelay.play();

            } catch (NumberFormatException e) {
                actionListView.getItems().add("Please enter a valid number!");
            }
        });
    }
    // ------- Decrese Array -----
    private void showDecreaseCapacityDialog() {
        if (arrayCapacity == 0) {
            actionListView.getItems().add("Please create an array first!");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(String.valueOf(arrayCapacity));
        dialog.setTitle("Decrease Capacity");
        dialog.setHeaderText("Current capacity: " + arrayCapacity + "\nCurrent size: " + arrayList.size());
        dialog.setContentText("Enter new capacity (minimum " + arrayList.size() + "):");

        dialog.showAndWait().ifPresent(value -> {
            try {
                int newCapacity = Integer.parseInt(value.trim());

                // Validation
                if (newCapacity < arrayList.size()) {
                    actionListView.getItems().add("Cannot decrease capacity below current size (" + arrayList.size() + ")!");
                    actionListView.getItems().add("Please remove elements first or choose a larger capacity.");
                    return;
                }

                if (newCapacity >= arrayCapacity) {
                    actionListView.getItems().add("New capacity must be less than current capacity (" + arrayCapacity + ")!");
                    actionListView.getItems().add("Use 'Increase Capacity' to make capacity larger.");
                    return;
                }

                if (newCapacity <= 0) {
                    actionListView.getItems().add("Capacity must be positive!");
                    return;
                }

                // Perform capacity decrease with animation
                animateCapacityDecrease(newCapacity);

            } catch (NumberFormatException e) {
                actionListView.getItems().add("Please enter a valid number!");
            }
        });
    }
    private void animateCapacityDecrease(int newCapacity) {
        int oldCapacity = arrayCapacity;
        int removedCells = oldCapacity - newCapacity;

        actionListView.getItems().add("📉 Decreasing capacity from " + oldCapacity + " → " + newCapacity);
        actionListView.getItems().add("Removing " + removedCells + " empty position(s) from the end...");

        SequentialTransition seq = new SequentialTransition();

        // Animate removal of cells from the end
        for (int i = oldCapacity - 1; i >= newCapacity; i--) {
            final int cellIndex = i;

            if (cellIndex < arrayContainer.getChildren().size()) {
                VBox cellToRemove = (VBox) arrayContainer.getChildren().get(cellIndex);

                // Check if cell has data (shouldn't happen due to validation, but just in case)
                Text valueText = (Text) cellToRemove.getChildren().get(1);
                boolean hasData = !"null".equals(valueText.getText());

                if (hasData) {
                    // This shouldn't happen due to validation, but handle gracefully
                    actionListView.getItems().add("⚠️ Warning: Cell [" + cellIndex + "] had data! Aborting.");
                    refreshArrayVisualization();
                    return;
                }

                // Red flash for removal
                Timeline flash = new Timeline(
                        new KeyFrame(Duration.millis(0), e -> cellToRemove.setStyle(CELL_RED)),
                        new KeyFrame(Duration.millis(200), e -> cellToRemove.setStyle(CELL_DEFAULT))
                );

                // Fade out animation
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), cellToRemove);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);

                // Scale down animation for dramatic effect
                ScaleTransition scaleDown = new ScaleTransition(Duration.millis(300), cellToRemove);
                scaleDown.setFromX(1.0);
                scaleDown.setToX(0.0);
                scaleDown.setFromY(1.0);
                scaleDown.setToY(0.0);

                ParallelTransition removeAnim = new ParallelTransition(flash, fadeOut, scaleDown);

                removeAnim.setOnFinished(e -> javafx.application.Platform.runLater(() -> {
                    // Remove the cell from container
                    if (cellIndex < arrayContainer.getChildren().size()) {
                        arrayContainer.getChildren().remove(cellIndex);
                    }
                    actionListView.getItems().add("  ✗ Removed position [" + cellIndex + "]");
                }));

                seq.getChildren().add(removeAnim);

                // Small pause between removals
                if (i > newCapacity) {
                    seq.getChildren().add(new PauseTransition(Duration.millis(100)));
                }
            }
        }

        seq.setOnFinished(e -> javafx.application.Platform.runLater(() -> {
            // Update capacity
            arrayCapacity = newCapacity;

            // Ensure all remaining cells have correct styling
            for (int i = 0; i < arrayContainer.getChildren().size(); i++) {
                VBox cell = (VBox) arrayContainer.getChildren().get(i);
                cell.setStyle(CELL_DEFAULT);
                cell.setScaleX(1.0);
                cell.setScaleY(1.0);
                cell.setOpacity(1.0);

                // Update index text to reflect new indices
                Text indexText = (Text) cell.getChildren().get(0);
                indexText.setText("[" + i + "]");

                if (i < arrayList.size()) {
                    Text valueText = (Text) cell.getChildren().get(1);
                    valueText.setText(String.valueOf(arrayList.get(i)));
                    valueText.setStyle(TEXT_VALUE);
                }
            }

            updatePaneWidth();
            actionListView.getItems().add("✅ Capacity successfully decreased to " + newCapacity);
            actionListView.getItems().add("   Remaining cells: " + arrayContainer.getChildren().size());
            actionListView.getItems().add("   Data size: " + arrayList.size() + " / " + arrayCapacity);
            updateSize();

            // Optional: Green flash on remaining cells to show successful operation
            for (int i = 0; i < arrayContainer.getChildren().size(); i++) {
                highlightCellWithAnimation(i, "green", 300);
            }
        }));

        seq.play();
    }

    // ─── Append ───────────────────────────────────────────────────────────────
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

        dialog.showAndWait().ifPresent(value -> {
            try {
                int intValue = Integer.parseInt(value.trim());
                int position = arrayList.size();
                arrayList.add(intValue);
                updateCellValue(position, intValue);
                highlightCellWithAnimation(position, "green", 1000);
                actionListView.getItems().add("Appended " + intValue + " at position [" + position + "]");
                updateSize();
            } catch (NumberFormatException e) {
                actionListView.getItems().add("Please enter a valid number!");
            }
        });
    }

    // ─── Insert ───────────────────────────────────────────────────────────────
    private void showInsertDialog() {
        if (arrayCapacity == 0) {
            actionListView.getItems().add("Please create an array first!");
            return;
        }
        if (arrayList.size() >= arrayCapacity) {
            actionListView.getItems().add("Array is full! Cannot insert.");
            return;
        }

        TextInputDialog posDialog = new TextInputDialog();
        posDialog.setTitle("Insert Value");
        posDialog.setHeaderText("Enter position to insert (0 to " + arrayList.size() + "):");
        posDialog.setContentText("Position:");

        posDialog.showAndWait().ifPresent(posValue -> {
            try {
                int position = Integer.parseInt(posValue.trim());
                if (position < 0 || position > arrayList.size()) {
                    actionListView.getItems().add("Invalid position! Must be 0 to " + arrayList.size());
                    return;
                }
                TextInputDialog valDialog = new TextInputDialog();
                valDialog.setTitle("Insert Value");
                valDialog.setHeaderText("Enter value to insert at position " + position + ":");
                valDialog.setContentText("Value:");

                valDialog.showAndWait().ifPresent(valValue -> {
                    try {
                        int intValue = Integer.parseInt(valValue.trim());
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

    // ─── Delete Value ─────────────────────────────────────────────────────────
    private void showDeleteValueDialog() {
        if (arrayList.isEmpty()) {
            actionListView.getItems().add("Array is empty!");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Value");
        dialog.setHeaderText("Enter value to delete:");
        dialog.setContentText("Value:");

        dialog.showAndWait().ifPresent(value -> {
            try {
                int intValue = Integer.parseInt(value.trim());
                ArrayList<Integer> positions = new ArrayList<>();
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i) == intValue) positions.add(i);
                }
                if (positions.isEmpty()) {
                    actionListView.getItems().add("Value " + intValue + " not found!");
                    return;
                }
                for (int i = positions.size() - 1; i >= 0; i--) {
                    animateDeleteWithLeftShift(positions.get(i), intValue);
                }
            } catch (NumberFormatException e) {
                actionListView.getItems().add("Please enter a valid number!");
            }
        });
    }

    // ─── Delete Position ──────────────────────────────────────────────────────
    private void showDeletePositionDialog() {
        if (arrayList.isEmpty()) {
            actionListView.getItems().add("Array is empty!");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete at Position");
        dialog.setHeaderText("Enter position to delete (0 to " + (arrayList.size() - 1) + "):");
        dialog.setContentText("Position:");

        dialog.showAndWait().ifPresent(value -> {
            try {
                int position = Integer.parseInt(value.trim());
                if (position < 0 || position >= arrayList.size()) {
                    actionListView.getItems().add("Invalid position! Must be 0 to " + (arrayList.size() - 1));
                    return;
                }
                animateDeleteWithLeftShift(position, arrayList.get(position));
            } catch (NumberFormatException e) {
                actionListView.getItems().add("Please enter a valid number!");
            }
        });
    }

    // ─── Delete Animation ─────────────────────────────────────────────────────
    private void animateDeleteWithLeftShift(int deletePos, int deletedValue) {
        if (deletePos < arrayContainer.getChildren().size()) {
            VBox cellToDelete = (VBox) arrayContainer.getChildren().get(deletePos);
            cellToDelete.setStyle(CELL_RED);
            Text vt = (Text) cellToDelete.getChildren().get(1);
            vt.setText("null");
            vt.setStyle(TEXT_NULL);
            actionListView.getItems().add("Position [" + deletePos + "] emptied.");
        }

        SequentialTransition seq = new SequentialTransition();

        for (int i = deletePos + 1; i < arrayList.size(); i++) {
            final int ci = i;
            final int ti = i - 1;

            if (ci < arrayContainer.getChildren().size()) {
                VBox movingCell = (VBox) arrayContainer.getChildren().get(ci);
                VBox targetCell = (VBox) arrayContainer.getChildren().get(ti);
                Text movingText = (Text) movingCell.getChildren().get(1);
                String movingValue = movingText.getText();

                FadeTransition fadeOut = new FadeTransition(Duration.millis(150), movingCell);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.3);

                TranslateTransition moveLeft = new TranslateTransition(Duration.millis(250), movingCell);
                moveLeft.setByX(-(CELL_WIDTH + CELL_SPACING));
                moveLeft.setOnFinished(e -> javafx.application.Platform.runLater(() -> {
                    Text targetText = (Text) targetCell.getChildren().get(1);
                    targetText.setText(movingValue);
                    targetText.setStyle(TEXT_VALUE);
                    targetCell.setStyle(CELL_DEFAULT);

                    movingCell.setTranslateX(0);
                    movingText.setText("null");
                    movingText.setStyle(TEXT_NULL);
                    movingCell.setStyle(CELL_DEFAULT);

                    FadeTransition fadeIn = new FadeTransition(Duration.millis(100), movingCell);
                    fadeIn.setFromValue(0.3);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();
                }));

                ParallelTransition step = new ParallelTransition(fadeOut, moveLeft);
                seq.getChildren().add(step);
            }
        }

        seq.setOnFinished(e -> javafx.application.Platform.runLater(() -> {
            arrayList.remove(deletePos);
            refreshArrayVisualization();
            actionListView.getItems().add("Deleted value " + deletedValue + " from position [" + deletePos + "]");
            updateSize();
        }));
        seq.play();
    }

    // ─── Insert Animation ─────────────────────────────────────────────────────
    private void animateShiftRightForInsert(int insertPos, int valueToInsert) {
        if (arrayList.size() >= arrayCapacity) {
            actionListView.getItems().add("Array is full!");
            return;
        }

        SequentialTransition seq = new SequentialTransition();

        for (int i = arrayList.size() - 1; i >= insertPos; i--) {
            final int ci = i;
            final int ti = i + 1;

            if (ci < arrayContainer.getChildren().size() && ti < arrayContainer.getChildren().size()) {
                VBox movingCell = (VBox) arrayContainer.getChildren().get(ci);
                VBox targetCell = (VBox) arrayContainer.getChildren().get(ti);
                Text movingText = (Text) movingCell.getChildren().get(1);
                String movingValue = movingText.getText();

                FadeTransition fadeOut = new FadeTransition(Duration.millis(150), movingCell);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.3);

                TranslateTransition moveRight = new TranslateTransition(Duration.millis(250), movingCell);
                moveRight.setByX(CELL_WIDTH + CELL_SPACING);
                moveRight.setOnFinished(e -> javafx.application.Platform.runLater(() -> {
                    if (!"null".equals(movingValue)) {
                        Text targetText = (Text) targetCell.getChildren().get(1);
                        targetText.setText(movingValue);
                        targetText.setStyle(TEXT_VALUE);
                        targetCell.setStyle(CELL_DEFAULT);
                    }
                    movingCell.setTranslateX(0);
                    movingText.setText("null");
                    movingText.setStyle(TEXT_NULL);
                    movingCell.setStyle(CELL_DEFAULT);

                    FadeTransition fadeIn = new FadeTransition(Duration.millis(100), movingCell);
                    fadeIn.setFromValue(0.3);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();
                }));

                ParallelTransition step = new ParallelTransition(fadeOut, moveRight);
                seq.getChildren().add(step);
            }
        }

        seq.setOnFinished(e -> javafx.application.Platform.runLater(() -> {
            arrayList.add(insertPos, valueToInsert);
            refreshArrayVisualization();
            highlightCellWithAnimation(insertPos, "orange", 1200);
            actionListView.getItems().add("Inserted " + valueToInsert + " at position [" + insertPos + "]");
            updateSize();
        }));
        seq.play();
    }

    // ─── Search ───────────────────────────────────────────────────────────────
    private void showSearchDialog() {
        if (arrayList.isEmpty()) {
            actionListView.getItems().add("Array is empty!");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Value");
        dialog.setHeaderText("Enter value to search:");
        dialog.setContentText("Value:");

        dialog.showAndWait().ifPresent(value -> {
            try {
                int searchValue = Integer.parseInt(value.trim());
                isSearching = true;
                actionListView.getItems().clear();
                actionListView.getItems().add("🔍 Searching for: " + searchValue);

                ArrayList<Integer> foundPositions = new ArrayList<>();
                SequentialTransition searchSeq = new SequentialTransition();

                for (int i = 0; i < arrayList.size(); i++) {
                    final int ci = i;
                    final int cv = arrayList.get(i);

                    PauseTransition pause = new PauseTransition(Duration.millis(450));

                    Timeline check = new Timeline(
                            new KeyFrame(Duration.millis(0), e -> {
                                resetAllCellsToDefault();
                                if (ci < arrayContainer.getChildren().size()) {
                                    VBox cell = (VBox) arrayContainer.getChildren().get(ci);
                                    cell.setStyle(CELL_BLUE);
                                }
                            }),
                            new KeyFrame(Duration.millis(250), e -> {
                                if (cv == searchValue) {
                                    foundPositions.add(ci);
                                    if (ci < arrayContainer.getChildren().size()) {
                                        VBox cell = (VBox) arrayContainer.getChildren().get(ci);
                                        Timeline flash = new Timeline(
                                                new KeyFrame(Duration.millis(0), ev -> cell.setStyle(CELL_GREEN)),
                                                new KeyFrame(Duration.millis(300), ev -> cell.setStyle(CELL_DEFAULT))
                                        );
                                        flash.play();
                                    }
                                    actionListView.getItems().add("  ✓ [" + ci + "] = " + cv + " → FOUND");
                                } else {
                                    if (ci < arrayContainer.getChildren().size()) {
                                        VBox cell = (VBox) arrayContainer.getChildren().get(ci);
                                        Timeline flash = new Timeline(
                                                new KeyFrame(Duration.millis(0), ev -> cell.setStyle(CELL_RED)),
                                                new KeyFrame(Duration.millis(200), ev -> cell.setStyle(CELL_DEFAULT))
                                        );
                                        flash.play();
                                    }
                                    actionListView.getItems().add("  → [" + ci + "] = " + cv + " ≠ " + searchValue);
                                }
                            })
                    );

                    searchSeq.getChildren().addAll(pause, check);
                }

                searchSeq.setOnFinished(e -> {
                    resetAllCellsToDefault();
                    if (foundPositions.isEmpty()) {
                        actionListView.getItems().add("❌ Value " + searchValue + " not found.");
                    } else {
                        actionListView.getItems().add("✅ Found at positions: " + foundPositions);
                        highlightFoundPositions(foundPositions);
                    }
                    isSearching = false;
                });
                searchSeq.play();

            } catch (NumberFormatException e) {
                actionListView.getItems().add("Please enter a valid number!");
            }
        });
    }

    private void highlightFoundPositions(ArrayList<Integer> positions) {
        actionListView.getItems().add("★ Highlighting found positions:");
        SequentialTransition seq = new SequentialTransition();

        for (int pos : positions) {
            PauseTransition pause = new PauseTransition(Duration.millis(350));
            Timeline flash = new Timeline(
                    new KeyFrame(Duration.millis(0), e -> {
                        if (pos < arrayContainer.getChildren().size())
                            ((VBox) arrayContainer.getChildren().get(pos)).setStyle(CELL_GOLD);
                    }),
                    new KeyFrame(Duration.millis(200), e -> {
                        if (pos < arrayContainer.getChildren().size())
                            ((VBox) arrayContainer.getChildren().get(pos)).setStyle(CELL_GREEN);
                    }),
                    new KeyFrame(Duration.millis(500), e -> {
                        if (pos < arrayContainer.getChildren().size())
                            ((VBox) arrayContainer.getChildren().get(pos)).setStyle(CELL_DEFAULT);
                    })
            );
            seq.getChildren().addAll(pause, flash);
        }
        seq.play();
    }

    // ─── Traverse ─────────────────────────────────────────────────────────────
    private void traverseArray() {
        if (arrayList.isEmpty()) {
            actionListView.getItems().add("Array is empty!");
            return;
        }

        isTraversing = true;
        actionListView.getItems().add("Traversing array:");
        Timeline timeline = new Timeline();

        for (int i = 0; i < arrayList.size(); i++) {
            final int ci = i;
            final int cv = arrayList.get(i);

            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(i * 500L), e -> {
                resetAllCellsToDefault();
                if (ci < arrayContainer.getChildren().size()) {
                    VBox cell = (VBox) arrayContainer.getChildren().get(ci);
                    cell.setStyle(CELL_GREEN);
                    Text vt = (Text) cell.getChildren().get(1);
                    vt.setStyle(TEXT_VALUE);
                }
                actionListView.getItems().add("  [" + ci + "] = " + cv);

                if (ci == arrayList.size() - 1) {
                    isTraversing = false;
                    new Timeline(new KeyFrame(Duration.millis(600), ev -> resetAllCellsToDefault())).play();
                    actionListView.getItems().add("Traversal complete. Size: " + arrayList.size());
                }
            }));
        }
        timeline.setCycleCount(1);
        timeline.play();
    }

    // ─── Update ───────────────────────────────────────────────────────────────
    private void showUpdateDialog() {
        if (arrayList.isEmpty()) {
            actionListView.getItems().add("Array is empty!");
            return;
        }

        TextInputDialog posDialog = new TextInputDialog();
        posDialog.setTitle("Update Value");
        posDialog.setHeaderText("Enter position to update (0 to " + (arrayList.size() - 1) + "):");
        posDialog.setContentText("Position:");

        posDialog.showAndWait().ifPresent(posValue -> {
            try {
                int position = Integer.parseInt(posValue.trim());
                if (position < 0 || position >= arrayList.size()) {
                    actionListView.getItems().add("Invalid position!");
                    return;
                }
                TextInputDialog valDialog = new TextInputDialog(String.valueOf(arrayList.get(position)));
                valDialog.setTitle("Update Value");
                valDialog.setHeaderText("New value for position [" + position + "] (current: " + arrayList.get(position) + "):");
                valDialog.setContentText("New value:");

                valDialog.showAndWait().ifPresent(valValue -> {
                    try {
                        int intValue = Integer.parseInt(valValue.trim());
                        arrayList.set(position, intValue);
                        updateCellValue(position, intValue);
                        highlightCellWithAnimation(position, "purple", 1200);
                        actionListView.getItems().add("Updated [" + position + "] → " + intValue);
                    } catch (NumberFormatException e) {
                        actionListView.getItems().add("Please enter a valid number!");
                    }
                });
            } catch (NumberFormatException e) {
                actionListView.getItems().add("Please enter a valid position!");
            }
        });
    }

    // ─── Reset ────────────────────────────────────────────────────────────────
    private void clearArray() {
        arrayList.clear();
        refreshArrayVisualization();
        actionListView.getItems().add("Array reset to null.");
        updateSize();
    }

    // ─── Cell Helpers ─────────────────────────────────────────────────────────
    private void updateCellValue(int index, int value) {
        if (index < 0 || index >= arrayContainer.getChildren().size()) return;
        VBox cell = (VBox) arrayContainer.getChildren().get(index);
        cell.setStyle(CELL_DEFAULT);
        cell.setPrefWidth(CELL_WIDTH);
        cell.setPrefHeight(CELL_HEIGHT);
        Text valueText = (Text) cell.getChildren().get(1);
        valueText.setText(String.valueOf(value));
        valueText.setStyle(TEXT_VALUE);
    }

    private void highlightCellWithAnimation(int index, String color, int duration) {
        if (index < 0 || index >= arrayContainer.getChildren().size()) return;
        VBox cell = (VBox) arrayContainer.getChildren().get(index);
        Text vt = (Text) cell.getChildren().get(1);

        String highlightStyle;
        switch (color) {
            case "green":
                highlightStyle = CELL_GREEN;
                break;
            case "red":
                highlightStyle = CELL_RED;
                break;
            case "orange":
                highlightStyle = CELL_ORANGE;
                break;
            case "purple":
                highlightStyle = CELL_PURPLE;
                break;
            case "gold":
                highlightStyle = CELL_GOLD;
                break;
            default:
                highlightStyle = CELL_BLUE;
                break;
        }

        cell.setStyle(highlightStyle);
        vt.setStyle(TEXT_VALUE);

        new Timeline(new KeyFrame(Duration.millis(duration), e -> {
            cell.setStyle(CELL_DEFAULT);
            vt.setStyle(index < arrayList.size() ? TEXT_VALUE : TEXT_NULL);
        })).play();
    }

    private void resetAllCellsToDefault() {
        for (int i = 0; i < arrayContainer.getChildren().size(); i++) {
            VBox cell = (VBox) arrayContainer.getChildren().get(i);
            cell.setStyle(CELL_DEFAULT);
            Text vt = (Text) cell.getChildren().get(1);
            vt.setStyle(i < arrayList.size() ? TEXT_VALUE : TEXT_NULL);
        }
    }

    private void refreshArrayVisualization() {
        arrayContainer.getChildren().clear();
        arrayContainer.setSpacing(CELL_SPACING);
        arrayContainer.setAlignment(Pos.CENTER_LEFT);

        for (int i = 0; i < arrayCapacity; i++) {
            VBox cell = createArrayCell(i);
            if (i < arrayList.size()) {
                Text vt = (Text) cell.getChildren().get(1);
                vt.setText(String.valueOf(arrayList.get(i)));
                vt.setStyle(TEXT_VALUE);
            }
            arrayContainer.getChildren().add(cell);
        }
        updatePaneWidth();
    }

    private void updatePaneWidth() {
        double required = 80 + arrayCapacity * (CELL_WIDTH + CELL_SPACING) + 100;
        animationPane.setPrefWidth(Math.max(740, required));
    }

    private void updateStatus(String status) {
        lblStatus.setText("Status: " + status);
    }

    private void updateSize() {
        lblSize.setText("Size: " + arrayList.size() + " / " + arrayCapacity);
    }
    @FXML private Label lblAnimationStatus;

}