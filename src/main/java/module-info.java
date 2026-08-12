module isi.diti3.micoguest {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires static lombok;
    requires jakarta.persistence;
    requires jbcrypt;

    exports isi.diti3.micoguest;
    opens isi.diti3.micoguest to javafx.fxml;
    exports com.microgest.model;
    exports com.microgest.repository;
    exports com.microgest.service;
    exports com.microgest.util;
    exports com.microgest.controller;
    opens com.microgest.model to org.hibernate.orm.core;
    opens com.microgest.controller to javafx.fxml;
    opens com.microgest.util to org.hibernate.orm.core;
    //exports com.microgest.controllers;
    //opens com.microgest.controllers to javafx.fxml;
}