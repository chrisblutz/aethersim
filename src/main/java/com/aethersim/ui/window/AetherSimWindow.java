package com.aethersim.ui.window;

import com.aethersim.designs.*;
import com.aethersim.designs.templates.*;
import com.aethersim.designs.wires.WireNode;
import com.aethersim.designs.wires.WireSegment;
import com.aethersim.designs.wires.WireWaypoint;
import com.aethersim.projects.Project;
import com.aethersim.projects.Scope;
import com.aethersim.projects.io.ProjectIO;
import com.aethersim.simulation.SimulatedDesign;
import com.aethersim.simulation.Simulation;
import com.aethersim.simulation.mesh.MeshSimulator;
import com.aethersim.simulation.mesh.mesh.MeshSimulatedDesign;
import com.aethersim.simulation.mesh.threading.MeshSimulationCoordinator;
import com.aethersim.ui.render.designs.DesignEditor;
import com.aethersim.ui.toolkit.UITheme;
import com.aethersim.ui.toolkit.UIWindow;
import com.aethersim.ui.toolkit.builtin.containers.UIBorder;
import com.aethersim.ui.toolkit.builtin.containers.UIFlexContainer;
import com.aethersim.ui.toolkit.builtin.containers.UIToolbar;
import com.aethersim.ui.toolkit.builtin.input.UIButton;
import com.aethersim.ui.toolkit.builtin.input.UITextField;
import com.aethersim.ui.toolkit.builtin.text.UIText;
import com.aethersim.ui.toolkit.display.theming.ThemeKeys;
import com.aethersim.ui.toolkit.layout.Direction;
import com.aethersim.ui.toolkit.layout.UIDimension;

import javax.swing.*;
import java.io.File;

public class AetherSimWindow {

    private static JFrame frame;
    private static AetherSimCanvas canvas;

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
        norInput1.setChipLocation(new Point(0, 1));
        norInput1.setDesignLocation(new Point(0, 8));
        norInput1.setChipOrientation(com.aethersim.utils.Direction.LEFT);
        norInput1.setDesignOrientation(com.aethersim.utils.Direction.RIGHT);

        Pin norInput2 = new Pin();
        norInput2.setId("input_2");
        norInput2.setName("Input");
        norInput2.setChipLocation(new Point(0, 3));
        norInput2.setDesignLocation(new Point(0, 14));
        norInput2.setChipOrientation(com.aethersim.utils.Direction.LEFT);
        norInput2.setDesignOrientation(com.aethersim.utils.Direction.RIGHT);

        Pin norOutput = new Pin();
        norOutput.setId("output");
        norOutput.setName("Output");
        norOutput.setChipLocation(new Point(6, 2));
        norOutput.setDesignLocation(new Point(20, 10));
        norOutput.setChipOrientation(com.aethersim.utils.Direction.RIGHT);
        norOutput.setDesignOrientation(com.aethersim.utils.Direction.LEFT);

        Chip norTransistor1 = new Chip();
        norTransistor1.setChipTemplate(ChipTemplate.get("transistor_npn"));
        norTransistor1.setLocation(new Point(9, 5));

        Chip norTransistor2 = new Chip();
        norTransistor2.setChipTemplate(ChipTemplate.get("transistor_npn"));
        norTransistor2.setLocation(new Point(9, 11));

        Chip norDrivenLow = new Chip();
        norDrivenLow.setChipTemplate(ChipTemplate.get("driven_low"));
        norDrivenLow.setLocation(new Point(1, 1));

        Chip norPulledHigh = new Chip();
        norPulledHigh.setChipTemplate(ChipTemplate.get("pulled_high"));
        norPulledHigh.setLocation(new Point(11, 1));

        WireSegment norInput1WireSegment = new WireSegment(
                norDesign,
                new ChipPin(null, norInput1),
                new ChipPin(norTransistor1, TransistorTemplate.NPN_BASE)
        );

