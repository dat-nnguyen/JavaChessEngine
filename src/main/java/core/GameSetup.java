package core;

import entities.Alliance;
import gui.ChessApp;
import gui.SoundManager;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.io.InputStream;

/**
 * Handles the game setup UI where players select mode, AI difficulty, time control, and side.
 * <p>
 * Provides a visually styled panel with dropdowns, radio buttons, and start/back buttons,
 * including video background handling.
 */
public class GameSetup {

    private final StackPane rootPane;
    private final VBox optionPanel;
    private Font pixelFont;

    private ToggleGroup modeGroup;
    private VBox difficultyBox;
    private ToggleGroup difficultyGroup;
    private ComboBox<Integer> timeDropdown;
    private ToggleGroup colorGroup;

    /**
     * Constructs the GameSetup panel and initializes all UI elements and background media.
     */
    public GameSetup() {
        // --- ROOT PANE & BACKGROUND ---
        this.rootPane = new StackPane();
        this.rootPane.setStyle("-fx-background-color: black;");
        addBackground("/assets/background/background.mp4");

        // --- FONT & OPTION PANEL ---
        this.pixelFont = loadCustomFont("/assets/Retro Gaming.ttf", 20);
        this.optionPanel = new VBox(10);
        this.optionPanel.setMaxSize(550, 520);
        this.optionPanel.setAlignment(Pos.TOP_CENTER);
        this.optionPanel.setPadding(new Insets(40));
        this.optionPanel.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.85);" +
                        "-fx-border-color: #8f563b;" +
                        "-fx-border-width: 4px;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;"
        );

        // --- HEADER ---
        Label header = new Label("GAME CONFIGURATION");
        header.setFont(loadCustomFont("/assets/Retro Gaming.ttf", 32));
        header.setStyle("-fx-text-fill: #e67e22; -fx-effect: dropshadow(gaussian, black, 3, 1.0, 0, 0);");

        // --- MODE SELECTION ---
        Label modeLabel = createHeaderLabel("Select Mode");
        RadioButton pvpBtn = createPixelRadioButton("Player vs Player");
        pvpBtn.setSelected(true);
        RadioButton aiBtn = createPixelRadioButton("Practice (vs AI)");

        modeGroup = new ToggleGroup();
        pvpBtn.setToggleGroup(modeGroup);
        aiBtn.setToggleGroup(modeGroup);

        // --- AI DIFFICULTY ---
        difficultyBox = new VBox(10);
        difficultyBox.setAlignment(Pos.CENTER);
        difficultyBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-padding: 10; -fx-background-radius: 5;");

        Label diffLabel = new Label("AI Difficulty");
        diffLabel.setFont(loadCustomFont("/assets/Retro Gaming.ttf", 22));
        diffLabel.setStyle("-fx-text-fill: #f1c40f; -fx-effect: dropshadow(gaussian, black, 2, 1.0, 0, 0);");

        RadioButton easyBtn = createPixelRadioButton("Easy");
        RadioButton medBtn = createPixelRadioButton("Medium");
        medBtn.setSelected(true);
        RadioButton hardBtn = createPixelRadioButton("Hard");

        difficultyGroup = new ToggleGroup();
        easyBtn.setToggleGroup(difficultyGroup);
        medBtn.setToggleGroup(difficultyGroup);
        hardBtn.setToggleGroup(difficultyGroup);

        difficultyBox.getChildren().addAll(diffLabel, easyBtn, medBtn, hardBtn);
        difficultyBox.setVisible(false);
        difficultyBox.setManaged(false);

        modeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            boolean isAI = (newVal == aiBtn);
            difficultyBox.setVisible(isAI);
            difficultyBox.setManaged(isAI);
        });

        // --- TIME CONTROL ---
        Label timeLabel = createHeaderLabel("Time per Player (Minutes)");
        timeDropdown = new ComboBox<>();
        timeDropdown.getItems().addAll(10, 20, 30);
        timeDropdown.setValue(10);
        loadCustomFont("/assets/Retro Gaming.ttf", 10);
        timeDropdown.setStyle("-fx-font-family: 'Retro Gaming'; -fx-font-size: 18px; -fx-background-color: #dcdcdc;");
        timeDropdown.setOnAction(e -> SoundManager.playClick());

        // --- COLOR SELECTION ---
        Label colorLabel = createHeaderLabel("Choose Side");
        RadioButton whiteBtn = createPixelRadioButton("White");
        whiteBtn.setSelected(true);
        RadioButton blackBtn = createPixelRadioButton("Black");
        RadioButton randBtn = createPixelRadioButton("Random");

        colorGroup = new ToggleGroup();
        whiteBtn.setToggleGroup(colorGroup);
        blackBtn.setToggleGroup(colorGroup);
        randBtn.setToggleGroup(colorGroup);

        HBox colorBox = new HBox(20, whiteBtn, blackBtn, randBtn);
        colorBox.setAlignment(Pos.CENTER);

        // --- BUTTONS ---
        Button startBtn = createImageButton("/assets/buttons/start.png");
        startBtn.setOnAction(e -> {
            SoundManager.playClick();

            RadioButton selectedMode = (RadioButton) modeGroup.getSelectedToggle();
            boolean isAI = selectedMode.getText().contains("AI");
            GameConfiguration.GameMode mode = isAI ? GameConfiguration.GameMode.HUMAN_VS_AI : GameConfiguration.GameMode.HUMAN_VS_HUMAN;

            GameConfiguration.Difficulty diff = null;
            if (isAI) {
                RadioButton selectedDiff = (RadioButton) difficultyGroup.getSelectedToggle();
                String t = selectedDiff.getText();
                if (t.equals("Easy")) diff = GameConfiguration.Difficulty.EASY;
                else if (t.equals("Medium")) diff = GameConfiguration.Difficulty.MEDIUM;
                else diff = GameConfiguration.Difficulty.HARD;
            }

            int time = timeDropdown.getValue();
            RadioButton selectedColor = (RadioButton) colorGroup.getSelectedToggle();
            String cText = selectedColor.getText();
            Alliance alliance = null;
            if (cText.equals("White")) alliance = Alliance.WHITE;
            else if (cText.equals("Black")) alliance = Alliance.BLACK;

            GameConfiguration config = new GameConfiguration(mode, diff, time, alliance);
            ChessApp.showGameEngine(config);
        });

        Button backBtn = createImageButton("/assets/buttons/back.png");
        backBtn.setOnAction(e -> {
            SoundManager.playClick();
            ChessApp.showMainMenu();
        });

        HBox buttonBox = new HBox(30, backBtn, startBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        // --- ADD ELEMENTS TO PANEL ---
        optionPanel.getChildren().addAll(header, new Separator(), modeLabel, pvpBtn, aiBtn, difficultyBox, timeLabel, timeDropdown, colorLabel, colorBox, new Separator(), buttonBox);
        rootPane.getChildren().add(optionPanel);
    }

    /**
     * Returns the root StackPane containing the setup UI.
     *
     * @return root layout node
     */
    public StackPane getLayout() { return this.rootPane; }

    // ---------------------------------------------------------------------------
    // --- HELPERS ---
    // ---------------------------------------------------------------------------

    private Label createHeaderLabel(String text) {
        Label l = new Label(text);
        l.setFont(loadCustomFont("/assets/Retro Gaming.ttf", 24));
        l.setStyle("-fx-text-fill: #e67e22; -fx-effect: dropshadow(gaussian, black, 2, 1.0, 0, 0);");
        return l;
    }

    private RadioButton createPixelRadioButton(String text) {
        RadioButton rb = new RadioButton(text);
        rb.setFont(loadCustomFont("/assets/Retro Gaming.ttf", 18));
        rb.setStyle("-fx-text-fill: #f0e6d2; -fx-cursor: hand;");
        rb.setOnMouseClicked(e -> SoundManager.playClick());
        return rb;
    }

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

            InputStream imgStream = getClass().getResourceAsStream("/assets/background/background.png");
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
                fade.setOnFinished(e -> this.rootPane.getChildren().remove(placeholder));
                fade.play();
            });
        } catch (Exception e) { e.printStackTrace(); this.rootPane.setStyle("-fx-background-color: #2b2b2b;"); }
    }

    private Button createImageButton(String path) {
        Button btn = new Button();
        InputStream is = getClass().getResourceAsStream(path);
        if (is != null) {
            ImageView v = new ImageView(new Image(is));
            v.setFitWidth(160); v.setPreserveRatio(true);
            btn.setGraphic(v);
            btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setScaleX(1.1));
            btn.setOnMouseExited(e -> btn.setScaleX(1.0));
        } else btn.setText("MISSING");
        return btn;
    }

    private Font loadCustomFont(String path, double size) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is != null) return Font.loadFont(is, size);
        } catch (Exception e) {}
        return new Font("Arial", size);
    }
}
