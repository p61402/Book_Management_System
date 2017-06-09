package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class VideoPlaylistController {

    private static Stage primaryStage;

    private void setPrimaryStage(Stage stage)
    {
        VideoPlaylistController.primaryStage = stage;
    }

    static public Stage getPrimaryStage() {
        return VideoPlaylistController.primaryStage;
    }


    private String host;

    @FXML
    private ListView<String> playlist;

    public void initialize(String host)
    {
        this.host = host;
        updatePlayList();
        play_video();
    }

    @FXML
    void BackToMenuButtonOnClicked(ActionEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        Parent menu = loader.load(getClass().getResource("menu.fxml").openStream());
        MenuController menuController = loader.getController();
        menuController.initialize(host);

        Scene menu_scene = new Scene(menu);
        menu_scene.getStylesheets().setAll(getClass().getResource("css/menu.css").toExternalForm());
        Stage menu_stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        menu_stage.setScene(menu_scene);
        menu_stage.show();
    }

    private void updatePlayList()
    {
        MediaClient mediaClient = new MediaClient();
        if (mediaClient.Connect(host, "VIDEO").equals("SUCCESS")) {
            ObservableList<String> list = FXCollections.observableArrayList(mediaClient.getList());
            playlist.setItems(list);

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
                        imageView.setImage(new Image("file:icons/mp4_icon.png"));
                        setText(name);
                        setGraphic(imageView);
                    }
                }
            });
        }
    }

    private void play_video()
    {
        playlist.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2 && !playlist.getSelectionModel().isEmpty())
                {
                    String name = playlist.getSelectionModel().getSelectedItem();
                    MediaClient mediaClient = new MediaClient();
                    File file = new File("VIDEO/" + name);
                    if (!file.exists())
                        mediaClient.Connect(host, "VIDEO " + name);

                    FXMLLoader loader = new FXMLLoader();
                    Parent video_player = null;
                    try {
                        video_player = loader.load(getClass().getResource("sample.fxml").openStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Controller controller = loader.getController();
                    controller.initialize(host, name);

                    Scene video_scene = new Scene(video_player);
                    Stage video_stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
                    setPrimaryStage(video_stage);
                    video_scene.getStylesheets().setAll(getClass().getResource("css/style.css").toExternalForm());
                    video_stage.setResizable(true);
                    video_stage.setScene(video_scene);
                    video_stage.show();
                }
            }
        });
    }
}
