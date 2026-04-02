package com.example.algoanimate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.scene.Cursor;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

public class GraphController {

    @FXML
    private Button btnBack;
    @FXML
    private ChoiceBox<String> GraphChoiceBox;
    @FXML
    private Pane animationPane;
    @FXML
    private ListView<String> actionListView;
    @FXML
    private ListView<String> nodeListView;
    @FXML
    private Label lblStatus;
    @FXML
    private Label lblSize;

    // Graph data structures
    private GraphOperation graph;
    private Map<Integer, Circle> nodeCircles;
    private Map<String, Line> edgeLines;
    private Map<Circle, Integer> circleToData;
    private Map<Integer, Text> nodeLabels;

    // Selection state
    private Circle selectedNode = null;
    private boolean isCreatingGraph = false;
    private boolean isEditMode = false;
    private Button doneButton;

    // For edge creation
    private Circle firstNodeForEdge = null;

    @FXML
    private void onBackClick(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        graph = new GraphOperation();
        nodeCircles = new HashMap<>();
        edgeLines = new HashMap<>();
        circleToData = new HashMap<>();
        nodeLabels = new HashMap<>();

        GraphChoiceBox.getItems().addAll(
                "Create",
                "Edit",
                "BFS",
                "DFS",
                "Detect Cycle",
                "Clear"
        );

        GraphChoiceBox.setOnAction(e -> handleChoiceSelection());

        clearGraphDisplay();
        updateStatus("Ready");
        updateSize(0);
    }

    private void handleChoiceSelection() {
        String selected = GraphChoiceBox.getValue();
        if (selected == null) return;
        actionListView.getItems().clear();

        switch (selected) {
            case "Create":
                enterCreateMode();
                break;
            case "BFS":
                performBFS();
                break;
            case "DFS":
                performDFS();
                break;
            case "Edit":
                enterEditMode();
                break;
            case "Detect Cycle":
                detectCycle();
                break;
            case "Clear":
                // Clear main animation pane
                animationPane.getChildren().clear();  // ← Clears the pane
                break;
        }
        GraphChoiceBox.setValue(null);
    }

    /**
     * Enter graph creation mode
     */
    private void enterCreateMode() {
        isCreatingGraph = true;
        isEditMode = false;
        clearGraphDisplay();

        // Create a new pane for graph creation
        Pane createPane = new Pane();
        createPane.setPrefSize(740, 578);
        createPane.setStyle("-fx-background-color: #2e3142; -fx-border-color: #cccccc; -fx-border-width: 2;");

        // Add instruction label
        Label instruction = new Label("Click to add nodes | Click node to select/deselect | Click two nodes to create edge | Click 'Done' to finish");
        instruction.setStyle("-fx-text-fill: #333333; -fx-font-size: 12px; -fx-background-color: #ffffff; -fx-padding: 5;");
        instruction.setLayoutX(10);
        instruction.setLayoutY(10);
        createPane.getChildren().add(instruction);

        // Add Done button
        doneButton = new Button("Done");
        doneButton.setLayoutX(650);
        doneButton.setLayoutY(10);
        doneButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        doneButton.setOnAction(e -> finishGraphCreation());
        createPane.getChildren().add(doneButton);

        // Set click handler for adding nodes
        createPane.setOnMouseClicked(this::handleCreatePaneClick);

        // Clear and set the new pane
        animationPane.getChildren().clear();
        animationPane.getChildren().add(createPane);

        // Clear data structures
        nodeCircles.clear();
        circleToData.clear();
        nodeLabels.clear();
        edgeLines.clear();
        graph = new GraphOperation();
        selectedNode = null;
        firstNodeForEdge = null;

        updateStatus("Create Mode: Click to add nodes");
        actionListView.getItems().add("Entered Create Mode");
    }

    /**
     * Enter Edit mode - similar to Create mode but with existing graph
     */
    private void enterEditMode() {
        isCreatingGraph = false;
        isEditMode = true;

        // Save current graph state
        Map<Integer, double[]> nodePositions = new HashMap<>();
        for (Map.Entry<Integer, Circle> entry : nodeCircles.entrySet()) {
            nodePositions.put(entry.getKey(),
                    new double[]{entry.getValue().getCenterX(), entry.getValue().getCenterY()});
        }

        // Create a new editable pane
        Pane editPane = new Pane();
        editPane.setPrefSize(740, 578);
        editPane.setStyle("-fx-background-color: #2e3142; -fx-border-color: #cccccc; -fx-border-width: 2;");

        // Add instruction label
        Label instruction = new Label("Edit Mode: Click to add nodes | Click node to select/deselect |\n Click two nodes to create edge | Drag nodes to reposition | Click 'Done' to finish");
        instruction.setStyle("-fx-text-fill: #333333; -fx-font-size: 12px; -fx-background-color: #ffffff; -fx-padding: 5;");
        instruction.setLayoutX(10);
        instruction.setLayoutY(0);
        editPane.getChildren().add(instruction);

        // Add Done button
        doneButton = new Button("Done");
        doneButton.setLayoutX(650);
        doneButton.setLayoutY(10);
        doneButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        doneButton.setOnAction(e -> finishEditMode());
        editPane.getChildren().add(doneButton);

        // Add Delete Vertex button
        Button delVertexButton = new Button("Delete Vertex");
        delVertexButton.setLayoutX(630);
        delVertexButton.setLayoutY(530);
        delVertexButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        delVertexButton.setOnAction(e -> enableDeleteVertexInEditMode(editPane));
        editPane.getChildren().add(delVertexButton);

        // Add Delete Edge button
        Button delEdgeButton = new Button("Delete Edge");
        delEdgeButton.setLayoutX(630);
        delEdgeButton.setLayoutY(490);
        delEdgeButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        delEdgeButton.setOnAction(e -> enableDeleteEdgeInEditMode(editPane));
        editPane.getChildren().add(delEdgeButton);

        // Set click handler for adding nodes
        // Set click handler for adding nodes
        editPane.setOnMouseClicked(this::handleEditPaneClick);

        // Clear and set the new pane
        animationPane.getChildren().clear();
        animationPane.getChildren().add(editPane);

        // Recreate all nodes and edges in the edit pane
        recreateGraphInEditMode(editPane, nodePositions);

        selectedNode = null;
        firstNodeForEdge = null;

        updateStatus("Edit Mode: Click to add nodes, click nodes to create edges");
        actionListView.getItems().add("Entered Edit Mode");
    }


