package gui;

import core.GameConfiguration;
import core.GameEngine;
import core.GameSetup;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChessApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(final Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("JavaFX Chess");
        primaryStage.setResizable(false);

        SoundManager.playMusic();
        showMainMenu();
        primaryStage.show();
    }

    public static void showMainMenu() {
        final MainMenu menu = new MainMenu();
        final Scene scene = new Scene(menu.getLayout(), 900, 700);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    public static void showGameSetup() {
        final GameSetup setup = new GameSetup();
        final Scene scene = new Scene(setup.getLayout(), 900, 700);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    public static void showGameEngine(final GameConfiguration config) {
        final GameEngine engine = new GameEngine(config);
        final Scene scene = new Scene(engine.getLayout(), 900, 700);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
