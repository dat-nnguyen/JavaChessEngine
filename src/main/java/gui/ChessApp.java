import gui.MainMenu;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChessApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("JavaFX Chess");
        showMainMenu();
        primaryStage.show();
    }

    public static void showMainMenu() {
        MainMenu menu = new MainMenu();
        Scene scene = new Scene(menu.getLayout(), 600, 600);
        primaryStage.setScene(scene);
    }

    // --- THIS IS THE MISSING PART ---
    // MainMenu tries to call this. If it's missing, you get "Cannot find symbol"
    public static void showGameSetup() {
        System.out.println("Navigate to Game Setup...");
    }

    public static void main(String[] args) {
        launch(args);
    }
}