    /**
     * Recreate the existing graph in the edit pane
     */
    private void recreateGraphInEditMode(Pane editPane, Map<Integer, double[]> nodePositions) {
        // First, recreate all nodes (but don't add edges yet)
        for (Map.Entry<Integer, double[]> entry : nodePositions.entrySet()) {
            int data = entry.getKey();
            double[] pos = entry.getValue();
            createNodeInEditMode(editPane, data, pos[0], pos[1]);
        }

        // Clear existing edge lines map for the new pane
        Map<String, Line> newEdgeLines = new HashMap<>();

        // Recreate all edges and add them to the pane (at the back)
        for (Map.Entry<String, Line> entry : edgeLines.entrySet()) {
            String edgeKey = entry.getKey();
            String[] parts = edgeKey.split("-");
            int u = Integer.parseInt(parts[0]);
            int v = Integer.parseInt(parts[1]);

            Circle node1 = nodeCircles.get(u);
            Circle node2 = nodeCircles.get(v);

            if (node1 != null && node2 != null) {
                // Create a new edge line
                Line edge = new Line();
                edge.setStartX(node1.getCenterX());
                edge.setStartY(node1.getCenterY());
                edge.setEndX(node2.getCenterX());
                edge.setEndY(node2.getCenterY());
                edge.setStroke(Color.BLACK);
                edge.setStrokeWidth(3);
                edge.setOpacity(0.9);

                // Add to pane at the very back (index 0)
                editPane.getChildren().add(0, edge);

                // Store in the new map
                newEdgeLines.put(edgeKey, edge);
            }
        }

        // Replace the old edgeLines map with the new one
        edgeLines.clear();
        edgeLines.putAll(newEdgeLines);

        // Update node list
        nodeListView.getItems().clear();
        for (Integer data : nodeCircles.keySet()) {
            nodeListView.getItems().add("Node " + data);
        }
    }

    /**
     * Create a node in the edit pane
     */
    private void createNodeInEditMode(Pane editPane, int data, double x, double y) {
        // Create circle
        Circle nodeCircle = new Circle(20);
        nodeCircle.setCenterX(x);
        nodeCircle.setCenterY(y);
        nodeCircle.setFill(Color.LIGHTBLUE);
        nodeCircle.setStroke(Color.BLACK);
        nodeCircle.setStrokeWidth(2);
        nodeCircle.setCursor(Cursor.HAND);

        // Create label
        Text label = new Text(String.valueOf(data));
        label.setX(x - 7);
        label.setY(y + 5);
        label.setFill(Color.BLACK);
        label.setFont(javafx.scene.text.Font.font("Arial", 14));

        // Store mappings
        nodeCircles.put(data, nodeCircle);
        circleToData.put(nodeCircle, data);
        nodeLabels.put(data, label);

        // Add click handler for node selection and edge creation
        nodeCircle.setOnMouseClicked(this::handleNodeClickInEditMode);

        // Make node draggable
        makeNodeDraggable(nodeCircle, label);

        // Add to pane
        editPane.getChildren().addAll(nodeCircle, label);
    }

