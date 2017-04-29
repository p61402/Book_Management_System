package sample;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;


public class Controller{

    @FXML
    private ToggleButton website_toggle;
    @FXML
    private ToggleButton picture_toggle;
    @FXML
    private TextField text_field;
    @FXML
    private Button search_button;
    @FXML
    private ListView<String> list = new ListView<>();
    @FXML
    private ProgressBar progress_bar;
    @FXML
    private ProgressIndicator progress_indicator;
    @FXML
    private Pagination paging;

    private ObservableList<String> items = FXCollections.observableArrayList ();
    private List<String> titles;
    private List<String> urls;
    private int max_page;
    private SimpleDoubleProperty prop = new SimpleDoubleProperty(0);
    private String text;

    @FXML
    public void initialize() {
        progress_bar.progressProperty().bind(prop);
        progress_indicator.progressProperty().bind(prop);
        website_toggle.setSelected(true);
        paging.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> update_list(newIndex.intValue()));
        goto_url_event();
    }

    @FXML
    void search_button_onClick(ActionEvent event) {
        text = text_field.getText();
        if (text.isEmpty())
            return;

        disable_all_buttons();

        if (website_toggle.isSelected()) {
            boolean not_store_the_keyword = already_have_theKeyword(text);
            if (not_store_the_keyword)
                return;

            Spider spider = new Spider("https://www.google.com.tw/search?q=" + text, text, 1);

            spider.start();
            progress_running(spider);

        } else {
            Spider spider = new Spider("https://www.google.com/search?tbm=isch&q=" + text, text, 2);

            spider.start();
            progress_running(spider);
        }
    }

    @FXML
    void website_toggle_onClick(ActionEvent event) {
        website_toggle.setSelected(true);
        picture_toggle.setSelected(false);
    }

    @FXML
    void picture_toggle_onClick(ActionEvent event) {
        picture_toggle.setSelected(true);
        website_toggle.setSelected(false);
    }

    @FXML
    void disable_all_buttons() {
        website_toggle.setDisable(true);
        picture_toggle.setDisable(true);
        search_button.setDisable(true);
        paging.setDisable(true);
    }

    @FXML
    void enable_all_buttons() {
        website_toggle.setDisable(false);
        picture_toggle.setDisable(false);
        search_button.setDisable(false);
        paging.setDisable(false);
    }

    private boolean already_have_theKeyword(String keyword) {
        Database db = new Database();
        if (db.exist_keyword(keyword)) {
            db.access_keyword(keyword);
            titles = db.get_titles();
            urls = db.get_links();

            new Thread(){
                double progress = 0;
                @Override
                public void run(){
                    while (progress < 1) {
                        progress += 0.1;
                        prop.set(progress);
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    Platform.runLater(new Runnable() {
                        @Override public void run() {
                            paging.setCurrentPageIndex(0);
                            max_page = titles.size() / 10;
                            update_list(0);
                            enable_all_buttons();
                        }
                    });
                }
            }.start();

            return true;
        } else {
            return false;
        }
    }

    private void store_keyword(String keyword) {
        Database db = new Database();
        db.store_keyword(keyword, titles, urls);
    }

    private void goto_url_event() {
        list.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    if (!list.getSelectionModel().isEmpty())
                    {
                        int index = list.getSelectionModel().getSelectedIndex();
                        try {
                            Desktop.getDesktop().browse(new URI(urls.get(index)));
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void update_list(int new_index) {
        items.clear();
        int first_item = new_index * 10;
        int last_item = (new_index + 1) * 10;

        if (new_index > max_page)
            return;
        else if (new_index == max_page) {
            last_item = titles.size();
        }
        for (int i = first_item; i < last_item; i++) {
            items.add(titles.get(i));
        }

        list.setItems(items);
    }

    private void progress_running(Spider spider) {
        new Thread(){
            @Override
            public void run(){
                double progress = 0;
                if (spider.getMode() == 1) {
                    while (progress < 1) {
                        progress = spider.get_progress();
                        prop.set(progress);
                    }
                } else if (spider.getMode() == 2) {
                    while (progress < 1) {
                        progress += 0.1;
                        prop.set(progress);
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                Platform.runLater(new Runnable() {
                    @Override public void run() {
                        paging.setCurrentPageIndex(0);
                        titles = spider.getTitlesStored();
                        urls = spider.getPagesStored();
                        max_page = titles.size() / 10;
                        store_keyword(text);
                        update_list(0);

                        enable_all_buttons();
                    }
                });
            }
        }.start();
    }
}

