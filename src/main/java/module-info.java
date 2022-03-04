module com.ihor.spendingorganizer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.json;

    opens com.ihor.spendingorganizer.controllers to javafx.fxml;
    opens com.ihor.spendingorganizer.model to javafx.base;
    exports com.ihor.spendingorganizer;
    opens com.ihor.spendingorganizer to javafx.fxml, javafx.graphics;
}