package core;

import entities.Board;
import entities.MoveTransition;
import entities.Piece;
import entities.Square;
import gui.BoardPanel;
import gui.ChessApp;
import gui.SoundManager;
import gui.TimerPanel;
import java.io.InputStream;
import java.util.Collection;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;

/**
 * Main engine responsible for handling gameplay, UI layering, input processing, timers,
 * background media, and communication with BoardPanel.
 */
public class GameEngine {

    private final StackPane rootLayer;
    private final BorderPane uiLayer;

    private final BoardPanel boardPanel;
    private final TimerPanel gameTimer;
    private Board chessBoard;

    private VBox pauseMenu;
    private VBox confirmationOverlay;

    private Square sourceSquare;
    private Square destinationSquare;
    private Piece humanMovedPiece;
    private boolean isMusicMuted = false;

    /**
     * Constructs the main GameEngine, sets up UI layers, menus, video background, and connects
     * gameplay components such as board, timers, and user input.
     *
     * @param config game configuration including time control and settings
     */
    public GameEngine(GameConfiguration config) {
        // --- INITIALIZE GAME STATE ---
        this.chessBoard = Board.createStandardBoard();
        this.gameTimer = new TimerPanel(config.getTimeControlMinutes());
        this.boardPanel = new BoardPanel(this, config);

        // --- SETUP ROOT LAYER + BACKGROUND ---
        this.rootLayer = new StackPane();
        this.rootLayer.setStyle("-fx-background-color: black;");
        addBackground("/assets/background.mp4");

        // --- SETUP MAIN UI LAYER ---
        this.uiLayer = new BorderPane();
        this.uiLayer.setCenter(this.boardPanel);
        BorderPane.setMargin(this.boardPanel, new Insets(25, 0, 0, 0));
        this.uiLayer.setBottom(this.gameTimer);

        // --- CREATE UI MENUS ---
        createPauseMenu();
        createConfirmationOverlay();

        // --- PAUSE BUTTON (TOP LEFT) ---
        Button menuBtn = createImageButton("/assets/pause.png", 100);
        menuBtn.setOnAction(
                e -> {
                    SoundManager.playClick();
                    gameTimer.pause();
                    pauseMenu.setVisible(true);
                    confirmationOverlay.setVisible(false);
                });
        StackPane.setAlignment(menuBtn, Pos.TOP_LEFT);
        StackPane.setMargin(menuBtn, new Insets(15));

        // --- SOUND TOGGLE BUTTON (BOTTOM LEFT) ---
        Button soundBtn = new Button();
        ImageView soundOnIcon = loadIcon("/assets/unmute.png", 70);
        ImageView soundOffIcon = loadIcon("/assets/mute.png", 70);

        soundBtn.setGraphic(soundOnIcon);
        soundBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        soundBtn.setOnAction(
                e -> {
                    SoundManager.playClick();
                    isMusicMuted = !isMusicMuted;
                    if (isMusicMuted) {
                        SoundManager.setMusicMuted(true);
                        soundBtn.setGraphic(soundOffIcon);
                    } else {
                        SoundManager.setMusicMuted(false);
                        soundBtn.setGraphic(soundOnIcon);
                    }
                });

        soundBtn.setOnMouseEntered(e -> soundBtn.setScaleX(1.1));
        soundBtn.setOnMouseEntered(
                e -> {
                    soundBtn.setScaleX(1.1);
                    soundBtn.setScaleY(1.1);
                });
        soundBtn.setOnMouseExited(
                e -> {
                    soundBtn.setScaleX(1.0);
                    soundBtn.setScaleY(1.0);
                });

        StackPane.setAlignment(soundBtn, Pos.BOTTOM_LEFT);
        StackPane.setMargin(soundBtn, new Insets(15));

        // --- ATTACH ALL UI LAYERS ---
        this.rootLayer.getChildren()
                .addAll(this.uiLayer, menuBtn, soundBtn, pauseMenu, confirmationOverlay);

        // --- INITIAL DRAW ---
        this.boardPanel.drawBoard(this.chessBoard);
    }

