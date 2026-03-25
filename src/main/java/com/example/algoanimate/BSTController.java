package com.example.algoanimate;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class BSTController {

    @FXML private TextField inputField;
    @FXML private Pane visualizationPane;
    @FXML private ListView<String> codeListView;
    @FXML private Label lblStatus, lblCurrentNode, lblComparison, lblTraversal;

    private Node root;
    private String traversalOutput = "";

    private final double NODE_RADIUS = 22;
    private final double VERTICAL_GAP = 80;

    private class Node {
        int value;
        Node left, right;
        StackPane nodeUI;
        Line leftEdge, rightEdge;

        public Node(int value) {
            this.value = value;
        }
    }

    @FXML
    public void initialize() {
        loadPseudocode("insert");
        Platform.runLater(() -> {
            lblStatus.setText("Status: Empty Tree. Insert or Gen Random.");
        });
    }

    // --- Core BST Algorithms ---

    private Node insertRecursive(Node current, int value, double x, double y, double hGap) throws InterruptedException {
//        loadPseudocode("insert");

        if (current == null) {
            highlightCode(2);
            return createNodeUI(value, x, y, true);
        }

        highlightNodeUI(current, true);
        setStats(String.valueOf(current.value), "N/A");

        if (value < current.value) {
            highlightCode(5);
            setStats(String.valueOf(current.value), value + " < " + current.value + " (Go Left)");
            sleepAnimation();

            highlightNodeUI(current, false);
            current.left = insertRecursive(current.left, value, x - hGap, y + VERTICAL_GAP, hGap / 2);
            connectNodes(current, current.left, true);
        } else if (value > current.value) {
            highlightCode(7);
            setStats(String.valueOf(current.value), value + " > " + current.value + " (Go Right)");
            sleepAnimation();

            highlightNodeUI(current, false);
            current.right = insertRecursive(current.right, value, x + hGap, y + VERTICAL_GAP, hGap / 2);
            connectNodes(current, current.right, false);
        } else {
            highlightNodeUI(current, false);
            updateStatus("Value " + value + " already exists.", true);
            sleepAnimation();
        }

        return current;
    }

    private Node searchRecursive(Node current, int value) throws InterruptedException {
//        loadPseudocode("search");

        if (current == null) {
            highlightCode(2);
            return null;
        }

        highlightNodeUI(current, true);
        setStats(String.valueOf(current.value), "Comparing with " + value);
        sleepAnimation();

        if (current.value == value) {
            highlightCode(4);
            foundNodeUI(current);
            return current;
        }

        highlightNodeUI(current, false);

        if (value < current.value) {
            highlightCode(7);
            setStats(String.valueOf(current.value), value + " < " + current.value + " (Go Left)");
            return searchRecursive(current.left, value);
        } else {
            highlightCode(6);
            setStats(String.valueOf(current.value), value + " > " + current.value + " (Go Right)");
            return searchRecursive(current.right, value);
        }
    }

    // --- DELETION WITH REDRAW LOGIC ---

    // --- DELETION WITH REDRAW LOGIC ---

    private Node logicalDeleteVisual(Node root, int value) throws InterruptedException {
        if (root == null) {
            highlightCode(1);
            sleepAnimation();
            return null;
        }

        highlightNodeUI(root, true);
        sleepAnimation();

        if (value < root.value) {
            highlightCode(3);
            highlightNodeUI(root, false);
            root.left = logicalDeleteVisual(root.left, value);
        } else if (value > root.value) {
            highlightCode(5);
            highlightNodeUI(root, false);
            root.right = logicalDeleteVisual(root.right, value);
        } else {
            // Node Found!
            highlightCode(6);
            foundNodeUI(root);
            sleepAnimation();

            if (root.left == null) {
                highlightCode(7);
                sleepAnimation();
                return root.right;
            } else if (root.right == null) {
                highlightCode(8);
                sleepAnimation();
                return root.left;
            }

            // Node with two children
            highlightCode(9);
            Node temp = minValueNode(root.right);

            highlightCode(10);
            root.value = temp.value;
            // Update UI text instantly to show value swap
            Platform.runLater(() -> {
                Text text = (Text) root.nodeUI.getChildren().get(1);
                text.setText(String.valueOf(root.value));
            });
            sleepAnimation();

            highlightCode(11);
            highlightNodeUI(root, false);
            root.right = logicalDeleteVisual(root.right, temp.value);
        }
        return root;
    }

    private void redrawTree() {
        Platform.runLater(() -> {
            visualizationPane.getChildren().clear();
            if (root != null) {
                double startX = visualizationPane.getWidth() > 0 ? visualizationPane.getWidth() / 2 : 400;
                double startY = 40;
                double dynamicGap = startX / 2.5;
                drawNodeRecursive(root, startX, startY, dynamicGap);
            }
        });
    }

    private void drawNodeRecursive(Node current, double x, double y, double hGap) {
        if (current == null) return;

        current.nodeUI = null;
        current.leftEdge = null;
        current.rightEdge = null;

        createNodeUI(current, x, y);

        if (current.left != null) {
            drawNodeRecursive(current.left, x - hGap, y + VERTICAL_GAP, hGap / 2);
            connectNodes(current, current.left, true);
        }
        if (current.right != null) {
            drawNodeRecursive(current.right, x + hGap, y + VERTICAL_GAP, hGap / 2);
            connectNodes(current, current.right, false);
        }
    }

    private Node minValueNode(Node node) {
        Node current = node;
        while (current.left != null) current = current.left;
        return current;
    }

    // --- ADVANCED OPERATIONS ---

    private Node findPredecessor(Node root, int val) throws InterruptedException {
        highlightCode(1); // Node pred = null, curr = root;
        Node pred = null;
        Node curr = root;

        while (curr != null) {
            highlightCode(2); // while (curr != null)
            sleepAnimation();

            highlightNodeUI(curr, true);
            setStats(String.valueOf(curr.value), "Checking for Predecessor");

            highlightCode(3); // if (val <= curr.key)
            sleepAnimation();
            highlightNodeUI(curr, false);

            if (val <= curr.value) {
                highlightCode(4); // curr = curr.left;
                curr = curr.left;
            } else {
                highlightCode(6); // pred = curr;
                pred = curr;
                highlightCode(7); // curr = curr.right;
                curr = curr.right;
            }
        }
        highlightCode(10); // return pred;
        return pred;
    }

    private Node findSuccessor(Node root, int val) throws InterruptedException {
        highlightCode(1); // Node succ = null, curr = root;
        Node succ = null;
        Node curr = root;

        while (curr != null) {
            highlightCode(2); // while (curr != null)
            sleepAnimation();

            highlightNodeUI(curr, true);
            setStats(String.valueOf(curr.value), "Checking for Successor");

            highlightCode(3); // if (val >= curr.key)
            sleepAnimation();
            highlightNodeUI(curr, false);

            if (val >= curr.value) {
                highlightCode(4); // curr = curr.right;
                curr = curr.right;
            } else {
                highlightCode(6); // succ = curr;
                succ = curr;
                highlightCode(7); // curr = curr.left;
                curr = curr.left;
            }
        }
        highlightCode(10); // return succ;
        return succ;
    }

    // --- TRAVERSALS WITH PRINTING ---

    private void traverseInOrder(Node node) throws InterruptedException {
        if (node == null) return;

        traverseInOrder(node.left);

        highlightNodeUI(node, true);
        highlightCode(3);
        setStats(String.valueOf(node.value), "Printing Node");
        sleepAnimation();
        foundNodeUI(node);

        appendTraversalOutput(node.value);

        traverseInOrder(node.right);
    }

    private void traversePreOrder(Node node) throws InterruptedException {
        if (node == null) return;

        highlightNodeUI(node, true);
        highlightCode(2);
        setStats(String.valueOf(node.value), "Printing Node");
        sleepAnimation();
        foundNodeUI(node);

        appendTraversalOutput(node.value);

        traversePreOrder(node.left);
        traversePreOrder(node.right);
    }

    private void traversePostOrder(Node node) throws InterruptedException {
        if (node == null) return;

        traversePostOrder(node.left);
        traversePostOrder(node.right);

        highlightNodeUI(node, true);
        highlightCode(4);
        setStats(String.valueOf(node.value), "Printing Node");
        sleepAnimation();
        foundNodeUI(node);

        appendTraversalOutput(node.value);
    }

    private void appendTraversalOutput(int val) {
        traversalOutput += val + " ";
        Platform.runLater(() -> lblTraversal.setText(traversalOutput));
    }

    // --- JavaFX Visualization Helpers ---

    private Node createNodeUI(int value, double x, double y, boolean animate) {
        Node newNode = new Node(value);
        createNodeUI(newNode, x, y);

        if (animate) {
            newNode.nodeUI.setOpacity(0);
            Platform.runLater(() -> {
                FadeTransition ft = new FadeTransition(Duration.millis(300), newNode.nodeUI);
                ft.setToValue(1.0);
                ft.play();
            });
        }
        return newNode;
    }

    private void createNodeUI(Node node, double x, double y) {
        Circle circle = new Circle(NODE_RADIUS);
        circle.getStyleClass().add("tree-node");

        Text text = new Text(String.valueOf(node.value));
        text.getStyleClass().add("tree-node-text");

        StackPane nodeStack = new StackPane(circle, text);
        nodeStack.setLayoutX(x - NODE_RADIUS);
        nodeStack.setLayoutY(y - NODE_RADIUS);

        node.nodeUI = nodeStack;
        Platform.runLater(() -> visualizationPane.getChildren().add(nodeStack));
    }

    private void connectNodes(Node parent, Node child, boolean isLeft) {
        if (parent == null || child == null) return;

        Platform.runLater(() -> {
            Line line;
            if (isLeft) {
                if (parent.leftEdge == null) {
                    parent.leftEdge = new Line();
                    parent.leftEdge.getStyleClass().add("tree-edge");
                    visualizationPane.getChildren().add(parent.leftEdge);
                    parent.leftEdge.toBack();
                }
                line = parent.leftEdge;
            } else {
                if (parent.rightEdge == null) {
                    parent.rightEdge = new Line();
                    parent.rightEdge.getStyleClass().add("tree-edge");
                    visualizationPane.getChildren().add(parent.rightEdge);
                    parent.rightEdge.toBack();
                }
                line = parent.rightEdge;
            }

            line.setStartX(parent.nodeUI.getLayoutX() + NODE_RADIUS);
            line.setStartY(parent.nodeUI.getLayoutY() + NODE_RADIUS + 5);
            line.setEndX(child.nodeUI.getLayoutX() + NODE_RADIUS);
            line.setEndY(child.nodeUI.getLayoutY() + NODE_RADIUS);
        });
    }

    private void highlightNodeUI(Node node, boolean highlightOn) {
        Platform.runLater(() -> {
            Circle c = (Circle) node.nodeUI.getChildren().get(0);
            c.getStyleClass().removeAll("tree-node", "tree-node-highlight", "tree-node-found");
            if (highlightOn) c.getStyleClass().add("tree-node-highlight");
            else c.getStyleClass().add("tree-node");
        });
    }

    private void foundNodeUI(Node node) {
        Platform.runLater(() -> {
            Circle c = (Circle) node.nodeUI.getChildren().get(0);
            c.getStyleClass().removeAll("tree-node", "tree-node-highlight");
            c.getStyleClass().add("tree-node-found");
        });
    }

    private void resetAllHighlights(Node node) {
        if (node == null) return;
        Platform.runLater(() -> {
            Circle c = (Circle) node.nodeUI.getChildren().get(0);
            c.getStyleClass().removeAll("tree-node-highlight", "tree-node-found");
            if (!c.getStyleClass().contains("tree-node")) c.getStyleClass().add("tree-node");
        });
        resetAllHighlights(node.left);
        resetAllHighlights(node.right);
    }

    // --- FXML Button Handler Methods ---

    @FXML
    private void handleInsert() {
        if (!validateInput()) return;
        int value = Integer.parseInt(inputField.getText());
        inputField.clear();
        loadPseudocode("insert");
        updateStatus("Inserting: " + value, false);
        resetAllHighlights(root);
        Platform.runLater(() -> lblTraversal.setText("Printed: "));

        new Thread(() -> {
            try {
                double startX = visualizationPane.getWidth() > 0 ? visualizationPane.getWidth() / 2 : 400;
                double startY = 40;
                double dynamicGap = startX / 2.5;

                root = insertRecursive(root, value, startX, startY, dynamicGap);
                Platform.runLater(() -> visualizationPane.requestLayout());

                updateStatus("Inserted " + value, false);
                sleepAnimation();
                Platform.runLater(() -> lblStatus.setText("Status: Ready"));
            } catch (InterruptedException e) {}
        }).start();
    }


    @FXML
    private void handleDelete() {
        if (!validateInput()) return;
        int value = Integer.parseInt(inputField.getText());
        inputField.clear();

        loadPseudocode("delete"); // Load proper delete pseudocode
        updateStatus("Deleting " + value + "...", false);
        resetAllHighlights(root);
        Platform.runLater(() -> lblTraversal.setText("Printed: "));

        new Thread(() -> {
            try {
                root = logicalDeleteVisual(root, value);
                redrawTree();
                updateStatus("Delete operation finished.", false);
            } catch (InterruptedException e) {}
        }).start();
    }

    @FXML
    private void handleSearch() {
        if (!validateInput()) return;
        int value = Integer.parseInt(inputField.getText());
        inputField.clear();
        loadPseudocode("search");
        updateStatus("Searching: " + value, false);
        resetAllHighlights(root);

        new Thread(() -> {
            try {
                Node found = searchRecursive(root, value);
                if (found != null) updateStatus("FOUND " + value + "!", false);
                else updateStatus("Value " + value + " not found.", true);
            } catch (InterruptedException e) {}
        }).start();
    }


    @FXML
    private void handlePredecessor() {
        if (!validateInput()) return;
        int value = Integer.parseInt(inputField.getText());
        inputField.clear();

        loadPseudocode("predecessor"); // <-- NEW

        updateStatus("Finding Predecessor of " + value, false);
        resetAllHighlights(root);

        new Thread(() -> {
            try {
                Node pred = findPredecessor(root, value);
                if (pred != null) {
                    foundNodeUI(pred);
                    updateStatus("Predecessor is: " + pred.value, false);
                } else updateStatus("No Predecessor found!", true);
            } catch (InterruptedException e) {}
        }).start();
    }

    @FXML
    private void handleSuccessor() {
        if (!validateInput()) return;
        int value = Integer.parseInt(inputField.getText());
        inputField.clear();

        loadPseudocode("successor"); // <-- NEW

        updateStatus("Finding Successor of " + value, false);
        resetAllHighlights(root);

        new Thread(() -> {
            try {
                Node succ = findSuccessor(root, value);
                if (succ != null) {
                    foundNodeUI(succ);
                    updateStatus("Successor is: " + succ.value, false);
                } else updateStatus("No Successor found!", true);
            } catch (InterruptedException e) {}
        }).start();
    }

    // --- NEW DIRECT TRAVERSAL METHODS ---

    @FXML
    private void handleInOrder() {
        if (root == null) { updateStatus("Tree is empty!", true); return; }

        traversalOutput = "Printed: ";
        Platform.runLater(() -> lblTraversal.setText(traversalOutput));
        updateStatus("In-Order Traversal Started", false);
        resetAllHighlights(root);
        loadPseudocode("inorder");

        new Thread(() -> {
            try {
                traverseInOrder(root);
                updateStatus("In-Order Traversal Complete", false);
                setStats("N/A", "N/A");
            } catch (InterruptedException e) {}
        }).start();
    }

    @FXML
    private void handlePreOrder() {
        if (root == null) { updateStatus("Tree is empty!", true); return; }

        traversalOutput = "Printed: ";
        Platform.runLater(() -> lblTraversal.setText(traversalOutput));
        updateStatus("Pre-Order Traversal Started", false);
        resetAllHighlights(root);
        loadPseudocode("preorder");

        new Thread(() -> {
            try {
                traversePreOrder(root);
                updateStatus("Pre-Order Traversal Complete", false);
                setStats("N/A", "N/A");
            } catch (InterruptedException e) {}
        }).start();
    }

    @FXML
    private void handlePostOrder() {
        if (root == null) { updateStatus("Tree is empty!", true); return; }

        traversalOutput = "Printed: ";
        Platform.runLater(() -> lblTraversal.setText(traversalOutput));
        updateStatus("Post-Order Traversal Started", false);
        resetAllHighlights(root);
        loadPseudocode("postorder");

        new Thread(() -> {
            try {
                traversePostOrder(root);
                updateStatus("Post-Order Traversal Complete", false);
                setStats("N/A", "N/A");
            } catch (InterruptedException e) {}
        }).start();
    }

    @FXML
    private void handleReset() {
        root = null;
        Platform.runLater(() -> {
            visualizationPane.getChildren().clear();
            inputField.clear();
            lblStatus.setText("Status: Empty Tree");
            lblTraversal.setText("Printed: ");
            setStats("N/A", "N/A");
        });
        loadPseudocode("insert");
    }

    @FXML
    private void handleRandomize() {
        handleReset();

        new Thread(() -> {
            try {
                int[] randomValues = generateRandomBSTValues();
                for (int value : randomValues) {
                    Platform.runLater(() -> {
                        inputField.setText(String.valueOf(value));
                        updateStatus("Inserting: " + value, false);
                    });

                    double startX = visualizationPane.getWidth() > 0 ? visualizationPane.getWidth() / 2 : 400;
                    double startY = 40;
                    double dynamicGap = startX / 2.5;

                    root = insertRecursive(root, value, startX, startY, dynamicGap);

                    Platform.runLater(() -> {
                        inputField.clear();
                        visualizationPane.requestLayout();
                    });
                    Thread.sleep(400);
                }
                updateStatus("Random Tree Generated!", false);
            } catch (InterruptedException e) {}
        }).start();
    }

    private int[] generateRandomBSTValues() {
        java.util.Random rand = new java.util.Random();
        int rootVal = 50;
        int l1 = rand.nextInt(20) + 20;
        int r1 = rand.nextInt(20) + 60;
        int l1_l = rand.nextInt(10) + 5;
        int l1_r = rand.nextInt(10) + 40;
        int r1_l = rand.nextInt(10) + 50;
        int r1_r = rand.nextInt(10) + 80;
        return new int[]{rootVal, l1, r1, l1_l, l1_r, r1_l, r1_r};
    }

    @FXML
    private void handleBack() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        Stage stage = (Stage) visualizationPane.getScene().getWindow();
        stage.setScene(scene);
        stage.setResizable(false);
    }

    // --- Internal Helpers ---

    private boolean validateInput() {
        try {
            Integer.parseInt(inputField.getText());
            return true;
        } catch (NumberFormatException e) {
            updateStatus("Invalid input! Use Integers.", true);
            return false;
        }
    }

    private void updateStatus(String msg, boolean isError) {
        Platform.runLater(() -> {
            lblStatus.setText("Status: " + msg);
            lblStatus.setStyle(isError ? "-fx-text-fill: #ef4444;" : "-fx-text-fill: #38bdf8;");
        });
    }

    private void setStats(String currNode, String comp) {
        Platform.runLater(() -> {
            lblCurrentNode.setText("Current Node: " + currNode);
            lblComparison.setText("Comparison: " + comp);
        });
    }

    private void highlightCode(int line) {
        Platform.runLater(() -> codeListView.getSelectionModel().select(line));
    }

    private void sleepAnimation() throws InterruptedException {
        Thread.sleep(600);
    }

    private void loadPseudocode(String algo) {
        codeListView.getItems().clear();
        if (algo.equals("insert")) {
            codeListView.getItems().addAll(
                    "Node insert(Node root, int key) {",
                    "  if (root == null)",
                    "    return createNode(key);",
                    "",
                    "  if (key < root.key)",
                    "    root.left = insert(root.left, key);",
                    "  else if (key > root.key)",
                    "    root.right = insert(root.right, key);",
                    "",
                    "  return root;",
                    "}"
            );
        } else if (algo.equals("search")) {
            codeListView.getItems().addAll(
                    "Node search(Node root, int key) {",
                    "  if (root == null)",
                    "    return null;",
                    "  if (root.key == key)",
                    "    return root;",
                    "  if (root.key < key)",
                    "    return search(root.right, key);",
                    "  return search(root.left, key);",
                    "}"
            );
        } else if (algo.equals("inorder")) {
            codeListView.getItems().addAll(
                    "void inOrder(Node node) {",
                    "  if (node == null) return;",
                    "  inOrder(node.left);",
                    "  print(node.key);",
                    "  inOrder(node.right);",
                    "}"
            );
        } else if (algo.equals("preorder")) {
            codeListView.getItems().addAll(
                    "void preOrder(Node node) {",
                    "  if (node == null) return;",
                    "  print(node.key);",
                    "  preOrder(node.left);",
                    "  preOrder(node.right);",
                    "}"
            );
        } else if (algo.equals("postorder")) {
            codeListView.getItems().addAll(
                    "void postOrder(Node node) {",
                    "  if (node == null) return;",
                    "  postOrder(node.left);",
                    "  postOrder(node.right);",
                    "  print(node.key);",
                    "}"
            );
        }

        else if (algo.equals("delete")) {
            codeListView.getItems().addAll(
                    "Node delete(Node root, int key) {",          // 0
                    "  if (root == null) return root;",           // 1
                    "  if (key < root.key)",                      // 2
                    "    root.left = delete(root.left, key);",    // 3
                    "  else if (key > root.key)",                 // 4
                    "    root.right = delete(root.right, key);",  // 5
                    "  else {",                                   // 6
                    "    if (root.left == null) return root.right;",// 7
                    "    if (root.right == null) return root.left;",// 8
                    "    Node temp = minValue(root.right);",      // 9
                    "    root.key = temp.key;",                   // 10
                    "    root.right = delete(root.right, temp.key);",// 11
                    "  }",                                        // 12
                    "  return root;",                             // 13
                    "}"                                           // 14
            );
        }

     else if (algo.equals("predecessor")) {
        codeListView.getItems().addAll(
                "Node predecessor(Node root, int key) {", // 0
                "  Node pred = null, curr = root;",       // 1
                "  while (curr != null) {",               // 2
                "    if (key <= curr.key)",               // 3
                "      curr = curr.left;",                // 4
                "    else {",                             // 5
                "      pred = curr;",                     // 6
                "      curr = curr.right;",               // 7
                "    }",                                  // 8
                "  }",                                    // 9
                "  return pred;",                         // 10
                "}"                                       // 11
        );
    } else if (algo.equals("successor")) {
        codeListView.getItems().addAll(
                "Node successor(Node root, int key) {",   // 0
                "  Node succ = null, curr = root;",       // 1
                "  while (curr != null) {",               // 2
                "    if (key >= curr.key)",               // 3
                "      curr = curr.right;",               // 4
                "    else {",                             // 5
                "      succ = curr;",                     // 6
                "      curr = curr.left;",                // 7
                "    }",                                  // 8
                "  }",                                    // 9
                "  return succ;",                         // 10
                "}"                                       // 11
        );
    }
    }
}