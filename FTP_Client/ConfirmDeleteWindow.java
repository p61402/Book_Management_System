package sample;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmDeleteWindow {

    static boolean response;

    public static boolean display()
    {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Delete");
        window.setResizable(false);
        window.getIcons().add(new Image("file:icons/delete_icon.png"));
        window.setMinWidth(300);

        Label label = new Label();
        label.setText("Are you sure you want to delete this item?");
        Button true_button = new Button("Delete");
        true_button.setStyle("-fx-background-color: red; -fx-text-fill: white");
        Button false_button = new Button("Cancel");

        true_button.setOnAction(event -> {
            response = true;
            window.close();
        });

        false_button.setOnAction(event -> {
            response = false;
            window.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, true_button, false_button);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        return response;
    }
}
