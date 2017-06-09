package sample;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.nio.file.Paths;

public class Controller {
    @FXML
    private Label video_title;
    @FXML
    private MediaView media_view;
    @FXML
    private Button play_button;
    @FXML
    private Slider time_slider;
    @FXML
    private Label playTime;
    @FXML
    private Slider volume_slider;
    @FXML
    private Button full_screen_button;

    private Media media;
    private MediaPlayer mediaPlayer;
    private boolean atEndOfMedia = false;
    private Duration duration;
    private final boolean repeat = true;
    private boolean stopRequested = false;
    private String host;

    @FXML
    public void initialize(String host, String name) {
        this.host = host;
        video_title.setText(name);
        media = new Media(Paths.get("VIDEO/" + name).toUri().toString());
        mediaPlayer = new MediaPlayer(media);
        media_view.setMediaPlayer(mediaPlayer);
        mediaPlayer.setAutoPlay(true);
        DoubleProperty width = media_view.fitWidthProperty();
        DoubleProperty height = media_view.fitHeightProperty();
        width.bind(Bindings.selectDouble(media_view.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(media_view.sceneProperty(), "height"));

        setProperty();
    }

    private void setProperty()
    {
        full_screen_button.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ESCAPE)
                {
                    full_screen_button.setStyle("-fx-background-image: url('File:icons/full_screen_icon.png')");
                }
            }
        });

        mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                updateValues();
            }
        });

        mediaPlayer.setOnPlaying(new Runnable() {
            public void run() {
                if (stopRequested) {
                    mediaPlayer.pause();
                    stopRequested = false;
                } else {
                    play_button.setStyle("-fx-background-image: url('File:icons/pause_icon.png');");
                }
            }
        });

        mediaPlayer.setOnPaused(new Runnable() {
            public void run() {
                play_button.setStyle("-fx-background-image: url('File:icons/play_icon.png');");
            }
        });

        mediaPlayer.setOnReady(new Runnable() {
            public void run() {
                duration = mediaPlayer.getMedia().getDuration();
                updateValues();
            }
        });

        mediaPlayer.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            public void run() {
                if (!repeat) {
                    play_button.setStyle("-fx-background-image: url('File:icons/play_icon.png');");
                    //stopRequested = true;
                    setProperty();
                    atEndOfMedia = true;
                }
                else
                {
                    setProperty();
                    mediaPlayer.seek(mediaPlayer.getStartTime());
                    mediaPlayer.play();
                }
            }
        });

        time_slider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (time_slider.isPressed())
                    mediaPlayer.seek(mediaPlayer.getMedia().getDuration().multiply(time_slider.getValue() / 100.0));
                if (time_slider.isValueChanging())
                    mediaPlayer.seek(duration.multiply(time_slider.getValue() / 100.0));
            }
        });

        volume_slider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (volume_slider.isPressed())
                    mediaPlayer.setVolume(volume_slider.getValue() / 100.0);
                if (volume_slider.isValueChanging())
                    mediaPlayer.setVolume(volume_slider.getValue() / 100.0);
            }
        });
    }

    @FXML
    void Play_Button_OnClicked() {
        MediaPlayer.Status status = mediaPlayer.getStatus();
        System.out.println(status);
        if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED)
            return;

        if (status == MediaPlayer.Status.PAUSED || status == MediaPlayer.Status.READY || status == MediaPlayer.Status.STOPPED) {
            if (atEndOfMedia) {
                mediaPlayer.seek(mediaPlayer.getStartTime());
                atEndOfMedia = false;
            }
            mediaPlayer.play();
        }
        else
        {
            if (atEndOfMedia)
            {
                play_button.setStyle("-fx-background-image: url('File:icons/pause_icon.png');");
                mediaPlayer.seek(mediaPlayer.getStartTime());
                atEndOfMedia = false;
            }
            else
            {
                mediaPlayer.pause();
            }
        }
    }

    @FXML
    void FullScreen_Button_OnClicked() {
        if (VideoPlaylistController.getPrimaryStage().isFullScreen()) {
            full_screen_button.setStyle("-fx-background-image: url('File:icons/full_screen_icon.png')");
            VideoPlaylistController.getPrimaryStage().setFullScreen(false);
        } else {
            full_screen_button.setStyle("-fx-background-image: url('File:icons/minimize_screen_icon.png')");
            VideoPlaylistController.getPrimaryStage().setFullScreen(true);
        }
    }

    protected void updateValues () {
        if (playTime != null && time_slider != null && volume_slider != null) {
            Platform.runLater(new Runnable() {
                public void run() {
                    Duration currentTime = mediaPlayer.getCurrentTime();
                    playTime.setText(formatTime(currentTime, duration));
                    time_slider.setDisable(duration.isUnknown());
                    if (!time_slider.isDisabled() && duration.greaterThan(Duration.ZERO) && !time_slider.isValueChanging()) {
                        time_slider.setValue(currentTime.divide(duration).toMillis() * 100.0);
                    }
                    if (!volume_slider.isValueChanging()) {
                        volume_slider.setValue((int) Math.round(mediaPlayer.getVolume() * 100));
                    }
                }
            });
        }
    }

    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds, durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d", elapsedMinutes, elapsedSeconds, durationMinutes, durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes, elapsedSeconds);
            }
        }
    }

    @FXML
    void MediaView_OnClicked()
    {
        media_view.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2)
                {
                    if (VideoPlaylistController.getPrimaryStage().isFullScreen()) {
                        full_screen_button.setStyle("-fx-background-image: url('File:icons/full_screen_icon.png')");
                        VideoPlaylistController.getPrimaryStage().setFullScreen(false);
                    } else {
                        full_screen_button.setStyle("-fx-background-image: url('File:icons/minimize_screen_icon.png')");
                        VideoPlaylistController.getPrimaryStage().setFullScreen(true);
                    }
                }
                else if (event.getClickCount() == 1)
                {
                    Play_Button_OnClicked();
                }
            }
        });
    }

    @FXML
    void BackToPlaylistButtonOnClicked(ActionEvent event) throws IOException
    {
        mediaPlayer.stop();

        FXMLLoader loader = new FXMLLoader();
        Parent music = loader.load(getClass().getResource("video_playlist.fxml").openStream());
        VideoPlaylistController videoPlaylistController = loader.getController();
        videoPlaylistController.initialize(host);

        Scene video_scene = new Scene(music);
        video_scene.getStylesheets().setAll(getClass().getResource("css/style.css").toExternalForm());
        Stage video_stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        video_stage.setResizable(false);
        video_stage.setScene(video_scene);
        video_stage.show();
    }
}
