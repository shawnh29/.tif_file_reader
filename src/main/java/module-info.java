module com.example.cmpt365_tiff_reader {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.cmpt365_tiff_reader to javafx.fxml;
    exports com.example.cmpt365_tiff_reader;
}