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

        algoComboBox.getItems().addAll("Bubble Sort", "Selection Sort", "Insertion Sort", "Merge Sort");
        algoComboBox.getSelectionModel().selectFirst();


        algoComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            loadPseudocode(newVal);
        });

        loadPseudocode("Bubble Sort");
        handleRandomize();
    }


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
        } else if (algo.equals("Merge Sort")){
            codeListView.getItems().addAll(
                    "void mergeSort(arr, left, right) {",    // Line 0
                    "  if (left >= right) return;",          // Line 1
                    "  int mid = left + (right - left)/2;",  // Line 2
                    "  mergeSort(arr, left, mid);",          // Line 3
                    "  mergeSort(arr, mid + 1, right);",     // Line 4
                    "  merge(arr, left, mid, right);",       // Line 5
                    "}",                                     // Line 6
                    "",                                      // Line 7 (Empty)
                    "void merge(arr, left, mid, right) {",   // Line 8
                    "  create temp arrays L[] and R[]",      // Line 9
                    "  while (i < n1 && j < n2) {",          // Line 10
                    "    if (L[i] <= R[j])",                 // Line 11
                    "      arr[k] = L[i];",                  // Line 12
                    "    else",                              // Line 13
                    "      arr[k] = R[j];",                  // Line 14
                    "  }",                                   // Line 15
                    "  copy remaining elements",             // Line 16
                    "}"                                      // Line 17
            );
        }
    }


    @FXML
    private void handleRandomize() {
        int[] randomArray = new int[12];
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

                    else if (algo.equals("Merge Sort")) {
                    mergeSortRecursive(0, array.length - 1, 1);
                    markSorted();
                    }
                }             catch (InterruptedException e) { e.printStackTrace(); }
        }).start();
    }



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

    // --- MERGE SORT ALGORITHM ---

    // --- MERGE SORT ALGORITHM (VIDEO STYLE) ---
