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
    int size = 0;

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
        size++;
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
        size++;
        return tail;
    }

    public Node insertAtPosition(int value, int index) {
        if(index < 0 || index > size) {return null;}
        if (index == 0) {
            return insertAtHead(value);
        }
        if(index == size) {return insertAtTail(value);}

        Node newNode = new Node(value);
        Node current = head;
        int id = 0;
        while(current != null && id < index - 1)
        {
            current = current.next;
            id++;
        }
        newNode.next = current.next;
        current.next = newNode;
        size++;
        return newNode;
    }

    public int getSize(){
        return size;
    }

    public Node getHead()
    {
        return head;
    }
    public Node getTail()
    {
        return tail;
    }

    public Node deleteNodeAtPosition(int pos){
        if(head == null) return null;
        Node del ;
        if(pos < 0 || pos >= size) {return null;}
        if(pos == 0) {
            del = head;
            head = head.next;
            return del ;
        }
        Node prev = head;
        int i = 0;
        while(i < pos - 1)
        {
            prev = prev.next;
            i++;
        }
        del = prev.next;
       prev.next = prev.next.next;
       size--;
       return del;
    }
    public Node deleteNode(int value) {
        if (head == null) return null;

        if (head.data == value) {
           Node del = head;
            head = head.next;

            // If list becomes empty, update tail
            if (head == null) {
                tail = null;
            }
            size --;
            return del;
        }

        Node curr = head;
        while (curr.next != null && curr.next.data != value) curr = curr.next;
        if (curr.next != null)
        {
            Node del = curr.next;
            curr.next = curr.next.next;
            // If we deleted the last node, update tail
            if (curr.next == null) {
                tail = curr;
            }
            size--;
            return del;
        }
        return null;
    }

    public int countValue(int v)
    {
        if(head == null) return 0;
        Node curr = head;
        int count = 0;
        while(curr != null)
        {
            if(curr.data == v) count++;
            curr = curr.next;
        }
        return count;
    }

}
