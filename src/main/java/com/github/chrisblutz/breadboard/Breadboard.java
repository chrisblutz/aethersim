package com.github.chrisblutz.breadboard;

import com.github.chrisblutz.breadboard.components.builtins.AndGateChipLogic;
import com.github.chrisblutz.breadboard.components.builtins.NotGateChipLogic;
import com.github.chrisblutz.breadboard.simulation.components.BuiltinChip;
import com.github.chrisblutz.breadboard.simulation.components.BuiltinNode;
import com.github.chrisblutz.breadboard.simulation.components.Node;
import com.github.chrisblutz.breadboard.simulation.components.WireSegment;
import com.github.chrisblutz.breadboard.simulation.workers.SimulationWorkerTraversable;
import com.github.chrisblutz.breadboard.simulation.workers.Worker;
import com.github.chrisblutz.breadboard.simulation.workers.WorkerScheduler;
import com.github.chrisblutz.breadboard.ui.BreadboardUI;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Breadboard {
    public static void main(String[] args) {

        Boolean x = null;
        System.out.println();

//        Project project = new Project();
//        project.id = "test-project";
//        project.name = "Test Project";
//
//        ChipTemplate chip1 = new ChipTemplate();
//        chip1.id = "chip1";
//        chip1.name = "Chip 1";
//        project.projectSpecificChipTemplates.add(chip1);
//
//        ChipTemplate chip2 = new ChipTemplate();
//        chip2.id = "chip2";
//        chip2.name = "Chip 2";
//        project.projectSpecificChipTemplates.add(chip2);
//
//        Design design = new Design();
//        design.width = 10;
//        design.height = 10;
//        project.design = design;
//
//        Pin input1 = new Pin();
//        input1.id = "In1";
//        input1.name = "Input 1";
//        design.inputs.add(input1);
//
//        Pin input2 = new Pin();
//        input2.id = "In2";
//        input2.name = "Input 2";
//        design.inputs.add(input2);
//
//        Pin output1 = new Pin();
//        output1.id = "Out1";
//        output1.name = "Output 1";
//        design.outputs.add(output1);
//
//        Pin output2 = new Pin();
//        output2.id = "Out2";
//        output2.name = "Output 2";
//        design.outputs.add(output2);
//
//        try {
//
//            ProjectOutputWriter projectWriter = new ProjectOutputWriter(new File("./test.bbproj"));
//            projectWriter.initializeProjectFile();
//            projectWriter.writeYamlFile("", "project", project.dumpToYAML(projectWriter));
//            projectWriter.close();
//
//        }catch(IOException e) {
//            e.printStackTrace();
//        }

//        WireSegment finalSegmentPath1 = new WireSegment(null);
//        WireSegment nextToLastSegmentPath1 = new WireSegment(finalSegmentPath1);
//        WireSegment finalSegmentPath2 = new WireSegment(null);
//        WireSegment nextToLastSegmentPath2 = new WireSegment(finalSegmentPath2);
//        Node testNode = new Node(new SimulationWorkerTraversable[] {nextToLastSegmentPath1, nextToLastSegmentPath2});
//        WireSegment second = new WireSegment(testNode);
//        WireSegment first = new WireSegment(second);
//
//        Worker worker = new Worker(true, first);
//        WorkerScheduler.nextTickWorkers = new Worker[]{worker};
//
//        Scanner scanner = new Scanner(System.in);
//        // After each loop, print states
//        while (true) {
//            System.out.println("Segment 1: " + first.signalState);
//            System.out.println("Segment 2: " + second.signalState);
//            System.out.println("Node: " + testNode.signalState);
//            System.out.println("Segment 3 (Path 1): " + nextToLastSegmentPath1.signalState);
//            System.out.println("Segment 4 (Path 1): " + finalSegmentPath1.signalState);
//            System.out.println("Segment 3 (Path 2): " + nextToLastSegmentPath2.signalState);
//            System.out.println("Segment 4 (Path 2): " + finalSegmentPath2.signalState);
//
//            System.out.println("Hit Enter to continue...");
//            scanner.nextLine();
//
//            try {
//                Thread.sleep(1000);
//            }catch (Exception ignored){}
//
//            System.out.printf("Workers: %d%n", WorkerScheduler.nextTickWorkers.length);
//            long time = WorkerScheduler.tick();
//            double seconds = (double) time / 1000d;
//            System.out.printf("Tick Time: %.3fs%n", seconds);
//        }

//        boolean[][] settings = new boolean[][] {
//                new boolean[]{false, false, false},
//                new boolean[]{false, false, true},
//                new boolean[]{false, true, false},
//                new boolean[]{false, true, true},
//                new boolean[]{true, false, false},
//                new boolean[]{true, false, true},
//                new boolean[]{true, true, false},
//                new boolean[]{true, true, true}
//            };
//        TestSetup[] setups = new TestSetup[settings.length];
//        for (int i = 0; i < settings.length; i++)
//            setups[i] = new TestSetup(settings[i][0], settings[i][1], settings[i][2]);
//
//        Scanner scanner = new Scanner(System.in);
//
//        // After each loop, print states
//        while (true) {
//
//            for (TestSetup setup : setups)
//                setup.display();
//
//            System.out.println("Hit Enter to continue...");
//            scanner.nextLine();
//
//            System.out.printf("Workers:       %d%n", WorkerScheduler.nextTickWorkers.size());
//            System.out.printf("Builtin Chips: %d%n", WorkerScheduler.builtinChips.size());
//            long time = WorkerScheduler.tick();
//            //double seconds = (double) time / 1000d;
//            //System.out.printf("Tick Time:     %.3fs%n", seconds);
//            System.out.printf("Tick Time:     %dms%n", time);
//
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }

        BreadboardUI.initialize();
    }
}
