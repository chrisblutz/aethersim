package com.github.chrisblutz.breadboard.ui.window;

import com.github.chrisblutz.breadboard.Breadboard;
import com.github.chrisblutz.breadboard.components.BuiltinChipTemplate;
import com.github.chrisblutz.breadboard.components.DesignedChipTemplate;
import com.github.chrisblutz.breadboard.components.builtins.AndGateChipLogic;
import com.github.chrisblutz.breadboard.components.builtins.NotGateChipLogic;
import com.github.chrisblutz.breadboard.designs.Design;
import com.github.chrisblutz.breadboard.designs.components.Chip;
import com.github.chrisblutz.breadboard.designs.components.Pin;
import com.github.chrisblutz.breadboard.designs.components.Wire;
import com.github.chrisblutz.breadboard.simulation.generator.DesignInstance;
import com.github.chrisblutz.breadboard.simulation.generator.MeshGenerator;
import com.github.chrisblutz.breadboard.simulation.workers.Worker;
import com.github.chrisblutz.breadboard.simulation.workers.WorkerScheduler;
import com.github.chrisblutz.breadboard.ui.render.BreadboardRenderer;
import com.github.chrisblutz.breadboard.utils.Vertex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BreadboardCanvas extends JPanel {

    private int GUTTER_WIDTH = 60;

    static {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, Breadboard.class.getResourceAsStream("/fonts/Montserrat-Regular.ttf")));
        } catch (IOException | FontFormatException e) {
            throw new RuntimeException(e);
        }
    }

    private List<BreadboardRenderer> renderers = new ArrayList<>();

    public BreadboardCanvas() {

        // +-----------------+
        // | NAND GATE SETUP |
        // +-----------------+

        Design nandDesign = new Design();
        nandDesign.width = 24;
        nandDesign.height = 8;

        Pin nandDesignInput1 = new Pin();
        nandDesignInput1.chipX = 0;
        nandDesignInput1.chipY = 1;
        nandDesignInput1.designX = 0;
        nandDesignInput1.designY = 3;
        Pin nandDesignInput2 = new Pin();
        nandDesignInput2.chipX = 0;
        nandDesignInput2.chipY = 3;
        nandDesignInput2.designX = 0;
        nandDesignInput2.designY = 5;
        Pin nandDesignOutput = new Pin();
        nandDesignOutput.chipX = 6;
        nandDesignOutput.chipY = 2;
        nandDesignOutput.designX = 24;
        nandDesignOutput.designY = 4;
        nandDesign.getPins().addAll(Arrays.asList(nandDesignInput1, nandDesignInput2, nandDesignOutput));

        Chip nandAnd = new Chip();
        nandAnd.chipTemplate = BuiltinChipTemplate.getAndGateTemplate();
        nandAnd.x = 4;
        nandAnd.y = 2;
        Chip nandNot = new Chip();
        nandNot.chipTemplate = BuiltinChipTemplate.getNotGateTemplate();
        nandNot.x = 14;
        nandNot.y = 3;
        nandDesign.getChips().addAll(Arrays.asList(nandAnd, nandNot));

        Wire nandDesignInput1AndInput1 = new Wire();
        nandDesignInput1AndInput1.vertices = new Vertex[] {new Vertex(0, 3), new Vertex(4, 3)};
        nandDesignInput1AndInput1.startPin = nandDesignInput1;
        nandDesignInput1AndInput1.endPin = AndGateChipLogic.OPERAND_1_PIN;
        nandDesignInput1AndInput1.endChip = nandAnd;
        Wire nandDesignInput2AndInput2 = new Wire();
        nandDesignInput2AndInput2.vertices = new Vertex[] {new Vertex(0, 5), new Vertex(4, 5)};
        nandDesignInput2AndInput2.startPin = nandDesignInput2;
        nandDesignInput2AndInput2.endPin = AndGateChipLogic.OPERAND_2_PIN;
        nandDesignInput2AndInput2.endChip = nandAnd;
        Wire nandAndOutputNotInput = new Wire();
        nandAndOutputNotInput.vertices = new Vertex[] {new Vertex(10, 4), new Vertex(14, 4)};
        nandAndOutputNotInput.startPin = AndGateChipLogic.RESULT_PIN;
        nandAndOutputNotInput.startChip = nandAnd;
        nandAndOutputNotInput.endPin = NotGateChipLogic.OPERAND_PIN;
        nandAndOutputNotInput.endChip = nandNot;
        Wire nandNotOutputDesignOutput = new Wire();
        nandNotOutputDesignOutput.vertices = new Vertex[] {new Vertex(20, 4), new Vertex(24, 4)};
        nandNotOutputDesignOutput.startPin = NotGateChipLogic.RESULT_PIN;
        nandNotOutputDesignOutput.startChip = nandNot;
        nandNotOutputDesignOutput.endPin = nandDesignOutput;
        nandDesign.getWires().addAll(Arrays.asList(nandDesignInput1AndInput1, nandDesignInput2AndInput2, nandAndOutputNotInput, nandNotOutputDesignOutput));

        DesignedChipTemplate nandGateTemplate = new DesignedChipTemplate();
        nandGateTemplate.id = "nand";
        nandGateTemplate.name = "NAND";
        nandGateTemplate.width = 6;
        nandGateTemplate.height = 4;
        nandGateTemplate.design = nandDesign;

        // +------------------------+
        // | TEST ORIENTATION SETUP |
        // +------------------------+

        Design testDesign = new Design();
        testDesign.width = 24;
        testDesign.height = 8;

        Pin testDesignInput1 = new Pin();
        testDesignInput1.designX = 0;
        testDesignInput1.designY = 3;
        Pin testDesignInput2 = new Pin();
        testDesignInput2.designX = 0;
        testDesignInput2.designY = 5;
        Pin testDesignInput3 = new Pin();
        testDesignInput3.designX = 0;
        testDesignInput3.designY = 9;
        Pin testDesignInput4 = new Pin();
        testDesignInput4.designX = 0;
        testDesignInput4.designY = 11;
        Pin testDesignOutput = new Pin();
        testDesignOutput.designX = 24;
        testDesignOutput.designY = 7;
        testDesign.getPins().addAll(Arrays.asList(testDesignInput1, testDesignInput2, testDesignInput3, testDesignInput4, testDesignOutput));

        Chip testNand1 = new Chip();
        testNand1.chipTemplate = nandGateTemplate;
        testNand1.x = 4;
        testNand1.y = 2;
        Chip testNand2 = new Chip();
        testNand2.chipTemplate = nandGateTemplate;
        testNand2.x = 4;
        testNand2.y = 8;
        Chip testAnd1 = new Chip();
        testAnd1.chipTemplate = BuiltinChipTemplate.getAndGateTemplate();
        testAnd1.x = 14;
        testAnd1.y = 5;
        testDesign.getChips().addAll(Arrays.asList(testNand1, testNand2, testAnd1));

        Wire testDesignInput1Nand1Input1 = new Wire();
        testDesignInput1Nand1Input1.vertices = new Vertex[] {new Vertex(0, 3), new Vertex(4, 3)};
        testDesignInput1Nand1Input1.startPin = testDesignInput1;
        testDesignInput1Nand1Input1.endPin = nandDesignInput1;
        testDesignInput1Nand1Input1.endChip = testNand1;
        Wire testDesignInput2Nand1Input2 = new Wire();
        testDesignInput2Nand1Input2.vertices = new Vertex[] {new Vertex(0, 5), new Vertex(4, 5)};
        testDesignInput2Nand1Input2.startPin = testDesignInput2;
        testDesignInput2Nand1Input2.endPin = nandDesignInput2;
        testDesignInput2Nand1Input2.endChip = testNand1;
        Wire testDesignInput3Nand2Input1 = new Wire();
        testDesignInput3Nand2Input1.vertices = new Vertex[] {new Vertex(0, 9), new Vertex(4, 9)};
        testDesignInput3Nand2Input1.startPin = testDesignInput3;
        testDesignInput3Nand2Input1.endPin = nandDesignInput1;
        testDesignInput3Nand2Input1.endChip = testNand2;
        Wire testDesignInput4Nand2Input2 = new Wire();
        testDesignInput4Nand2Input2.vertices = new Vertex[] {new Vertex(0, 11), new Vertex(4, 11)};
        testDesignInput4Nand2Input2.startPin = testDesignInput4;
        testDesignInput4Nand2Input2.endPin = nandDesignInput2;
        testDesignInput4Nand2Input2.endChip = testNand2;
        Wire testNand1OutputAndInput1 = new Wire();
        testNand1OutputAndInput1.vertices = new Vertex[] {new Vertex(10, 4), new Vertex(12, 4), new Vertex(12, 6), new Vertex(14, 6)};
        testNand1OutputAndInput1.startPin = nandDesignOutput;
        testNand1OutputAndInput1.startChip = testNand1;
        testNand1OutputAndInput1.endPin = AndGateChipLogic.OPERAND_1_PIN;
        testNand1OutputAndInput1.endChip = testAnd1;
        Wire testNand2OutputAndInput2 = new Wire();
        testNand2OutputAndInput2.vertices = new Vertex[] {new Vertex(10, 10), new Vertex(12, 10), new Vertex(12, 8), new Vertex(14, 8)};
        testNand2OutputAndInput2.startPin = nandDesignOutput;
        testNand2OutputAndInput2.startChip = testNand2;
        testNand2OutputAndInput2.endPin = AndGateChipLogic.OPERAND_2_PIN;
        testNand2OutputAndInput2.endChip = testAnd1;
        Wire testAndOutputDesignOutput = new Wire();
        testAndOutputDesignOutput.vertices = new Vertex[] {new Vertex(20, 7), new Vertex(24, 7)};
        testAndOutputDesignOutput.startPin = AndGateChipLogic.RESULT_PIN;
        testAndOutputDesignOutput.startChip = testAnd1;
        testAndOutputDesignOutput.endPin = testDesignOutput;
        testDesign.getWires().addAll(Arrays.asList(testDesignInput1Nand1Input1, testDesignInput2Nand1Input2, testDesignInput3Nand2Input1, testDesignInput4Nand2Input2, testNand1OutputAndInput1, testNand2OutputAndInput2, testAndOutputDesignOutput));

        DesignInstance designInstance = MeshGenerator.generateMeshForDesign(testDesign);
        DesignInstance nand1DesignInstance = designInstance.getDesignInstanceForChip(testNand1);
        DesignInstance nand2DesignInstance = designInstance.getDesignInstanceForChip(testNand2);

        renderers.add(new BreadboardRenderer(testDesign, designInstance));
        renderers.add(new BreadboardRenderer(nandDesign, nand1DesignInstance));
        renderers.add(new BreadboardRenderer(nandDesign, nand2DesignInstance));

        Worker input1 = new Worker(true, designInstance.getNodeForPin(testDesignInput1));
        Worker input2 = new Worker(true, designInstance.getNodeForPin(testDesignInput2));
        Worker input3 = new Worker(true, designInstance.getNodeForPin(testDesignInput3));
        Worker input4 = new Worker(true, designInstance.getNodeForPin(testDesignInput4));

        WorkerScheduler.queue(input1);
        WorkerScheduler.queue(input2);
        WorkerScheduler.queue(input3);
        WorkerScheduler.queue(input4);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                WorkerScheduler.tick();
                revalidate();
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics graphics) {

        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (BreadboardRenderer renderer : renderers) {
            renderer.render((Graphics2D) g.create(), getWidth(), getHeight());

            g.translate(0, 300);
        }
    }
}
