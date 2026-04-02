package com.example.algoanimate;

public class DoublyLinkedListOperation {

    public class Node {
        int data;
        Node prev;
        Node next;

        Node(int data) {
            this.data = data;
            this.prev = null;
            this.next = null;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    public DoublyLinkedListOperation() {
        head = null;
        tail = null;
        size = 0;
    }

    public Node insertAtHead(int data) {
        Node newNode = new Node(data);

        if (head == null) {
            head = tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
        size++;
        return newNode;
    }

    public Node insertAtTail(int data) {
        Node newNode = new Node(data);

        if (head == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
        return newNode;
    }

    public Node insertAtPosition(int data, int position) {
        if (position < 0 || position > size) {
            throw new IllegalArgumentException("Invalid position");
        }

        if (position == 0) {
            return insertAtHead(data);
        }

        if (position == size) {
            return insertAtTail(data);
        }

        Node newNode = new Node(data);
        Node current = head;

        for (int i = 0; i < position; i++) {
            current = current.next;
        }

        // current is now at the position where new node should be inserted
        newNode.prev = current.prev;
        newNode.next = current;
        current.prev.next = newNode;
        current.prev = newNode;

        size++;
        return newNode;
    }

    public void deleteNodeAtPosition(int position) {
        if (position < 0 || position >= size || head == null) {
            return;
        }

        if (position == 0) {
            // Delete head
            head = head.next;
            if (head != null) {
                head.prev = null;
            } else {
                tail = null;
            }
        } else if (position == size - 1) {
            // Delete tail
            tail = tail.prev;
            if (tail != null) {
                tail.next = null;
            } else {
                head = null;
            }
        } else {
            // Delete middle node
            Node current = head;
            for (int i = 0; i < position; i++) {
                current = current.next;
            }
            current.prev.next = current.next;
            current.next.prev = current.prev;
        }

        size--;
    }

    public boolean updateNodeAtPosition(int position, int newValue) {
        if (position < 0 || position >= size) {
            return false;
        }

        Node current = head;
        for (int i = 0; i < position; i++) {
            current = current.next;
        }

        current.data = newValue;
        return true;
    }

    // Add this method to DoublyLinkedListOperation class
    public int deleteAllOccurrences(int value) {
        if (head == null) return 0;

        int deletedCount = 0;

        // Delete all matching nodes from head
        while (head != null && head.data == value) {
            head = head.next;
            if (head != null) {
                head.prev = null;
            } else {
                tail = null;
            }
            deletedCount++;
            size--;
        }

        if (head == null) {
            return deletedCount;
        }

        // Delete matching nodes from the rest of the list
        Node current = head;
        while (current != null) {
            if (current.data == value) {
                // Remove current node
                if (current.prev != null) {
                    current.prev.next = current.next;
                }
                if (current.next != null) {
                    current.next.prev = current.prev;
                }

                // Update tail if we're deleting the last node
                if (current == tail) {
                    tail = current.prev;
                }

                deletedCount++;
                size--;

                // Move to next node (which is now current.next)
                current = current.next;
            } else {
                current = current.next;
            }
        }

        return deletedCount;
    }

    public Node getHead() {
        return head;
    }

    public Node getTail() {
        return tail;
    }

    public int getSize() {
        return size;
    }
}