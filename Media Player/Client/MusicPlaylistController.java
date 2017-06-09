package sample;

import com.sun.javafx.tk.Toolkit;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class MusicPlaylistController {

    @FXML
    private Text music_title;
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
    private Button loop_button;

    private String host;
    private Media media;
    private MediaPlayer mediaPlayer;
    private boolean atEndOfMedia = false;
    private Duration duration;
    private String play_type = "S";
    private boolean stopRequested = false;
    private int number_of_songs;
    private int current_song_index;
    private Timeline timeline;

    @FXML
    private ListView<String> playlist;

    public void initialize(String host)
    {
        this.host = host;
        updatePlayList();
        playlistDoubleClicked();

        mediaPlayer = new MediaPlayer(media);
        media_view.setVisible(false);
        media_view.setMediaPlayer(mediaPlayer);
        mediaPlayer.setAutoPlay(true);

        setProperty();
    }

    private void setProperty()
    {
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

        mediaPlayer.setCycleCount(play_type.equals("L") ? MediaPlayer.INDEFINITE : 1);
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            public void run() {
                switch (play_type) {
                    case "S":
                        //play_button.setStyle("-fx-background-image: url('File:icons/play_icon.png');");
                        //stopRequested = true;
                        //atEndOfMedia = true;
                        nextSong();
                        break;
                    case "L":
                        mediaPlayer.seek(mediaPlayer.getStartTime());
                        mediaPlayer.play();
                        break;
                    default:
                        randomSong();
                        break;
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
    void BackToMenuButtonOnClicked(ActionEvent event) throws IOException
    {
        mediaPlayer.stop();

        FXMLLoader loader = new FXMLLoader();
        Parent menu = loader.load(getClass().getResource("menu.fxml").openStream());
        MenuController menuController = loader.getController();
        menuController.initialize(host);

        Scene menu_scene = new Scene(menu);
        menu_scene.getStylesheets().setAll(getClass().getResource("css/menu.css").toExternalForm());
        Stage menu_stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        menu_stage.setResizable(false);
        menu_stage.setScene(menu_scene);
        menu_stage.show();
    }

    @FXML
    void Next_Button_OnClicked()
    {
        nextSong();
    }

    @FXML
    void Loop_Button_OnClicked()
    {
        switch (play_type) {
            case "S":
                play_type = "L";
                loop_button.setStyle("-fx-background-image: url('File:icons/loop_icon.png')");
                break;
            case "L":
                play_type = "R";
                loop_button.setStyle("-fx-background-image: url('File:icons/random_icon.png')");
                break;
            default:
                play_type = "S";
                loop_button.setStyle("-fx-background-image: url('File:icons/sequential_icon.png')");
                break;
        }
    }

    private void updatePlayList()
    {
        MediaClient mediaClient = new MediaClient();
        if (mediaClient.Connect(host, "MUSIC").equals("SUCCESS")) {
            ObservableList<String> list = FXCollections.observableArrayList(mediaClient.getList());
            playlist.setItems(list);
            number_of_songs = list.size();

            playlist.setCellFactory(param -> new ListCell<String>() {
                private ImageView imageView = new ImageView();

                @Override
                public void updateItem(String name, boolean empty) {
                    super.updateItem(name, empty);
                    if (empty)
                    {
                        setText(null);
                        setGraphic(null);
                    }
                    else
                    {
                        imageView.setImage(new Image("file:icons/mp3_icon.png"));
                        setText(name);
                        setGraphic(imageView);
                    }
                }
            });

            File file = new File("MUSIC/" + list.get(0));
            if (!file.exists())
                mediaClient.Connect(host, "MUSIC " + list.get(0));
            media = new Media(Paths.get("MUSIC/" + list.get(0)).toUri().toString());
            music_title.setText(list.get(0));
            current_song_index = 0;
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

    private void playlistDoubleClicked()
    {
        playlist.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2 && !playlist.getSelectionModel().isEmpty())
                {
                    String name = playlist.getSelectionModel().getSelectedItem();
                    MediaClient mediaClient = new MediaClient();
                    File file = new File("MUSIC/" + name);
                    if (!file.exists())
                        mediaClient.Connect(host, "MUSIC " + name);
                    mediaPlayer.stop();
                    media = new Media(Paths.get("MUSIC/" + name).toUri().toString());
                    mediaPlayer = new MediaPlayer(media);
                    setProperty();
                    media_view.setMediaPlayer(mediaPlayer);
                    mediaPlayer.play();
                    music_title.setText(name);
                    current_song_index = playlist.getSelectionModel().getSelectedIndex();
                }
            }
        });
    }

    private void nextSong()
    {
        current_song_index++;
        if (current_song_index >= number_of_songs)
            current_song_index = 0;

        MediaClient mediaClient = new MediaClient();
        String name = playlist.getItems().get(current_song_index);
        File file = new File("MUSIC/" + name);
        if (!file.exists())
            mediaClient.Connect(host, "MUSIC " + name);
        mediaPlayer.stop();
        media = new Media(Paths.get("MUSIC/" + name).toUri().toString());
        mediaPlayer = new MediaPlayer(media);
        setProperty();
        media_view.setMediaPlayer(mediaPlayer);
        mediaPlayer.play();
        music_title.setText(name);
    }

    private void randomSong()
    {
        int random_number = (int) (Math.random() * (number_of_songs - 1 + 1) + 0);
        MediaClient mediaClient = new MediaClient();
        String name = playlist.getItems().get(random_number);
        File file = new File("MUSIC/" + name);
        if (!file.exists())
            mediaClient.Connect(host, "MUSIC " + name);
        mediaPlayer.stop();
        media = new Media(Paths.get("MUSIC/" + name).toUri().toString());
        mediaPlayer = new MediaPlayer(media);
        setProperty();
        media_view.setMediaPlayer(mediaPlayer);
        mediaPlayer.play();
        music_title.setText(name);
    }

    private void set_title_animation(int speed)
    {
        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setAutoReverse(false);

        double textWidth = getTextWidth(music_title);
        KeyValue value = new KeyValue(music_title.layoutXProperty(), -textWidth);
        KeyFrame frame = new KeyFrame(Duration.millis(speed), value);
        timeline.getKeyFrames().add(frame);
        timeline.play();
    }


    private double getTextWidth(Text text) {
        return Toolkit.getToolkit().getFontLoader().computeStringWidth(text.getText(), text.getFont());
    }
}
