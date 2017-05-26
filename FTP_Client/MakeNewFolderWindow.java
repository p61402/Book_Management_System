package sample;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MakeNewFolderWindow {

    static boolean response;
    static String text;

    public static boolean display()
    {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Make new folder");
        window.setResizable(false);
        window.getIcons().add(new Image("file:icons/folder_icon.png"));
        window.setMinWidth(350);

        Label label = new Label();
        label.setText("Enter the folder name");
        TextField textField = new TextField();
        Button true_button = new Button("OK");
        Button false_button = new Button("Cancel");

        true_button.disableProperty().bind(textField.textProperty().isEmpty());

        true_button.setOnAction(event -> {
            response = true;
            text = textField.getText();
            window.close();
        });

        false_button.setOnAction(event -> {
            response = false;
            window.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, textField, true_button, false_button);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        return response;
    }

    public static String getText()
    {
        return text;
    }
}
