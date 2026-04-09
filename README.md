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
5.   Configure the Client Connection (Choose Your Scenario):

               Case 1: Running on a Single Machine (Localhost) - Recommended for quick testing
                       If you are running both the server and the client application on the same PC, you do not need an active internet connection.
                        Ensure the connection in LoginController.java is set to localhost:  Socket socket = new Socket("localhost", 5000);



               Case 2: Running over a Network (Multiple Machines)
                        If you want to run the AuthServer on one PC (e.g., Team Member 1) and launch the app from another PC (e.g., Team Member 2),
                       both devices must be on the same Wi-Fi network.

                        Find the Server PC's IPv4 address using ipconfig (Windows) or ifconfig (Mac/Linux).

                        Update the IP address in LoginController.java on the client machine: // Replace with the actual Server IP
                                                                                             Socket socket = new Socket("192.168.x.x", 5000);
     
6.Finally, run the Launcher class to start the AlgoAnimate application!



👥 Meet the Team
This project was developed by undergraduate students of the CSE Department at BUET.
Moumita Talukder Mou-2405160
Sabiha Jannat Adiba-2405162

Supervisor: Arnob Saha Ankon

