package com.example.algoanimate;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;



public class SinglyLinkedListOperation  {

    Node head;

    static class Node {
        int data;
        Node next;
        Node(int data) { this.data = data; next = null;}
    }

    public void insertAtHead() {
//        String valueText = inputField.getText(); // user input নাও
//        int value = Integer.parseInt(valueText); // convert String to int
//        Node newNode = new Node(value);
//        newNode.next = head;
//        head = newNode;
//        System.out.println(value + " inserted at head");

    }

    public void insertAtTail(int value) {
        Node newNode = new Node(value);
        if (head == null) {
            head = newNode;
            return;
        }
        Node curr = head;
        while (curr.next != null) curr = curr.next;
        curr.next = newNode;
    }

    public void deleteNode(int value) {
        if (head == null) return;
        if (head.data == value) { head = head.next; return; }
        Node curr = head;
        while (curr.next != null && curr.next.data != value) curr = curr.next;
        if (curr.next != null) curr.next = curr.next.next;
    }

}
