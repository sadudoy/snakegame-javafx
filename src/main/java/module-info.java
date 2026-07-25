module com.snakegame {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;

    opens com.snakegame to javafx.fxml;
    exports com.snakegame;
}