    /**
     * Handle node clicks in edit mode
     */
    private void handleNodeClickInEditMode(MouseEvent event) {
        if (!isEditMode) return;

        Circle clickedNode = (Circle) event.getSource();
        int nodeData = circleToData.get(clickedNode);

        // Check if we're in edge creation mode (first node selected)
        if (firstNodeForEdge != null) {
            // Create edge between first node and clicked node
            if (firstNodeForEdge != clickedNode) {
                createEdgeInEditMode(firstNodeForEdge, clickedNode, true);

                // Reset first node selection
                firstNodeForEdge.setStroke(Color.BLACK);
                firstNodeForEdge.setStrokeWidth(2);
                firstNodeForEdge = null;
                selectedNode = null;
                updateStatus("Edge created. Continue editing.");
            } else {
                // Clicked the same node - cancel edge creation
                firstNodeForEdge.setStroke(Color.BLACK);
                firstNodeForEdge.setStrokeWidth(2);
                firstNodeForEdge = null;
                selectedNode = null;
                updateStatus("Edge creation cancelled");
            }
        } else {
            // Toggle selection
            if (selectedNode == clickedNode) {
                // Deselect
                clickedNode.setStroke(Color.BLACK);
                clickedNode.setStrokeWidth(2);
                selectedNode = null;
                updateStatus("Node deselected");
            } else {
                // Deselect previous if any
                if (selectedNode != null) {
                    selectedNode.setStroke(Color.BLACK);
                    selectedNode.setStrokeWidth(2);
                }
                // Select new node as first node for edge
                clickedNode.setStroke(Color.RED);
                clickedNode.setStrokeWidth(3);
                firstNodeForEdge = clickedNode;
                selectedNode = clickedNode;
                updateStatus("Node " + nodeData + " selected. Click another node to create an edge.");
            }
        }
    }

    /**
     * Handle pane clicks in edit mode (for adding new nodes)
     */
    private void handleEditPaneClick(MouseEvent event) {
        if (!isEditMode) return;

        Pane editPane = (Pane) event.getSource();

        // Only create node if clicking on empty area (not on a node)
        if (event.getTarget() == editPane) {
            int newNodeData = generateNodeData();
            createNodeInEditMode(editPane, newNodeData, event.getX(), event.getY());

            // Add to graph data structure
            graph.addVertex(newNodeData);

            // Update node list
            nodeListView.getItems().add("Node " + newNodeData);

            // Log action
            actionListView.getItems().add("Added node " + newNodeData);
            updateStatus("Node " + newNodeData + " added");
        }
    }

    /**
     * Create an edge in edit mode
     */
    private void createEdgeInEditMode(Circle node1, Circle node2, boolean flash) {
        int data1 = circleToData.get(node1);
        int data2 = circleToData.get(node2);

        // Check if edge already exists
        String edgeKey = data1 + "-" + data2;
        String reverseKey = data2 + "-" + data1;

        if (edgeLines.containsKey(edgeKey) || edgeLines.containsKey(reverseKey)) {
            updateStatus("Edge already exists");
            return;
        }

        // Create visible line
        Line edge = new Line();
        edge.setStartX(node1.getCenterX());
        edge.setStartY(node1.getCenterY());
        edge.setEndX(node2.getCenterX());
        edge.setEndY(node2.getCenterY());
        edge.setStroke(Color.BLACK);
        edge.setStrokeWidth(3);
        edge.setOpacity(0.9);

        // Add to graph data structure
        graph.addEdge(data1, data2);

        // Store edge
        edgeLines.put(edgeKey, edge);

        // Add to pane at the back
        Pane editPane = (Pane) node1.getParent();
        editPane.getChildren().add(0, edge);

        if (flash) {
            // Flash the new edge green
            flashEdge(edge);
        }

        // Log action
        actionListView.getItems().add("Created edge between " + data1 + " and " + data2);
        updateStatus("Edge created");
    }

    /**
     * Handle clicks on the creation pane
     */
    private void handleCreatePaneClick(MouseEvent event) {
        if (!isCreatingGraph) return;

        Pane createPane = (Pane) event.getSource();

        // Only create node if clicking on empty area (not on a node)
        if (event.getTarget() == createPane) {
            int newNodeData = generateNodeData();
            createNodeInCreateMode(createPane, newNodeData, event.getX(), event.getY());
        }
    }

    /**
     * Enable delete vertex mode within edit mode
     */
    private void enableDeleteVertexInEditMode(Pane editPane) {
        updateStatus("Delete Vertex mode: Click a node to delete it");

        // Temporarily override node click handlers for deletion
        for (Circle node : nodeCircles.values()) {
            node.setOnMouseClicked(e -> {
                Circle clickedNode = (Circle) e.getSource();
                int data = circleToData.get(clickedNode);

                // Delete the vertex from edit pane
                deleteVertexInEditMode(editPane, data);

                // Restore normal edit mode click handlers
                restoreEditModeClickHandlers(editPane);
            });
        }
    }

    /**
     * Delete a vertex within edit mode
     */
    private void deleteVertexInEditMode(Pane editPane, int data) {
        Circle node = nodeCircles.remove(data);
        Text label = nodeLabels.remove(data);
        circleToData.remove(node);

        if (node != null) {
            // Remove connected edges
            List<String> edgesToRemove = new ArrayList<>();
            for (Map.Entry<String, Line> entry : edgeLines.entrySet()) {
                String edgeKey = entry.getKey();
                String[] parts = edgeKey.split("-");
                int u = Integer.parseInt(parts[0]);
                int v = Integer.parseInt(parts[1]);

                if (u == data || v == data) {
                    editPane.getChildren().remove(entry.getValue());
                    edgesToRemove.add(edgeKey);
                }
            }

            for (String key : edgesToRemove) {
                edgeLines.remove(key);
            }

            // Remove from graph
            graph.removeVertex(data);

            // Remove from UI
            editPane.getChildren().removeAll(node, label);
            nodeListView.getItems().remove("Node " + data);

            actionListView.getItems().add("Deleted vertex " + data);
            updateSize(graph.getSize());
            updateStatus("Vertex " + data + " deleted");
        }
    }

