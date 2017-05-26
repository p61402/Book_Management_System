package sample;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class QuestionWindow {
    public static void display()
    {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("User Guide");
        window.setResizable(false);
        window.getIcons().add(new Image("file:icons/question_icon.png"));
        window.setMinWidth(350);

        String content = "1. Enter the server IP, your username, and password to connect.\n" +
                "2. Click the Disconnect button to disconnect.\n" +
                "3. Path area shows the current path\n" +
                "4. Double click local file or choose the file and press >> to upload the file.\n" +
                "5. Double click server file or choose the file and press << to download the file.\n" +
                "6. Double click the directory to change directory.\n" +
                "7. Bottom area shows the history status.";

        Text text = new Text(content);
        Button button = new Button("OK");

        button.setOnAction(event -> {
            window.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(text, button);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }
}
