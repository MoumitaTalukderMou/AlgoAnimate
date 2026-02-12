package com.example.algoanimate;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Random;

public class SortingController {

    @FXML private HBox displayPane;
    @FXML private ComboBox<String> algoComboBox;
    @FXML private ListView<String> codeListView;
    @FXML private Label lblStatus, lblComparisons, lblSwaps;
    @FXML private Slider speedSlider;
    @FXML private TextField inputField;

    private int[] array;
    private VBox[] barNodes;
    private Rectangle[] rects;
    private int comparisons = 0, swaps = 0;

    @FXML
    public void initialize() {
        // অ্যালগরিদম লিস্ট সেটআপ
        algoComboBox.getItems().addAll("Bubble Sort", "Selection Sort", "Insertion Sort", "Merge Sort");
        algoComboBox.getSelectionModel().selectFirst();

        // ড্রপডাউন চেঞ্জ হলে সি++ কোড আপডেট হবে
        algoComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            loadPseudocode(newVal);
        });

        loadPseudocode("Bubble Sort");
        handleRandomize(); // শুরুতে কিছু রেন্ডম বার দেখানো
    }

    // --- সি++ কোড এবং ব্র্যাকেট সেটআপ ---
    private void loadPseudocode(String algo) {
        codeListView.getItems().clear();
        if (algo.equals("Bubble Sort")) {
            codeListView.getItems().addAll(
                    "void bubbleSort(int arr[], int n) {",
                    "  for (int i = 0; i < n-1; i++) {",
                    "    bool swapped = false;",
                    "    for (int j = 0; j < n-i-1; j++) {",
                    "      if (arr[j] > arr[j+1]) {",
                    "        swap(arr[j], arr[j+1]);",
                    "        swapped = true;",
                    "      }",
                    "    }",
                    "    if (!swapped) break;",
                    "  }",
                    "}"
            );
        } else if (algo.equals("Selection Sort")) {
            codeListView.getItems().addAll(
                    "void selectionSort(int arr[], int n) {",
                    "  for (int i = 0; i < n-1; i++) {",
                    "    int min_idx = i;",
                    "    for (int j = i+1; j < n; j++) {",
                    "      if (arr[j] < arr[min_idx]) {",
                    "        min_idx = j;",
                    "      }",
                    "    }",
                    "    swap(arr[min_idx], arr[i]);",
                    "  }",
                    "}"
            );
        } else if (algo.equals("Insertion Sort")) {
            codeListView.getItems().addAll(
                    "void insertionSort(int arr[], int n) {",
                    "  for (int i = 1; i < n; i++) {",
                    "    int key = arr[i]; int j = i - 1;",
                    "    while (j >= 0 && arr[j] > key) {",
                    "      arr[j + 1] = arr[j];",
                    "      j = j - 1;",
                    "    }",
                    "    arr[j + 1] = key;",
                    "  }",
                    "}"
            );
        } else {
            codeListView.getItems().addAll("// Merge Sort Logic Coming Soon...");
        }
    }

    // --- বার জেনারেশন লজিক ---
    @FXML
    private void handleRandomize() {
        int[] randomArray = new int[12]; // ১২টি বার স্পেস ভরাট রাখবে
        Random rand = new Random();
        for (int i = 0; i < randomArray.length; i++) {
            randomArray[i] = rand.nextInt(50) + 10;
        }
        loadArray(randomArray);
    }

    @FXML
    private void handleCustomInput() {
        String input = inputField.getText();
        if (input == null || input.isEmpty()) return;
        try {
            String[] parts = input.split(",");
            int[] arr = new int[parts.length];
            for(int i=0; i<parts.length; i++) arr[i] = Integer.parseInt(parts[i].trim());
            loadArray(arr);
        } catch (Exception e) {
            lblStatus.setText("Status: Invalid Input!");
        }
    }

    private void loadArray(int[] newArray) {
        this.array = newArray;
        this.barNodes = new VBox[array.length];
        this.rects = new Rectangle[array.length];
        displayPane.getChildren().clear();
        comparisons = 0; swaps = 0; updateStats("Ready");

        for (int i = 0; i < array.length; i++) {
            Rectangle bar = new Rectangle(40, array[i] * 6);
            bar.getStyleClass().add("vis-bar");

            Label valLabel = new Label(String.valueOf(array[i]));
            valLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

            Label idxLabel = new Label(String.valueOf(i));
            idxLabel.setStyle("-fx-text-fill: #64748b;");

            VBox container = new VBox(5, valLabel, bar, idxLabel);
            container.setAlignment(javafx.geometry.Pos.BOTTOM_CENTER);

            barNodes[i] = container;
            rects[i] = bar;
            displayPane.getChildren().add(container);
        }
    }

    @FXML
    private void handleSort() {
        String algo = algoComboBox.getValue();
        new Thread(() -> {
            try {
                if (algo.equals("Bubble Sort")) bubbleSort();
                else if (algo.equals("Selection Sort")) selectionSort();
                else if (algo.equals("Insertion Sort")) insertionSort();
                // Merge Sort logic can be added similarly
            } catch (InterruptedException e) { e.printStackTrace(); }
        }).start();
    }

    // --- সর্টিং অ্যালগরিদমসমূহ ---

    private void bubbleSort() throws InterruptedException {
        for (int i = 0; i < array.length - 1; i++) {
            trace(2);
            boolean swapped = false;
            for (int j = 0; j < array.length - i - 1; j++) {
                trace(4);
                highlight(j, j+1, "compare");
                updateStats("Comparing " + array[j] + " & " + array[j+1]);
                sleep();
                comparisons++;

                if (array[j] > array[j+1]) {
                    highlight(j, j+1, "swap");
                    trace(6);
                    animateJump(rects[j]); animateJump(rects[j+1]);

                    int temp = array[j]; array[j] = array[j+1]; array[j+1] = temp;
                    swapped = true;
                    swapBars(j, j+1);
                    swaps++;
                    sleep();
                }
                resetColor(j, j+1);
            }
            if(!swapped) break;
        }
        markSorted();
    }

    private void selectionSort() throws InterruptedException {
        for (int i = 0; i < array.length - 1; i++) {
            trace(1);
            int minIdx = i;
            for (int j = i + 1; j < array.length; j++) {
                trace(3);
                highlight(minIdx, j, "compare");
                comparisons++;
                sleep();
                if (array[j] < array[minIdx]) {
                    resetColor(minIdx);
                    minIdx = j;
                    highlight(minIdx, "swap");
                } else resetColor(j);
            }
            if (minIdx != i) {
                trace(8);
                int temp = array[minIdx]; array[minIdx] = array[i]; array[i] = temp;
                animateJump(rects[minIdx]); animateJump(rects[i]);
                swapBars(minIdx, i);
                swaps++;
                sleep();
            }
            rects[i].getStyleClass().add("bar-sorted");
        }
        markSorted();
    }

    private void insertionSort() throws InterruptedException {
        for (int i = 1; i < array.length; i++) {
            trace(1);
            int key = array[i];
            int j = i - 1;
            highlight(i, "swap");
            sleep();

            while (j >= 0 && array[j] > key) {
                trace(3);
                highlight(j, j + 1, "compare");
                comparisons++;
                sleep();

                array[j + 1] = array[j];
                swapBars(j, j + 1);
                swaps++;
                j--;
                sleep();
            }
            array[j + 1] = key;
            trace(7);
            resetColor(j + 1);
        }
        markSorted();
    }

    // --- এনিমেশন এবং হেল্পার মেথড ---

    private void animateJump(Rectangle bar) {
        Platform.runLater(() -> {
            TranslateTransition tt = new TranslateTransition(Duration.millis(150), bar);
            tt.setByY(-20);
            tt.setAutoReverse(true);
            tt.setCycleCount(2);
            tt.play();
        });
    }

    private void trace(int lineIndex) {
        Platform.runLater(() -> {
            codeListView.getSelectionModel().select(lineIndex);
        });
    }

    private void swapBars(int i, int j) {
        Platform.runLater(() -> {
            double h1 = rects[i].getHeight();
            double h2 = rects[j].getHeight();
            rects[i].setHeight(h2);
            rects[j].setHeight(h1);

            Label l1 = (Label) barNodes[i].getChildren().get(0);
            Label l2 = (Label) barNodes[j].getChildren().get(0);
            String txt1 = l1.getText();
            l1.setText(l2.getText());
            l2.setText(txt1);
        });
    }

    private void highlight(int i, int j, String type) {
        Platform.runLater(() -> {
            rects[i].getStyleClass().add(type.equals("compare") ? "bar-compare" : "bar-swap");
            rects[j].getStyleClass().add(type.equals("compare") ? "bar-compare" : "bar-swap");
        });
    }

    private void highlight(int i, String type) {
        Platform.runLater(() -> rects[i].getStyleClass().add("bar-swap"));
    }

    private void resetColor(int... indices) {
        Platform.runLater(() -> {
            for (int i : indices) rects[i].getStyleClass().removeAll("bar-compare", "bar-swap");
        });
    }

    private void markSorted() {
        Platform.runLater(() -> {
            for(Rectangle r : rects) r.getStyleClass().add("bar-sorted");
            updateStats("Sorted!");
        });
    }

    private void updateStats(String status) {
        Platform.runLater(() -> {
            lblStatus.setText("Status: " + status);
            lblComparisons.setText("Comparisons: " + comparisons);
            lblSwaps.setText("Swaps: " + swaps);
        });
    }

    private void sleep() throws InterruptedException {
        Thread.sleep((long) (1100 - speedSlider.getValue()));
    }

    @FXML
    private void handleBack() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        Stage stage = (Stage) displayPane.getScene().getWindow();
        stage.setScene(scene);
    }
}