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
    @FXML private Button pauseBtn; // <-- Missing button added

    private int[] array;
    private VBox[] barNodes;
    private Rectangle[] rects;
    private int comparisons = 0, swaps = 0;

    // --- Threading & Pause Variables ---
    private Thread sortingThread;
    private volatile boolean isPaused = false;
    private volatile boolean isStopped = false;

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
                    "void mergeSort(arr, left, right) {",
                    "  if (left >= right) return;",
                    "  int mid = left + (right - left)/2;",
                    "  mergeSort(arr, left, mid);",
                    "  mergeSort(arr, mid + 1, right);",
                    "  merge(arr, left, mid, right);",
                    "}",
                    "",
                    "void merge(arr, left, mid, right) {",
                    "  create temp arrays L[] and R[]",
                    "  while (i < n1 && j < n2) {",
                    "    if (L[i] <= R[j])",
                    "      arr[k] = L[i];",
                    "    else",
                    "      arr[k] = R[j];",
                    "  }",
                    "  copy remaining elements",
                    "}"
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
        stopSorting();
        this.array = newArray;
        this.barNodes = new VBox[array.length];
        this.rects = new Rectangle[array.length];
        displayPane.getChildren().clear();

        // --- THE REAL FIX ---
        // বারগুলো নিচে সমান থাকবে, কিন্তু নিচে ২৫০ পিক্সেল ফাঁকা জায়গা (Padding) দেওয়া হলো
        // যাতে অ্যানিমেশনের সময় বারগুলো আরামে নিচে নামতে পারে!
        displayPane.setAlignment(javafx.geometry.Pos.BOTTOM_CENTER);
        displayPane.setPadding(new javafx.geometry.Insets(0, 0, 250, 0));

        comparisons = 0; swaps = 0; updateStats("Ready");

        for (int i = 0; i < array.length; i++) {
            Rectangle bar = new Rectangle(40, array[i] * 3); // হাইট ছোট রাখা হয়েছে
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

    // --- NEW: PAUSE & STOP LOGIC ---
    @FXML
    private void handlePause() {
        if (sortingThread == null || !sortingThread.isAlive()) return;

        isPaused = !isPaused;
        if (isPaused) {
            pauseBtn.setText("Resume");
            updateStats("Paused");
        } else {
            pauseBtn.setText("Pause");
            updateStats("Resumed");
        }
    }

    private void stopSorting() {
        isStopped = true;
        isPaused = false;
        if (sortingThread != null && sortingThread.isAlive()) {
            sortingThread.interrupt();
        }
        Platform.runLater(() -> {
            if (pauseBtn != null) pauseBtn.setText("Pause");
        });
    }

    @FXML
    private void handleSort() {
        stopSorting();
        isStopped = false;
        isPaused = false;
        if (pauseBtn != null) pauseBtn.setText("Pause");

        String algo = algoComboBox.getValue();

        sortingThread = new Thread(() -> {
            try {
                if (algo.equals("Bubble Sort")) bubbleSort();
                else if (algo.equals("Selection Sort")) selectionSort();
                else if (algo.equals("Insertion Sort")) insertionSort();
                else if (algo.equals("Merge Sort")) {
                    mergeSortRecursive(0, array.length - 1, 1);
                    markSorted();
                }
            } catch (InterruptedException e) {
                Platform.runLater(() -> updateStats("Sorting Stopped!"));
            }
        });
        sortingThread.start();
    }

    private void sleep() throws InterruptedException {
        if (isStopped) throw new InterruptedException();

        while (isPaused) {
            Thread.sleep(50);
            if (isStopped) throw new InterruptedException();
        }

        Thread.sleep((long) (1100 - speedSlider.getValue()));
    }

    // --- SORTING ALGORITHMS ---

    private void bubbleSort() throws InterruptedException {
        for (int i = 0; i < array.length - 1; i++) {
            trace(2); // bool swapped = false;
            boolean swapped = false;
            sleep(); // সিউডোকোড দেখার জন্য একটু বিরতি

            for (int j = 0; j < array.length - i - 1; j++) {

                trace(4); // if (arr[j] > arr[j+1])
                highlight(j, j+1, "compare");
                updateStats("Comparing " + array[j] + " & " + array[j+1]);
                sleep();
                comparisons++;

                if (array[j] > array[j+1]) {

                    trace(5); // swap(arr[j], arr[j+1]);  <-- এই লাইনটি যোগ করা হয়েছে
                    highlight(j, j+1, "swap");
                    animateJump(rects[j]); animateJump(rects[j+1]);

                    int temp = array[j]; array[j] = array[j+1]; array[j+1] = temp;
                    swapBars(j, j+1);
                    swaps++;
                    sleep();

                    trace(6); // swapped = true;
                    swapped = true;
                    sleep();
                }
                resetColor(j, j+1);
            }

            trace(9); // if (!swapped) break; <-- এই লাইনটি যোগ করা হয়েছে
            sleep();

            if(!swapped) break;
        }
        markSorted();
    }

    private void selectionSort() throws InterruptedException {
        for (int i = 0; i < array.length - 1; i++) {
            trace(1); // for (int i = 0; i < n-1; i++) {
            sleep();

            trace(2); // int min_idx = i;
            int minIdx = i;
            highlight(minIdx, "swap");
            updateStats("Assuming minimum is at index " + minIdx + " (Value: " + array[minIdx] + ")");
            sleep();

            for (int j = i + 1; j < array.length; j++) {
                trace(3); // for (int j = i+1; j < n; j++) {
                sleep();

                trace(4); // if (arr[j] < arr[min_idx]) {
                highlight(j, "compare");
                updateStats("Comparing " + array[j] + " with current min " + array[minIdx]);
                sleep();
                comparisons++;

                if (array[j] < array[minIdx]) {
                    trace(5);


                    if (minIdx != i) resetColor(minIdx);

                    minIdx = j;
                    highlight(minIdx, "swap");
                    updateStats("Found new minimum: " + array[minIdx]);
                    sleep();
                } else {
                    resetColor(j);
                }
            }

            trace(8);


            if (minIdx != i) {
                highlight(i, "compare");
                sleep();

                int temp = array[minIdx];
                array[minIdx] = array[i];
                array[i] = temp;

                animateJump(rects[minIdx]);
                animateJump(rects[i]);
                swapBars(minIdx, i);
                swaps++;
                sleep();

                resetColor(minIdx);
            }


            resetColor(i);
            setBarColor(i, "partition-merged");
        }


        setBarColor(array.length - 1, "partition-merged");
        markSorted();
    }

    private void insertionSort() throws InterruptedException {
        for (int i = 1; i < array.length; i++) {
            trace(1); // for (int i = 1; i < n; i++)
            sleep();

            trace(2); // int key = arr[i]; int j = i - 1;
            int key = array[i];
            int j = i - 1;
            highlight(i, "swap");
            sleep();


            trace(3); // while (j >= 0 && arr[j] > key)
            if (j >= 0) {
                highlight(j, "compare");
                updateStats("Comparing " + array[j] + " with key " + key);
            }
            comparisons++;
            sleep();

            while (j >= 0 && array[j] > key) {

                trace(4); // arr[j + 1] = arr[j];
                array[j + 1] = array[j];
                swapBars(j, j + 1);
                swaps++;
                sleep();

                trace(5); // j = j - 1;
                resetColor(j + 1);
                j--;
                sleep();


                trace(3); // while (j >= 0 && arr[j] > key)
                if (j >= 0) {
                    highlight(j, "compare");
                    updateStats("Comparing " + array[j] + " with key " + key);
                    comparisons++;
                }
                sleep();
            }


            if (j >= 0) {
                resetColor(j);
            }

            trace(7); // arr[j + 1] = key;
            array[j + 1] = key;
            resetColor(j + 1);
            sleep();
        }
        markSorted();
    }

    private void mergeSortRecursive(int left, int right, int depth) throws InterruptedException {
        trace(0); // void mergeSort(arr, left, right)
        updateStats("Calling mergeSort on indices " + left + " to " + right);
        sleep();

        trace(1);
        sleep();
        if (left >= right) {
            return;
        }

        trace(2);
        int mid = left + (right - left) / 2;
        updateStats("Splitting array at mid index " + mid);
        animateSplit(left, mid, right, depth);
        sleep();

        trace(3);
        updateStats("Recursively sorting left half (" + left + " to " + mid + ")");
        sleep();
        mergeSortRecursive(left, mid, depth + 1);

        trace(4);
        updateStats("Recursively sorting right half (" + (mid + 1) + " to " + right + ")");
        sleep();
        mergeSortRecursive(mid + 1, right, depth + 1);

        trace(5);
        updateStats("Merging left and right halves");
        sleep();
        merge(left, mid, right);

        animateMergeBack(left, right, depth);
        sleep();
    }

    private void merge(int left, int mid, int right) throws InterruptedException {
        trace(8);
        sleep();

        trace(9);
        updateStats("Creating temporary L[] and R[] arrays");
        int n1 = mid - left + 1;
        int n2 = right - mid;
        int[] L = new int[n1];
        int[] R = new int[n2];

        for (int i = 0; i < n1; ++i) L[i] = array[left + i];
        for (int j = 0; j < n2; ++j) R[j] = array[mid + 1 + j];

        Platform.runLater(() -> {
            for (int x = left; x <= right; x++) {
                rects[x].setOpacity(0.3);
            }
        });
        sleep();

        int i = 0, j = 0, k = left;

        while (i < n1 && j < n2) {
            trace(10); // while (i < n1 && j < n2)
            sleep();

            trace(11); // if (L[i] <= R[j])
            updateStats("Comparing " + L[i] + " and " + R[j]);
            comparisons++;
            sleep();

            if (L[i] <= R[j]) {
                trace(12); // arr[k] = L[i];
                array[k] = L[i];
                updateBar(k, array[k], "partition-merged");
                updateStats("Placed " + L[i] + " from left half");
                i++;
            } else {
                trace(14); // arr[k] = R[j];
                array[k] = R[j];
                updateBar(k, array[k], "partition-merged");
                updateStats("Placed " + R[j] + " from right half");
                j++;
            }
            k++;
            sleep();
        }

        trace(16); // copy remaining elements
        updateStats("Copying remaining elements...");
        sleep();

        while (i < n1) {
            array[k] = L[i];
            updateBar(k, array[k], "partition-merged");
            i++;
            k++;
            sleep();
        }

        while (j < n2) {
            array[k] = R[j];
            updateBar(k, array[k], "partition-merged");
            j++;
            k++;
            sleep();
        }

        updateStats("Merge complete for this level");
    }



    private void animateSplit(int left, int mid, int right, int depth) {
        Platform.runLater(() -> {

            for (int i = left; i <= mid; i++) {
                rects[i].setStyle("-fx-fill: #00FF7F;");
                TranslateTransition tt = new TranslateTransition(Duration.millis(400), barNodes[i]);
                tt.setToX(0);
                tt.setToY(depth * 50);
                tt.play();
            }

            for (int i = mid + 1; i <= right; i++) {
                rects[i].setStyle("-fx-fill: #FF1493;");
                TranslateTransition tt = new TranslateTransition(Duration.millis(400), barNodes[i]);
                tt.setToX(0);
                tt.setToY(depth * 50);
                tt.play();
            }
        });
    }

    private void animateMergeBack(int left, int right, int depth) {
        Platform.runLater(() -> {
            for (int i = left; i <= right; i++) {
                TranslateTransition tt = new TranslateTransition(Duration.millis(400), barNodes[i]);
                tt.setToX(0);
                tt.setToY((depth - 1) * 50);
                tt.play();
            }
        });
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

    private void setBarColor(int index, String styleClass) {
        Platform.runLater(() -> {
            rects[index].setStyle("");
            rects[index].getStyleClass().removeAll("vis-bar", "bar-compare", "bar-swap", "bar-left-part", "bar-right-part", "bar-merged");
            rects[index].getStyleClass().add(styleClass);
        });
    }

    private void updateBar(int index, int value, String styleClass) {
        Platform.runLater(() -> {
            rects[index].setOpacity(1.0);
            rects[index].setHeight(value * 3);
            Label valLabel = (Label) barNodes[index].getChildren().get(0);
            valLabel.setText(String.valueOf(value));

            rects[index].setStyle("-fx-fill: #0ea5e9;");
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

    @FXML
    private void handleBack() throws IOException {
        stopSorting(); // Stop animation before going back
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        Stage stage = (Stage) displayPane.getScene().getWindow();
        stage.setScene(scene);
        stage.setResizable(false);
    }
}