module com.snakegame {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens com.snakegame to javafx.fxml;
    exports com.snakegame;
}
