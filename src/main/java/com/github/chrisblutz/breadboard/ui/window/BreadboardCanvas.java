package com.github.chrisblutz.breadboard.ui.window;

import com.github.chrisblutz.breadboard.Breadboard;
import com.github.chrisblutz.breadboard.components.BuiltinChipTemplate;
import com.github.chrisblutz.breadboard.components.ChipTemplate;
import com.github.chrisblutz.breadboard.components.DesignedChipTemplate;
import com.github.chrisblutz.breadboard.components.builtins.AndGateChipLogic;
import com.github.chrisblutz.breadboard.components.builtins.NotGateChipLogic;
import com.github.chrisblutz.breadboard.designs.Design;
import com.github.chrisblutz.breadboard.designs.components.Chip;
import com.github.chrisblutz.breadboard.designs.components.Pin;
import com.github.chrisblutz.breadboard.designs.components.Wire;
import com.github.chrisblutz.breadboard.simulation.components.BuiltinChip;
import com.github.chrisblutz.breadboard.simulation.components.BuiltinNode;
import com.github.chrisblutz.breadboard.simulation.components.Node;
import com.github.chrisblutz.breadboard.simulation.components.WireSegment;
import com.github.chrisblutz.breadboard.simulation.workers.Worker;
import com.github.chrisblutz.breadboard.simulation.workers.WorkerScheduler;
import com.github.chrisblutz.breadboard.utils.Vertex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BreadboardCanvas extends JPanel {

    private int GUTTER_WIDTH = 60;

    private static Color DESIGNER_BACKGROUND = new Color(30, 34, 46);
    private static Color DESIGNER_BORDER = new Color(36, 41, 56);
    private static Color DESIGNER_GRID_DOTS = new Color(48, 55, 74);

    private static Color CHIP_FILL = new Color(26, 26, 26);
    private static Color CHIP_FONT_COLOR = new Color(146, 146, 146);

    private static Color PIN_INACTIVE_BORDER = new Color(10, 1, 1);
    private static Color PIN_INACTIVE_FILL = new Color(20, 1, 1);
    private static Color PIN_ACTIVE_BORDER = new Color(74, 7, 7);
    private static Color PIN_ACTIVE_FILL = new Color(132, 8, 8);

    private static int GRID_SQUARE_SIZE = 20;
    private static int GRID_DOT_DIAMETER = (GRID_SQUARE_SIZE / 10);
    private static int CHIP_BORDER_WIDTH = (GRID_SQUARE_SIZE / 6);
    private static int PIN_DIAMETER = (GRID_SQUARE_SIZE * 4 / 5);
    private static int WIRE_WIDTH = (GRID_SQUARE_SIZE * 2 / 5);

    private static final float CORNER_ARC_MIDPOINT_DISTANCE = ((float) GRID_SQUARE_SIZE / 2) - (((float) Math.sqrt(2) / 2) * ((float)GRID_SQUARE_SIZE / 2));

    static {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, Breadboard.class.getResourceAsStream("/fonts/Montserrat-Regular.ttf")));
        } catch (IOException | FontFormatException e) {
            throw new RuntimeException(e);
        }
    }

    private Design design;

    public BreadboardCanvas() {
        design = new Design();

        Node designOutputNode = new Node(null);
        WireSegment not1OutputDesignOutput4 = new WireSegment(designOutputNode);
        WireSegment not1OutputDesignOutput3 = new WireSegment(not1OutputDesignOutput4);
        WireSegment not1OutputDesignOutput2 = new WireSegment(not1OutputDesignOutput3);
        WireSegment not1OutputDesignOutput1 = new WireSegment(not1OutputDesignOutput2);
        BuiltinNode not1OutputNode = new BuiltinNode(NotGateChipLogic.RESULT_PIN_TEMPLATE, new WireSegment[] {not1OutputDesignOutput1});
        BuiltinNode not1InputNode = new BuiltinNode(NotGateChipLogic.OPERAND_PIN_TEMPLATE, null);
        WireSegment and2OutputNot1Input4 = new WireSegment(not1InputNode);
        WireSegment and2OutputNot1Input3 = new WireSegment(and2OutputNot1Input4);
        WireSegment and2OutputNot1Input2 = new WireSegment(and2OutputNot1Input3);
        WireSegment and2OutputNot1Input1 = new WireSegment(and2OutputNot1Input2);
        BuiltinNode and2OutputNode = new BuiltinNode(AndGateChipLogic.RESULT_PIN_TEMPLATE, new WireSegment[] {and2OutputNot1Input1});
        BuiltinNode and2Input2Node = new BuiltinNode(AndGateChipLogic.OPERAND_2_PIN_TEMPLATE, null);
        WireSegment designInput3And2Input2_14 = new WireSegment(and2Input2Node);
        WireSegment designInput3And2Input2_13 = new WireSegment(designInput3And2Input2_14);
        WireSegment designInput3And2Input2_12 = new WireSegment(designInput3And2Input2_13);
        WireSegment designInput3And2Input2_11 = new WireSegment(designInput3And2Input2_12);
        WireSegment designInput3And2Input2_10 = new WireSegment(designInput3And2Input2_11);
        WireSegment designInput3And2Input2_9 = new WireSegment(designInput3And2Input2_10);
        WireSegment designInput3And2Input2_8 = new WireSegment(designInput3And2Input2_9);
        WireSegment designInput3And2Input2_7 = new WireSegment(designInput3And2Input2_8);
        WireSegment designInput3And2Input2_6 = new WireSegment(designInput3And2Input2_7);
        WireSegment designInput3And2Input2_5 = new WireSegment(designInput3And2Input2_6);
        WireSegment designInput3And2Input2_4 = new WireSegment(designInput3And2Input2_5);
        WireSegment designInput3And2Input2_3 = new WireSegment(designInput3And2Input2_4);
        WireSegment designInput3And2Input2_2 = new WireSegment(designInput3And2Input2_3);
        WireSegment designInput3And2Input2_1 = new WireSegment(designInput3And2Input2_2);
        Node designInput3Node = new Node(new WireSegment[] {designInput3And2Input2_1});
        BuiltinNode and2Input1Node = new BuiltinNode(AndGateChipLogic.OPERAND_1_PIN_TEMPLATE, null);
        WireSegment and1OutputAnd2Input1_5 = new WireSegment(and2Input1Node);
        WireSegment and1OutputAnd2Input1_4 = new WireSegment(and1OutputAnd2Input1_5);
        WireSegment and1OutputAnd2Input1_3 = new WireSegment(and1OutputAnd2Input1_4);
        WireSegment and1OutputAnd2Input1_2 = new WireSegment(and1OutputAnd2Input1_3);
        WireSegment and1OutputAnd2Input1_1 = new WireSegment(and1OutputAnd2Input1_2);
        Node nand1OutputNode = new Node(new WireSegment[] {and1OutputAnd2Input1_1});

        WireSegment nand1Not1OutputNand1Output1 = new WireSegment(nand1OutputNode);
        BuiltinNode nand1Not1OutputNode = new BuiltinNode(NotGateChipLogic.RESULT_PIN_TEMPLATE, new WireSegment[] {nand1Not1OutputNand1Output1});
        BuiltinNode nand1Not1InputNode = new BuiltinNode(NotGateChipLogic.OPERAND_PIN_TEMPLATE, null);
        WireSegment nand1And1OutputNot1Input1 = new WireSegment(nand1Not1InputNode);
        BuiltinNode nand1And1OutputNode = new BuiltinNode(AndGateChipLogic.RESULT_PIN_TEMPLATE, new WireSegment[] {nand1And1OutputNot1Input1});
        BuiltinNode nand1And1Input2Node = new BuiltinNode(AndGateChipLogic.OPERAND_2_PIN_TEMPLATE, null);
        WireSegment designInput2Nand1And1Input2_1 = new WireSegment(nand1And1Input2Node);
        BuiltinNode nand1And1Input1Node = new BuiltinNode(AndGateChipLogic.OPERAND_1_PIN_TEMPLATE, null);
        WireSegment designInput1Nand1And1Input1_1 = new WireSegment(nand1And1Input1Node);

        Node nand1Input2Node = new Node(new WireSegment[] {designInput2Nand1And1Input2_1});
        WireSegment designInput2Nand1Input2_4 = new WireSegment(nand1Input2Node);
        WireSegment designInput2Nand1Input2_3 = new WireSegment(designInput2Nand1Input2_4);
        WireSegment designInput2Nand1Input2_2 = new WireSegment(designInput2Nand1Input2_3);
        WireSegment designInput2Nand1Input2_1 = new WireSegment(designInput2Nand1Input2_2);
        Node designInput2Node = new Node(new WireSegment[] {designInput2Nand1Input2_1});
        Node nand1Input1Node = new Node(new WireSegment[]  {designInput1Nand1And1Input1_1});
        WireSegment designInput1Nand1Input1_4 = new WireSegment(nand1Input1Node);
        WireSegment designInput1Nand1Input1_3 = new WireSegment(designInput1Nand1Input1_4);
        WireSegment designInput1Nand1Input1_2 = new WireSegment(designInput1Nand1Input1_3);
        WireSegment designInput1Nand1Input1_1 = new WireSegment(designInput1Nand1Input1_2);
        Node designInput1Node = new Node(new WireSegment[] {designInput1Nand1Input1_1});

        Pin designInput1 = new Pin();
        designInput1.x = 0;
        designInput1.y = 10;
        designInput1.simulationNode = designInput1Node;
        Pin designInput2 = new Pin();
        designInput2.x = 0;
        designInput2.y = 12;
        designInput2.simulationNode = designInput2Node;
        Pin designInput3 = new Pin();
        designInput3.x = 0;
        designInput3.y = 14;
        designInput3.simulationNode = designInput3Node;
        design.getInputPins().addAll(Arrays.asList(designInput1, designInput2, designInput3));

        Pin nandDesignInput1 = new Pin();
        nandDesignInput1.x = 0;
        nandDesignInput1.y = 20;
        nandDesignInput1.simulationNode = nand1Input1Node;
        Pin nandDesignInput2 = new Pin();
        nandDesignInput2.x = 0;
        nandDesignInput2.y = 22;
        nandDesignInput2.simulationNode = nand1Input2Node;
        design.getInputPins().addAll(Arrays.asList(nandDesignInput1, nandDesignInput2));

        Pin designOutput = new Pin();
        designOutput.x = 34;
        designOutput.y = 13;
        designOutput.simulationNode = designOutputNode;
        design.getOutputPins().add(designOutput);

        Pin nandDesignOutput = new Pin();
        nandDesignOutput.x = 15;
        nandDesignOutput.y = 21;
        nandDesignOutput.simulationNode = nand1OutputNode;
        design.getOutputPins().add(nandDesignOutput);

        ChipTemplate andTemplate = new BuiltinChipTemplate();
        andTemplate.id = "and";
        andTemplate.name = "AND";
        andTemplate.width = 6;
        andTemplate.height = 4;

        Pin nand1And1Input1 = new Pin();
        nand1And1Input1.x = 1;
        nand1And1Input1.y = 20;
        nand1And1Input1.simulationNode = nand1And1Input1Node;
        Pin nand1And1Input2 = new Pin();
        nand1And1Input2.x = 1;
        nand1And1Input2.y = 22;
        nand1And1Input2.simulationNode = nand1And1Input2Node;
        Pin nand1And1Output = new Pin();
        nand1And1Output.x = 7;
        nand1And1Output.y = 21;
        nand1And1Output.simulationNode = nand1And1OutputNode;
        AndGateChipLogic nand1And1Logic = new AndGateChipLogic();
        BuiltinChip nand1And1SimChip = new BuiltinChip();
        nand1And1SimChip.inputNodes = new BuiltinNode[] {nand1And1Input1Node, nand1And1Input2Node};
        nand1And1SimChip.outputNodes = new BuiltinNode[] {nand1And1OutputNode};
        nand1And1SimChip.logic = nand1And1Logic;
        Chip nand1And1 = new Chip();
        nand1And1.chipTemplate = andTemplate;
        nand1And1.x = 1;
        nand1And1.y = 19;
        nand1And1.pins.addAll(Arrays.asList(nand1And1Input1, nand1And1Input2, nand1And1Output));

        ChipTemplate nandTemplate = new DesignedChipTemplate();
        nandTemplate.id = "nand";
        nandTemplate.name = "NAND";
        nandTemplate.width = 6;
        nandTemplate.height = 4;

        Pin nand1Input1 = new Pin();
        nand1Input1.x = 4;
        nand1Input1.y = 10;
        nand1Input1.simulationNode = nand1Input1Node;
        Pin nand1Input2 = new Pin();
        nand1Input2.x = 4;
        nand1Input2.y = 12;
        nand1Input2.simulationNode = nand1Input2Node;
        Pin nand1Output = new Pin();
        nand1Output.x = 10;
        nand1Output.y = 11;
        nand1Output.simulationNode = nand1OutputNode;
        Chip nand1 = new Chip();
        nand1.chipTemplate = nandTemplate;
        nand1.x = 4;
        nand1.y = 9;
        nand1.pins.addAll(Arrays.asList(nand1Input1, nand1Input2, nand1Output));

        Pin and2Input1 = new Pin();
        and2Input1.x = 14;
        and2Input1.y = 12;
        and2Input1.simulationNode = and2Input1Node;
        Pin and2Input2 = new Pin();
        and2Input2.x = 14;
        and2Input2.y = 14;
        and2Input2.simulationNode = and2Input2Node;
        Pin and2Output = new Pin();
        and2Output.x = 20;
        and2Output.y = 13;
        and2Output.simulationNode = and2OutputNode;
        AndGateChipLogic and2Logic = new AndGateChipLogic();
        BuiltinChip and2SimChip = new BuiltinChip();
        and2SimChip.inputNodes = new BuiltinNode[] {and2Input1Node, and2Input2Node};
        and2SimChip.outputNodes = new BuiltinNode[] {and2OutputNode};
        and2SimChip.logic = and2Logic;
        Chip and2 = new Chip();
        and2.chipTemplate = andTemplate;
        and2.x = 14;
        and2.y = 11;
        and2.pins.addAll(Arrays.asList(and2Input1, and2Input2, and2Output));

        ChipTemplate notTemplate = new BuiltinChipTemplate();
        notTemplate.id = "not";
        notTemplate.name = "NOT";
        notTemplate.width = 6;
        notTemplate.height = 2;

        Pin nand1Not1Input = new Pin();
        nand1Not1Input.x = 8;
        nand1Not1Input.y = 21;
        nand1Not1Input.simulationNode = nand1Not1InputNode;
        Pin nand1Not1Output = new Pin();
        nand1Not1Output.x = 14;
        nand1Not1Output.y = 21;
        nand1Not1Output.simulationNode = nand1Not1OutputNode;
        NotGateChipLogic nand1Not1Logic = new NotGateChipLogic();
        BuiltinChip nand1Not1SimChip = new BuiltinChip();
        nand1Not1SimChip.inputNodes = new BuiltinNode[] {nand1Not1InputNode};
        nand1Not1SimChip.outputNodes = new BuiltinNode[] {nand1Not1OutputNode};
        nand1Not1SimChip.logic = nand1Not1Logic;
        Chip nand1Not1 = new Chip();
        nand1Not1.chipTemplate = notTemplate;
        nand1Not1.x = 8;
        nand1Not1.y = 20;
        nand1Not1.pins.addAll(Arrays.asList(nand1Not1Input, nand1Not1Output));

        Pin not1Input = new Pin();
        not1Input.x = 24;
        not1Input.y = 13;
        not1Input.simulationNode = not1InputNode;
        Pin not1Output = new Pin();
        not1Output.x = 30;
        not1Output.y = 13;
        not1Output.simulationNode = not1OutputNode;
        NotGateChipLogic not1Logic = new NotGateChipLogic();
        BuiltinChip not1SimChip = new BuiltinChip();
        not1SimChip.inputNodes = new BuiltinNode[] {not1InputNode};
        not1SimChip.outputNodes = new BuiltinNode[] {not1OutputNode};
        not1SimChip.logic = not1Logic;
        Chip not1 = new Chip();
        not1.chipTemplate = notTemplate;
        not1.x = 24;
        not1.y = 12;
        not1.pins.addAll(Arrays.asList(not1Input, not1Output));

        design.chips.addAll(Arrays.asList(nand1, nand1And1, nand1Not1, and2, not1));

        Wire designInput1And1Input1 = new Wire();
        designInput1And1Input1.vertices = new Vertex[] {new Vertex(0, 10), new Vertex(4, 10)};
        designInput1And1Input1.simulationSegments = new WireSegment[] {designInput1Nand1Input1_1, designInput1Nand1Input1_2, designInput1Nand1Input1_3, designInput1Nand1Input1_4};
        Wire designInput2And1Input2 = new Wire();
        designInput2And1Input2.vertices = new Vertex[] {new Vertex(0, 12), new Vertex(4, 12)};
        designInput2And1Input2.simulationSegments = new WireSegment[] {designInput2Nand1Input2_1, designInput2Nand1Input2_2, designInput2Nand1Input2_3, designInput2Nand1Input2_4};
        Wire and1OutputAnd2Input1 = new Wire();
        and1OutputAnd2Input1.vertices = new Vertex[] {new Vertex(10, 11), new Vertex(12, 11), new Vertex(12, 12), new Vertex(14, 12)};
        and1OutputAnd2Input1.simulationSegments = new WireSegment[] {and1OutputAnd2Input1_1, and1OutputAnd2Input1_2, and1OutputAnd2Input1_3, and1OutputAnd2Input1_4, and1OutputAnd2Input1_5};
        Wire designInput3And2Input2 = new Wire();
        designInput3And2Input2.vertices = new Vertex[] {new Vertex(0, 14), new Vertex(14, 14)};
        designInput3And2Input2.simulationSegments = new WireSegment[] {designInput3And2Input2_1, designInput3And2Input2_2, designInput3And2Input2_3, designInput3And2Input2_4, designInput3And2Input2_5, designInput3And2Input2_6, designInput3And2Input2_7, designInput3And2Input2_8, designInput3And2Input2_9, designInput3And2Input2_10, designInput3And2Input2_11, designInput3And2Input2_12, designInput3And2Input2_13, designInput3And2Input2_14};
        Wire and2OutputNot1Input = new Wire();
        and2OutputNot1Input.vertices = new Vertex[] {new Vertex(20, 13), new Vertex(24, 13)};
        and2OutputNot1Input.simulationSegments = new WireSegment[] {and2OutputNot1Input1, and2OutputNot1Input2, and2OutputNot1Input3, and2OutputNot1Input4};
        Wire not1OutputDesignOutput = new Wire();
        not1OutputDesignOutput.vertices = new Vertex[] {new Vertex(30, 13), new Vertex(34, 13)};
        not1OutputDesignOutput.simulationSegments = new WireSegment[] {not1OutputDesignOutput1, not1OutputDesignOutput2, not1OutputDesignOutput3, not1OutputDesignOutput4};
        Wire nand1Not1OutputNand1Output = new Wire();
        nand1Not1OutputNand1Output.vertices = new Vertex[] {new Vertex(14, 21), new Vertex(15, 21)};
        nand1Not1OutputNand1Output.simulationSegments = new WireSegment[] {nand1Not1OutputNand1Output1};
        Wire nand1And1OutputNot1Input = new Wire();
        nand1And1OutputNot1Input.vertices = new Vertex[] {new Vertex(7, 21), new Vertex(8, 21)};
        nand1And1OutputNot1Input.simulationSegments = new WireSegment[] {nand1And1OutputNot1Input1};
        Wire designInput2Nand1And1Input2 = new Wire();
        designInput2Nand1And1Input2.vertices = new Vertex[] {new Vertex(0, 20), new Vertex(1, 20)};
        designInput2Nand1And1Input2.simulationSegments = new WireSegment[] {designInput2Nand1And1Input2_1};
        Wire designInput1Nand1And1Input1 = new Wire();
        designInput1Nand1And1Input1.vertices = new Vertex[] {new Vertex(0, 22), new Vertex(1, 22)};
        designInput1Nand1And1Input1.simulationSegments = new WireSegment[] {designInput1Nand1And1Input1_1};

        design.wires.addAll(Arrays.asList(designInput1And1Input1, designInput2And1Input2, and1OutputAnd2Input1, designInput3And2Input2, and2OutputNot1Input, not1OutputDesignOutput,
                nand1Not1OutputNand1Output, nand1And1OutputNot1Input, designInput2Nand1And1Input2, designInput1Nand1And1Input1));

        Worker worker1 = new Worker(true, designInput1Node);
        Worker worker2 = new Worker(true, designInput2Node);
        Worker worker3 = new Worker(true, designInput3Node);

        WorkerScheduler.builtinChips.addAll(Arrays.asList(nand1And1SimChip, nand1Not1SimChip, and2SimChip, not1SimChip));
        WorkerScheduler.nextTickWorkers.addAll(Arrays.asList(worker1, worker2, worker3));

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

        g.setColor(DESIGNER_BACKGROUND);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(DESIGNER_BORDER);
        Stroke current = g.getStroke();
        g.setStroke(new BasicStroke(3));
        Rectangle2D.Float borderRect = new Rectangle2D.Float(GUTTER_WIDTH, 0, getWidth() - (2 * GUTTER_WIDTH), getHeight());
        g.drawLine(GUTTER_WIDTH, 0, GUTTER_WIDTH, getHeight());
        g.drawLine(getWidth() - GUTTER_WIDTH, 0, getWidth() - GUTTER_WIDTH, getHeight());
        g.setStroke(current);

        g.setClip(borderRect);
        g.setColor(DESIGNER_GRID_DOTS);
        int gridWidth = getWidth() - (2 * GUTTER_WIDTH);
        int gridHeight = getHeight();
        int gridSquareCountX = gridWidth / GRID_SQUARE_SIZE;
        int gridSquareCountY = gridHeight / GRID_SQUARE_SIZE;

        for (int x = 0; x <= gridSquareCountX; x++) {
            for (int y = 0; y <= gridSquareCountY; y++) {
                g.fillOval(GUTTER_WIDTH + (x * GRID_SQUARE_SIZE) - (GRID_DOT_DIAMETER / 2) + 20, (y * GRID_SQUARE_SIZE) - (GRID_DOT_DIAMETER / 2) + 20, GRID_DOT_DIAMETER, GRID_DOT_DIAMETER);
            }
        }

        drawDesign(g, design);
    }

    private void drawDesign(Graphics2D g, Design design) {
        // Draw all design chips
        for (Chip chip : design.getChips())
            drawChip((Graphics2D) g.create(), chip);

        // Draw all design input pins
        for (Pin pin : design.getInputPins())
            drawPin((Graphics2D) g.create(), pin);

        // Draw all design output pins
        for (Pin pin : design.getOutputPins())
            drawPin((Graphics2D) g.create(), pin);

        // Draw all design wires
        for (Wire wire : design.getWires())
            drawWire((Graphics2D) g.create(), wire);
    }

    private void drawPin(Graphics2D g, Pin pin) {
        // Determine whether this pin is active
        boolean pinActive = pin.getSimulationNode() != null && pin.getSimulationNode().isActive();
        g.setColor(pinActive ? PIN_ACTIVE_FILL : PIN_INACTIVE_FILL);
        g.fillOval(getActualX(pin.getX()) - (PIN_DIAMETER / 2), getActualY(pin.getY()) - (PIN_DIAMETER / 2), PIN_DIAMETER, PIN_DIAMETER);
        g.setColor(pinActive ? PIN_ACTIVE_BORDER : PIN_INACTIVE_BORDER);
        Stroke current = g.getStroke();
        g.setStroke(new BasicStroke(2));
        g.drawOval(getActualX(pin.getX()) - (PIN_DIAMETER / 2), getActualY(pin.getY()) - (PIN_DIAMETER / 2), PIN_DIAMETER, PIN_DIAMETER);
        g.setStroke(current);
    }

    private void drawWire(Graphics2D g, Wire wire) {
        List<Path2D.Float> wirePaths = new ArrayList<>();
        List<Boolean> wirePathsActive = new ArrayList<>();

        Path2D.Float currentPath = new Path2D.Float();
        boolean pathInitialized = false;
        boolean pathPositioned = false;
        boolean currentPathActive = false;

        Vertex[] vertices = wire.getVertices();
        int currentIndex = 0;
        for (int vertexIndex = 0; vertexIndex < vertices.length - 1; vertexIndex++) {
            Vertex vertexStart = vertices[vertexIndex];
            Vertex vertexEnd = vertices[vertexIndex + 1];

            // Calculate the distance between these vertices
            // Since wire segments must be either horizontal or vertical, we can simplify
            int length = Math.abs((vertexEnd.getX() - vertexStart.getX()) + (vertexEnd.getY() - vertexStart.getY()));
            int xOffset = (vertexEnd.getX() - vertexStart.getX()) / length;
            int yOffset = (vertexEnd.getY() - vertexStart.getY()) / length;

            for (int segmentIndex = 0; segmentIndex < length; segmentIndex++) {
                int startX = vertexStart.getX() + (xOffset * segmentIndex);
                int startY = vertexStart.getY() + (yOffset * segmentIndex);
                int endX = vertexStart.getX() + (xOffset * (segmentIndex + 1));
                int endY = vertexStart.getY() + (yOffset * (segmentIndex + 1));
                boolean segmentActive = wire.getSimulationSegments() != null && wire.getSimulationSegments()[currentIndex].isActive();

                // If the segment we're on differs in "active" state from the one before, end the current path and start a new one
                if (!pathInitialized || currentPathActive != segmentActive) {
                    pathInitialized = true;
                    pathPositioned = false;
                    currentPath = new Path2D.Float();
                    currentPathActive = segmentActive;
                    currentPath.moveTo(getActualX(startX), getActualY(startY));
                    wirePaths.add(currentPath);
                    wirePathsActive.add(segmentActive);
                }

                // If we're on a corner, render segments in halves
                if ((vertexIndex > 0 && segmentIndex == 0) || (vertexIndex < vertices.length - 2 && segmentIndex == length - 1)) {
                    // Since we can have a corner directly into another corner, handle both cases here
                    float edgeMidX = getActualX(startX) + (xOffset * ((float) GRID_SQUARE_SIZE / 2));
                    float edgeMidY = getActualY(startY) + (yOffset * ((float) GRID_SQUARE_SIZE / 2));

                    // If we're coming out of a corner, draw the second half of the corner
                    // Otherwise, draw the straight line
                    if (vertexIndex > 0 && segmentIndex == 0) {
                        // Determine previous vertex direction
                        Vertex previousStart = vertices[vertexIndex - 1];
                        int previousLength = Math.abs((vertexStart.getX() - previousStart.getX()) + (vertexStart.getY() - previousStart.getY()));
                        int previousXOffset = (vertexStart.getX() - previousStart.getX()) / previousLength;
                        int previousYOffset = (vertexStart.getY() - previousStart.getY()) / previousLength;

                        // Calculate the "center" of the arc the corners will follow
                        int cornerArcMidXOffset = xOffset - previousXOffset;
                        int cornerArcMidYOffset = yOffset - previousYOffset;
                        float cornerArcMidX = getActualX(startX) + (cornerArcMidXOffset * CORNER_ARC_MIDPOINT_DISTANCE);
                        float cornerArcMidY = getActualY(startY) + (cornerArcMidYOffset * CORNER_ARC_MIDPOINT_DISTANCE);

                        // Calculate the quad control point
                        float quadControlX = getActualX(startX) + (2 * xOffset * CORNER_ARC_MIDPOINT_DISTANCE);
                        float quadControlY = getActualY(startY) + (2 * yOffset * CORNER_ARC_MIDPOINT_DISTANCE);

                        // If the path hasn't started yet, set the position
                        if (!pathPositioned) {
                            pathPositioned = true;
                            currentPath.moveTo(cornerArcMidX, cornerArcMidY);
                        }

                        // Draw the quad curve
                        currentPath.quadTo(quadControlX, quadControlY, edgeMidX, edgeMidY);
                    } else {
                        // If the path hasn't started yet, set the position
                        if (!pathPositioned) {
                            pathPositioned = true;
                            currentPath.moveTo(getActualX(startX), getActualY(startY));
                        }

                        // Draw the straight line
                        currentPath.lineTo(edgeMidX, edgeMidY);
                    }

                    // If we're going into a corner, draw the first half of a corner
                    // Otherwise, draw the straight line
                    if (vertexIndex < vertices.length - 2 && segmentIndex == length - 1) {
                        // Determine next vertex direction
                        Vertex nextEnd = vertices[vertexIndex + 2];
                        int nextLength = Math.abs((nextEnd.getX() - vertexEnd.getX()) + (nextEnd.getY() - vertexEnd.getY()));
                        int nextXOffset = (nextEnd.getX() - vertexEnd.getX()) / nextLength;
                        int nextYOffset = (nextEnd.getY() - vertexEnd.getY()) / nextLength;

                        // Calculate the "center" of the arc the corners will follow
                        int cornerArcMidXOffset = nextXOffset - xOffset;
                        int cornerArcMidYOffset = nextYOffset - yOffset;
                        float cornerArcMidX = getActualX(endX) + (cornerArcMidXOffset * CORNER_ARC_MIDPOINT_DISTANCE);
                        float cornerArcMidY = getActualY(endY) + (cornerArcMidYOffset * CORNER_ARC_MIDPOINT_DISTANCE);

                        // Calculate the quad control point
                        float quadControlX = getActualX(endX) + (-2 * xOffset * CORNER_ARC_MIDPOINT_DISTANCE);
                        float quadControlY = getActualY(endY) + (-2 * yOffset * CORNER_ARC_MIDPOINT_DISTANCE);

                        // Draw the quad curve
                        currentPath.quadTo(quadControlX, quadControlY, cornerArcMidX, cornerArcMidY);
                    } else {
                        // Draw the straight line
                        currentPath.lineTo(getActualX(endX), getActualY(endY));
                    }
                } else {
                    // If the path hasn't started yet, set the position
                    if (!pathPositioned) {
                        pathPositioned = true;
                        currentPath.moveTo(getActualX(startX), getActualY(startY));
                    }

                    // Draw the straight line
                    // Otherwise, draw the segment we're currently in
                    currentPath.lineTo(getActualX(endX), getActualY(endY));
                }

                // Continue with the next segment
                currentIndex++;
            }
        }

        // Set the stroke used for the path
        g.setStroke(new BasicStroke(WIRE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));

        // Now draw the wires we calculated
        for (int segmentIndex = 0; segmentIndex < wirePaths.size(); segmentIndex++) {
            g.setColor(wirePathsActive.get(segmentIndex) ? PIN_ACTIVE_FILL : PIN_INACTIVE_FILL);
            g.draw(wirePaths.get(segmentIndex));
        }
    }

    private void drawChip(Graphics2D g, Chip chip) {
        // Calculate initial actual values from grid values
        int chipX = getActualX(chip.getX());
        int chipY = getActualY(chip.getY());
        int chipWidth = getActualDimension(chip.getChipTemplate().getWidth());
        int chipHeight = getActualDimension(chip.getChipTemplate().getHeight());

        // Draw the chip background
        g.setColor(CHIP_FILL);
        g.fillRoundRect(chipX, chipY, chipWidth, chipHeight, GRID_SQUARE_SIZE, GRID_SQUARE_SIZE);

        // Create a new graphics object so we can apply clips to it
        Graphics2D gBorder = (Graphics2D) g.create();
        gBorder.setStroke(new BasicStroke(CHIP_BORDER_WIDTH));

        // Draw the "lighter" side of the border
        gBorder.setClip(new Polygon(new int[] {chipX - CHIP_BORDER_WIDTH, chipX + chipWidth + CHIP_BORDER_WIDTH, chipX - CHIP_BORDER_WIDTH}, new int[] {chipY - CHIP_BORDER_WIDTH, chipY - CHIP_BORDER_WIDTH, chipY + chipHeight + CHIP_BORDER_WIDTH}, 3));
        gBorder.setColor(CHIP_FILL.brighter());
        gBorder.drawRoundRect(chipX, chipY, chipWidth, chipHeight, GRID_SQUARE_SIZE, GRID_SQUARE_SIZE);

        // Draw the "darker" side of the border
        gBorder.setClip(new Polygon(new int[] {chipX - CHIP_BORDER_WIDTH, chipX + chipWidth + CHIP_BORDER_WIDTH, chipX + chipWidth + CHIP_BORDER_WIDTH}, new int[] {chipY + chipHeight + CHIP_BORDER_WIDTH, chipY - CHIP_BORDER_WIDTH, chipY + chipHeight + CHIP_BORDER_WIDTH}, 3));
        gBorder.setColor(CHIP_FILL.darker());
        gBorder.drawRoundRect(chipX, chipY, chipWidth, chipHeight, GRID_SQUARE_SIZE, GRID_SQUARE_SIZE);

        // Set the font and font color
        g.setColor(CHIP_FONT_COLOR);
        g.setFont(new Font("Montserrat", Font.PLAIN, 20));

        // Draw the chip text in the center of the chip
        String chipText = chip.getChipTemplate().getName();
        int stringWidth = g.getFontMetrics().stringWidth(chipText);
        int stringHeight = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
        g.drawString(chipText, chipX + (chipWidth / 2) - (stringWidth / 2), chipY + (chipHeight / 2) + (stringHeight / 2));

        // Draw pins for chip
        for (Pin pin : chip.getPins())
            drawPin(g, pin);
    }

    private int getActualX(int gridX) {
        return GUTTER_WIDTH + (gridX * GRID_SQUARE_SIZE) + 20;
    }

    private int getActualY(int gridY) {
        return gridY * GRID_SQUARE_SIZE + 20;
    }

    private int getActualDimension(int gridDimension) {
        return gridDimension * GRID_SQUARE_SIZE;
    }
}
