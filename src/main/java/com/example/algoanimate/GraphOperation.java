package com.example.algoanimate;

import java.util.*;

public class GraphOperation {

    // Node class
    public static class Node {
        int data;
        List<Node> neighbors;

        Node(int data) {
            this.data = data;
            neighbors = new ArrayList<>();
        }
    }

    // Store all nodes
    private Map<Integer, Node> nodes;

    // Constructor
    public GraphOperation() {
        nodes = new HashMap<>();
    }

    // Add a vertex
    public void addVertex(int data) {
        if (!nodes.containsKey(data)) {
            nodes.put(data, new Node(data));
        }
    }

    // Add an edge (Undirected Graph)
    public void addEdge(int u, int v) {
        Node nodeU = nodes.get(u);
        Node nodeV = nodes.get(v);

        if (nodeU == null || nodeV == null) {
            System.out.println("Vertex not found!");
            return;
        }

        // Check if edge already exists
        if (!nodeU.neighbors.contains(nodeV)) {
            nodeU.neighbors.add(nodeV);
            nodeV.neighbors.add(nodeU);
        }
    }

    // Remove a vertex
    public void removeVertex(int data) {
        Node nodeToRemove = nodes.get(data);
        if (nodeToRemove != null) {
            // Remove all edges connected to this node
            for (Node neighbor : nodeToRemove.neighbors) {
                neighbor.neighbors.remove(nodeToRemove);
            }
            nodes.remove(data);
        }
    }

    // Remove an edge
    public void removeEdge(int u, int v) {
        Node nodeU = nodes.get(u);
        Node nodeV = nodes.get(v);

        if (nodeU != null && nodeV != null) {
            nodeU.neighbors.remove(nodeV);
            nodeV.neighbors.remove(nodeU);
        }
    }

    // Get node by data
    public Node getNode(int data) {
        return nodes.get(data);
    }

    // Check if vertex exists
    public boolean hasVertex(int data) {
        return nodes.containsKey(data);
    }

    // Check if edge exists
    public boolean hasEdge(int u, int v) {
        Node nodeU = nodes.get(u);
        Node nodeV = nodes.get(v);

        return nodeU != null && nodeV != null &&
                nodeU.neighbors.contains(nodeV);
    }

    // Get all vertices
    public Set<Integer> getAllVertices() {
        return nodes.keySet();
    }

    // Get size of graph
    public int getSize() {
        return nodes.size();
    }

    // BFS traversal for entire graph (handles disconnected graphs)
    public List<Integer> BFS() {
        List<Integer> result = new ArrayList<>();
        Set<Node> visited = new HashSet<>();

        // Traverse all components
        for (Node node : nodes.values()) {
            if (!visited.contains(node)) {
                // Perform BFS for this component
                Queue<Node> queue = new LinkedList<>();
                queue.add(node);
                visited.add(node);

                while (!queue.isEmpty()) {
                    Node current = queue.poll();
                    result.add(current.data);

                    for (Node neighbor : current.neighbors) {
                        if (!visited.contains(neighbor)) {
                            visited.add(neighbor);
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }

        return result;
    }

    // BFS starting from a specific node (only that component)
    public List<Integer> BFS(int startData) {
        List<Integer> result = new ArrayList<>();
        Node start = nodes.get(startData);

        if (start == null) return result;

        Queue<Node> queue = new LinkedList<>();
        Set<Node> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            result.add(current.data);

            for (Node neighbor : current.neighbors) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        return result;
    }

    // DFS traversal for entire graph (handles disconnected graphs)
    public List<Integer> DFS() {
        List<Integer> result = new ArrayList<>();
        Set<Node> visited = new HashSet<>();

        // Traverse all components
        for (Node node : nodes.values()) {
            if (!visited.contains(node)) {
                DFSRecursive(node, visited, result);
            }
        }

        return result;
    }

    // DFS starting from a specific node (only that component)
    public List<Integer> DFS(int startData) {
        List<Integer> result = new ArrayList<>();
        Node start = nodes.get(startData);

        if (start == null) return result;

        Set<Node> visited = new HashSet<>();
        DFSRecursive(start, visited, result);

        return result;
    }

    private void DFSRecursive(Node node, Set<Node> visited, List<Integer> result) {
        visited.add(node);
        result.add(node.data);

        for (Node neighbor : node.neighbors) {
            if (!visited.contains(neighbor)) {
                DFSRecursive(neighbor, visited, result);
            }
        }
    }

    // Get neighbors of a vertex
    public List<Integer> getNeighbors(int vertex) {
        List<Integer> neighbors = new ArrayList<>();
        Node node = nodes.get(vertex);
        if (node != null) {
            for (Node neighbor : node.neighbors) {
                neighbors.add(neighbor.data);
            }
        }
        return neighbors;
    }

    // Get all connected components
    public List<List<Integer>> getConnectedComponents() {
        List<List<Integer>> components = new ArrayList<>();
        Set<Node> visited = new HashSet<>();

        for (Node node : nodes.values()) {
            if (!visited.contains(node)) {
                List<Integer> component = new ArrayList<>();
                Queue<Node> queue = new LinkedList<>();
                queue.add(node);
                visited.add(node);

                while (!queue.isEmpty()) {
                    Node current = queue.poll();
                    component.add(current.data);

                    for (Node neighbor : current.neighbors) {
                        if (!visited.contains(neighbor)) {
                            visited.add(neighbor);
                            queue.add(neighbor);
                        }
                    }
                }
                components.add(component);
            }
        }

        return components;
    }
}