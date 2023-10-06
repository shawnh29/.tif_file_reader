package com.example.cmpt365_tiff_reader;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.IntBuffer;

public class MainController {

    @FXML
    private ImageView image;
    @FXML
    private Button exitButton;
    @FXML
    private Label width;
    @FXML
    private Label height;
    @FXML
    public void openFileClicked() throws IOException {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(".tif files", "*.tif"));
        File file = fc.showOpenDialog(null);
        exitButton.setVisible(true);

        if (file != null) {
            System.out.println("File selected! --> " + file.getPath());
            BufferedImage buffImage = ImageIO.read(file);
            BufferedImage newImg = new BufferedImage(buffImage.getWidth(), buffImage.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
            newImg.createGraphics().drawImage(buffImage, 0,0, buffImage.getWidth(), buffImage.getHeight(), null);

            int[] argb_arr = ((DataBufferInt) newImg.getRaster().getDataBuffer()).getData();
            IntBuffer buff = IntBuffer.wrap(argb_arr);

            PixelFormat<IntBuffer> pixelFormat = PixelFormat.getIntArgbPreInstance();
            PixelBuffer<IntBuffer> pixelBuf = new PixelBuffer<>(newImg.getWidth(), newImg.getHeight(), buff, pixelFormat);

            image.setImage(new WritableImage(pixelBuf));
            width.setText("Width: " + buffImage.getWidth());
            height.setText("Height: " + buffImage.getHeight());
            width.setVisible(true);
            height.setVisible(true);
        }
    }
    @FXML
    public void exitButtonClicked() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}