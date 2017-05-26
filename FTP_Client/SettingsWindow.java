package sample;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SettingsWindow {
    public static boolean display(String current_mode)
    {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Settings");
        window.setResizable(false);
        window.getIcons().add(new Image("file:icons/settings_icon.png"));
        window.setMinWidth(350);

        Label label = new Label("File Transfer Mode");
        label.setStyle("-fx-font-size: 16px");

        ToggleGroup group = new ToggleGroup();
        RadioButton b1 = new RadioButton("ASCII mode");
        RadioButton b2 = new RadioButton("Binary mode");
        b1.setToggleGroup(group);
        b2.setToggleGroup(group);
        if (current_mode.equals("ascii"))
            b1.setSelected(true);
        else if (current_mode.equals("binary"))
            b2.setSelected(true);

        Button button = new Button("OK");

        button.setOnAction(event -> {
            window.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, b1, b2, button);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        return b1.isSelected();
    }
}
