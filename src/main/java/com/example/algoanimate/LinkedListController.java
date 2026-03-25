package com.example.algoanimate;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;


public class LinkedListController {

    @FXML
    private Button btnSinglyLinkedList;

    @FXML
    private Button btnDoublyLinkedList;

    @FXML
    private Button btnCircularLinkedList;

    @FXML
    private Button btnBack;

    @FXML
    private Label lblTitle;

    @FXML
    private void onSinglyLinkedListClick() {
        System.out.println("Singly LinkedList clicked!");
        try {
            // ১. Sorting FXML file load
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SinglyLinkedList-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);


            Stage stage = (Stage) btnSinglyLinkedList.getScene().getWindow();

            stage.setScene(scene);

            stage.setResizable(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDoublyLinkedListClick() {
        System.out.println("Doubly LinkedList clicked!");
        // এখানে তুমি Doubly LinkedList visualizer open করতে পারো
        try {
            // ১. Sorting FXML file load
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DoublyLinkedList-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
            Stage stage = (Stage) btnDoublyLinkedList.getScene().getWindow();

            stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCircularLinkedListClick() {
        System.out.println("Circular LinkedList clicked!");
        //  Circular LinkedList visualizer open
        try {
            // ১. Sorting FXML file load
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CircularLinkedList-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);

            Stage stage = (Stage) btnCircularLinkedList.getScene().getWindow();

            stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onBackClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}




