package core;

import core.GameConfiguration;
import entities.Board;
import core.Move;
import entities.MoveTransition;
import entities.Piece;
import entities.Square;
import entities.Alliance;
import gui.BoardPanel;
import gui.ChessApp;
import gui.SoundManager;
import gui.TimerPanel;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.Collection;

public class GameEngine {

    private final StackPane rootLayer;
    private final BorderPane uiLayer;

    private final BoardPanel boardPanel;
    private final TimerPanel gameTimer;
    private Board chessBoard;

    // Menus
    private VBox pauseMenu;
    private VBox confirmationOverlay;
    private VBox gameOverMenu;

    private Square sourceSquare;
    private Square destinationSquare;
    private Piece humanMovedPiece;

    private boolean isMusicMuted = false;
    private boolean isGameEnded = false;

    private final GameConfiguration config;
    private Font pixelFont;

    public GameEngine(GameConfiguration config) {
        this.config = config;
        this.chessBoard = Board.createStandardBoard();
        this.pixelFont = loadCustomFont("/assets/Retro Gaming.ttf", 20);

        this.gameTimer = new TimerPanel(config.getTimeControlMinutes(), this::handleTimeOut);
        this.boardPanel = new BoardPanel(this, config);

        this.rootLayer = new StackPane();
        this.rootLayer.setStyle("-fx-background-color: black;");
        addBackground("/assets/background.mp4");

        this.uiLayer = new BorderPane();
        this.uiLayer.setCenter(this.boardPanel);
        this.uiLayer.setBottom(this.gameTimer);
        BorderPane.setMargin(this.boardPanel, new Insets(25, 0, 0, 0));

        // --- CREATE MENUS ---
        createPauseMenu();
        createConfirmationOverlay();
        createGameOverMenu();

        // --- SOUND BUTTON ---
        Button soundBtn = new Button();
        ImageView soundOnIcon = loadIcon("/assets/unmute.png", 50);
        ImageView soundOffIcon = loadIcon("/assets/mute.png", 50);
        soundBtn.setGraphic(soundOnIcon);
        soundBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        soundBtn.setOnAction(e -> {
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
        // Sound Btn Styling
        soundBtn.setOnMouseEntered(e -> soundBtn.setScaleX(1.1));
        soundBtn.setOnMouseEntered(e -> { soundBtn.setScaleX(1.1); soundBtn.setScaleY(1.1); });
        soundBtn.setOnMouseExited(e -> { soundBtn.setScaleX(1.0); soundBtn.setScaleY(1.0); });
        StackPane.setAlignment(soundBtn, Pos.BOTTOM_LEFT);
        StackPane.setMargin(soundBtn, new Insets(15));

        // --- MENU BUTTON ---
        Button menuBtn = createImageButton("/assets/pause.png", 90);
        menuBtn.setOnAction(e -> {
            if (!isGameEnded) { // Disable pause if game is over
                SoundManager.playClick();
                gameTimer.pause();
                pauseMenu.setVisible(true);
                confirmationOverlay.setVisible(false);
            }
        });
        StackPane.setAlignment(menuBtn, Pos.TOP_LEFT);
        StackPane.setMargin(menuBtn, new Insets(15));

        // --- ASSEMBLE ROOT ---
        this.rootLayer.getChildren().addAll(this.uiLayer, menuBtn, soundBtn, pauseMenu, confirmationOverlay, gameOverMenu);

        this.boardPanel.drawBoard(this.chessBoard);
    }

    public StackPane getLayout() { return this.rootLayer; }

    private void createGameOverMenu() {
        this.gameOverMenu = new VBox(20);
        this.gameOverMenu.setAlignment(Pos.CENTER);
        this.gameOverMenu.setMaxSize(400, 350);

        // Gold Border Style
        this.gameOverMenu.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.95);" +
                        "-fx-border-color: #f1c40f; -fx-border-width: 5px;" +
                        "-fx-background-radius: 15; -fx-border-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, black, 20, 0.5, 0, 0);"
        );

        // 1. Title Image
        ImageView titleImage = new ImageView();
        titleImage.setFitWidth(300);
        titleImage.setPreserveRatio(true);

        // 2. Winner Text
        Label winnerLabel = new Label("");
        winnerLabel.setFont(loadCustomFont("/assets/Retro Gaming.ttf", 28));
        winnerLabel.setStyle("-fx-text-fill: #f0e6d2; -fx-effect: dropshadow(gaussian, black, 2, 1.0, 0, 0);");

        // 3. Buttons
        Button playAgainBtn = createImageButton("/assets/buttons/playagain.png", 200);
        playAgainBtn.setOnAction(e -> {
            SoundManager.playClick();
            // Restart with same config
            ChessApp.showGameEngine(this.config);
        });

        Button exitBtn = createImageButton("/assets/exitmatch.png", 200);
        exitBtn.setOnAction(e -> {
            SoundManager.playClick();
            SoundManager.playMusic(); // Restart music for menu
            ChessApp.showMainMenu();
        });

        this.gameOverMenu.getChildren().addAll(titleImage, winnerLabel, playAgainBtn, exitBtn);
        this.gameOverMenu.setVisible(false);
    }