    /**
     * Enable delete edge mode within edit mode
     */
    private void enableDeleteEdgeInEditMode(Pane editPane) {
        updateStatus("Delete Edge mode: Click on an edge to delete it");

        // Make edges clickable for deletion
        for (Line edge : edgeLines.values()) {
            edge.setStrokeWidth(5);  // Make easier to click
            edge.setCursor(Cursor.HAND);

            edge.setOnMouseClicked(e -> {
                Line clickedEdge = (Line) e.getSource();

                // Find and remove the edge
                String edgeToRemove = null;
                for (Map.Entry<String, Line> entry : edgeLines.entrySet()) {
                    if (entry.getValue() == clickedEdge) {
                        edgeToRemove = entry.getKey();
                        break;
                    }
                }

                if (edgeToRemove != null) {
                    String[] parts = edgeToRemove.split("-");
                    int u = Integer.parseInt(parts[0]);
                    int v = Integer.parseInt(parts[1]);

                    // Remove from graph
                    graph.removeEdge(u, v);

                    // Remove from UI (edit pane)
                    editPane.getChildren().remove(clickedEdge);
                    edgeLines.remove(edgeToRemove);

                    actionListView.getItems().add("Deleted edge between " + u + " and " + v);
                    updateStatus("Edge deleted");

                    // Restore normal edit mode click handlers
                    restoreEditModeClickHandlers(editPane);
                }

                // Consume the event to prevent it from bubbling
                e.consume();
            });
        }
    }

    /**
     * Restore normal edit mode click handlers for nodes and edges
     */
    private void restoreEditModeClickHandlers(Pane editPane) {
        // Restore node click handlers for edge creation
        for (Circle node : nodeCircles.values()) {
            node.setOnMouseClicked(this::handleNodeClickInEditMode);
        }

        // Restore edge appearance (remove thicker stroke for deletion)
        for (Line edge : edgeLines.values()) {
            edge.setStrokeWidth(3);
            edge.setCursor(Cursor.DEFAULT);
            edge.setOnMouseClicked(null); // Remove edge click handler
        }

        updateStatus("Edit Mode: Click nodes to create edges");
    }

    /**
     * Create a node in the creation pane
     */
    private void createNodeInCreateMode(Pane createPane, int data, double x, double y) {
        // Create circle
        Circle nodeCircle = new Circle(20);
        nodeCircle.setCenterX(x);
        nodeCircle.setCenterY(y);
        nodeCircle.setFill(Color.LIGHTBLUE);
        nodeCircle.setStroke(Color.BLACK);
        nodeCircle.setStrokeWidth(2);
        nodeCircle.setCursor(Cursor.HAND);

        // Create label
        Text label = new Text(String.valueOf(data));
        label.setX(x - 7);
        label.setY(y + 5);
        label.setFill(Color.BLACK);
        label.setFont(javafx.scene.text.Font.font("Arial", 14));

        // Store mappings
        nodeCircles.put(data, nodeCircle);
        circleToData.put(nodeCircle, data);
        nodeLabels.put(data, label);

        // Add to graph data structure
        graph.addVertex(data);

        // Add click handler for node selection and edge creation
        nodeCircle.setOnMouseClicked(this::handleNodeClick);

        // Make node draggable
        makeNodeDraggable(nodeCircle, label);

        // Add to pane
        createPane.getChildren().addAll(nodeCircle, label);

        // Update node list
        nodeListView.getItems().add("Node " + data);

        // Log action
        actionListView.getItems().add("Created node " + data);
        updateStatus("Node " + data + " created. Select it or add another node.");
    }

    /**
     * Handle node clicks (for selection and edge creation) in create mode
     */
    private void handleNodeClick(MouseEvent event) {
        if (!isCreatingGraph) return;

        Circle clickedNode = (Circle) event.getSource();
        int nodeData = circleToData.get(clickedNode);

        // Check if we're in edge creation mode (first node selected)
        if (firstNodeForEdge != null) {
            // Create edge between first node and clicked node
            if (firstNodeForEdge != clickedNode) {
                createEdgeInCreateMode(firstNodeForEdge, clickedNode);

                // Reset first node selection
                firstNodeForEdge.setStroke(Color.BLACK);
                firstNodeForEdge.setStrokeWidth(2);
                firstNodeForEdge = null;
                selectedNode = null;
                updateStatus("Edge created. Continue adding nodes/edges.");
            } else {
                // Clicked the same node - cancel edge creation
                firstNodeForEdge.setStroke(Color.BLACK);
                firstNodeForEdge.setStrokeWidth(2);
                firstNodeForEdge = null;
                selectedNode = null;
                updateStatus("Edge creation cancelled");
            }
        } else {
            // Toggle selection
            if (selectedNode == clickedNode) {
                // Deselect
                clickedNode.setStroke(Color.BLACK);
                clickedNode.setStrokeWidth(2);
                selectedNode = null;
                updateStatus("Node deselected");
            } else {
                // Deselect previous if any
                if (selectedNode != null) {
                    selectedNode.setStroke(Color.BLACK);
                    selectedNode.setStrokeWidth(2);
                }
                // Select new node as first node for edge
                clickedNode.setStroke(Color.RED);
                clickedNode.setStrokeWidth(3);
                firstNodeForEdge = clickedNode;
                selectedNode = clickedNode;
                updateStatus("Node " + nodeData + " selected. Click another node to create an edge.");
            }
        }
    }

