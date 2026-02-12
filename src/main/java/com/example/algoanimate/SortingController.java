package com.example.algoanimate;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;

public class SortingController {

    @FXML
    private Pane displayPane;

    private int[] array;
    private Rectangle[] bars;
    private static final int NUM_BARS = 20; // Number of bars
    private static final int SPACING = 10;

    @FXML
    public void initialize() {
        handleRandomize(); // Draw bars when screen opens
    }

    @FXML
    private void handleRandomize() {
        displayPane.getChildren().clear();
        array = new int[NUM_BARS];
        bars = new Rectangle[NUM_BARS];
        Random rand = new Random();

        // Calculate width dynamically
        double startX = 50;
        double barWidth = 40;

        for (int i = 0; i < NUM_BARS; i++) {
            array[i] = rand.nextInt(400) + 50; // Random height between 50 and 450

            Rectangle rect = new Rectangle();
            rect.setX(startX + i * (barWidth + SPACING));
            rect.setY(600 - array[i]); // Align to bottom
            rect.setWidth(barWidth);
            rect.setHeight(array[i]);
            rect.setFill(Color.web("#3b82f6")); // Blue Color

            bars[i] = rect;
            displayPane.getChildren().add(rect);
        }
    }

    @FXML
    private void handleSort() {
        // Run sorting in a separate thread so UI doesn't freeze
        new Thread(() -> {
            try {
                bubbleSort();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void bubbleSort() throws InterruptedException {
        for (int i = 0; i < NUM_BARS - 1; i++) {
            for (int j = 0; j < NUM_BARS - i - 1; j++) {

                // Color: Red (Comparing)
                setColor(bars[j], Color.RED);
                setColor(bars[j + 1], Color.RED);
                Thread.sleep(200); // Slow down to see animation

                if (array[j] > array[j + 1]) {
                    // Swap Logic in Array
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;

                    // Swap Logic in UI (Animation)
                    swapRectangles(bars[j], bars[j + 1]);

                    // Swap Logic in Rectangle Array
                    Rectangle tempRect = bars[j];
                    bars[j] = bars[j + 1];
                    bars[j + 1] = tempRect;
                }

                // Color: Blue (Reset)
                setColor(bars[j], Color.web("#3b82f6"));
                setColor(bars[j + 1], Color.web("#3b82f6"));
            }
            // Color: Green (Sorted part)
            setColor(bars[NUM_BARS - 1 - i], Color.LIGHTGREEN);
        }
        setColor(bars[0], Color.LIGHTGREEN); // Last one is also sorted
    }

    // Helper to change color safely
    private void setColor(Rectangle r, Color c) {
        Platform.runLater(() -> r.setFill(c));
    }

    // Helper to swap positions safely
    private void swapRectangles(Rectangle r1, Rectangle r2) {
        Platform.runLater(() -> {
            double x1 = r1.getX();
            double x2 = r2.getX();
            r1.setX(x2);
            r2.setX(x1);
        });
    }

    @FXML
    private void handleBack() throws IOException {
        // Go back to Home Screen
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1100, 750);
        Stage stage = (Stage) displayPane.getScene().getWindow();
        stage.setScene(scene);
    }
}