package sample;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MessageWindow {

    public static void display(String message)
    {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Message");
        window.setResizable(false);
        String[] icons = {"pikachu.png", "bullbasaur.png", "charmander.png", "squirtle.png"};
        int random_number = (int) (Math.random() * (icons.length - 1 + 1) + 0);
        window.getIcons().add(new Image("file:icons/" + icons[random_number]));
        window.setMinWidth(350);

        Label label = new Label();
        label.setText(message);
        Button button = new Button("OK");

        button.setOnAction(event -> {
            window.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, button);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }
}
