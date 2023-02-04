package com.github.chrisblutz.breadboard.ui.window;

import com.github.chrisblutz.breadboard.components.SignalSourceTemplate;
import com.github.chrisblutz.breadboard.components.TransistorTemplate;
import com.github.chrisblutz.breadboard.designs.Design;
import com.github.chrisblutz.breadboard.designs.components.Chip;
import com.github.chrisblutz.breadboard.designs.components.Pin;
import com.github.chrisblutz.breadboard.designs.components.Wire;
import com.github.chrisblutz.breadboard.simulation.workers.WorkerScheduler;
import com.github.chrisblutz.breadboard.simulationproto.SimulatedDesign;
import com.github.chrisblutz.breadboard.simulationproto.Simulation;
import com.github.chrisblutz.breadboard.simulationproto.standard.MeshSimulator;
import com.github.chrisblutz.breadboard.simulationproto.standard.mesh.*;
import com.github.chrisblutz.breadboard.simulationproto.standard.threading.MeshSimulationCoordinator;
import com.github.chrisblutz.breadboard.ui.render.designs.DesignEditor;
import com.github.chrisblutz.breadboard.ui.toolkit.UIWindow;
import com.github.chrisblutz.breadboard.ui.toolkit.builtin.containers.*;
import com.github.chrisblutz.breadboard.ui.toolkit.builtin.input.*;
import com.github.chrisblutz.breadboard.ui.toolkit.builtin.text.UIText;
import com.github.chrisblutz.breadboard.ui.toolkit.display.theming.ThemeKeys;
import com.github.chrisblutz.breadboard.ui.toolkit.UITheme;
import com.github.chrisblutz.breadboard.ui.toolkit.layout.UIDimension;
import com.github.chrisblutz.breadboard.ui.toolkit.layout.Direction;
import com.github.chrisblutz.breadboard.utils.Vertex;

import javax.swing.*;
import java.util.Arrays;

public class BreadboardWindow {

    private static JFrame frame;
    private static BreadboardCanvas canvas;

    // TODO
    public static MeshSimulationCoordinator coordinator;
    public static MeshSimulatedDesign simulatedDesign;
    public static MeshDriver resetDriver, setDriver;

