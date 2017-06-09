package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;


public class MainController {
    @FXML
    private TextField host_text;
    @FXML
    private Button connect_button;

    @FXML
    void MakeConnection(ActionEvent event) throws IOException
    {
        MediaClient mediaClient = new MediaClient();
        if (mediaClient.Connect(host_text.getText(), "TEST").equals("Connection Successful"))
        {
            System.out.println("Connection Successful");

            FXMLLoader loader = new FXMLLoader();
            Parent menu = loader.load(getClass().getResource("menu.fxml").openStream());
            MenuController menuController = loader.getController();
            menuController.initialize(host_text.getText());

            Scene menu_scene = new Scene(menu);
            menu_scene.getStylesheets().setAll(getClass().getResource("css/menu.css").toExternalForm());
            Stage menu_stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            menu_stage.setResizable(false);
            menu_stage.setScene(menu_scene);
            menu_stage.show();
        }
        else
        {
            AlertWindow.display("Connection Failed");
        }
    }
}
