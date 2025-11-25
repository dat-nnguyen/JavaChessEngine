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
import java.util.function.Consumer;

public class TimerPanel extends HBox {

    private final Label whiteTimerLabel;
    private final Label blackTimerLabel;

    private long whiteSecondsLeft;
    private long blackSecondsLeft;
    private boolean isWhiteTurn;
    private Timeline timeline;
    private Font clockFont;

    private final Consumer<Alliance> onTimeOut;

    public TimerPanel(int totalMinutes, Consumer<Alliance> onTimeOut) {
        this.onTimeOut = onTimeOut;

        this.setSpacing(50);
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-padding: 15; -fx-background-radius: 10;");

        this.clockFont = loadFont();

        this.whiteSecondsLeft = totalMinutes * 60L;
        this.blackSecondsLeft = totalMinutes * 60L;
        this.isWhiteTurn = true;

        this.blackTimerLabel = createTimerLabel();
        this.whiteTimerLabel = createTimerLabel();

        updateLabels();

        // Highlight White initially
        whiteTimerLabel.setStyle("-fx-text-fill: #fff; -fx-border-color: #90EE90; -fx-border-width: 2px; -fx-padding: 5;");
        blackTimerLabel.setStyle("-fx-text-fill: #aaa; -fx-border-color: transparent; -fx-padding: 5;");

        this.getChildren().addAll(blackTimerLabel, whiteTimerLabel);

        this.timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
        this.timeline.setCycleCount(Timeline.INDEFINITE);
        this.timeline.play();
    }

    private void tick() {
        if (isWhiteTurn) {
            whiteSecondsLeft--;
            if (whiteSecondsLeft <= 0) {
                timeline.stop();
                if (onTimeOut != null) onTimeOut.accept(Alliance.WHITE);
            }
        } else {
            blackSecondsLeft--;
            if (blackSecondsLeft <= 0) {
                timeline.stop();
                if (onTimeOut != null) onTimeOut.accept(Alliance.BLACK);
            }
        }
        updateLabels();
    }

    // --- THIS IS THE MISSING METHOD ---
    public void switchTurn() {
        this.isWhiteTurn = !this.isWhiteTurn;

        // Update Visuals to show who is active
        if (isWhiteTurn) {
            whiteTimerLabel.setStyle("-fx-text-fill: #fff; -fx-border-color: #90EE90; -fx-border-width: 2px; -fx-padding: 5;");
            blackTimerLabel.setStyle("-fx-text-fill: #aaa; -fx-border-color: transparent; -fx-padding: 5;");
        } else {
            blackTimerLabel.setStyle("-fx-text-fill: #fff; -fx-border-color: #90EE90; -fx-border-width: 2px; -fx-padding: 5;");
            whiteTimerLabel.setStyle("-fx-text-fill: #aaa; -fx-border-color: transparent; -fx-padding: 5;");
        }
    }
    // ----------------------------------

    public void pause() {
        if (this.timeline != null) this.timeline.pause();
    }

    public void resume() {
        if (this.timeline != null) this.timeline.play();
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
        Label l = new Label("00:00");
        l.setFont(this.clockFont);
        l.setStyle("-fx-text-fill: #f0e6d2; -fx-padding: 5;");
        return l;
    }

    private Font loadFont() {
        try {
            InputStream is = getClass().getResourceAsStream("/assets/Retro Gaming.ttf");
            if (is != null) return Font.loadFont(is, 30);
        } catch (Exception e) { }
        return new Font("Arial", 30);
    }
}