// --- ADVANCED MERGE SORT (VIDEO STYLE) ---

    // --- VIDEO STYLE MERGE SORT (SPLIT & MERGE) ---

    private void mergeSortRecursive(int left, int right, int depth) throws InterruptedException {
        if (left < right) {
            int mid = left + (right - left) / 2;

            // ১. [DIVIDE] বাম ও ডান পাশকে দুই দিকে সরিয়ে নিচে নামানো
            animateSplit(left, mid, right, depth);
            sleep();

            // রিকার্সিভ কল
            mergeSortRecursive(left, mid, depth + 1);
            mergeSortRecursive(mid + 1, right, depth + 1);

            // ২. মার্জ লজিক
            merge(left, mid, right);

            // ৩. [CONQUER] সর্ট হওয়ার পর আবার আগের উচ্চতায় এবং মাঝখানে ফিরিয়ে আনা
            animateMergeBack(left, right, depth);
            sleep();
        }
    }

    // --- এনিমেশন: দুই পাশে সরিয়ে নিচে নামানো ---
    private void animateSplit(int left, int mid, int right, int depth) {
        Platform.runLater(() -> {
            // বাম পাশের বারগুলো বামে সরবে (-X) এবং নিচে নামবে (+Y)
            for (int i = left; i <= mid; i++) {
                TranslateTransition tt = new TranslateTransition(Duration.millis(500), barNodes[i]);
                tt.setToX(-20 * depth); // বামে শিফট
                tt.setToY(depth * 60);  // নিচে শিফট
                tt.play();
            }
            // ডান পাশের বারগুলো ডানে সরবে (+X) এবং নিচে নামবে (+Y)
            for (int i = mid + 1; i <= right; i++) {
                TranslateTransition tt = new TranslateTransition(Duration.millis(500), barNodes[i]);
                tt.setToX(20 * depth); // ডানে শিফট
                tt.setToY(depth * 60); // নিচে শিফট
                tt.play();
            }
        });
    }

    // --- এনিমেশন: মাঝখানে ফিরিয়ে ওপরে তোলা ---
    private void animateMergeBack(int left, int right, int depth) {
        Platform.runLater(() -> {
            for (int i = left; i <= right; i++) {
                TranslateTransition tt = new TranslateTransition(Duration.millis(500), barNodes[i]);
                tt.setToX(0); // মাঝখানে ফিরে আসবে
                tt.setToY((depth - 1) * 60); // এক লেভেল ওপরে উঠবে
                tt.play();
            }
        });
    }

    // --- MERGE লজিক (রঙ বদলানোসহ) ---
    private void merge(int left, int mid, int right) throws InterruptedException {
        // বাম পাশে লাল, ডান পাশে হলুদ রঙ করা (ভিডিওর মতো)
        for (int i = left; i <= mid; i++) setBarColor(i, "partition-left");
        for (int i = mid + 1; i <= right; i++) setBarColor(i, "partition-right");
        sleep();

        int n1 = mid - left + 1;
        int n2 = right - mid;
        int[] L = new int[n1];
        int[] R = new int[n2];

        for (int i = 0; i < n1; ++i) L[i] = array[left + i];
        for (int j = 0; j < n2; ++j) R[j] = array[mid + 1 + j];

        int i = 0, j = 0, k = left;

        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                array[k] = L[i];
                updateBar(k, array[k], "partition-merged"); // সর্ট হলে সবুজ
                i++;
            } else {
                array[k] = R[j];
                updateBar(k, array[k], "partition-merged"); // সর্ট হলে সবুজ
                j++;
            }
            k++;
            sleep();
        }

        while (i < n1) {
            array[k] = L[i];
            updateBar(k, array[k], "partition-merged");
            i++; k++; sleep();
        }
        while (j < n2) {
            array[k] = R[j];
            updateBar(k, array[k], "partition-merged");
            j++; k++; sleep();
        }
    }
    // --- এনিমেশন হেল্পার মেথড ---
    private void translateRange(int start, int end, double yOffset) {
        Platform.runLater(() -> {
            for (int i = start; i <= end; i++) {
                TranslateTransition tt = new TranslateTransition(Duration.millis(400), barNodes[i]);
                tt.setToY(yOffset);
                tt.play();
            }
        });
    }

    // --- NEW HELPER METHODS (Add/Replace these) ---

    // শুধু রঙ পরিবর্তন করার জন্য
    private void setBarColor(int index, String styleClass) {
        Platform.runLater(() -> {
            rects[index].getStyleClass().removeAll("vis-bar", "bar-compare", "bar-swap", "bar-left-part", "bar-right-part", "bar-merged");
            rects[index].getStyleClass().add(styleClass);
        });
    }

    // হাইট এবং রঙ একসাথে পরিবর্তন করার জন্য (Merge Sort-এর জন্য স্পেশাল)
    private void updateBar(int index, int value, String styleClass) {
        Platform.runLater(() -> {
            // ১. হাইট পরিবর্তন
            rects[index].setHeight(value * 6);

            // ২. টেক্সট আপডেট
            Label valLabel = (Label) barNodes[index].getChildren().get(0);
            valLabel.setText(String.valueOf(value));

            // ৩. রঙ পরিবর্তন
            rects[index].getStyleClass().removeAll("vis-bar", "bar-compare", "bar-swap", "bar-left-part", "bar-right-part", "bar-merged");
            rects[index].getStyleClass().add(styleClass);
        });
    }

    // মার্জ শেষে কালার আগের অবস্থায় ফেরানো
    private void resetRangeColor(int start, int end) {
        Platform.runLater(() -> {
            for (int i = start; i <= end; i++) {
                rects[i].getStyleClass().removeAll("bar-compare", "bar-swap");
                rects[i].getStyleClass().add("vis-bar");
            }
        });
    }

    // বারগুলোকে ওপরে বা নিচে মুভ করার জন্য এনিমেশন মেথড




}