    /**
     * Create a visible edge between two nodes in creation mode
     */
    private void createEdgeInCreateMode(Circle node1, Circle node2) {
        int data1 = circleToData.get(node1);
        int data2 = circleToData.get(node2);

        // Create visible line with good styling
        Line edge = new Line();
        edge.setStartX(node1.getCenterX());
        edge.setStartY(node1.getCenterY());
        edge.setEndX(node2.getCenterX());
        edge.setEndY(node2.getCenterY());
        edge.setStroke(Color.BLACK);
        edge.setStrokeWidth(3);  // Thicker line for visibility
        edge.setOpacity(0.9);     // Slight transparency for better look

        // Add to graph data structure
        graph.addEdge(data1, data2);

        // Store edge with both possible keys (undirected graph)
        String edgeKey = data1 + "-" + data2;
        edgeLines.put(edgeKey, edge);

        // Add to pane at the back (index 0) so nodes appear on top
        Pane createPane = (Pane) node1.getParent();
        createPane.getChildren().add(0, edge);

        // Flash the new edge green to show it was created
        flashEdge(edge);

        // Log action
        actionListView.getItems().add("Created edge between " + data1 + " and " + data2);
    }

    /**
     * Flash the edge green when created for visual feedback
     */
    private void flashEdge(Line edge) {
        edge.setStroke(Color.GREEN);
        edge.setStrokeWidth(4);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(300), e -> {
                    edge.setStroke(Color.BLACK);
                    edge.setStrokeWidth(3);
                })
        );
        timeline.setCycleCount(1);
        timeline.play();
    }

    /**
     * Make node draggable
     */
    private void makeNodeDraggable(Circle node, Text label) {
        final double[] dragDelta = new double[2];

        node.setOnMousePressed(e -> {
            dragDelta[0] = e.getX() - node.getCenterX();
            dragDelta[1] = e.getY() - node.getCenterY();
            node.setCursor(Cursor.CLOSED_HAND);
        });

        node.setOnMouseDragged(e -> {
            double newX = e.getX() - dragDelta[0];
            double newY = e.getY() - dragDelta[1];

            node.setCenterX(newX);
            node.setCenterY(newY);
            label.setX(newX - 7);
            label.setY(newY + 5);

            // Update connected edges in real-time
            updateConnectedEdges(node);
        });

        node.setOnMouseReleased(e -> {
            node.setCursor(Cursor.HAND);
        });
    }

    /**
     * Update edges connected to a node when it's dragged
     */
    private void updateConnectedEdges(Circle node) {
        int nodeData = circleToData.get(node);

        for (Map.Entry<String, Line> entry : edgeLines.entrySet()) {
            String edgeKey = entry.getKey();
            Line edge = entry.getValue();

            String[] parts = edgeKey.split("-");
            int u = Integer.parseInt(parts[0]);
            int v = Integer.parseInt(parts[1]);

            if (u == nodeData) {
                edge.setStartX(node.getCenterX());
                edge.setStartY(node.getCenterY());
            } else if (v == nodeData) {
                edge.setEndX(node.getCenterX());
                edge.setEndY(node.getCenterY());
            }
        }
    }

    /**
     * Finish graph creation and show on main pane with visible edges
     */
    private void finishGraphCreation() {
        isCreatingGraph = false;

        // Clear main animation pane
        animationPane.getChildren().clear();

        // First add all edges (so they appear behind nodes)
        for (Map.Entry<String, Line> entry : edgeLines.entrySet()) {
            String edgeKey = entry.getKey();
            Line oldEdge = entry.getValue();

            Line newEdge = new Line();
            newEdge.setStartX(oldEdge.getStartX());
            newEdge.setStartY(oldEdge.getStartY());
            newEdge.setEndX(oldEdge.getEndX());
            newEdge.setEndY(oldEdge.getEndY());
            newEdge.setStroke(Color.WHITESMOKE);
            newEdge.setStrokeWidth(3);  // Keep visible thickness
            newEdge.setOpacity(0.9);

            animationPane.getChildren().add(newEdge);
            edgeLines.put(edgeKey, newEdge);
        }

        // Then add all nodes (on top of edges)
        for (Map.Entry<Integer, Circle> entry : nodeCircles.entrySet()) {
            int data = entry.getKey();
            Circle oldCircle = entry.getValue();

            // Create new circle for main pane
            Circle newCircle = new Circle(25);
            newCircle.setCenterX(oldCircle.getCenterX());
            newCircle.setCenterY(oldCircle.getCenterY());
            newCircle.setFill(Color.LIGHTYELLOW);
            newCircle.setStroke(Color.BLACK);
            newCircle.setStrokeWidth(5);

            // Create new label
            Text newLabel = new Text(String.valueOf(data));
            newLabel.setX(oldCircle.getCenterX() - 8);
            newLabel.setY(oldCircle.getCenterY() + 5);
            newLabel.setFill(Color.BLACK);
            newLabel.setFont(javafx.scene.text.Font.font("Arial", 14));

            // Add to main pane
            animationPane.getChildren().addAll(newCircle, newLabel);

            // Update mappings
            nodeCircles.put(data, newCircle);
            circleToData.put(newCircle, data);
            nodeLabels.put(data, newLabel);
        }

        // Update UI
        updateStatus("Graph creation completed");
        updateSize(graph.getSize());
        actionListView.getItems().add("Graph creation completed");

        // Reset selection
        selectedNode = null;
        firstNodeForEdge = null;
    }

    /**
     * Finish edit mode and return to main view
     */
    private void finishEditMode() {
        isEditMode = false;

        // Clear main animation pane
        animationPane.getChildren().clear();

        // First add all edges (so they appear behind nodes)
        for (Map.Entry<String, Line> entry : edgeLines.entrySet()) {
            String edgeKey = entry.getKey();
            Line oldEdge = entry.getValue();

            Line newEdge = new Line();
            newEdge.setStartX(oldEdge.getStartX());
            newEdge.setStartY(oldEdge.getStartY());
            newEdge.setEndX(oldEdge.getEndX());
            newEdge.setEndY(oldEdge.getEndY());
            newEdge.setStroke(Color.WHITESMOKE);
            newEdge.setStrokeWidth(3);
            newEdge.setOpacity(0.9);

            animationPane.getChildren().add(newEdge);
            edgeLines.put(edgeKey, newEdge);
        }

        // Then add all nodes (on top of edges)
        for (Map.Entry<Integer, Circle> entry : nodeCircles.entrySet()) {
            int data = entry.getKey();
            Circle oldCircle = entry.getValue();

            // Create new circle for main pane
            Circle newCircle = new Circle(25);
            newCircle.setCenterX(oldCircle.getCenterX());
            newCircle.setCenterY(oldCircle.getCenterY());
            newCircle.setFill(Color.LIGHTYELLOW);
            newCircle.setStroke(Color.BLACK);
            newCircle.setStrokeWidth(5);

            // Create new label
            Text newLabel = new Text(String.valueOf(data));
            newLabel.setX(oldCircle.getCenterX() - 8);
            newLabel.setY(oldCircle.getCenterY() + 5);
            newLabel.setFill(Color.BLACK);
            newLabel.setFont(javafx.scene.text.Font.font("Arial", 14));

            // Add to main pane
            animationPane.getChildren().addAll(newCircle, newLabel);

            // Update mappings
            nodeCircles.put(data, newCircle);
            circleToData.put(newCircle, data);
            nodeLabels.put(data, newLabel);
        }

        // Update UI
        updateStatus("Edit mode completed");
        updateSize(graph.getSize());
        actionListView.getItems().add("Edit mode completed");

        // Reset selection
        selectedNode = null;
        firstNodeForEdge = null;
    }




    /**
     * Perform BFS traversal
     */
    private void performBFS() {
        if (graph.getSize() == 0) {
            updateStatus("Graph is empty");
            return;
        }

        // Use the BFS method that traverses all components
        List<Integer> bfsResult = graph.BFS(); // This now traverses the entire graph

        actionListView.getItems().add("BFS Traversal (all components):");
        actionListView.getItems().add(bfsResult.toString());

        // Display Adjacency List
        actionListView.getItems().add("=== ADJACENCY LIST ===");
        displayAdjacencyList();
        actionListView.getItems().add("");

        // Visualize BFS (highlight nodes in order)
        new Thread(() -> {
            try {
                for (int nodeData : bfsResult) {
                    Circle node = nodeCircles.get(nodeData);
                    if (node != null) {
                        javafx.application.Platform.runLater(() -> {
                            node.setFill(Color.YELLOW);
                        });
                        Thread.sleep(1000);
                        javafx.application.Platform.runLater(() -> {
                            node.setFill(Color.LIGHTBLUE);
                        });
                    }
                }
                // Wait 2 seconds after traversal completes
                Thread.sleep(2000);
                // After traversal is complete, ensure all nodes are default yellow
                javafx.application.Platform.runLater(() -> {
                    for (Circle node : nodeCircles.values()) {
                        node.setFill(Color.LIGHTYELLOW);
                    }
                    updateStatus("BFS completed");
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();


    }

    /**
     * Perform DFS traversal
     */
    private void performDFS() {
        if (graph.getSize() == 0) {
            updateStatus("Graph is empty");
            return;
        }

        // Use the DFS method that traverses all components
        List<Integer> dfsResult = graph.DFS(); // This now traverses the entire graph

        actionListView.getItems().add("DFS Traversal (all components):");
        actionListView.getItems().add(dfsResult.toString());

        // Display Adjacency List
        actionListView.getItems().add("=== ADJACENCY LIST ===");
        displayAdjacencyList();
        actionListView.getItems().add("");

        // Visualize DFS (highlight nodes in order)
        new Thread(() -> {
            try {
                for (int nodeData : dfsResult) {
                    Circle node = nodeCircles.get(nodeData);
                    if (node != null) {
                        javafx.application.Platform.runLater(() -> {
                            node.setFill(Color.ORANGE);
                        });
                        Thread.sleep(1000);
                        javafx.application.Platform.runLater(() -> {
                            node.setFill(Color.LIGHTBLUE);
                        });
                    }
                }
                // Wait 2 seconds after traversal completes
                Thread.sleep(2000);
                // After traversal is complete, ensure all nodes are default yellow
                javafx.application.Platform.runLater(() -> {
                    for (Circle node : nodeCircles.values()) {
                        node.setFill(Color.LIGHTYELLOW);
                    }
                    updateStatus("DFS completed");
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();


    }

    //function to detect Cycle

    /**
     * Detect cycle in the graph using DFS
     */
    /**
     * Detect cycle in the graph using DFS and highlight the cycle nodes in RED
     */
    private void detectCycle() {
        if (graph.getSize() == 0) {
            updateStatus("Graph is empty");
            return;
        }

        actionListView.getItems().add("=== CYCLE DETECTION ===");

        // Set to keep track of visited nodes
        Set<Integer> visited = new HashSet<>();
        // Set to keep track of nodes in current recursion stack
        Set<Integer> recStack = new HashSet<>();
        // Map to store parent of each node for cycle reconstruction
        Map<Integer, Integer> parent = new HashMap<>();

        // Variables to store cycle information
        List<Integer> cycleNodes = new ArrayList<>();
        boolean[] cycleFound = new boolean[1];

        // Check each unvisited vertex (for disconnected graphs)
        for (Integer vertex : graph.getAllVertices()) {
            if (!visited.contains(vertex)) {
                // Clear recStack and parent for each new DFS start
                recStack.clear();
                parent.clear();

                if (dfsCycleDetection(vertex, visited, recStack, parent, cycleNodes, -1)) {
                    cycleFound[0] = true;
                    break;
                }
            }
        }

        if (cycleFound[0]) {
            // Cycle detected - highlight the cycle nodes in RED
            actionListView.getItems().add("✓ Cycle detected!");
            actionListView.getItems().add("Cycle nodes: " + cycleNodes);

            // Visualize the cycle (highlight nodes in RED)
            new Thread(() -> {
                try {
                    // First, reset all nodes to default color
                    javafx.application.Platform.runLater(() -> {
                        for (Circle node : nodeCircles.values()) {
                            node.setFill(Color.LIGHTYELLOW);
                        }
                    });

                    Thread.sleep(500);

                    // Highlight cycle nodes in RED
                    for (int nodeData : cycleNodes) {
                        Circle node = nodeCircles.get(nodeData);
                        if (node != null) {
                            javafx.application.Platform.runLater(() -> {
                                node.setFill(Color.RED);
                            });
                            Thread.sleep(500);
                        }
                    }

                    // Keep them red for 3 seconds
                    Thread.sleep(3000);

                    // Reset all nodes back to default color
                    javafx.application.Platform.runLater(() -> {
                        for (Circle node : nodeCircles.values()) {
                            node.setFill(Color.LIGHTYELLOW);
                        }
                        updateStatus("Cycle detection completed - Cycle found");
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } else {
            // No cycle detected
            actionListView.getItems().add("✗ No cycle detected in the graph");
            updateStatus("No cycle detected");

            // Visual feedback - flash all nodes green quickly
            new Thread(() -> {
                try {
                    for (Circle node : nodeCircles.values()) {
                        javafx.application.Platform.runLater(() -> {
                            node.setFill(Color.LIGHTGREEN);
                        });
                    }

                    Thread.sleep(1000);

                    javafx.application.Platform.runLater(() -> {
                        for (Circle node : nodeCircles.values()) {
                            node.setFill(Color.LIGHTYELLOW);
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        // Display adjacency list for reference
        actionListView.getItems().add("");
        actionListView.getItems().add("=== ADJACENCY LIST ===");
        displayAdjacencyList();
        actionListView.getItems().add("");
    }

    /**
     * DFS helper for cycle detection with cycle reconstruction
     */
    private boolean dfsCycleDetection(int current, Set<Integer> visited, Set<Integer> recStack,
                                      Map<Integer, Integer> parent, List<Integer> cycleNodes, int previous) {
        // Mark current node as visited and add to recursion stack
        visited.add(current);
        recStack.add(current);

        // Get all adjacent vertices
        GraphOperation.Node currentNode = graph.getNode(current);

        if (currentNode != null) {
            for (GraphOperation.Node neighbor : currentNode.neighbors) {
                int neighborData = neighbor.data;

                // Skip the parent edge (for undirected graph)
                if (neighborData == previous) {
                    continue;
                }

                // If not visited, then recurse
                if (!visited.contains(neighborData)) {
                    parent.put(neighborData, current);
                    if (dfsCycleDetection(neighborData, visited, recStack, parent, cycleNodes, current)) {
                        return true;
                    }
                }
                // If neighbor is in recursion stack, then we found a cycle
                else if (recStack.contains(neighborData)) {
                    // Reconstruct the cycle
                    reconstructCycle(current, neighborData, parent, cycleNodes);
                    return true;
                }
            }
        }

        // Remove current from recursion stack
        recStack.remove(current);
        return false;
    }

    /**
     * Reconstruct the cycle path from parent map
     */
    private void reconstructCycle(int current, int neighborData, Map<Integer, Integer> parent,
                                  List<Integer> cycleNodes) {
        cycleNodes.clear();

        // Add the edge nodes
        cycleNodes.add(neighborData);
        cycleNodes.add(current);

        // Backtrack from current node to neighborData using parent map
        int node = current;
        while (parent.containsKey(node) && parent.get(node) != neighborData) {
            node = parent.get(node);
            cycleNodes.add(node);
        }

        // Add the starting node to complete the cycle
        if (parent.containsKey(node)) {
            cycleNodes.add(parent.get(node));
        }

        // Remove duplicates at the end if necessary
        // For undirected graphs, a cycle must have at least 3 nodes
        Set<Integer> uniqueNodes = new LinkedHashSet<>(cycleNodes);
        cycleNodes.clear();
        cycleNodes.addAll(uniqueNodes);

        // Ensure we have at least 3 nodes for a valid cycle
        if (cycleNodes.size() < 3) {
            // This might be a 2-node "cycle" (which is actually just an edge in undirected graph)
            // For undirected graphs, we need at least 3 nodes for a proper cycle
            cycleNodes.clear();

            // Try to find a longer cycle by looking at the neighbor's neighbors
            findAlternateCycle(current, neighborData, cycleNodes);
        }
    }

    /**
     * Alternative method to find a proper cycle (with at least 3 nodes)
     */
    private void findAlternateCycle(int node1, int node2, List<Integer> cycleNodes) {
        cycleNodes.clear();

        // For an undirected graph, a proper cycle needs at least 3 nodes
        // Let's find a path from node2 back to node1 through another node

        GraphOperation.Node node2Obj = graph.getNode(node2);
        if (node2Obj != null) {
            for (GraphOperation.Node neighbor : node2Obj.neighbors) {
                int neighborData = neighbor.data;

                // Skip node1 as it's directly connected
                if (neighborData != node1) {
                    // Check if this neighbor is connected to node1
                    GraphOperation.Node neighborObj = graph.getNode(neighborData);
                    if (neighborObj != null) {
                        for (GraphOperation.Node n : neighborObj.neighbors) {
                            if (n.data == node1) {
                                // Found a 3-node cycle: node1 - node2 - neighborData - node1
                                cycleNodes.add(node1);
                                cycleNodes.add(node2);
                                cycleNodes.add(neighborData);
                                return;
                            }
                        }
                    }
                }
            }
        }

        // If no 3-node cycle found, try a simple approach
        // For debugging: if we can't find a proper cycle, just return the two nodes
        // (this should rarely happen with proper graph input)
        if (cycleNodes.isEmpty()) {
            cycleNodes.add(node1);
            cycleNodes.add(node2);
        }
    }



    /**
     * Display adjacency list in the action list view
     */
    private void displayAdjacencyList() {
        Set<Integer> vertices = graph.getAllVertices();
        List<Integer> sortedVertices = new ArrayList<>(vertices);
        Collections.sort(sortedVertices);

        for (int vertex : sortedVertices) {
            GraphOperation.Node node = graph.getNode(vertex);
            StringBuilder sb = new StringBuilder();
            sb.append(vertex).append(" -> ");

            List<Integer> neighbors = new ArrayList<>();
            for (GraphOperation.Node neighbor : node.neighbors) {
                neighbors.add(neighbor.data);
            }
            Collections.sort(neighbors);

            if (neighbors.isEmpty()) {
                sb.append("[]");
            } else {
                sb.append("[");
                for (int i = 0; i < neighbors.size(); i++) {
                    sb.append(neighbors.get(i));
                    if (i < neighbors.size() - 1) {
                        sb.append(", ");
                    }
                }
                sb.append("]");
            }

            actionListView.getItems().add(sb.toString());
        }
    }

    /**
     * Clear the graph display
     */
    private void clearGraphDisplay() {
        animationPane.getChildren().clear();
        nodeListView.getItems().clear();
        actionListView.getItems().clear();
        nodeCircles.clear();
        edgeLines.clear();
        circleToData.clear();
        nodeLabels.clear();
        graph = new GraphOperation();
        selectedNode = null;
        firstNodeForEdge = null;
    }

    /**
     * Generate next available node data
     */
    private int generateNodeData() {
        int data = 1;
        while (nodeCircles.containsKey(data)) {
            data++;
        }
        return data;
    }

    /**
     * Update status label
     */
    private void updateStatus(String status) {
        lblStatus.setText("Status: " + status);
    }

    /**
     * Update size label
     */
    private void updateSize(int size) {
        lblSize.setText("Size: " + size);
    }
}