        WireSegment norInput2WireSegment = new WireSegment(
                norDesign,
                new ChipPin(null, norInput2),
                new ChipPin(norTransistor2, TransistorTemplate.NPN_BASE)
        );

        WireNode norDrivenLowWireNode = new WireNode(new Point(7, 6));
        WireSegment norDrivenLowWireSegment1 = new WireSegment(
                norDesign,
                new ChipPin(norDrivenLow, ConstantTemplate.OUTPUT),
                norDrivenLowWireNode,
                new WireWaypoint(new Point(7, 2))
        );
        WireSegment norDrivenLowWireSegment2 = new WireSegment(
                norDesign,
                norDrivenLowWireNode,
                new ChipPin(norTransistor1, TransistorTemplate.NPN_COLLECTOR)
        );
        WireSegment norDrivenLowWireSegment3 = new WireSegment(
                norDesign,
                norDrivenLowWireNode,
                new ChipPin(norTransistor2, TransistorTemplate.NPN_COLLECTOR),
                new WireWaypoint(new Point(7, 12))
        );

        WireNode norPulledHighWireT1Node = new WireNode(new Point(17, 7));
        WireNode norPulledHighWireOutputNode = new WireNode(new Point(17, 10));
        WireSegment norPulledHighWireSegment1 = new WireSegment(
                norDesign,
                new ChipPin(norPulledHigh, ConstantTemplate.OUTPUT),
                norPulledHighWireT1Node,
                new WireWaypoint(new Point(17, 2))
        );
        WireSegment norPulledHighWireSegment2 = new WireSegment(
                norDesign,
                norPulledHighWireT1Node,
                new ChipPin(norTransistor1, TransistorTemplate.NPN_EMITTER)
        );
        WireSegment norPulledHighWireSegment3 = new WireSegment(
                norDesign,
                norPulledHighWireT1Node,
                norPulledHighWireOutputNode
        );
        WireSegment norPulledHighWireSegment4 = new WireSegment(
                norDesign,
                norPulledHighWireOutputNode,
                new ChipPin(null, norOutput)
        );
        WireSegment norPulledHighWireSegment5 = new WireSegment(
                norDesign,
                norPulledHighWireOutputNode,
                new ChipPin(norTransistor2, TransistorTemplate.NPN_EMITTER),
                new WireWaypoint(new Point(17, 13))
        );

        norDesign.addPins(
                norInput1, norInput2, norOutput
        );
        norDesign.addChips(
                norTransistor1, norTransistor2,
                norDrivenLow, norPulledHigh
        );
        norDesign.addWireNodes(
                norDrivenLowWireNode,
                norPulledHighWireT1Node, norPulledHighWireOutputNode
        );
        norDesign.addWireSegments(
                norInput1WireSegment, norInput2WireSegment,
                norDrivenLowWireSegment1, norDrivenLowWireSegment2, norDrivenLowWireSegment3,
                norPulledHighWireSegment1, norPulledHighWireSegment2, norPulledHighWireSegment3,
                norPulledHighWireSegment4, norPulledHighWireSegment5
        );

        norDesign.rerouteWires();
        norDesign.recalculateOpenDistances();

        ChipTemplate norTemplate = new DesignedTemplate(norDesign);
        norTemplate.setId("nor");
        norTemplate.setName("NOR");
        norTemplate.setWidth(6);
        norTemplate.setHeight(4);

        ChipTemplate.register(norTemplate, Scope.PROJECT);

        // +----------------------+
        // |    SR Latch Setup    |
        // +----------------------+

        Design srLatchDesign = new Design();
        srLatchDesign.setWidth(15);
        srLatchDesign.setHeight(13);

        Pin srLatchReset = new Pin();
        srLatchReset.setId("reset");
        srLatchReset.setName("Reset");
        srLatchReset.setChipLocation(new Point(0, 1));
        srLatchReset.setDesignLocation(new Point(0, 2));
        srLatchReset.setChipOrientation(com.aethersim.utils.Direction.LEFT);
        srLatchReset.setDesignOrientation(com.aethersim.utils.Direction.RIGHT);