    /**
     * Returns the main layout StackPane that contains video background, board UI, controls, and menus.
     *
     * @return root layout node
     */
    public StackPane getLayout() {
        return this.rootLayer;
    }

    // ---------------------------------------------------------------------------
    // --- UI / RESOURCE HELPERS ---
    // ---------------------------------------------------------------------------

    /**
     * Loads an icon (ImageView) from resources.
     *
     * @param path internal resource path
     * @param size desired width
     * @return resized ImageView
     */
    private ImageView loadIcon(String path, double size) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is != null) {
                ImageView v = new ImageView(new Image(is));
                v.setFitWidth(size);
                v.setPreserveRatio(true);
                return v;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ImageView();
    }

    /**
     * Creates and styles the pause menu UI.
     */
    private void createPauseMenu() {
        this.pauseMenu = new VBox(15);
        this.pauseMenu.setAlignment(Pos.CENTER);
        this.pauseMenu.setMaxSize(350, 300);
        this.pauseMenu.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.9); "
                        + "-fx-border-color: #8f563b; -fx-border-width: 4px; "
                        + "-fx-background-radius: 10; -fx-border-radius: 10;");

        Label title = new Label("GAME PAUSED");
        title.setFont(loadCustomFont("/assets/Retro Gaming.ttf", 32));
        title.setStyle(
                "-fx-text-fill: #e67e22; "
                        + "-fx-effect: dropshadow(gaussian, black, 3, 1.0, 0, 0);");

        Button continueBtn = createImageButton("/assets/continue.png", 180);
        continueBtn.setOnAction(
                e -> {
                    SoundManager.playClick();
                    this.pauseMenu.setVisible(false);
                    gameTimer.resume();
                });

        Button exitBtn = createImageButton("/assets/exitmatch.png", 180);
        exitBtn.setOnAction(
                e -> {
                    SoundManager.playClick();
                    this.pauseMenu.setVisible(false);
                    this.confirmationOverlay.setVisible(true);
                });

        this.pauseMenu.getChildren().addAll(title, continueBtn, exitBtn);
        this.pauseMenu.setVisible(false);
    }

    /**
     * Creates the confirmation overlay that appears when the user attempts to exit a match.
     */
    private void createConfirmationOverlay() {
        this.confirmationOverlay = new VBox(20);
        this.confirmationOverlay.setAlignment(Pos.CENTER);
        this.confirmationOverlay.setMaxSize(400, 250);
        this.confirmationOverlay.setStyle(
                "-fx-background-color: rgba(50, 0, 0, 0.95); "
                        + "-fx-border-color: #c0392b; -fx-border-width: 4px; "
                        + "-fx-background-radius: 10;");

        Label warning = new Label("You will lose the match.\nAre you sure?");
        warning.setFont(loadCustomFont("/assets/Retro Gaming.ttf", 24));
        warning.setStyle("-fx-text-fill: #ecf0f1; -fx-text-alignment: center;");

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        Button yesBtn = createImageButton("/assets/yes.png", 100);
        yesBtn.setOnAction(
                e -> {
                    SoundManager.playClick();
                    SoundManager.playMusic();
                    ChessApp.showMainMenu();
                });

        Button noBtn = createImageButton("/assets/no.png", 100);
        noBtn.setOnAction(
                e -> {
                    SoundManager.playClick();
                    this.confirmationOverlay.setVisible(false);
                    gameTimer.resume();
                });

        buttons.getChildren().addAll(yesBtn, noBtn);
        this.confirmationOverlay.getChildren().addAll(warning, buttons);
        this.confirmationOverlay.setVisible(false);
    }

    /**
     * Creates a button using an image from resources.
     *
     * @param path path to image
     * @param width displayed width
     * @return configured Button
     */
    private Button createImageButton(String path, double width) {
        Button btn = new Button();
        InputStream is = getClass().getResourceAsStream(path);
        if (is != null) {
            ImageView v = new ImageView(new Image(is));
            v.setFitWidth(width);
            v.setPreserveRatio(true);
            btn.setGraphic(v);
            btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setScaleX(1.1));
            btn.setOnMouseExited(e -> btn.setScaleX(1.0));
        }
        return btn;
    }

    /**
     * Loads a custom TrueType font from resources.
     *
     * @param path font resource path
     * @param size point size
     * @return loaded Font instance
     */
    private Font loadCustomFont(String path, double size) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is != null) {
                return Font.loadFont(is, size);
            }
        } catch (Exception e) {
            // ignore
        }
        return new Font("Arial", size);
    }

    /**
     * Adds and configures a looping background video behind the UI.
     *
     * @param videoPath resource path to mp4 file
     */
    private void addBackground(String videoPath) {
        try {
            String mediaUrl = getClass().getResource(videoPath).toExternalForm();
            Media media = new Media(mediaUrl);
            MediaPlayer player = new MediaPlayer(media);

            player.setCycleCount(MediaPlayer.INDEFINITE);
            player.setAutoPlay(true);
            player.setMute(true);

            MediaView mediaView = new MediaView(player);
            mediaView.fitWidthProperty().bind(this.rootLayer.widthProperty());
            mediaView.fitHeightProperty().bind(this.rootLayer.heightProperty());
            mediaView.setPreserveRatio(false);

            this.rootLayer.getChildren().add(0, mediaView);
        } catch (Exception e) {
            e.printStackTrace();
            this.rootLayer.setStyle("-fx-background-color: #202020;");
        }
    }

    // ---------------------------------------------------------------------------
    // --- INPUT HANDLING: USER CLICK LOGIC ---
    // ---------------------------------------------------------------------------

    /**
     * Handles board clicks: selecting pieces, highlighting moves, and executing moves.
     *
     * @param squareId ID of the clicked square (0..63)
     */
    public void handleMouseClick(int squareId) {
        boardPanel.drawBoard(this.chessBoard);

        // --- SECOND CLICK: attempt to move ---
        if (sourceSquare != null) {
            SoundManager.playClick();
            destinationSquare = chessBoard.getSquare(squareId);

            final Move move =
                    findLegalMove(
                            sourceSquare.getSquareCoordinate(), destinationSquare.getSquareCoordinate());

            if (move != null) {
                final MoveTransition transition = chessBoard.getCurrentPlayer().makeMove(move);
                if (transition.getMoveStatus().isDone()) {
                    this.chessBoard = transition.getTransitionBoard();
                    this.gameTimer.switchTurn();
                    boardPanel.drawBoard(this.chessBoard);
                    System.out.println("Move executed!");
                }
            }

            sourceSquare = null;
            destinationSquare = null;
            humanMovedPiece = null;
            return;
        }

        // --- FIRST CLICK: selecting a piece ---
        Square clickedSquare = chessBoard.getSquare(squareId);
        if (clickedSquare.isOccupied()) {
            Piece piece = clickedSquare.getPiece();
            if (piece.getPieceAlliance() == chessBoard.getCurrentPlayer().getAlliance()) {
                SoundManager.playClick();

                sourceSquare = clickedSquare;
                humanMovedPiece = piece;

                boardPanel.highlightSourceSquare(squareId);
                final Collection<Move> legalMoves = piece.calculateLegalMoves(this.chessBoard);
                boardPanel.highlightLegals(legalMoves, piece.getPieceAlliance());
            }
        }
    }

    /**
     * Utility method to identify a legal move based on coordinates.
     *
     * @param currentPos origin square index
     * @param destinationPos target square index
     * @return matching Move or null if illegal
     */
    private Move findLegalMove(int currentPos, int destinationPos) {
        for (final Move move : this.chessBoard.getCurrentPlayer().getLegalMoves()) {
            if (move.getMovedPiece().getPiecePosition() == currentPos
                    && move.getDestinationCoordinate() == destinationPos) {
                return move;
            }
        }
        return null;
    }
}
