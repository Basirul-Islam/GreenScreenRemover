package com.Bashir;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    private static int[][][] skin = new int[256][256][256];
    private static int[][][] nonSkin = new int[256][256][256];
    private static double[][][] probability = new double[256][256][256];

    private static void initializeAra() {
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                for (int k = 0; k < 256; k++) {
                    skin[i][j][k] = 0;
                    nonSkin[i][j][k] = 0;
                    probability[i][j][k] = 0.0f;
                }
            }
        }
    }

    private static void readImages() {
        File NonMaskFolder = new File("C:\\Users\\Asus\\IdeaProjects\\GreenScreenRemover\\src\\Res\\NonMask\\");
        File[] NonMasklistOfFiles = NonMaskFolder.listFiles();

        File MaskFolder = new File("C:\\Users\\Asus\\IdeaProjects\\GreenScreenRemover\\src\\Res\\Mask\\");
        File[] MasklistOfFiles = MaskFolder.listFiles();

        for (int i = 0; i < NonMasklistOfFiles.length; i++) {
            File NonMaskImagePath = NonMasklistOfFiles[i];
            File MaskImagePath = MasklistOfFiles[i];

            try {
                BufferedImage NonMaskImage = ImageIO.read(NonMaskImagePath);
                BufferedImage MaskImage = ImageIO.read(MaskImagePath);

                int height = NonMaskImage.getHeight();
                int width = NonMaskImage.getWidth();

                System.out.println("Height: " + height +"  width: " + width);

                System.out.println("Reading image...: " + NonMaskImagePath.getName());

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {

                        //System.out.println("height: " + y +"\nwidth: " + x);

                        int NonMaskImageRGB = NonMaskImage.getRGB(x, y);
                        int MaskImageRGB = MaskImage.getRGB(x, y);

                        int NonMaskImageA = (NonMaskImageRGB >> 24) & 0xff;
                        int NonMaskImageR = (NonMaskImageRGB >> 16) & 0xff;
                        int NonMaskImageG = (NonMaskImageRGB >> 8) & 0xff;
                        int NonMaskImageB = NonMaskImageRGB & 0xff;

                        int MaskImageA = (MaskImageRGB >> 24) & 0xff;
                        int MaskImageR = (MaskImageRGB >> 16) & 0xff;
                        int MaskImageG = (MaskImageRGB >> 8) & 0xff;
                        int MaskImageB = MaskImageRGB & 0xff;

                        if (MaskImageR >= 20 && MaskImageG <= 230 && MaskImageB >= 30)
                            nonSkin[NonMaskImageR][NonMaskImageG][NonMaskImageB]++;
                        else
                            skin[NonMaskImageR][NonMaskImageG][NonMaskImageB]++;

                    }
                }

                System.out.println("Finished read image: " + NonMaskImagePath.getName());


            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void calculateProbability() {
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                for (int k = 0; k < 256; k++) {
                    try {
                        double a = (skin[i][j][k] + nonSkin[i][j][k]);
                        if (a != 0.0)
                            probability[i][j][k] = skin[i][j][k] / a;
                    } catch (Exception e) {
                        System.out.println();
                    }
                }
            }
        }
    }

    private static void testImage() throws IOException {
        BufferedImage image = ImageIO.read(new File("C:\\Users\\Asus\\IdeaProjects\\GreenScreenRemover\\src\\Res\\testImage.jpeg"));
        int height = image.getHeight();
        int width = image.getWidth();

        System.out.println("TestImage Height: " + height +"     TestImage width: " + width);

        BufferedImage backgroundImage = ImageIO.read(new File("C:\\Users\\Asus\\IdeaProjects\\GreenScreenRemover\\src\\Res\\backgroundToBeReplaced.jpg"));

        int backgroundImageHeight = backgroundImage.getHeight();
        int backgroundImageWidth = backgroundImage.getWidth();
        System.out.println("BackgroundImage Height: " + backgroundImageHeight + "   BackgroundImage Width: " + backgroundImageWidth);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x, y);
                //System.out.println(p);
                int a = (p >> 24) & 0xff;
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;


                int p0 = backgroundImage.getRGB(x, y);
                //System.out.println(p);
                int a0 = (p0 >> 24) & 0xff;
                int r0 = (p0 >> 16) & 0xff;
                int g0 = (p0 >> 8) & 0xff;
                int b0 = p0 & 0xff;


                if (probability[r][g][b] >= 0.01) {
                    a = 255;
                    r = r0;
                    g = g0;
                    b = b0;


                    p = (a << 24) | (r << 16) | (g << 8) | b;
                    image.setRGB(x, y, p);
                } else {
                    a = 255;
                    r = a;
                    g = g;
                    b = b;

                    p = (a << 24) | (r << 16) | (g << 8) | b;
                    image.setRGB(x, y, p);
                }


            }
        }

        ImageIO.write(image, "jpg", new File("C:\\Users\\Asus\\IdeaProjects\\GreenScreenRemover\\src\\Res\\testImageoutPut.jpeg"));


    }


    public static void main(String[] args) throws IOException {

        System.out.println("Start");
        initializeAra();
        readImages();
        calculateProbability();
        System.out.println("Finished training process");

        testImage();
    }

}