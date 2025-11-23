package gui;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.application.Platform;
import java.io.InputStream;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

public class MainMenu {

    private final StackPane rootPane;
    private final VBox menuBox;
    private Font pixelFont;

    public MainMenu() {
        this.rootPane = new StackPane();
        this.rootPane.setStyle("-fx-background-color: black;");
        addBackground("/assets/background.mp4");

        this.pixelFont = loadCustomFont("/assets/alagard.ttf", 80);

        this.menuBox = new VBox(-50);
        this.menuBox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("CHESS GAME");
        titleLabel.setFont(this.pixelFont);
        titleLabel.setStyle("-fx-text-fill: #f0e6d2; -fx-effect: dropshadow(gaussian, black, 5, 1.0, 0, 0);");

        Button playButton = createImageButton("/assets/play.png");

        playButton.setOnAction(e -> ChessApp.showGameSetup());

        Button exitButton = createImageButton("/assets/exit.png");
        exitButton.setOnAction(e -> {
            Platform.exit();
            System.exit(0);
        });

        this.menuBox.getChildren().addAll(titleLabel, playButton, exitButton);
        this.rootPane.getChildren().add(this.menuBox);
    }

    public StackPane getLayout() {
        return this.rootPane;
    }

    // --- HELPER: Load Custom Font ---
    private Font loadCustomFont(String path, double size) {
        try {
            InputStream fontStream = getClass().getResourceAsStream(path);
            if (fontStream != null) {
                // Load the font
                return Font.loadFont(fontStream, size);
            } else {
                System.out.println("Font not found: " + path);
                return new Font("Arial", size);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("Arial", size);
        }
    }

    // --- HELPER: Add Background ---
    private void addBackground(String videoPath) {
        try {
            String mediaUrl = getClass().getResource(videoPath).toExternalForm();
            Media media = new Media(mediaUrl);
            MediaPlayer player = new MediaPlayer(media);
            player.setCycleCount(MediaPlayer.INDEFINITE);
            player.setAutoPlay(true);
            player.setMute(true);

            MediaView mediaView = new MediaView(player);
            mediaView.fitWidthProperty().bind(this.rootPane.widthProperty());
            mediaView.fitHeightProperty().bind(this.rootPane.heightProperty());
            mediaView.setPreserveRatio(false);

            InputStream imgStream = getClass().getResourceAsStream("/assets/background.png");
            ImageView placeholder = new ImageView(new Image(imgStream));
            placeholder.fitWidthProperty().bind(this.rootPane.widthProperty());
            placeholder.fitHeightProperty().bind(this.rootPane.heightProperty());
            placeholder.setPreserveRatio(false);

            this.rootPane.getChildren().add(0, mediaView);
            this.rootPane.getChildren().add(1, placeholder);

            player.setOnPlaying(() -> {

                FadeTransition fade = new FadeTransition(Duration.millis(500), placeholder);
                fade.setFromValue(1.0);
                fade.setToValue(0.0);
                fade.setOnFinished(e -> {
                    this.rootPane.getChildren().remove(placeholder);
                });
                fade.play();
            });

        } catch (Exception e) {
            e.printStackTrace();
            this.rootPane.setStyle("-fx-background-color: #2b2b2b;");
        }
    }

    // --- HELPER: Image Button ---
    private Button createImageButton(String imagePath) {
        Button btn = new Button();
        InputStream imageStream = getClass().getResourceAsStream(imagePath);
        if (imageStream != null) {
            Image img = new Image(imageStream);
            ImageView view = new ImageView(img);
            view.setFitWidth(300);
            view.setPreserveRatio(true);
            btn.setGraphic(view);
            btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            // Hover Effects
            btn.setOnMouseEntered(e -> btn.setScaleX(1.1));
            btn.setOnMouseEntered(e -> { btn.setScaleX(1.1); btn.setScaleY(1.1); });
            btn.setOnMouseExited(e -> { btn.setScaleX(1.0); btn.setScaleY(1.0); });
        }
        return btn;
    }
}