        Pin srLatchSet = new Pin();
        srLatchSet.setId("set");
        srLatchSet.setName("Set");
        srLatchSet.setChipLocation(new Point(0, 3));
        srLatchSet.setDesignLocation(new Point(0, 11));
        srLatchSet.setChipOrientation(com.aethersim.utils.Direction.LEFT);
        srLatchSet.setDesignOrientation(com.aethersim.utils.Direction.RIGHT);

        Pin srLatchOutput = new Pin();
        srLatchOutput.setId("output");
        srLatchOutput.setName("Output");
        srLatchOutput.setChipLocation(new Point(6, 1));
        srLatchOutput.setDesignLocation(new Point(15, 3));
        srLatchOutput.setChipOrientation(com.aethersim.utils.Direction.RIGHT);
        srLatchOutput.setDesignOrientation(com.aethersim.utils.Direction.LEFT);

        Pin srLatchInverseOutput = new Pin();
        srLatchInverseOutput.setId("inverse_output");
        srLatchInverseOutput.setName("Inverse Output");
        srLatchInverseOutput.setChipLocation(new Point(6, 3));
        srLatchInverseOutput.setDesignLocation(new Point(15, 10));
        srLatchInverseOutput.setChipOrientation(com.aethersim.utils.Direction.RIGHT);
        srLatchInverseOutput.setDesignOrientation(com.aethersim.utils.Direction.LEFT);

        Chip srLatchNor1 = new Chip();
        srLatchNor1.setChipTemplate(ChipTemplate.get("nor"));
        srLatchNor1.setLocation(new Point(4, 1));

        Chip srLatchNor2 = new Chip();
        srLatchNor2.setChipTemplate(ChipTemplate.get("nor"));
        srLatchNor2.setLocation(new Point(4, 8));

        WireSegment srLatchResetNor1WireSegment = new WireSegment(
                srLatchDesign,
                new ChipPin(null, srLatchReset),
                new ChipPin(srLatchNor1, norInput1)
        );

        WireSegment srLatchSetNor2WireSegment = new WireSegment(
                srLatchDesign,
                new ChipPin(null, srLatchSet),
                new ChipPin(srLatchNor2, norInput2)
        );

        WireNode srLatchNor1OutputWireNode = new WireNode(new Point(12, 3));
        WireSegment srLatchNor1OutputWireSegment1 = new WireSegment(
                srLatchDesign,
                new ChipPin(srLatchNor1, norOutput),
                srLatchNor1OutputWireNode
        );
        WireSegment srLatchNor1OutputWireSegment2 = new WireSegment(
                srLatchDesign,
                srLatchNor1OutputWireNode,
                new ChipPin(null, srLatchOutput)
        );
        WireSegment srLatchNor1OutputWireSegment3 = new WireSegment(
                srLatchDesign,
                srLatchNor1OutputWireNode,
                new ChipPin(srLatchNor2, norInput1),
                new WireWaypoint(new Point(12, 7)), new WireWaypoint(new Point(2, 7)), new WireWaypoint(new Point(2, 9))
        );

        WireNode srLatchNor2OutputWireNode = new WireNode(new Point(13, 10));
        WireSegment srLatchNor2OutputWireSegment1 = new WireSegment(
                srLatchDesign,
                new ChipPin(srLatchNor2, norOutput),
                srLatchNor2OutputWireNode
        );
        WireSegment srLatchNor2OutputWireSegment2 = new WireSegment(
                srLatchDesign,
                srLatchNor2OutputWireNode,
                new ChipPin(null, srLatchInverseOutput)
        );
        WireSegment srLatchNor2OutputWireSegment3 = new WireSegment(
                srLatchDesign,
                srLatchNor2OutputWireNode,
                new ChipPin(srLatchNor1, norInput2),
                new WireWaypoint(new Point(13, 6)), new WireWaypoint(new Point(2, 6)), new WireWaypoint(new Point(2, 4))
        );

