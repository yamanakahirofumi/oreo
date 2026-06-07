module net.hero.editor.oreo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens net.hero.editor.oreo to javafx.fxml;
    opens net.hero.editor.oreo.controller to javafx.fxml;
    exports net.hero.editor.oreo;
}
