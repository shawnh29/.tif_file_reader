package com.example.cmpt365_tiff_reader;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

public class MainController {

    @FXML
    private ImageView image;
    @FXML
    public void openFileClicked() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(".tif files", "*.tif"));
        File file = fc.showOpenDialog(null);

        if (file != null) {
            System.out.println("File selected! --> " + file.getPath());
        }
    }
}