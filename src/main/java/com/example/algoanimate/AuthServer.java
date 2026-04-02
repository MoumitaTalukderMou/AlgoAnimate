package com.example.algoanimate;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class AuthServer {

    private static final int PORT = 5000;
    private static final String USERS_FILE = "users.txt";

    // In-memory user store
    private static final Map<String, String> users = new HashMap<>();

    static {
        loadUsers();  // Load users from file on startup
        // Add default users if file is empty
        if (!users.containsKey("admin")) {
            users.put("admin", "1234");
            users.put("user1", "pass1");
            saveUsers();  // save to file
            System.out.println("Default users created.");
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Auth server running on port " + PORT);

        while (true) {
            Socket client = serverSocket.accept();
            new Thread(() -> handleClient(client)).start();
        }
    }

    private static void handleClient(Socket client) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(client.getOutputStream(), true)
        ) {
            String message = in.readLine();
            System.out.println("Received: " + message);

            if (message.startsWith("LOGIN:")) {
                handleLogin(message.substring(6), out);
            } else if (message.startsWith("REGISTER:")) {
                handleRegister(message.substring(9), out);
            } else {
                out.println("FAIL");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleLogin(String data, PrintWriter out) {
        String[] parts = data.split(":", 2);
        if (parts.length == 2) {
            String username = parts[0];
            String password = parts[1];

            if (users.containsKey(username) && users.get(username).equals(password)) {
                out.println("SUCCESS");
                System.out.println("✓ Login success: " + username);
            } else {
                out.println("FAIL");
                System.out.println("✗ Login failed: " + username);
            }
        } else {
            out.println("FAIL");
        }
    }

    private static void handleRegister(String data, PrintWriter out) {
        String[] parts = data.split(":", 2);
        if (parts.length == 2) {
            String username = parts[0];
            String password = parts[1];

            if (users.containsKey(username)) {
                out.println("USER_EXISTS");
                System.out.println("✗ Register failed - already exists: " + username);
            } else {
                users.put(username, password);
                saveUsers();
                out.println("SUCCESS");
                System.out.println("✓ Registered: " + username);
            }
        } else {
            out.println("FAIL");
        }
    }

    // Load users from file
    private static void loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    users.put(parts[0], parts[1]);
                }
            }
            System.out.println("Loaded " + users.size() + " users from file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save users to file
    private static void saveUsers() {
        File file = new File(USERS_FILE);
        System.out.println("Saving users to: " + file.getAbsolutePath());  // ← debug

        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (Map.Entry<String, String> entry : users.entrySet()) {
                writer.println(entry.getKey() + ":" + entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}