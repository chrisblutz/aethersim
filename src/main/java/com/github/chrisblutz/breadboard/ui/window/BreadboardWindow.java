package com.github.chrisblutz.breadboard.ui.window;

import javax.swing.*;
import java.awt.*;

public class BreadboardWindow {

    private static JFrame frame;
    private static BreadboardCanvas canvas;

    public static void initializeWindow() {

        frame = new JFrame("Breadboard");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);

        ImageIcon icon = new ImageIcon("./icon.png");
        frame.setIconImage(icon.getImage());

        canvas = new BreadboardCanvas();
        frame.setContentPane(canvas);

        frame.setVisible(true);
    }
}
