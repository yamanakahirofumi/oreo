package net.hero.editor.oreo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import net.hero.editor.oreo.controller.EditorController;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/net/hero/editor/oreo/fxml/EditorWindow.fxml"));
            BorderPane root = loader.load();
            
            // コントローラーの取得
            EditorController controller = loader.getController();
            
            Scene scene = new Scene(root, 800, 600);
            
            // ショートカットキーの登録 (OSごとの慣習に従い、Windows/LinuxではCtrl+S、MacではCmd+S)
            scene.getAccelerators().put(
                    new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN),
                    controller::saveActiveTab
            );
            
            primaryStage.setTitle("OREO - Overpowered Rich Editor for Overlords");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
