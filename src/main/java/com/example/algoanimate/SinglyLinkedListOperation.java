package com.example.algoanimate;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;



public class SinglyLinkedListOperation  {



    static class Node {
        int data;
        Node next;
        Node(int data) { this.data = data; next = null;}
    }

    Node head;
    Node tail;

    public Node insertAtHead(int data) {
        Node newNode = new Node(data);

        if(head == null) {
            head = newNode;
            tail = newNode;
        }
        else {
            newNode.next = head;
            head = newNode;
        }
        return head;
    }

    public Node insertAtTail(int value) {
        Node newNode = new Node(value);
        if (head == null) {
            head = tail =newNode;
        }
        else {
            tail.next = newNode;
            tail = newNode;
        }
        return tail;
    }

    public Node getHead()
    {
        return head;
    }
    public Node getTail()
    {
        return tail;
    }
    public void deleteNode(int value) {
        if (head == null) return;
        if (head.data == value) { head = head.next; return; }
        Node curr = head;
        while (curr.next != null && curr.next.data != value) curr = curr.next;
        if (curr.next != null) curr.next = curr.next.next;
    }

}
