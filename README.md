# AlgoAnimate 🚀

AlgoAnimate is an interactive, real-time Data Structures and Algorithms (DSA) visualizer. Developed as a Level 1 Term 2 academic project at the Bangladesh University of Engineering and Technology (BUET), this desktop application bridges the gap between theoretical algorithm concepts and practical understanding through dynamic visual feedback and step-by-step logic tracing.

## ✨ Key Features

* Arrays: Interactive visualization of core array operations (Insertion, Deletion, Search).
* Sorting Algorithms: Real-time, step-by-step animations for Bubble Sort, Selection Sort, Insertion Sort, and Merge Sort.
* Trees: Comprehensive Binary Search Tree (BST) operations including Insertion, Deletion, and Pre-order, In-order, and Post-order Traversals.
* Linked Lists: Visual representation of Singly and Doubly Linked List manipulations.
* Stacks & Queues: Clear demonstrations of LIFO (Stack) and FIFO (Queue) operations, handling overflow and underflow conditions.
* Graphs: Interactive graph creation with traversals (BFS, DFS) and Cycle Detection.
* Real-time Tracing: Integrated pseudocode panel that highlights the exact line of code executing alongside the visual animation.

## 🛠 Tech Stack

* Language: Java
* GUI Framework: JavaFX
* Concurrency: Multi-threading (utilized to ensure smooth, non-blocking UI updates and animations)
* Networking: Socket Programming (implemented for the client-server authentication/login module)

## 🚀 Getting Started

### Prerequisites

* Java Development Kit (JDK) 17 or higher.
* JavaFX SDK (configured with your IDE).
* IntelliJ IDEA 


### Running the Project

1.  Clone the repository:
    git clone (https://github.com/MoumitaTalukderMou/AlgoAnimate)
2.  Open the project in your IDE.
3.  Ensure JavaFX is properly configured in your VM options (e.g., --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml).
4.  Start the Server: run the AuthServer class first. This will start the server on port 5000 to listen for login requests.
5.  <img width="962" height="508" alt="image" src="https://github.com/user-attachments/assets/9daef5e7-5c2d-46a6-ad76-7eb8bf91ed82" />
6.   Start the Server: Run the AuthServer class first. This will start the server on port 5000 to listen for login requests
7.   Case 1: Running on a Single Machine (Localhost) - Recommended for quick testing
                       If you are running both the server and the client application on the same PC, you do not need an active internet connection.
                        Ensure the connection in LoginController.java is set to localhost:  Socket socket = new Socket("localhost", 5000);
 <img width="1107" height="385" alt="Screenshot 2026-04-09 211050" src="https://github.com/user-attachments/assets/fbd8d38a-112f-4495-9577-22c0fcd1f4e3" />
 <img width="1063" height="607" alt="image" src="https://github.com/user-attachments/assets/26ccda20-c85f-439b-88f6-a7536f1e3300" />
   Case 2: Running over a Network (Multiple Machines)
                        If you want to run the AuthServer on one PC (e.g., Team Member 1) and launch the app from another PC (e.g., Team Member 2),
                       both devices must be on the same Wi-Fi network.

                        Find the Server PC's IPv4 address using ipconfig (Windows) or ifconfig (Mac/Linux).
 <img width="1095" height="638" alt="image" src="https://github.com/user-attachments/assets/506e9aa7-873e-4b06-8474-5ac0690abfbc" />
 Update the IP address in LoginController.java on the client machine: // Replace with the actual Server IP
                                                                    Socket socket = new Socket("192.168.x.x", 5000);
                         <img width="1064" height="198" alt="image" src="https://github.com/user-attachments/assets/e598d2b0-19e7-4443-957e-2c3943f1c29f" />
                        <img width="1102" height="181" alt="image" src="https://github.com/user-attachments/assets/65ab1918-5f28-4138-99f1-5c901cdceb74" />
     
6. Run the Launcher class to start the AlgoAnimate application!
<img width="937" height="529" alt="image" src="https://github.com/user-attachments/assets/35568856-369c-4d46-9c0f-a39ba02c6857" />

7.Then Register and login to access the workpplace
<img width="1479" height="986" alt="Screenshot 2026-04-07 163006" src="https://github.com/user-attachments/assets/350d1b4b-bcc9-4fff-9ebb-295a50b030ef" />






👥 Meet the Team
This project was developed by undergraduate students of the CSE Department at BUET.
Moumita Talukder Mou-2405160
Sabiha Jannat Adiba-2405162

Supervisor: Arnob Saha Ankon