        srLatchDesign.addPins(
                srLatchReset, srLatchSet,
                srLatchOutput, srLatchInverseOutput
        );
        srLatchDesign.addChips(
                srLatchNor1, srLatchNor2
        );
        srLatchDesign.addWireNodes(
                srLatchNor1OutputWireNode, srLatchNor2OutputWireNode
        );
        srLatchDesign.addWireSegments(
                srLatchResetNor1WireSegment, srLatchSetNor2WireSegment,
                srLatchNor1OutputWireSegment1, srLatchNor1OutputWireSegment2, srLatchNor1OutputWireSegment3,
                srLatchNor2OutputWireSegment1, srLatchNor2OutputWireSegment2, srLatchNor2OutputWireSegment3
        );

        srLatchDesign.rerouteWires();
        srLatchDesign.recalculateOpenDistances();

        ChipTemplate srLatchTemplate = new DesignedTemplate(srLatchDesign);
        srLatchTemplate.setId("sr_latch");
        srLatchTemplate.setName("SR Latch");
        srLatchTemplate.setWidth(6);
        srLatchTemplate.setHeight(4);

        ChipTemplate.register(srLatchTemplate, Scope.PROJECT);

        // +----------------------+
        // |   Test Design Setup  |
        // +----------------------+

        Design testDesign = new Design();
        testDesign.setWidth(19);
        testDesign.setHeight(11);

        Chip testToggleReset = new Chip();
        testToggleReset.setChipTemplate(ChipTemplate.get("toggle"));
        testToggleReset.setLocation(new Point(1, 1));

        Chip testToggleSet = new Chip();
        testToggleSet.setChipTemplate(ChipTemplate.get("toggle"));
        testToggleSet.setLocation(new Point(1, 6));

        Chip testSRLatch = new Chip();
        testSRLatch.setChipTemplate(ChipTemplate.get("sr_latch"));
        testSRLatch.setLocation(new Point(9, 3));

        WireSegment testResetWireSegment = new WireSegment(
                testDesign,
                new ChipPin(testToggleReset, ToggleTemplate.OUTPUT),
                new ChipPin(testSRLatch, srLatchReset),
                new WireWaypoint(new Point(7, 3)), new WireWaypoint(new Point(7, 4))
        );

        WireSegment testSetWireSegment = new WireSegment(
                testDesign,
                new ChipPin(testToggleSet, ToggleTemplate.OUTPUT),
                new ChipPin(testSRLatch, srLatchSet),
                new WireWaypoint(new Point(7, 7)), new WireWaypoint(new Point(7, 6))
        );

        testDesign.addChips(
                testToggleReset, testToggleSet, testSRLatch
        );
        testDesign.addWireSegments(
                testResetWireSegment, testSetWireSegment
        );

        testDesign.rerouteWires();
        testDesign.recalculateOpenDistances();


        Project project = new Project();
        project.setId("test_project");
        project.setName("Test Project");
        project.setDesign(testDesign);
        ProjectIO.write(project, new File("test.asproj"));



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
       // renderFlexContainer.addComponent(new UIBorder(new DesignEditor(srLatchDesign, simulatedDesign.getSimulatedChipDesign(testSRLatch)), 2), 1);
       // renderFlexContainer.addComponent(new UIBorder(new DesignEditor(norDesign, simulatedDesign.getSimulatedChipDesign(testSRLatch).getSimulatedChipDesign(srLatchNor1)), 2), 1);
       // renderFlexContainer.addComponent(new UIBorder(new DesignEditor(norDesign, simulatedDesign.getSimulatedChipDesign(testSRLatch).getSimulatedChipDesign(srLatchNor2)), 2), 1);

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

        UIWindow window = new UIWindow("Test Project [test_proj.aether] - AetherSim");
        window.setContent(flexContainer);
        window.setVisible(true);
    }
}
