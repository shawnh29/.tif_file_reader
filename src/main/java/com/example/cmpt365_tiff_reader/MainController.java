package com.example.cmpt365_tiff_reader;

import javafx.application.Platform;
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
    private Button finalExitButton;
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
        Platform.exit();
    }
    @FXML
    public void grayScaleButtonClicked() throws IOException {
        root = FXMLLoader.load(getClass().getResource("processingScreen.fxml"));
        stage = (Stage) goToGrayScaleButton.getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("Image Processing");
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
    public void applyDithering() throws IOException {
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

                int gray = (int) (0.229 * rVal + 0.587 * gVal + 0.114 * bVal);
                pixel = (aVal << 24) | (gray << 16) | (gray << 8) | gray;
                buffImg.setRGB(x,y,pixel);
            }
        }
        originalImage.setImage(new WritableImage(makeViewableImg(buffImg)));
        leftImageLabel.setText("Original grayscale image");
        newImageLabel.setText("Image after Ordered Dithering");
        BufferedImage ditheredBuffImg = buffImg;
        height = ditheredBuffImg.getHeight();
        width = ditheredBuffImg.getWidth();

        int[][] ditherMatrix = {
                {56, 23, 47, 34, 15, 12, 41, 63, 77, 9},
                {31, 72, 84, 50, 37, 12, 30, 58, 61, 3},
                {46, 14, 28, 85, 69, 20, 6, 79, 5, 96},
                {71, 11, 87, 40, 65, 35, 53, 95, 64, 42},
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
    public void autoLevelClicked() throws IOException {
        originalImage.setImage(new WritableImage(makeViewableImg(originalBuffImage)));
        leftImageLabel.setText("Original Image");
        newImageLabel.setText("After applying auto level");
        autoLevelButton.setVisible(false);
        finalExitButton.setVisible(true);
        // first get a fresh image and turn it to grayscale
        BufferedImage grayUseBufImg = ImageIO.read(file);
        int gX = grayUseBufImg.getWidth();
        int gY = grayUseBufImg.getHeight();
        int maxR = -1000, maxG = -1000, maxB = -1000;
        int minR = 1000, minG = 1000, minB = 1000;
        for (int y=0; y<gY; y++) {
            for (int x=0; x<gX; x++) {
                int pixel = grayUseBufImg.getRGB(x,y);
                int a = (pixel >> 24) & 0xff;
                int r = (pixel >> 16) & 0xff;
                int g = (pixel >> 8) & 0xff;
                int b = pixel & 0xff;

                int gray = (int) ((int) 0.229 * r + 0.587 * g + 0.114 * b);
                pixel = (a << 24) | (gray << 16) | (gray << 8) | gray;
                grayUseBufImg.setRGB(x,y,pixel);

                // after making it grayscale, extract the max value of each RGB channel
                int newR = (pixel >> 16) & 0xff;
                int newG = (pixel >> 8) & 0xff;
                int newB = pixel & 0xff;

                maxR = Math.max(newR, maxR);
                maxG = Math.max(newG, maxG);
                maxB = Math.max(newB, maxB);

                minR = Math.min(newR, minR);
                minG = Math.min(newG, minG);
                minB = Math.min(newB, minB);
            }
        }

        int width = grayUseBufImg.getWidth();
        int height = grayUseBufImg.getHeight();
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                int pixel = originalBuffImage.getRGB(x,y);
                int aVal = (pixel >> 24) & 0xff;
                int rVal = (pixel >> 16) & 0xff;
                int gVal = (pixel >> 8) & 0xff;
                int bVal = pixel & 0xff;

                // calculating the scaling factor, this will stretch the pixel values to the full [0,255] range
                double rScale = 255.0 / (maxR - minR);
                double gScale = 255.0 / (maxR - minR);
                double bScale = 255.0 / (maxR - minR);

                // the new RGB values
                double newR = (rVal - minR) * rScale;
                double newG = (gVal - minG) * gScale;
                double newB = (bVal - minB) * bScale;

                // after doing the calculations, the values may exceed 255, in that case, bound it by 255
                if (newR > 255) {
                    newR = 255;
                }
                if (newG > 255) {
                    newG = 255;
                }
                if (newB > 255) {
                    newB = 255;
                }
                rVal = (int) Math.round(newR);
                gVal = (int) Math.round(newG);
                bVal = (int) Math.round(newB);

                pixel = (aVal << 24) | (rVal << 16) | (gVal << 8) | bVal;
                originalBuffImage.setRGB(x,y,pixel);
            }
        }
        newImage.setImage(new WritableImage(makeViewableImg(originalBuffImage)));
    }
}