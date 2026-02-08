module com.example.algoanimate {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.algoanimate to javafx.fxml;
    exports com.example.algoanimate;
}