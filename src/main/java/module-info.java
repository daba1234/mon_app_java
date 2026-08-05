module isi.diti3.micoguest {
    requires javafx.controls;
    requires javafx.fxml;


    opens isi.diti3.micoguest to javafx.fxml;
    exports isi.diti3.micoguest;
}