    private void checkGameOver() {
        if (chessBoard.getCurrentPlayer().isInCheckMate()) {
            // Opponent Won
            Alliance winner = chessBoard.getCurrentPlayer().getOpponent().getAlliance();
            String winnerText = winner.isWhite() ? "White Wins!" : "Black Wins!";
            showEndScreen("/assets/checkmate.png", winnerText);

        } else if (chessBoard.getCurrentPlayer().isInStaleMate()) {
            showEndScreen("/assets/stalemate.png", "Draw (Stalemate)");
        }
    }
    private void handleTimeOut(Alliance loser) {
        if (isGameEnded) return;
        String winner = loser.isWhite() ? "Black" : "White";
        showEndScreen("/assets/checkmate.png", winner + " Wins (Time Out)!");
    }
    private void showEndScreen(String imagePath, String text) {
        this.isGameEnded = true;
        this.gameTimer.pause(); // Stop the clock

        // Update Image
        ImageView title = (ImageView) this.gameOverMenu.getChildren().get(0);
        try {
            InputStream is = getClass().getResourceAsStream(imagePath);
            if (is != null) title.setImage(new Image(is));
        } catch (Exception e) { e.printStackTrace(); }

        // Update Text
        Label label = (Label) this.gameOverMenu.getChildren().get(1);
        label.setText(text);

        // Fade In Animation
        FadeTransition fade = new FadeTransition(Duration.millis(1000), this.gameOverMenu);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        this.gameOverMenu.setVisible(true);
        fade.play();
    }

    // --- EXISTING MENUS ---

    private void createPauseMenu() {
        this.pauseMenu = new VBox(15);
        this.pauseMenu.setAlignment(Pos.CENTER);
        this.pauseMenu.setMaxSize(350, 300);
        this.pauseMenu.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9); -fx-border-color: #8f563b; -fx-border-width: 4px; -fx-background-radius: 10;");

        Label title = new Label("GAME PAUSED");
        title.setFont(loadCustomFont("/assets/Retro Gaming.ttf", 32));
        title.setStyle("-fx-text-fill: #e67e22;");

        Button continueBtn = createImageButton("/assets/continue.png", 200);
        continueBtn.setOnAction(e -> {
            SoundManager.playClick();
            this.pauseMenu.setVisible(false);
            gameTimer.resume();
        });

        Button exitBtn = createImageButton("/assets/exitmatch.png", 200);
        exitBtn.setOnAction(e -> {
            SoundManager.playClick();
            this.pauseMenu.setVisible(false);
            this.confirmationOverlay.setVisible(true);
        });

