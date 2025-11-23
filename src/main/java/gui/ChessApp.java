package gui;

import core.GameSetup;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChessApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("JavaFX Chess");

        primaryStage.setResizable(false);

        showMainMenu();
        primaryStage.show();
    }

    public static void showMainMenu() {
        MainMenu menu = new MainMenu();
        // Fixed size: 600 width, 600 height
        Scene scene = new Scene(menu.getLayout(), 900, 700);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    public static void showGameSetup() {
        GameSetup setup = new GameSetup();
        Scene scene = new Scene(setup.getLayout(), 900, 700);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    // ... showGameEngine() will go here later ...

    public static void main(String[] args) {
        launch(args);
    }
}