    public static void initializeWindow() {

//        frame = new JFrame("Breadboard");
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//
//        try {
//            Image image = ImageIO.read(Breadboard.class.getResourceAsStream("/assets/icon.png"));
//            frame.setIconImage(image);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        canvas = new BreadboardCanvas();
//        frame.setContentPane(canvas);
//        frame.pack();
//
//        frame.addWindowListener(new WindowAdapter() {
//        });
//        frame.addContainerListener(new ContainerAdapter() {
//        });
//        frame.getContentPane().addComponentListener(new ComponentAdapter() {
//            @Override
//            public void componentResized(ComponentEvent e) {
//                System.out.println(e.getComponent().getWidth() + "x" + e.getComponent().getHeight());
//            }
//        });
//
//        frame.setVisible(true);


        // +-----------------+
        // | NAND GATE SETUP |
        // +-----------------+

//        Design nandDesign = new Design();
//        nandDesign.width = 24;
//        nandDesign.height = 8;
//
//        Pin nandDesignInput1 = new Pin();
//        nandDesignInput1.chipX = 0;
//        nandDesignInput1.chipY = 1;
//        nandDesignInput1.designX = 0;
//        nandDesignInput1.designY = 3;
//        Pin nandDesignInput2 = new Pin();
//        nandDesignInput2.chipX = 0;
//        nandDesignInput2.chipY = 3;
//        nandDesignInput2.designX = 0;
//        nandDesignInput2.designY = 5;
//        Pin nandDesignOutput = new Pin();
//        nandDesignOutput.chipX = 6;
//        nandDesignOutput.chipY = 2;
//        nandDesignOutput.designX = 24;
//        nandDesignOutput.designY = 4;
//        nandDesign.getPins().addAll(Arrays.asList(nandDesignInput1, nandDesignInput2, nandDesignOutput));
//
//        Chip nandAnd = new Chip();
//        nandAnd.chipTemplate = BuiltinChipTemplate.getAndGateTemplate();
//        nandAnd.x = 4;
//        nandAnd.y = 2;
//        Chip nandNot = new Chip();
//        nandNot.chipTemplate = BuiltinChipTemplate.getNotGateTemplate();
//        nandNot.x = 14;
//        nandNot.y = 3;
//        nandDesign.getChips().addAll(Arrays.asList(nandAnd, nandNot));
//
//        Wire nandDesignInput1AndInput1 = new Wire();
//        nandDesignInput1AndInput1.vertices = new Vertex[] {new Vertex(0, 3), new Vertex(4, 3)};
//        nandDesignInput1AndInput1.startPin = nandDesignInput1;
//        nandDesignInput1AndInput1.endPin = AndGateChipLogic.OPERAND_1_PIN;
//        nandDesignInput1AndInput1.endChip = nandAnd;
//        Wire nandDesignInput2AndInput2 = new Wire();
//        nandDesignInput2AndInput2.vertices = new Vertex[] {new Vertex(0, 5), new Vertex(4, 5)};
//        nandDesignInput2AndInput2.startPin = nandDesignInput2;
//        nandDesignInput2AndInput2.endPin = AndGateChipLogic.OPERAND_2_PIN;
//        nandDesignInput2AndInput2.endChip = nandAnd;
//        Wire nandAndOutputNotInput = new Wire();
//        nandAndOutputNotInput.vertices = new Vertex[] {new Vertex(10, 4), new Vertex(14, 4)};
//        nandAndOutputNotInput.startPin = AndGateChipLogic.RESULT_PIN;
//        nandAndOutputNotInput.startChip = nandAnd;
//        nandAndOutputNotInput.endPin = NotGateChipLogic.OPERAND_PIN;
//        nandAndOutputNotInput.endChip = nandNot;
//        Wire nandNotOutputDesignOutput = new Wire();
//        nandNotOutputDesignOutput.vertices = new Vertex[] {new Vertex(20, 4), new Vertex(24, 4)};
//        nandNotOutputDesignOutput.startPin = NotGateChipLogic.RESULT_PIN;
//        nandNotOutputDesignOutput.startChip = nandNot;
//        nandNotOutputDesignOutput.endPin = nandDesignOutput;
//        nandDesign.getWires().addAll(Arrays.asList(nandDesignInput1AndInput1, nandDesignInput2AndInput2, nandAndOutputNotInput, nandNotOutputDesignOutput));
//
//        DesignedChipTemplate nandGateTemplate = new DesignedChipTemplate();
//        nandGateTemplate.id = "nand";
//        nandGateTemplate.name = "NAND";
//        nandGateTemplate.width = 6;
//        nandGateTemplate.height = 4;
//        nandGateTemplate.design = nandDesign;

        // +------------------------+
        // | TEST ORIENTATION SETUP |
        // +------------------------+

//        Design testDesign = new Design();
//        testDesign.width = 24;
//        testDesign.height = 14;
//
//        Pin testDesignInput1 = new Pin();
//        testDesignInput1.designX = 0;
//        testDesignInput1.designY = 3;
//        Pin testDesignInput2 = new Pin();
//        testDesignInput2.designX = 0;
//        testDesignInput2.designY = 5;
//        Pin testDesignInput3 = new Pin();
//        testDesignInput3.designX = 0;
//        testDesignInput3.designY = 9;
//        Pin testDesignInput4 = new Pin();
//        testDesignInput4.designX = 0;
//        testDesignInput4.designY = 11;
//        Pin testDesignOutput = new Pin();
//        testDesignOutput.designX = 24;
//        testDesignOutput.designY = 7;
//        testDesign.getPins().addAll(Arrays.asList(testDesignInput1, testDesignInput2, testDesignInput3, testDesignInput4, testDesignOutput));
//
//        Chip testNand1 = new Chip();
//        testNand1.chipTemplate = nandGateTemplate;
//        testNand1.x = 4;
//        testNand1.y = 2;
//        Chip testNand2 = new Chip();
//        testNand2.chipTemplate = nandGateTemplate;
//        testNand2.x = 4;
//        testNand2.y = 8;
//        Chip testAnd1 = new Chip();
//        testAnd1.chipTemplate = BuiltinChipTemplate.getAndGateTemplate();
//        testAnd1.x = 14;
//        testAnd1.y = 5;
//        testDesign.getChips().addAll(Arrays.asList(testNand1, testNand2, testAnd1));
//
//        Wire testDesignInput1Nand1Input1 = new Wire();
//        testDesignInput1Nand1Input1.vertices = new Vertex[] {new Vertex(0, 3), new Vertex(4, 3)};
//        testDesignInput1Nand1Input1.startPin = testDesignInput1;
//        testDesignInput1Nand1Input1.endPin = nandDesignInput1;
//        testDesignInput1Nand1Input1.endChip = testNand1;
//        Wire testDesignInput2Nand1Input2 = new Wire();
//        testDesignInput2Nand1Input2.vertices = new Vertex[] {new Vertex(0, 5), new Vertex(4, 5)};
//        testDesignInput2Nand1Input2.startPin = testDesignInput2;
//        testDesignInput2Nand1Input2.endPin = nandDesignInput2;
//        testDesignInput2Nand1Input2.endChip = testNand1;
//        Wire testDesignInput3Nand2Input1 = new Wire();
//        testDesignInput3Nand2Input1.vertices = new Vertex[] {new Vertex(0, 9), new Vertex(4, 9)};
//        testDesignInput3Nand2Input1.startPin = testDesignInput3;
//        testDesignInput3Nand2Input1.endPin = nandDesignInput1;
//        testDesignInput3Nand2Input1.endChip = testNand2;
//        Wire testDesignInput4Nand2Input2 = new Wire();
//        testDesignInput4Nand2Input2.vertices = new Vertex[] {new Vertex(0, 11), new Vertex(4, 11)};
//        testDesignInput4Nand2Input2.startPin = testDesignInput4;
//        testDesignInput4Nand2Input2.endPin = nandDesignInput2;
//        testDesignInput4Nand2Input2.endChip = testNand2;
//        Wire testNand1OutputAndInput1 = new Wire();
//        testNand1OutputAndInput1.vertices = new Vertex[] {new Vertex(10, 4), new Vertex(12, 4), new Vertex(12, 6), new Vertex(14, 6)};
//        testNand1OutputAndInput1.startPin = nandDesignOutput;
//        testNand1OutputAndInput1.startChip = testNand1;
//        testNand1OutputAndInput1.endPin = AndGateChipLogic.OPERAND_1_PIN;
//        testNand1OutputAndInput1.endChip = testAnd1;
//        Wire testNand2OutputAndInput2 = new Wire();
//        testNand2OutputAndInput2.vertices = new Vertex[] {new Vertex(10, 10), new Vertex(12, 10), new Vertex(12, 8), new Vertex(14, 8)};
//        testNand2OutputAndInput2.startPin = nandDesignOutput;
//        testNand2OutputAndInput2.startChip = testNand2;
//        testNand2OutputAndInput2.endPin = AndGateChipLogic.OPERAND_2_PIN;
//        testNand2OutputAndInput2.endChip = testAnd1;
//        Wire testAndOutputDesignOutput = new Wire();
//        testAndOutputDesignOutput.vertices = new Vertex[] {new Vertex(20, 7), new Vertex(24, 7)};
//        testAndOutputDesignOutput.startPin = AndGateChipLogic.RESULT_PIN;
//        testAndOutputDesignOutput.startChip = testAnd1;
//        testAndOutputDesignOutput.endPin = testDesignOutput;
//        testDesign.getWires().addAll(Arrays.asList(testDesignInput1Nand1Input1, testDesignInput2Nand1Input2, testDesignInput3Nand2Input1, testDesignInput4Nand2Input2, testNand1OutputAndInput1, testNand2OutputAndInput2, testAndOutputDesignOutput));

//        Design testDesign = new Design();
//        testDesign.width = 14;
//        testDesign.height = 15;
//
//        Pin testDesignInput1 = new Pin();
//        testDesignInput1.designX = 0;
//        testDesignInput1.designY = 3;
//        Pin testDesignInput2 = new Pin();
//        testDesignInput2.designX = 0;
//        testDesignInput2.designY = 12;
//        Pin testDesignOutput1 = new Pin();
//        testDesignOutput1.designX = 14;
//        testDesignOutput1.designY = 4;
//        Pin testDesignOutput2 = new Pin();
//        testDesignOutput2.designX = 14;
//        testDesignOutput2.designY = 11;
//        testDesign.getPins().addAll(Arrays.asList(testDesignInput1, testDesignInput2, testDesignOutput1, testDesignOutput2));
//
//        Chip testNand1 = new Chip();
//        testNand1.chipTemplate = nandGateTemplate;
//        testNand1.x = 4;
//        testNand1.y = 2;
//        Chip testNand2 = new Chip();
//        testNand2.chipTemplate = nandGateTemplate;
//        testNand2.x = 4;
//        testNand2.y = 9;
//        testDesign.getChips().addAll(Arrays.asList(testNand1, testNand2));
//
//        Wire testDesignInput1Nand1Input1 = new Wire();
//        testDesignInput1Nand1Input1.vertices = new Vertex[] {new Vertex(0, 3), new Vertex(4, 3)};
//        testDesignInput1Nand1Input1.startPin = testDesignInput1;
//        testDesignInput1Nand1Input1.endPin = nandDesignInput1;
//        testDesignInput1Nand1Input1.endChip = testNand1;
//        Wire testDesignInput2Nand2Input2 = new Wire();
//        testDesignInput2Nand2Input2.vertices = new Vertex[] {new Vertex(0, 12), new Vertex(4, 12)};
//        testDesignInput2Nand2Input2.startPin = testDesignInput2;
//        testDesignInput2Nand2Input2.endPin = nandDesignInput2;
//        testDesignInput2Nand2Input2.endChip = testNand2;
//        Wire testDesignNand1OutputNand2Input1 = new Wire();
//        testDesignNand1OutputNand2Input1.vertices = new Vertex[] {new Vertex(10, 4), new Vertex(12, 4), new Vertex(12, 8), new Vertex(2, 8), new Vertex(2, 10), new Vertex(4, 10)};
//        testDesignNand1OutputNand2Input1.startPin = nandDesignOutput;
//        testDesignNand1OutputNand2Input1.startChip = testNand1;
//        testDesignNand1OutputNand2Input1.endPin = nandDesignInput1;
//        testDesignNand1OutputNand2Input1.endChip = testNand2;
//        Wire testDesignNand2OutputNand1Input2 = new Wire();
//        testDesignNand2OutputNand1Input2.vertices = new Vertex[] {new Vertex(10, 11), new Vertex(11, 11), new Vertex(11, 7), new Vertex(3, 7), new Vertex(3, 5), new Vertex(4, 5)};
//        testDesignNand2OutputNand1Input2.startPin = nandDesignOutput;
//        testDesignNand2OutputNand1Input2.startChip = testNand2;
//        testDesignNand2OutputNand1Input2.endPin = nandDesignInput2;
//        testDesignNand2OutputNand1Input2.endChip = testNand1;
//        Wire testNand1OutputDesignOutput1 = new Wire();
//        testNand1OutputDesignOutput1.vertices = new Vertex[] {new Vertex(10, 4), new Vertex(14, 4)};
//        testNand1OutputDesignOutput1.startPin = nandDesignOutput;
//        testNand1OutputDesignOutput1.startChip = testNand1;
//        testNand1OutputDesignOutput1.endPin = testDesignOutput1;
//        Wire testNand2OutputDesignOutput2 = new Wire();
//        testNand2OutputDesignOutput2.vertices = new Vertex[] {new Vertex(10, 11), new Vertex(14, 11)};
//        testNand2OutputDesignOutput2.startPin = nandDesignOutput;
//        testNand2OutputDesignOutput2.startChip = testNand2;
//        testNand2OutputDesignOutput2.endPin = testDesignOutput2;
//        testDesign.getWires().addAll(Arrays.asList(testDesignInput1Nand1Input1, testDesignInput2Nand2Input2, testDesignNand1OutputNand2Input1, testDesignNand2OutputNand1Input2, testNand1OutputDesignOutput1, testNand2OutputDesignOutput2));
//
//        DesignInstance designInstance = MeshGenerator.generateMeshForDesign(testDesign);
//        DesignInstance nand1DesignInstance = designInstance.getDesignInstanceForChip(testNand1);
//        DesignInstance nand2DesignInstance = designInstance.getDesignInstanceForChip(testNand2);

        Design testDesign = new Design();
        testDesign.setWidth(20);
        testDesign.setHeight(38);

        Pin reset = new Pin();
        reset.setDesignX(2);
        reset.setDesignY(9);
        Pin set = new Pin();
        set.setDesignX(2);
        set.setDesignY(31);
        testDesign.getPins().addAll(Arrays.asList(reset, set));

        Chip transistorRC2 = new Chip();
        transistorRC2.setChipTemplate(TransistorTemplate.getNPNTransistorTemplate());
        transistorRC2.setX(10);
        transistorRC2.setY(6);
        Chip transistorRC1 = new Chip();
        transistorRC1.setChipTemplate(TransistorTemplate.getNPNTransistorTemplate());
        transistorRC1.setX(10);
        transistorRC1.setY(12);

        Chip transistorSC1 = new Chip();
        transistorSC1.setChipTemplate(TransistorTemplate.getNPNTransistorTemplate());
        transistorSC1.setX(10);
        transistorSC1.setY(22);
        Chip transistorSC2 = new Chip();
        transistorSC2.setChipTemplate(TransistorTemplate.getNPNTransistorTemplate());
        transistorSC2.setX(10);
        transistorSC2.setY(28);

        Chip signalDriverR1 = new Chip();
        signalDriverR1.setChipTemplate(SignalSourceTemplate.getDrivenLowTemplate());
        signalDriverR1.setX(2);
        signalDriverR1.setY(2);
        Chip signalDriverS1 = new Chip();
        signalDriverS1.setChipTemplate(SignalSourceTemplate.getDrivenLowTemplate());
        signalDriverS1.setX(2);
        signalDriverS1.setY(34);

        Chip pulledR2 = new Chip();
        pulledR2.setChipTemplate(SignalSourceTemplate.getPulledHighTemplate());
        pulledR2.setX(12);
        pulledR2.setY(2);
        Chip pulledS2 = new Chip();
        pulledS2.setChipTemplate(SignalSourceTemplate.getPulledHighTemplate());
        pulledS2.setX(12);
        pulledS2.setY(34);

        Wire resetRC1B = new Wire();
        resetRC1B.setStartPin(reset);
        resetRC1B.setEndPin(transistorRC2, TransistorTemplate.NPN_BASE);
        resetRC1B.setVertices(new Vertex[] {new Vertex(2, 9), new Vertex(10, 9)});
        Wire setSC1B = new Wire();
        setSC1B.setStartPin(set);
        setSC1B.setEndPin(transistorSC2, TransistorTemplate.NPN_BASE);
        setSC1B.setVertices(new Vertex[] {new Vertex(2, 31), new Vertex(10, 31)});

        Wire driverR1RC2C = new Wire();
        driverR1RC2C.setStartPin(signalDriverR1, SignalSourceTemplate.OUTPUT);
        driverR1RC2C.setEndPin(transistorRC2, TransistorTemplate.NPN_COLLECTOR);
        driverR1RC2C.setVertices(new Vertex[] {new Vertex(6, 3), new Vertex(8, 3), new Vertex(8, 7), new Vertex(10, 7)});
        Wire driverR1RC1C = new Wire();
        driverR1RC1C.setStartPin(signalDriverR1, SignalSourceTemplate.OUTPUT);
        driverR1RC1C.setEndPin(transistorRC1, TransistorTemplate.NPN_COLLECTOR);
        driverR1RC1C.setVertices(new Vertex[] {new Vertex(6, 3), new Vertex(8, 3), new Vertex(8, 13), new Vertex(10, 13)});
        Wire driverS1SC1C = new Wire();
        driverS1SC1C.setStartPin(signalDriverS1, SignalSourceTemplate.OUTPUT);
        driverS1SC1C.setEndPin(transistorSC1, TransistorTemplate.NPN_COLLECTOR);
        driverS1SC1C.setVertices(new Vertex[] {new Vertex(6, 35), new Vertex(8, 35), new Vertex(8, 23), new Vertex(10, 23)});
        Wire driverS1SC2C = new Wire();
        driverS1SC2C.setStartPin(signalDriverS1, SignalSourceTemplate.OUTPUT);
        driverS1SC2C.setEndPin(transistorSC2, TransistorTemplate.NPN_COLLECTOR);
        driverS1SC2C.setVertices(new Vertex[] {new Vertex(6, 35), new Vertex(8, 35), new Vertex(8, 29), new Vertex(10, 29)});

        Wire pulledR2RC2E = new Wire();
        pulledR2RC2E.setStartPin(pulledR2, SignalSourceTemplate.OUTPUT);
        pulledR2RC2E.setEndPin(transistorRC2, TransistorTemplate.NPN_EMITTER);
        pulledR2RC2E.setVertices(new Vertex[] {new Vertex(16, 3), new Vertex(18, 3), new Vertex(18, 8), new Vertex(16, 8)});
        Wire pulledR2RC1E = new Wire();
        pulledR2RC1E.setStartPin(pulledR2, SignalSourceTemplate.OUTPUT);
        pulledR2RC1E.setEndPin(transistorRC1, TransistorTemplate.NPN_EMITTER);
        pulledR2RC1E.setVertices(new Vertex[] {new Vertex(16, 3), new Vertex(18, 3), new Vertex(18, 14), new Vertex(16, 14)});
        Wire pulledR2SC1B = new Wire();
        pulledR2SC1B.setStartPin(pulledR2, SignalSourceTemplate.OUTPUT);
        pulledR2SC1B.setEndPin(transistorSC1, TransistorTemplate.NPN_BASE);
        pulledR2SC1B.setVertices(new Vertex[] {new Vertex(16, 3), new Vertex(18, 3), new Vertex(18, 18), new Vertex(6, 18), new Vertex(6, 25), new Vertex(10, 25)});

        Wire pulledS2RC1B = new Wire();
        pulledS2RC1B.setStartPin(pulledS2, SignalSourceTemplate.OUTPUT);
        pulledS2RC1B.setEndPin(transistorRC1, TransistorTemplate.NPN_BASE);
        pulledS2RC1B.setVertices(new Vertex[] {new Vertex(16, 35), new Vertex(18, 35), new Vertex(18, 20), new Vertex(7, 20), new Vertex(7, 15), new Vertex(10, 15)});
        Wire pulledS2SC1E = new Wire();
        pulledS2SC1E.setStartPin(pulledS2, SignalSourceTemplate.OUTPUT);
        pulledS2SC1E.setEndPin(transistorSC1, TransistorTemplate.NPN_EMITTER);
        pulledS2SC1E.setVertices(new Vertex[] {new Vertex(16, 35), new Vertex(18, 35), new Vertex(18, 24), new Vertex(16, 24)});
        Wire pulledS2SC2E = new Wire();
        pulledS2SC2E.setStartPin(pulledS2, SignalSourceTemplate.OUTPUT);
        pulledS2SC2E.setEndPin(transistorSC2, TransistorTemplate.NPN_EMITTER);
        pulledS2SC2E.setVertices(new Vertex[] {new Vertex(16, 35), new Vertex(18, 35), new Vertex(18, 30), new Vertex(16, 30)});

        testDesign.addChips(Arrays.asList(
                transistorRC2, transistorRC1, transistorSC1, transistorSC2,
                signalDriverR1, signalDriverS1, pulledR2, pulledS2
        ));
        testDesign.addWires(Arrays.asList(
                resetRC1B, setSC1B,
                driverR1RC1C, driverR1RC2C, driverS1SC1C, driverS1SC2C,
                pulledR2RC1E, pulledR2RC2E, pulledR2SC1B,
                pulledS2SC1E, pulledS2SC2E, pulledS2RC1B
        ));

        Simulation.setSimulator(new MeshSimulator());
        Simulation.start();
        SimulatedDesign simulatedDesign = Simulation.initialize(testDesign);

//        MeshVertex vertexInR = new MeshVertex();
//        MeshVertex vertexInS = new MeshVertex();
//        MeshVertex vertexR1 = new MeshVertex();
//        MeshVertex vertexS1 = new MeshVertex();
//        MeshVertex vertexR2 = new MeshVertex();
//        MeshVertex vertexS2 = new MeshVertex();
//
//        MeshConnector connectorRC1 = new MeshConnector(vertexS2, false);
//        MeshEdge edgeRC1 = new MeshEdge(vertexR2, connectorRC1);
//        MeshConnector connectorRC2 = new MeshConnector(vertexInR, false);
//        MeshEdge edgeRC2 = new MeshEdge(vertexR2, connectorRC2);
//        vertexR1.setOutgoingEdges(Set.of(edgeRC1, edgeRC2));
//
//        MeshConnector connectorSC1 = new MeshConnector(vertexR2, false);
//        MeshEdge edgeSC1 = new MeshEdge(vertexS2, connectorSC1);
//        MeshConnector connectorSC2 = new MeshConnector(vertexInS, false);
//        MeshEdge edgeSC2 = new MeshEdge(vertexS2, connectorSC2);
//        vertexS1.setOutgoingEdges(Set.of(edgeSC1, edgeSC2));
//
//        resetDriver = new MeshDriver(vertexInR, LogicState.LOW, LogicState.UNKNOWN);
//        setDriver = new MeshDriver(vertexInS, LogicState.LOW, LogicState.UNKNOWN);
//        MeshDriver driverR1 = new MeshDriver(vertexR1, LogicState.LOW, LogicState.UNKNOWN);
//        MeshDriver driverS1 = new MeshDriver(vertexS1, LogicState.LOW, LogicState.UNKNOWN);
//        MeshDriver driverRD = new MeshDriver(vertexR2, LogicState.UNKNOWN, LogicState.HIGH);
//        MeshDriver driverSD = new MeshDriver(vertexS2, LogicState.UNKNOWN, LogicState.HIGH);
//
//        coordinator = new MeshSimulationCoordinator();
//        coordinator.start(new MeshDriver[] {
//                resetDriver,
//                setDriver,
//                driverR1,
//                driverS1,
//                driverRD,
//                driverSD
//        }, new MeshVertex[] {
//                vertexInR,
//                vertexInS,
//                vertexR1,
//                vertexR2,
//                vertexS1,
//                vertexS2
//        }, new MeshConnector[] {
//                connectorRC1,
//                connectorRC2,
//                connectorSC1,
//                connectorSC2
//        });
//
//        simulatedDesign = new MeshSimulatedDesign();
//
//        simulatedDesign.pinMapping.put(reset, vertexInR);
//        simulatedDesign.pinMapping.put(set, vertexInS);
//        simulatedDesign.wireMapping.put(resetRC1B, vertexInR);
//        simulatedDesign.wireMapping.put(setSC1B, vertexInS);
//
//        simulatedDesign.wireMapping.put(pulledR2RC2E, vertexR2);
//        simulatedDesign.wireMapping.put(pulledR2RC1E, vertexR2);
//        simulatedDesign.wireMapping.put(pulledR2SC1B, vertexR2);
//
//        simulatedDesign.wireMapping.put(pulledS2RC1B, vertexS2);
//        simulatedDesign.wireMapping.put(pulledS2SC1E, vertexS2);
//        simulatedDesign.wireMapping.put(pulledS2SC2E, vertexS2);
//
//        simulatedDesign.wireMapping.put(driverR1RC2C, vertexR1);
//        simulatedDesign.wireMapping.put(driverR1RC1C, vertexR1);
//
//        simulatedDesign.wireMapping.put(driverS1SC1C, vertexS1);
//        simulatedDesign.wireMapping.put(driverS1SC2C, vertexS1);
//
//        MeshSimulatedDesign driverR1SD = new MeshSimulatedDesign();
//        driverR1SD.pinMapping.put(SignalSourceTemplate.OUTPUT, vertexR1);
//        simulatedDesign.chipMapping.put(signalDriverR1, driverR1SD);
//
//        MeshSimulatedDesign driverS1SD = new MeshSimulatedDesign();
//        driverS1SD.pinMapping.put(SignalSourceTemplate.OUTPUT, vertexS1);
//        simulatedDesign.chipMapping.put(signalDriverS1, driverS1SD);
//
//        MeshSimulatedDesign pulledR2SD = new MeshSimulatedDesign();
//        pulledR2SD.pinMapping.put(SignalSourceTemplate.OUTPUT, vertexR2);
//        simulatedDesign.chipMapping.put(pulledR2, pulledR2SD);
//
//        MeshSimulatedDesign pulledS2SD = new MeshSimulatedDesign();
//        pulledS2SD.pinMapping.put(SignalSourceTemplate.OUTPUT, vertexS2);
//        simulatedDesign.chipMapping.put(pulledS2, pulledS2SD);
//
//        MeshSimulatedDesign transistorRC2SD = new MeshSimulatedDesign();
//        transistorRC2SD.pinMapping.put(TransistorTemplate.NPN_COLLECTOR, vertexR1);
//        transistorRC2SD.pinMapping.put(TransistorTemplate.NPN_BASE, vertexInR);
//        transistorRC2SD.pinMapping.put(TransistorTemplate.NPN_EMITTER, vertexR2);
//        simulatedDesign.chipMapping.put(transistorRC2, transistorRC2SD);
//
//        MeshSimulatedDesign transistorRC1SD = new MeshSimulatedDesign();
//        transistorRC1SD.pinMapping.put(TransistorTemplate.NPN_COLLECTOR, vertexR1);
//        transistorRC1SD.pinMapping.put(TransistorTemplate.NPN_BASE, vertexS2);
//        transistorRC1SD.pinMapping.put(TransistorTemplate.NPN_EMITTER, vertexR2);
//        simulatedDesign.chipMapping.put(transistorRC1, transistorRC1SD);
//
//        MeshSimulatedDesign transistorSC1SD = new MeshSimulatedDesign();
//        transistorSC1SD.pinMapping.put(TransistorTemplate.NPN_COLLECTOR, vertexS1);
//        transistorSC1SD.pinMapping.put(TransistorTemplate.NPN_BASE, vertexR2);
//        transistorSC1SD.pinMapping.put(TransistorTemplate.NPN_EMITTER, vertexS2);
//        simulatedDesign.chipMapping.put(transistorSC1, transistorSC1SD);
//
//        MeshSimulatedDesign transistorSC2SD = new MeshSimulatedDesign();
//        transistorSC2SD.pinMapping.put(TransistorTemplate.NPN_COLLECTOR, vertexS1);
//        transistorSC2SD.pinMapping.put(TransistorTemplate.NPN_BASE, vertexInS);
//        transistorSC2SD.pinMapping.put(TransistorTemplate.NPN_EMITTER, vertexS2);
//        simulatedDesign.chipMapping.put(transistorSC2, transistorSC2SD);

        System.out.println("Transistors: " + testDesign.getTransistorCount());

        UIToolbar toolbar = new UIToolbar(Direction.HORIZONTAL);
        toolbar.add(new UIButton("T1", () -> {}));
        toolbar.add(new UIButton("T2", () -> {}));
        toolbar.add(new UIButton("T3", () -> {}));
        toolbar.addSeparator();
        toolbar.add(new UIButton("T4", () -> {}));
        toolbar.add(new UIButton("T5", () -> {}));
        toolbar.addSeparator();
        toolbar.add(new UIStartStopButton(WorkerScheduler::startSimulation, WorkerScheduler::stopSimulation));
        toolbar.add(new UISpinner<>(UISpinner.getIntegerHandler(), "ticks/sec", 1, 1000, 1, WorkerScheduler::setTargetTicksPerSecond));
        UIButton simSettings = new UIButton("\uE8B8", () -> {});
        simSettings.setFont(UITheme.getFont(ThemeKeys.Fonts.UI.SPINNER_BUTTON_DEFAULT));
        simSettings.setPadding(simSettings.getPadding()); // TODO set to 8 on X
        toolbar.add(simSettings);
        UIButton restart = new UIButton("\uE5D5", Simulation::reset);
        restart.setFont(UITheme.getFont(ThemeKeys.Fonts.UI.SPINNER_BUTTON_DEFAULT));
        restart.setPadding(simSettings.getPadding()); // TODO set to 8 on X
        toolbar.add(restart);
        toolbar.addFlexSpace();
        UITextField textField = new UITextField("Search...", (s) -> {});
        textField.setText("Hello!");
        toolbar.add(textField);
        toolbar.add(new UIButton("T6", () -> {}));
        toolbar.addSeparator();
        toolbar.add(new UIButton("T7", () -> {}));

        UIToolbar bottomToolbar = new UIToolbar(Direction.HORIZONTAL, 2);
        UIText text = new UIText("Status: Testing");
        bottomToolbar.add(text);

        UIFloatingContainer floatingContainer = new UIFloatingContainer();
        //floatingContainer.addComponent(new UITextField("Search components...", (s) -> {}), Anchor.TOP_LEFT, new Padding(10));
        floatingContainer.setBackgroundComponent(new DesignEditor(testDesign, simulatedDesign));

        UIFlexContainer testFlexContainer = new UIFlexContainer(Direction.VERTICAL);
        for (int i = 0; i < 4; i++)
            testFlexContainer.addComponent(new UITextField(Integer.toString(i), (s) -> {}), 0f);

        UIScroll scroll = new UIScroll(floatingContainer);

        UIFlexContainer innerFlexContainer = new UIFlexContainer(Direction.VERTICAL);
        innerFlexContainer.addComponent(new UIBorder(toolbar, UIBorder.BorderLayout.BOTTOM), 0f);
        innerFlexContainer.addComponent(floatingContainer, 1f);
//        innerFlexContainer.addComponent(new DesignRenderer(nandDesign, null), 0.5f);
//        innerFlexContainer.addComponent(new DesignRenderer(nandDesign, nand2DesignInstance), 0f);
        innerFlexContainer.addComponent(new UIBorder(bottomToolbar, UIBorder.BorderLayout.TOP), 0f);

        UIFlexContainer flexContainer = new UIFlexContainer(Direction.HORIZONTAL);
        flexContainer.setPreferredSize(new UIDimension(1200, 1200));
        flexContainer.addComponent(new UIButton("Left", () -> {}), 0f);
        flexContainer.addComponent(innerFlexContainer, 1f);
        flexContainer.addComponent(new UIButton("Right", () -> {}), 0f);

//        Worker input1 = new Worker(true, designInstance.getNodeForPin(testDesignInput1));
//        Worker input2 = new Worker(true, designInstance.getNodeForPin(testDesignInput2));

//        WorkerScheduler.setTopLevelDesignInstance(designInstance);
//        WorkerScheduler.queue(input1);
//        WorkerScheduler.queue(input2);

        UIWindow window = new UIWindow("Breadboard");
        window.setContent(flexContainer);
        window.setVisible(true);
    }
}