        this.pauseMenu.getChildren().addAll(title, continueBtn, exitBtn);
        this.pauseMenu.setVisible(false);
    }

    private void createConfirmationOverlay() {
        this.confirmationOverlay = new VBox(20);
        this.confirmationOverlay.setAlignment(Pos.CENTER);
        this.confirmationOverlay.setMaxSize(400, 250);
        this.confirmationOverlay.setStyle("-fx-background-color: rgba(50, 0, 0, 0.95); -fx-border-color: #c0392b; -fx-border-width: 4px; -fx-background-radius: 10;");

        Label warning = new Label("You will lose the match.\nAre you sure?");
        warning.setFont(loadCustomFont("/assets/Retro Gaming.ttf", 24));
        warning.setStyle("-fx-text-fill: #ecf0f1; -fx-text-alignment: center;");

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        Button yesBtn = createImageButton("/assets/yes.png", 100);
        yesBtn.setOnAction(e -> {
            SoundManager.playClick();
            SoundManager.playMusic();
            ChessApp.showMainMenu();
        });

        Button noBtn = createImageButton("/assets/no.png", 100);
        noBtn.setOnAction(e -> {
            SoundManager.playClick();
            this.confirmationOverlay.setVisible(false);
            gameTimer.resume();
        });

        buttons.getChildren().addAll(yesBtn, noBtn);
        this.confirmationOverlay.getChildren().addAll(warning, buttons);
        this.confirmationOverlay.setVisible(false);
    }

    // --- INPUT HANDLING ---

    public void handleMouseClick(int squareId) {
        if (isGameEnded) return; // BLOCK INPUT IF GAME OVER

        boardPanel.drawBoard(this.chessBoard);

        if (sourceSquare != null) {
            SoundManager.playClick();
            destinationSquare = chessBoard.getSquare(squareId);

            // Note: Ensure Square.java has getTileCoordinate() method visible
            final Move move = findLegalMove(sourceSquare.getSquareCoordinate(), destinationSquare.getSquareCoordinate());

            if (move != null) {
                final MoveTransition transition = chessBoard.getCurrentPlayer().makeMove(move);
                if (transition.getMoveStatus().isDone()) {
                    this.chessBoard = transition.getTransitionBoard();
                    this.gameTimer.switchTurn();
                    boardPanel.drawBoard(this.chessBoard);

                    // --- CHECK END GAME ---
                    checkGameOver();
                }
            }
            sourceSquare = null;
            destinationSquare = null;
            humanMovedPiece = null;

        } else {
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
    }

    // --- UTILS ---
    private Move findLegalMove(int currentPos, int destinationPos) {
        for (final Move move : this.chessBoard.getCurrentPlayer().getLegalMoves()) {
            if (move.getMovedPiece().getPiecePosition() == currentPos &&
                    move.getDestinationCoordinate() == destinationPos) {
                return move;
            }
        }
        return null;
    }

    private Button createImageButton(String path, double width) {
        Button btn = new Button();
        InputStream is = getClass().getResourceAsStream(path);
        if (is != null) {
            ImageView v = new ImageView(new Image(is));
            v.setFitWidth(width); v.setPreserveRatio(true);
            btn.setGraphic(v);
            btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            btn.setOnMouseEntered(e -> btn.setScaleX(1.1));
            btn.setOnMouseExited(e -> btn.setScaleX(1.0));
        }
        return btn;
    }

    private Font loadCustomFont(String path, double size) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is != null) return Font.loadFont(is, size);
        } catch (Exception e) { }
        return new Font("Arial", size);
    }

    private ImageView loadIcon(String path, double size) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is != null) {
                ImageView v = new ImageView(new Image(is));
                v.setFitWidth(size);
                v.setPreserveRatio(true);
                return v;
            }
        } catch (Exception e) { }
        return new ImageView();
    }

    private void addBackground(String imagePath) {
        try {
            InputStream is = getClass().getResourceAsStream(imagePath);
            if (is != null) {
                String mediaUrl = getClass().getResource(imagePath).toExternalForm();
                Media media = new Media(mediaUrl);
                MediaPlayer player = new MediaPlayer(media);
                player.setCycleCount(MediaPlayer.INDEFINITE);
                player.setAutoPlay(true);
                player.setMute(true);
                MediaView mediaView = new MediaView(player);
                mediaView.fitWidthProperty().bind(rootLayer.widthProperty());
                mediaView.fitHeightProperty().bind(rootLayer.heightProperty());
                mediaView.setPreserveRatio(false);
                this.rootLayer.getChildren().add(0, mediaView);
            }
        } catch (Exception e) {
            this.rootLayer.setStyle("-fx-background-color: #202020;");
        }
    }
}