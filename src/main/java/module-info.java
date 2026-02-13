module com.example.algoanimate {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
//    requires com.example.algoanimate;


    opens com.example.algoanimate to javafx.fxml;
    exports com.example.algoanimate;
}