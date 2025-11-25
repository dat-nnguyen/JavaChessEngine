package gui;

import entities.Alliance;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.io.InputStream;

public class TimerPanel extends HBox {

    private final Label whiteTimerLabel;
    private final Label blackTimerLabel;

    private long whiteSecondsLeft;
    private long blackSecondsLeft;
    private boolean isWhiteTurn;
    private final Timeline timeline;
    private final Font clockFont;

    public TimerPanel(int totalMinutes) {
        // Layout styling
        this.setSpacing(50);
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-padding: 15;");

        this.clockFont = loadFont();

        this.whiteSecondsLeft = totalMinutes * 60L;
        this.blackSecondsLeft = totalMinutes * 60L;
        this.isWhiteTurn = true;

        this.blackTimerLabel = createTimerLabel();
        this.whiteTimerLabel = createTimerLabel();
        updateLabels();
        switchTurn(); // Initialize border highlight

        this.getChildren().addAll(blackTimerLabel, whiteTimerLabel);

        // Timer update every second
        this.timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
        this.timeline.setCycleCount(Timeline.INDEFINITE);
        this.timeline.play();
    }

    private void tick() {
        if (isWhiteTurn) {
            whiteSecondsLeft--;
            if (whiteSecondsLeft <= 0) handleTimeOut(Alliance.WHITE);
        } else {
            blackSecondsLeft--;
            if (blackSecondsLeft <= 0) handleTimeOut(Alliance.BLACK);
        }
        updateLabels();
    }

    public void switchTurn() {
        this.isWhiteTurn = !this.isWhiteTurn;
        if (isWhiteTurn) {
            whiteTimerLabel.setStyle("-fx-text-fill: #fff; -fx-border-color: #90EE90; -fx-border-width: 2px; -fx-padding: 5;");
            blackTimerLabel.setStyle("-fx-text-fill: #aaa; -fx-border-color: transparent; -fx-padding: 5;");
        } else {
            blackTimerLabel.setStyle("-fx-text-fill: #fff; -fx-border-color: #90EE90; -fx-border-width: 2px; -fx-padding: 5;");
            whiteTimerLabel.setStyle("-fx-text-fill: #aaa; -fx-border-color: transparent; -fx-padding: 5;");
        }
    }

    private void updateLabels() {
        whiteTimerLabel.setText("White: " + formatTime(whiteSecondsLeft));
        blackTimerLabel.setText("Black: " + formatTime(blackSecondsLeft));
    }

    private String formatTime(long totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private Label createTimerLabel() {
        Label label = new Label("00:00");
        label.setFont(this.clockFont);
        label.setStyle("-fx-text-fill: #f0e6d2; -fx-padding: 5;");
        return label;
    }

    private void handleTimeOut(Alliance loser) {
        this.timeline.stop();
        System.out.println("TIME OUT! " + loser + " lost.");
        // TODO: Trigger game-over event in GameEngine
    }

    private Font loadFont() {
        try (InputStream is = getClass().getResourceAsStream("/assets/Retro Gaming.ttf")) {
            if (is != null) return Font.loadFont(is, 30);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Font("Arial", 30);
    }

    public void pause() {
        if (this.timeline != null) this.timeline.pause();
    }

    public void resume() {
        if (this.timeline != null) this.timeline.play();
    }
}
