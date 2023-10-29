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
    private BufferedImage originalGrayscaleBuffImage;
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
        originalGrayscaleBuffImage = buffImg;
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
    public void applyDithering() throws IOException {
        // WHY IS THE ORIGINAL IMAGE HERE A GRAYSCALE WITH 50% LOWER BRIGHTNESS
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

//                int averageVal = (rVal + gVal + bVal) / 3;
                int averageVal = (int) (0.229 * rVal + 0.587 * gVal + 0.114 * bVal);
                pixel = (aVal << 24) | (averageVal << 16) | (averageVal << 8) | averageVal;
                buffImg.setRGB(x,y,pixel);
            }
        }
        originalImage.setImage(new WritableImage(makeViewableImg(buffImg)));
        leftImageLabel.setText("Original grayscale image");
        newImageLabel.setText("Image after Ordered Dithering");
        BufferedImage ditheredBuffImg = buffImg;
        height = ditheredBuffImg.getHeight();
        width = ditheredBuffImg.getWidth();

//        int[][] ditherMatrix = {{0,8,2,10},
//                                {12,4,14,6},
//                                {3,11,1,9},
//                                {15,7,13,5}};
//        int[][] ditherMatrix = {{0,32,8,40,2,34,10,42},
//                                {48,16,56,24,50,18,58,26},
//                                {12,44,4,46,14,46,6,38},
//                                {60,28,52,20,62,30,54,22},
//                                {3,35,11,43,1,33,9,41},
//                                {51,19,59,27,49,17,57,25},
//                                {15,47,7,39,13,45,5,37},
//                                {63,31,55,23,61,29,53,21}};

//        int ditherMatrix[][] = {
//
//            {63, 58, 50, 40, 41, 51, 59, 60, 64, 69, 77, 87, 86, 76, 68, 67},
//            {57, 33, 27, 18, 19, 28, 34, 52, 70, 94, 100, 109, 108, 99, 93, 75},
//            {49, 26, 13, 11, 12, 15, 29, 44, 78, 101, 114, 116, 115, 112, 98, 83},
//            {39, 17, 4, 3, 2, 9, 20, 42, 87, 110, 123, 124, 125, 118, 107, 85},
//            {38, 16, 5, 0, 1, 10, 21, 43, 89, 111, 122, 127, 126, 117, 106, 84},
//            {48, 25, 8, 6, 7, 14, 30, 45, 79, 102, 119, 121, 120, 113, 97, 82},
//            {56, 32, 24, 23, 22, 31, 35, 53, 71, 95, 103, 104, 105, 96, 92, 74},
//            {62, 55, 47, 37, 36, 46, 54, 61, 65, 72, 80, 90, 91, 81, 73, 66},
//            {64, 69, 77, 87, 86, 76, 68, 67, 63, 58, 50, 40, 41, 51, 59, 60},
//            {70, 94, 100, 109, 108, 99, 93, 75, 57, 33, 27, 18, 19, 28, 34, 52},
//            {78, 101, 114, 116, 115, 112, 98, 83, 49, 26, 13, 11, 12, 15, 29, 44},
//            {87, 110, 123, 124, 125, 118, 107, 85, 39, 17, 4, 3, 2, 9, 20, 42},
//            {89, 111, 122, 127, 126, 117, 106, 84, 38, 16, 5, 0, 1, 10, 21, 43},
//            {79, 102, 119, 121, 120, 113, 97, 82, 48, 25, 8, 6, 7, 14, 30, 45},
//            {71, 95, 103, 104, 105, 96, 92, 74, 56, 32, 24, 23, 22, 31, 35, 53},
//            {65, 72, 80, 90, 91, 81, 73, 66, 62, 55, 47, 37, 36, 46, 54, 61},
//        };
        int[][] ditherMatrix = {
                {56, 23, 47, 34, 15, 2, 41, 63, 77, 9},
                {31, 72, 84, 50, 37, 12, 30, 58, 61, 3},
                {46, 14, 28, 85, 69, 20, 6, 79, 5, 96},
                {71, 11, 87, 1, 65, 35, 53, 95, 64, 42},
                {74, 25, 68, 48, 13, 76, 70, 24, 78, 33},
                {8, 86, 21, 67, 75, 7, 16, 27, 91, 19},
                {80, 17, 10, 44, 60, 88, 59, 38, 45, 52},
                {32, 66, 22, 26, 55, 89, 18, 49, 90, 36},
                {81, 62, 54, 29, 73, 4, 40, 51, 94, 82},
                {93, 43, 83, 97, 70, 98, 6, 64, 76, 25}
        };

        // will be using 10x10 dithering matrix, so divide each pixel value by 100
        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                int pixel = buffImg.getRGB(x,y);

                int aVal = (pixel >> 24) & 0xff;
                int rVal = (pixel >> 16) & 0xff;

                int i = x % ditherMatrix.length;
                int j = y % ditherMatrix.length;
                int intensity = rVal / (256/100);
                if (intensity < ditherMatrix[i][j]) {
                    rVal = 0;
                } else {
                    rVal = 255;
                }
                pixel = (aVal << 24) | (rVal << 16) | (rVal << 8) | rVal;
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