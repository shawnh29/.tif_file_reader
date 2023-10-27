package com.example.cmpt365_tiff_reader;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    private Parent root;
    private Stage stage;
    private Scene scene;
    @FXML
    private ImageView image;
    @FXML
    private Button exitButton;
    @FXML
    private Button brightnessButton;
    @FXML
    private Label width;
    @FXML
    private Label height;
    @FXML
    private Label newImageLabel;
    @FXML
    private Button goToGrayScaleButton;
    @FXML
    private ImageView originalImage;
    @FXML
    private ImageView grayscaleImage;
    private File file;
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
            goToGrayScaleButton.setVisible(true);
        }
    }
    @FXML
    public void exitButtonClicked() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void grayScaleButtonClicked() throws IOException {
        root = FXMLLoader.load(getClass().getResource("grayScaleScreen.fxml"));
        stage = (Stage) goToGrayScaleButton.getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("Grayscale");
        stage.setScene(scene);
        stage.show();
    }
    public PixelBuffer makeViewableImg (BufferedImage buffImg) {
        BufferedImage newImg = new BufferedImage(buffImg.getWidth(), buffImg.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
        newImg.createGraphics().drawImage(buffImg, 0, 0, buffImg.getWidth(), buffImg.getHeight(), null);

        int[] argb_arr = ((DataBufferInt) newImg.getRaster().getDataBuffer()).getData();
        IntBuffer buff = IntBuffer.wrap(argb_arr);

        PixelFormat<IntBuffer> pixelFormat = PixelFormat.getIntArgbPreInstance();
        PixelBuffer<IntBuffer> pixelBuf = new PixelBuffer<>(newImg.getWidth(), newImg.getHeight(), buff, pixelFormat);
        return pixelBuf;
    }
    public void makeGreyscale(File file) throws IOException {
        BufferedImage buffImg = ImageIO.read(file);
        int width = buffImg.getWidth();
        int height = buffImg.getHeight();

        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                int pixel = buffImg.getRGB(x,y);

                int aVal = (pixel >> 24) & 0xff;
                int rVal = (pixel >> 16) & 0xff;
                int gVal = (pixel >> 8) & 0xff;
                int bVal = pixel & 0xff;

                int averageVal = (rVal + gVal + bVal) / 3;

                pixel = (aVal << 24) | (averageVal << 16) | (averageVal << 8) | averageVal;
                buffImg.setRGB(x,y,pixel);
            }
        }
        grayscaleImage.setImage(new WritableImage(makeViewableImg(buffImg)));
        brightnessButton.setVisible(true);
    }

//    public void lowerBrightness () {
//        BufferedImage buffImage = ImageIO.read()
//    }
    @FXML
    public void grayscaleFileButton() throws IOException {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(".tif files", "*.tif"));
        File file = fc.showOpenDialog(null);

        if (file != null) {
            BufferedImage buffImage = ImageIO.read(file);

            originalImage.setImage(new WritableImage(makeViewableImg(buffImage)));
            this.file = file;
            makeGreyscale(file);

        }
    }
    @FXML
    public void brightnessButtonClicked() throws IOException {
        newImageLabel.setText("Reduced brightness by 50%");
        BufferedImage bufImage = ImageIO.read(file);
        int width = bufImage.getWidth();
        int height = bufImage.getHeight();

        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                int pixel = bufImage.getRGB(x,y);

                int aVal = (pixel >> 24) & 0xff;
                int rVal = (pixel >> 16) & 0xff;
                int gVal = (pixel >> 8) & 0xff;
                int bVal = pixel & 0xff;

                aVal *= 0.5;
                rVal *= 0.5;
                gVal *= 0.5;
                bVal *= 0.5;

                pixel = (aVal << 24) | (rVal << 16) | (gVal << 8) | bVal;
                bufImage.setRGB(x,y,pixel);
            }
        }
    }
}