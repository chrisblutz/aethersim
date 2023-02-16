package com.github.chrisblutz.breadboard.ui.window;

import com.github.chrisblutz.breadboard.designs.*;
import com.github.chrisblutz.breadboard.designs.templates.*;
import com.github.chrisblutz.breadboard.designs.wires.WireNode;
import com.github.chrisblutz.breadboard.designs.wires.WireSegment;
import com.github.chrisblutz.breadboard.simulation.SimulatedDesign;
import com.github.chrisblutz.breadboard.simulation.Simulation;
import com.github.chrisblutz.breadboard.simulation.mesh.MeshSimulator;
import com.github.chrisblutz.breadboard.simulation.mesh.mesh.MeshSimulatedDesign;
import com.github.chrisblutz.breadboard.simulation.mesh.threading.MeshSimulationCoordinator;
import com.github.chrisblutz.breadboard.ui.render.designs.DesignEditor;
import com.github.chrisblutz.breadboard.ui.toolkit.UITheme;
import com.github.chrisblutz.breadboard.ui.toolkit.UIWindow;
import com.github.chrisblutz.breadboard.ui.toolkit.builtin.containers.*;
import com.github.chrisblutz.breadboard.ui.toolkit.builtin.input.UIButton;
import com.github.chrisblutz.breadboard.ui.toolkit.builtin.input.UITextField;
import com.github.chrisblutz.breadboard.ui.toolkit.builtin.text.UIText;
import com.github.chrisblutz.breadboard.ui.toolkit.display.theming.ThemeKeys;
import com.github.chrisblutz.breadboard.ui.toolkit.layout.Direction;
import com.github.chrisblutz.breadboard.ui.toolkit.layout.UIDimension;
import com.github.chrisblutz.breadboard.designs.Vertex;

import javax.swing.*;

public class BreadboardWindow {

    private static JFrame frame;
    private static BreadboardCanvas canvas;

    // TODO
    public static MeshSimulationCoordinator coordinator;
    public static MeshSimulatedDesign simulatedDesign;

