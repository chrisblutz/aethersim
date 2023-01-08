package com.github.chrisblutz.breadboard.ui.window;

import com.github.chrisblutz.breadboard.Breadboard;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class BreadboardWindow {

    private static JFrame frame;
    private static BreadboardCanvas canvas;

    public static void initializeWindow() {

        frame = new JFrame("Breadboard");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);

        try {
            Image image = ImageIO.read(Breadboard.class.getResourceAsStream("/assets/icon.png"));
            frame.setIconImage(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        canvas = new BreadboardCanvas();
        frame.setContentPane(canvas);

        frame.setVisible(true);
    }
}
