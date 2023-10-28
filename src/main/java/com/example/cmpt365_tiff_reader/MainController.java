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
import java.awt.*;
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
    private Label leftImageLabel;
    @FXML
    private Label newImageLabel;
    @FXML
    private Button goToGrayScaleButton;
    @FXML
    private Button ditherButton;
    @FXML
    private Button autoLevelButton;
    @FXML
    private Button openFileButton;
    @FXML
    private ImageView originalImage;
    @FXML
    private ImageView newImage;
    private File file;
    private BufferedImage grayscaleBuffImage;
    private BufferedImage originalBuffImage;
    @FXML
    public void openFileClicked() throws IOException {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(".tif files", "*.tif"));
        File file = fc.showOpenDialog(null);
        exitButton.setVisible(true);

        if (file != null) {
            System.out.println("File selected! --> " + file.getPath());
            BufferedImage buffImage = ImageIO.read(file);
            originalBuffImage = buffImage;
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
        root = FXMLLoader.load(getClass().getResource("processingScreen.fxml"));
        stage = (Stage) goToGrayScaleButton.getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("Processing Results");
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
        newImage.setImage(new WritableImage(makeViewableImg(buffImg)));
        grayscaleBuffImage = buffImg;
        brightnessButton.setVisible(true);
        openFileButton.setVisible(false);
    }

    @FXML
    public void grayscaleFileButton() throws IOException {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(".tif files", "*.tif"));
        File file = fc.showOpenDialog(null);

        if (file != null) {
            BufferedImage buffImage = ImageIO.read(file);
            originalBuffImage = buffImage;
            originalImage.setImage(new WritableImage(makeViewableImg(buffImage)));
            this.file = file;
            makeGreyscale(file);
        }
    }
    @FXML
    public void brightnessButtonClicked() throws IOException {
        newImageLabel.setText("Brightness reduced by 50%");
        leftImageLabel.setText("Brightness reduced by 50%");
        BufferedImage bufImage = ImageIO.read(file);
        BufferedImage grayBufImage = grayscaleBuffImage;
        int width = bufImage.getWidth();
        int height = bufImage.getHeight();

        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                int pixel1 = bufImage.getRGB(x,y);
                int pixel2 = grayBufImage.getRGB(x,y);
                int aVal = (pixel1 >> 24) & 0xff;
                int rVal = (pixel1 >> 16) & 0xff;
                int gVal = (pixel1 >> 8) & 0xff;
                int bVal = pixel1 & 0xff;

                int aVal2 = (pixel2 >> 24) & 0xff;
                int rVal2 = (pixel2 >> 16) & 0xff;
                int gVal2 = (pixel2 >> 8) & 0xff;
                int bVal2 = pixel2 & 0xff;

                aVal *= 0.5;
                rVal *= 0.5;
                gVal *= 0.5;
                bVal *= 0.5;

                aVal2 *= 0.5;
                rVal2 *= 0.5;
                gVal2 *= 0.5;
                bVal2 *= 0.5;

                pixel1 = (aVal << 24) | (rVal << 16) | (gVal << 8) | bVal;
                pixel2 = (aVal2 << 24) | (rVal2 << 16) | (gVal2 << 8) | bVal2;
                bufImage.setRGB(x,y,pixel1);
                grayBufImage.setRGB(x,y,pixel2);
            }
        }
        originalImage.setImage(new WritableImage(makeViewableImg(bufImage)));
        newImage.setImage(new WritableImage(makeViewableImg(grayBufImage)));
        brightnessButton.setVisible(false);
        ditherButton.setVisible(true);
    }
    @FXML
    public void applyDithering() {
        // WHY IS THE ORIGINAL IMAGE HERE A GRAYSCALE WITH 50% LOWER BRIGHTNESS
        originalImage.setImage(new WritableImage(makeViewableImg(grayscaleBuffImage)));
        leftImageLabel.setText("Original grayscale image");
        newImageLabel.setText("Image after Ordered Dithering");
        BufferedImage ditheredBuffImg = grayscaleBuffImage;
        int height = ditheredBuffImg.getHeight();
        int width = ditheredBuffImg.getWidth();

        int[][] ditherMatrix = {{2,11,4,13},
                                {15,6,16,8},
                                {5,14,3,12},
                                {16,9,16,7}};
//        int[][] ditherMatrix = {{0,8,2,10,12,4,14,6},
//                                {12,4,14,6,3,11,1,9},
//                                {3,11,1,9,15,7,13,5},
//                                {15,7,13,5,0,8,2,10},
//                                {0,8,2,10,12,4,14,6},
//                                {12,4,14,6,3,11,1,9},
//                                {3,11,1,9,15,7,13,5},
//                                {15,7,13,5,0,8,2,10}};
        int aVal, rVal, gVal, bVal = 0;
        // will be using 4x4 dithering matrix, so divide each pixel value by 16+1
        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                int color = new Color(ditheredBuffImg.getRGB(x,y)).getBlue();
                if (x == 0 && y ==0) {
                    System.out.println("Color: " + color);
                }
                int i = x % 4;
                int j = y % 4;
                int intensity = color / 17;
                if (intensity < ditherMatrix[i][j]) {
                    aVal = 0;
                    rVal = 0;
                    gVal = 0;
                    bVal = 0;
                } else {
                    aVal = 255;
                    rVal = 255;
                    gVal = 255;
                    bVal = 255;
                }
                int pixel = (aVal << 24) | (rVal << 16) | (gVal << 8) | bVal;
                ditheredBuffImg.setRGB(x,y,pixel);
            }
        }
        newImage.setImage(new WritableImage(makeViewableImg(ditheredBuffImg)));
        ditherButton.setVisible(false);
        autoLevelButton.setVisible(true);
    }
    @FXML
    public void autoLevelClicked() {
        originalImage.setImage(new WritableImage(makeViewableImg(originalBuffImage)));
        leftImageLabel.setText("Original Image");
        newImageLabel.setText("After applying auto level");
        // do auto level to image, then set new image to that
    }
}