    public static void initializeWindow() {

        // +----------------------+
        // |    NOR Gate Setup    |
        // +----------------------+

        Design norDesign = new Design();
        norDesign.setWidth(20);
        norDesign.setHeight(16);

        Pin norInput1 = new Pin();
        norInput1.setId("input_1");
        norInput1.setName("Input");
        norInput1.setChipLocation(new Vertex(0, 1));
        norInput1.setDesignLocation(new Vertex(0, 8));

        Pin norInput2 = new Pin();
        norInput2.setId("input_2");
        norInput2.setName("Input");
        norInput2.setChipLocation(new Vertex(0, 3));
        norInput2.setDesignLocation(new Vertex(0, 14));

        Pin norOutput = new Pin();
        norOutput.setId("output");
        norOutput.setName("Output");
        norOutput.setChipLocation(new Vertex(6, 2));
        norOutput.setDesignLocation(new Vertex(20, 10));

        Chip norTransistor1 = new Chip();
        norTransistor1.setChipTemplate(TransistorTemplate.getNPNTransistorTemplate());
        norTransistor1.setLocation(new Vertex(9, 5));

        Chip norTransistor2 = new Chip();
        norTransistor2.setChipTemplate(TransistorTemplate.getNPNTransistorTemplate());
        norTransistor2.setLocation(new Vertex(9, 11));

        Chip norDrivenLow = new Chip();
        norDrivenLow.setChipTemplate(ConstantTemplate.getDrivenLowTemplate());
        norDrivenLow.setLocation(new Vertex(1, 1));

        Chip norPulledHigh = new Chip();
        norPulledHigh.setChipTemplate(ConstantTemplate.getPulledHighTemplate());
        norPulledHigh.setLocation(new Vertex(11, 1));

        Wire norInput1Wire = new Wire();
        WireSegment norInput1WireSegment = new WireSegment(new Vertex(0, 8), new Vertex(9, 8));
        norInput1WireSegment.addEndpoint(new ChipPin(null, norInput1));
        norInput1WireSegment.addEndpoint(new ChipPin(norTransistor1, TransistorTemplate.NPN_BASE));
        norInput1Wire.addSegment(norInput1WireSegment);

        Wire norInput2Wire = new Wire();
        WireSegment norInput2WireSegment = new WireSegment(new Vertex(0, 14), new Vertex(9, 14));
        norInput2WireSegment.addEndpoint(new ChipPin(null, norInput2));
        norInput2WireSegment.addEndpoint(new ChipPin(norTransistor2, TransistorTemplate.NPN_BASE));
        norInput2Wire.addSegment(norInput2WireSegment);

        Wire norDrivenLowWire = new Wire();
        WireNode norDrivenLowWireNode = new WireNode(new Vertex(7, 6));
        WireSegment norDrivenLowWireSegment1 = new WireSegment(
                new ChipPin(norDrivenLow, ConstantTemplate.OUTPUT),
                norDrivenLowWireNode,
                new Vertex(5, 2), new Vertex(7, 2), new Vertex(7, 6)
        );
        WireSegment norDrivenLowWireSegment2 = new WireSegment(
                norDrivenLowWireNode,
                new ChipPin(norTransistor1, TransistorTemplate.NPN_COLLECTOR),
                new Vertex(7, 6), new Vertex(9, 6)
        );
        WireSegment norDrivenLowWireSegment3 = new WireSegment(
                norDrivenLowWireNode,
                new ChipPin(norTransistor2, TransistorTemplate.NPN_COLLECTOR),
                new Vertex(7, 6), new Vertex(7, 12), new Vertex(9, 12)
        );
        norDrivenLowWire.addSegments(
                norDrivenLowWireSegment1, norDrivenLowWireSegment2, norDrivenLowWireSegment3
        );

        Wire norPulledHighWire = new Wire();
        WireNode norPulledHighWireT1Node = new WireNode(new Vertex(17, 7));
        WireNode norPulledHighWireOutputNode = new WireNode(new Vertex(17, 10));
        WireSegment norPulledHighWireSegment1 = new WireSegment(
                new ChipPin(norPulledHigh, ConstantTemplate.OUTPUT),
                norPulledHighWireT1Node,
                new Vertex(15, 2), new Vertex(17, 2), new Vertex(17, 7)
        );
        WireSegment norPulledHighWireSegment2 = new WireSegment(
                norPulledHighWireT1Node,
                new ChipPin(norTransistor1, TransistorTemplate.NPN_EMITTER),
                new Vertex(15, 7), new Vertex(17, 7)
        );
        WireSegment norPulledHighWireSegment3 = new WireSegment(
                norPulledHighWireT1Node,
                norPulledHighWireOutputNode,
                new Vertex(17, 7), new Vertex(17, 10)
        );
        WireSegment norPulledHighWireSegment4 = new WireSegment(
                norPulledHighWireOutputNode,
                new ChipPin(null, norOutput),
                new Vertex(17, 10), new Vertex(20, 10)
        );
        WireSegment norPulledHighWireSegment5 = new WireSegment(
                norPulledHighWireOutputNode,
                new ChipPin(norTransistor2, TransistorTemplate.NPN_EMITTER),
                new Vertex(17, 10), new Vertex(17, 13), new Vertex(15, 13)
        );
        norPulledHighWire.addSegments(
                norPulledHighWireSegment1, norPulledHighWireSegment2, norPulledHighWireSegment3,
                norPulledHighWireSegment4, norPulledHighWireSegment5
        );

        norDesign.addPins(
                norInput1, norInput2, norOutput
        );
        norDesign.addChips(
                norTransistor1, norTransistor2,
                norDrivenLow, norPulledHigh
        );
        norDesign.addWires(
                norInput1Wire, norInput2Wire,
                norDrivenLowWire, norPulledHighWire
        );

        norDesign.recalculateOpenDistances();

        ChipTemplate norTemplate = new DesignedTemplate(norDesign);
        norTemplate.setId("nor");
        norTemplate.setName("NOR");
        norTemplate.setWidth(6);
        norTemplate.setHeight(4);

        // +----------------------+
        // |    SR Latch Setup    |
        // +----------------------+

        Design srLatchDesign = new Design();
        srLatchDesign.setWidth(15);
        srLatchDesign.setHeight(13);

        Pin srLatchReset = new Pin();
        srLatchReset.setId("reset");
        srLatchReset.setName("Reset");
        srLatchReset.setChipLocation(new Vertex(0, 1));
        srLatchReset.setDesignLocation(new Vertex(0, 2));

        Pin srLatchSet = new Pin();
        srLatchSet.setId("set");
        srLatchSet.setName("Set");
        srLatchSet.setChipLocation(new Vertex(0, 3));
        srLatchSet.setDesignLocation(new Vertex(0, 11));

        Pin srLatchOutput = new Pin();
        srLatchOutput.setId("output");
        srLatchOutput.setName("Output");
        srLatchOutput.setChipLocation(new Vertex(6, 1));
        srLatchOutput.setDesignLocation(new Vertex(15, 3));

        Pin srLatchInverseOutput = new Pin();
        srLatchInverseOutput.setId("inverse_output");
        srLatchInverseOutput.setName("Inverse Output");
        srLatchInverseOutput.setChipLocation(new Vertex(6, 3));
        srLatchInverseOutput.setDesignLocation(new Vertex(15, 10));

        Chip srLatchNor1 = new Chip();
        srLatchNor1.setChipTemplate(norTemplate);
        srLatchNor1.setLocation(new Vertex(4, 1));

        Chip srLatchNor2 = new Chip();
        srLatchNor2.setChipTemplate(norTemplate);
        srLatchNor2.setLocation(new Vertex(4, 8));

        Wire srLatchResetNor1Wire = new Wire();
        WireSegment srLatchResetNor1WireSegment = new WireSegment(
                new ChipPin(null, srLatchReset),
                new ChipPin(srLatchNor1, norInput1),
                new Vertex(0, 2), new Vertex(4, 2)
        );
        srLatchResetNor1Wire.addSegment(srLatchResetNor1WireSegment);

        Wire srLatchSetNor2Wire = new Wire();
        WireSegment srLatchSetNor2WireSegment = new WireSegment(
                new ChipPin(null, srLatchSet),
                new ChipPin(srLatchNor2, norInput2),
                new Vertex(0, 11), new Vertex(4, 11)
        );
        srLatchSetNor2Wire.addSegment(srLatchSetNor2WireSegment);

        Wire srLatchNor1OutputWire = new Wire();
        WireNode srLatchNor1OutputWireNode = new WireNode(new Vertex(12, 3));
        WireSegment srLatchNor1OutputWireSegment1 = new WireSegment(
                new ChipPin(srLatchNor1, norOutput),
                srLatchNor1OutputWireNode,
                new Vertex(10, 3), new Vertex(12, 3)
        );
        WireSegment srLatchNor1OutputWireSegment2 = new WireSegment(
                srLatchNor1OutputWireNode,
                new ChipPin(null, srLatchOutput),
                new Vertex(12, 3), new Vertex(15, 3)
        );
        WireSegment srLatchNor1OutputWireSegment3 = new WireSegment(
                srLatchNor1OutputWireNode,
                new ChipPin(srLatchNor2, norInput1),
                new Vertex(12, 3), new Vertex(12, 7), new Vertex(2, 7), new Vertex(2, 9), new Vertex(4, 9)
        );
        srLatchNor1OutputWire.addSegments(
                srLatchNor1OutputWireSegment1, srLatchNor1OutputWireSegment2, srLatchNor1OutputWireSegment3
        );

        Wire srLatchNor2OutputWire = new Wire();
        WireNode srLatchNor2OutputWireNode = new WireNode(new Vertex(13, 10));
        WireSegment srLatchNor2OutputWireSegment1 = new WireSegment(
                new ChipPin(srLatchNor2, norOutput),
                srLatchNor2OutputWireNode,
                new Vertex(10, 10), new Vertex(13, 10)
        );
        WireSegment srLatchNor2OutputWireSegment2 = new WireSegment(
                srLatchNor2OutputWireNode,
                new ChipPin(null, srLatchInverseOutput),
                new Vertex(13, 10), new Vertex(15, 10)
        );
        WireSegment srLatchNor2OutputWireSegment3 = new WireSegment(
                srLatchNor2OutputWireNode,
                new ChipPin(srLatchNor1, norInput2),
                new Vertex(13, 10), new Vertex(13, 6), new Vertex(2, 6), new Vertex(2, 4), new Vertex(4, 4)
        );
        srLatchNor2OutputWire.addSegments(
                srLatchNor2OutputWireSegment1, srLatchNor2OutputWireSegment2, srLatchNor2OutputWireSegment3
        );

        srLatchDesign.addPins(
                srLatchReset, srLatchSet,
                srLatchOutput, srLatchInverseOutput
        );
        srLatchDesign.addChips(
                srLatchNor1, srLatchNor2
        );
        srLatchDesign.addWires(
                srLatchResetNor1Wire, srLatchSetNor2Wire,
                srLatchNor1OutputWire, srLatchNor2OutputWire
        );

        srLatchDesign.recalculateOpenDistances();

        ChipTemplate srLatchTemplate = new DesignedTemplate(srLatchDesign);
        srLatchTemplate.setId("sr_latch");
        srLatchTemplate.setName("SR Latch");
        srLatchTemplate.setWidth(6);
        srLatchTemplate.setHeight(4);

        // +----------------------+
        // |   Test Design Setup  |
        // +----------------------+

        Design testDesign = new Design();
        testDesign.setWidth(19);
        testDesign.setHeight(10);

        Chip testToggleReset = new Chip();
        testToggleReset.setChipTemplate(ToggleTemplate.getTemplate());
        testToggleReset.setLocation(new Vertex(1, 1));

        Chip testToggleSet = new Chip();
        testToggleSet.setChipTemplate(ToggleTemplate.getTemplate());
        testToggleSet.setLocation(new Vertex(1, 5));

        Chip testSRLatch = new Chip();
        testSRLatch.setChipTemplate(srLatchTemplate);
        testSRLatch.setLocation(new Vertex(9, 3));

        Wire testResetWire = new Wire();
        WireSegment testResetWireSegment = new WireSegment(
                new ChipPin(testToggleReset, ToggleTemplate.OUTPUT),
                new ChipPin(testSRLatch, srLatchReset),
                new Vertex(5, 3), new Vertex(7, 3), new Vertex(7, 4), new Vertex(9, 4)
        );
        testResetWire.addSegment(testResetWireSegment);

        Wire testSetWire = new Wire();
        WireSegment testSetWireSegment = new WireSegment(
                new ChipPin(testToggleSet, ToggleTemplate.OUTPUT),
                new ChipPin(testSRLatch, srLatchSet),
                new Vertex(5, 7), new Vertex(7, 7), new Vertex(7, 6), new Vertex(9, 6)
        );
        testSetWire.addSegment(testSetWireSegment);

        testDesign.addChips(
                testToggleReset, testToggleSet, testSRLatch
        );
        testDesign.addWires(
                testResetWire, testSetWire
        );

        testDesign.recalculateOpenDistances();





        Simulation.setSimulator(new MeshSimulator());
        Simulation.start();
        SimulatedDesign simulatedDesign = Simulation.initialize(testDesign);

        UIToolbar toolbar = new UIToolbar(Direction.HORIZONTAL);
        toolbar.add(new UIButton("T1", () -> {}));
        toolbar.add(new UIButton("T2", () -> {}));
        toolbar.add(new UIButton("T3", () -> {}));
        toolbar.addSeparator();
        toolbar.add(new UIButton("T4", () -> {}));
        toolbar.add(new UIButton("T5", () -> {}));
        toolbar.addSeparator();
//        toolbar.add(new UIStartStopButton(WorkerScheduler::startSimulation, WorkerScheduler::stopSimulation));
//        toolbar.add(new UISpinner<>(UISpinner.getIntegerHandler(), "ticks/sec", 1, 1000, 1, WorkerScheduler::setTargetTicksPerSecond));
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

        //UIFloatingContainer floatingContainer = new UIFloatingContainer();
        //floatingContainer.addComponent(new UITextField("Search components...", (s) -> {}), Anchor.TOP_LEFT, new Padding(10));
        UIFlexContainer renderFlexContainer = new UIFlexContainer(Direction.VERTICAL);
        renderFlexContainer.addComponent(new UIBorder(new DesignEditor(testDesign, simulatedDesign), 2), 1);
        renderFlexContainer.addComponent(new UIBorder(new DesignEditor(srLatchDesign, simulatedDesign.getSimulatedChipDesign(testSRLatch)), 2), 1);
        renderFlexContainer.addComponent(new UIBorder(new DesignEditor(norDesign, simulatedDesign.getSimulatedChipDesign(testSRLatch).getSimulatedChipDesign(srLatchNor1)), 2), 1);
        renderFlexContainer.addComponent(new UIBorder(new DesignEditor(norDesign, simulatedDesign.getSimulatedChipDesign(testSRLatch).getSimulatedChipDesign(srLatchNor2)), 2), 1);

        UIFlexContainer testFlexContainer = new UIFlexContainer(Direction.VERTICAL);
        for (int i = 0; i < 4; i++)
            testFlexContainer.addComponent(new UITextField(Integer.toString(i), (s) -> {}), 0f);

        //UIScroll scroll = new UIScroll(re);

        UIFlexContainer innerFlexContainer = new UIFlexContainer(Direction.VERTICAL);
        innerFlexContainer.addComponent(new UIBorder(toolbar, UIBorder.BorderLayout.BOTTOM), 0f);
        innerFlexContainer.addComponent(renderFlexContainer, 1f);
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
