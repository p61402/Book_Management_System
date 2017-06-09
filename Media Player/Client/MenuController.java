package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MenuController {

    private String host;

    public void initialize(String host)
    {
        this.host = host;
        create_folder_ifNotExists();
    }

    private void create_folder_ifNotExists()
    {
        File music_folder = new File("MUSIC");
        File video_folder = new File("VIDEO");
        if (!music_folder.exists() || !music_folder.isDirectory())
            music_folder.mkdir();
        if (!video_folder.exists() || !video_folder.isDirectory())
            video_folder.mkdir();
    }

    @FXML
    void Connect_Another_Server(ActionEvent event) throws IOException
    {
        Parent main = FXMLLoader.load(getClass().getResource("main.fxml"));
        Scene main_scene = new Scene(main);
        Stage main_stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        main_scene.getStylesheets().setAll(getClass().getResource("css/main.css").toExternalForm());
        main_stage.setResizable(false);
        main_stage.setScene(main_scene);
        main_stage.show();
    }

    @FXML
    void MusicButtonOnClicked(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        Parent music = loader.load(getClass().getResource("music_playlist.fxml").openStream());
        MusicPlaylistController musicPlaylistController = loader.getController();
        musicPlaylistController.initialize(host);

        Scene music_scene = new Scene(music);
        music_scene.getStylesheets().setAll(getClass().getResource("css/music_playlist.css").toExternalForm());
        Stage music_stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        music_stage.setResizable(false);
        music_stage.setScene(music_scene);
        music_stage.show();
    }

    @FXML
    void VideoButtonOnClicked(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        Parent video = loader.load(getClass().getResource("video_playlist.fxml").openStream());
        VideoPlaylistController videoPlaylistController = loader.getController();
        videoPlaylistController.initialize(host);

        Scene video_scene = new Scene(video);
        video_scene.getStylesheets().setAll(getClass().getResource("css/style.css").toExternalForm());
        Stage video_stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        video_stage.setResizable(false);
        video_stage.setScene(video_scene);
        video_stage.show();
    }
}
