package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.List;

public class Controller {
    @FXML
    private TextField ip;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button Connect_Button;
    @FXML
    private Button Disconnect_Button;
    @FXML
    private ListView<String> LocalList;
    @FXML
    private ListView<String> ServerList;
    @FXML
    private TextField local_path;
    @FXML
    private TextField server_path;
    @FXML
    private Button local_MKD;
    @FXML
    private Button local_DELE;
    @FXML
    private Button MKD_Button;
    @FXML
    private Button DELE_Button;
    @FXML
    private Button upload_button;
    @FXML
    private Button download_button;
    @FXML
    private Button settings_button;
    @FXML
    private ListView<String> StatusList;

    private ObservableList<String> local_files = FXCollections.observableArrayList();
    private ObservableList<String> server_files = FXCollections.observableArrayList();
    private ObservableList<String> status_list = FXCollections.observableArrayList();
    private FTP_backend f = null;
    private boolean is_connected = false;
    private boolean first;

    @FXML
    public void initialize()
    {
        Disconnect_Button.setDisable(true);
        LocalList.setDisable(true);
        ServerList.setDisable(true);
        local_MKD.setDisable(true);
        local_DELE.setDisable(true);
        MKD_Button.setDisable(true);
        DELE_Button.setDisable(true);
        upload_button.setDisable(true);
        download_button.setDisable(true);
        settings_button.setDisable(true);
        local_path.setEditable(false);
        server_path.setEditable(false);
    }

