module isi.diti3.micoguest {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    exports isi.diti3.micoguest;
    opens isi.diti3.micoguest to javafx.fxml;
}