    @FXML
    void Connect_Button_OnClick()
    {
        if (ip.getText().isEmpty())
            return;

        is_connected = true;
        try
        {
            f = new FTP_backend();
            f.openConnection(ip.getText());
            f.doLogin(username.getText(), password.getText());
            first = true;
            MsgListen msgListen = new MsgListen();
            Thread msgthread = new Thread(msgListen);
            msgthread.start();
            f.getMsgs();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        LocalList.setDisable(false);
        ServerList.setDisable(false);
        Connect_Button.setDisable(true);
        Disconnect_Button.setDisable(false);
        local_MKD.setDisable(false);
        local_DELE.setDisable(false);
        MKD_Button.setDisable(false);
        DELE_Button.setDisable(false);
        upload_button.setDisable(false);
        download_button.setDisable(false);
        settings_button.setDisable(false);
    }

    @FXML
    void Disconnect_Button_OnClick()
    {
        is_connected = false;
        try {
            f.closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        f.doQuit();
        local_files.clear();
        server_files.clear();
        LocalList.setDisable(true);
        ServerList.setDisable(true);
        Connect_Button.setDisable(false);
        Disconnect_Button.setDisable(true);
        local_MKD.setDisable(true);
        local_DELE.setDisable(true);
        MKD_Button.setDisable(true);
        DELE_Button.setDisable(true);
        upload_button.setDisable(true);
        download_button.setDisable(true);
        settings_button.setDisable(true);

        MessageWindow.display("Disconnected");
    }

    @FXML
    void local_MKD_Button_OnClicked()
    {
        String folderName;
        if (MakeNewFolderWindow.display()) {
            folderName = MakeNewFolderWindow.getText();
            f.local_make_directory(folderName);
            local_files.clear();
            local_files = f.doLocalLs();
            update_local_list();
        }
    }

    @FXML
    void local_DELE_Button_OnClicked()
    {
        if (!LocalList.getSelectionModel().isEmpty())
        {
            int index = LocalList.getSelectionModel().getSelectedIndex();

            if (ConfirmDeleteWindow.display())
            {
                f.local_delete_file_or_directory(local_files.get(index));
                local_files.clear();
                local_files = f.doLocalLs();
                update_local_list();
            }
        }
    }

    @FXML
    void MKD_Button_OnClicked()
    {
        String folderName;
        if (MakeNewFolderWindow.display()) {
            folderName = MakeNewFolderWindow.getText();
            f.makeDirectory(folderName);
            server_files.clear();
            server_files = f.doLs();
            update_server_list();
        }
    }

    @FXML
    void DELE_Button_OnClicked()
    {
        if (!ServerList.getSelectionModel().isEmpty())
        {
            int index = ServerList.getSelectionModel().getSelectedIndex();

            if (f.get_server_file_status(index).equals("d"))
            {
                if (ConfirmDeleteWindow.display())
                {
                    f.removeDirectory(server_files.get(index));
                    server_files.clear();
                    server_files = f.doLs();
                    update_server_list();
                }
            }
            else
            {
                if (ConfirmDeleteWindow.display())
                {
                    f.doDeleteFile(server_files.get(index));
                    server_files.clear();
                    server_files = f.doLs();
                    update_server_list();
                }
            }
        }
    }

    @FXML
    void Upload_Button_OnClicked()
    {
        upload_button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!LocalList.getSelectionModel().isEmpty())
                {
                    int index = LocalList.getSelectionModel().getSelectedIndex();
                    if (f.get_local_file_status(index).equals("f"))
                    {
                        f.local_check_file_type(local_files.get(index));
                        local_files = f.doLocalLs();
                        local_path.setText(f.getLocal_path());
                        update_local_list();
                        server_files = f.doLs();
                        update_server_list();
                    }
                }
            }
        });
    }

    @FXML
    void Download_Button_OnClicked()
    {
        download_button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!ServerList.getSelectionModel().isEmpty())
                {
                    int index = ServerList.getSelectionModel().getSelectedIndex();
                    if (f.get_server_file_status(index).equals("f"))
                    {
                        f.server_check_file_type(index, server_files.get(index));
                        server_files = f.doLs();
                        update_server_list();
                        local_files = f.doLocalLs();
                        update_local_list();
                    }
                }
            }
        });
    }

    @FXML
    void Settings_Button_OnClicked()
    {
        if (SettingsWindow.display(f.getFile_transfer_mode()))
        {
            f.doAscii();
        }
        else
        {
            f.doBinary();
        }
    }

    @FXML
    void Question_Button_OnClicked()
    {
        QuestionWindow.display();
    }

    private void update_status_list()
    {
        status_list = f.getStatus_list();
        StatusList.setItems(status_list);
    }

    class MsgListen implements Runnable
    {
        public void run()
        {
            while (is_connected)
            {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (is_connected) {
                            server_path.setText(f.getServer_path());
                            update_status_list();
                        }

                        if (!f.IsConnected())
                        {
                            Disconnect_Button_OnClick();
                        }

                        if (f.IsConnected() & first)
                        {
                            local_files = f.doLocalLs();
                            local_path.setText(f.getLocal_path());
                            update_local_list();
                            local_Double_Clicked();
                            server_Double_Clicked();
                            server_files = f.doLs();
                            f.PWD();
                            update_server_list();
                            first = false;
                        }
                        else if (!f.IsConnected())
                        {
                            AlertWindow.display("Connection failed");
                        }
                    }
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void update_local_list()
    {
        LocalList.setItems(local_files);
        List<String> local_file_status = f.getLocal_file_status();

        LocalList.setCellFactory(param -> new ListCell<String>() {
            private ImageView imageView = new ImageView();

            @Override
            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    int index = local_files.indexOf(name);
                    if (local_file_status.get(index).equals("d")) {
                        imageView.setImage(new Image("file:icons/folder_icon.png", 25, 25, false, false));
                    } else {
                        imageView.setImage(new Image("file:icons/file_icon.png", 25, 25, false, false));
                    }
                    setText(name);
                    setGraphic(imageView);
                }
            }
        });
    }

    private void update_server_list()
    {
        ServerList.setItems(server_files);
        List<String> server_file_status = f.getServer_file_status();

        ServerList.setCellFactory(param -> new ListCell<String>() {
            private ImageView imageView = new ImageView();

            @Override
            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    int index = server_files.indexOf(name);
                    if (server_file_status.get(index).equals("d"))
                    {
                        imageView.setImage(new Image("file:icons/folder_icon.png", 25, 25, false, false));
                    }
                    else
                    {
                        imageView.setImage(new Image("file:icons/file_icon.png", 25, 25, false, false));
                    }

                    setText(name);
                    setGraphic(imageView);
                }
            }
        });
    }

    private void local_Double_Clicked()
    {
        LocalList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    if (!LocalList.getSelectionModel().isEmpty())
                    {
                        int index = LocalList.getSelectionModel().getSelectedIndex();
                        f.local_check_file_type(local_files.get(index));
                        local_files = f.doLocalLs();
                        local_path.setText(f.getLocal_path());
                        update_local_list();
                        server_files = f.doLs();
                        update_server_list();
                    }
                }
            }
        });
    }

    private void server_Double_Clicked()
    {
        ServerList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    if (!ServerList.getSelectionModel().isEmpty())
                    {
                        int index = ServerList.getSelectionModel().getSelectedIndex();
                        f.server_check_file_type(index, server_files.get(index));
                        server_files = f.doLs();
                        update_server_list();
                        local_files = f.doLocalLs();
                        update_local_list();
                    }
                }
            }
        });
    }
}
