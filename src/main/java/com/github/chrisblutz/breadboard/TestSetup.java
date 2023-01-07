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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class TestSetup {

    private AndGateChipLogic andXGateChipLogic = new AndGateChipLogic();
    private AndGateChipLogic andYGateChipLogic = new AndGateChipLogic();
    private NotGateChipLogic notZGateChipLogic = new NotGateChipLogic();

    private Node node12 = new Node(null);
    private WireSegment wireSegmentO = new WireSegment(node12);
    private WireSegment wireSegmentN = new WireSegment(wireSegmentO);
    private BuiltinNode node11 = new BuiltinNode(NotGateChipLogic.RESULT_PIN_TEMPLATE, new SimulationWorkerTraversable[]{wireSegmentN});
    private BuiltinNode node10 = new BuiltinNode(NotGateChipLogic.OPERAND_PIN_TEMPLATE, null);
    private WireSegment wireSegmentM = new WireSegment(node10);
    private WireSegment wireSegmentL = new WireSegment(wireSegmentM);
    private BuiltinNode node9 = new BuiltinNode(AndGateChipLogic.RESULT_PIN_TEMPLATE, new SimulationWorkerTraversable[] {wireSegmentL});
    private BuiltinNode node8 = new BuiltinNode(AndGateChipLogic.OPERAND_2_PIN_TEMPLATE, null);
    private BuiltinNode node7 = new BuiltinNode(AndGateChipLogic.OPERAND_1_PIN_TEMPLATE, null);
    private WireSegment wireSegmentK = new WireSegment(node8);
    private WireSegment wireSegmentJ = new WireSegment(wireSegmentK);
    private WireSegment wireSegmentI = new WireSegment(wireSegmentJ);
    private WireSegment wireSegmentH = new WireSegment(wireSegmentI);
    private WireSegment wireSegmentG = new WireSegment(wireSegmentH);
    private Node node3 = new Node(new SimulationWorkerTraversable[]{wireSegmentG});
    private WireSegment wireSegmentF = new WireSegment(node7);
    private WireSegment wireSegmentE = new WireSegment(wireSegmentF);
    private BuiltinNode node6 = new BuiltinNode(AndGateChipLogic.RESULT_PIN_TEMPLATE, new SimulationWorkerTraversable[] {wireSegmentE});
    private BuiltinNode node5 = new BuiltinNode(AndGateChipLogic.OPERAND_2_PIN_TEMPLATE, null);
    private BuiltinNode node4 = new BuiltinNode(AndGateChipLogic.OPERAND_1_PIN_TEMPLATE, null);
    private WireSegment wireSegmentD = new WireSegment(node5);
    private WireSegment wireSegmentC = new WireSegment(wireSegmentD);
    private Node node2 = new Node(new SimulationWorkerTraversable[]{wireSegmentC});
    private WireSegment wireSegmentB = new WireSegment(node4);
    private WireSegment wireSegmentA = new WireSegment(wireSegmentB);
    private Node node1 = new Node(new SimulationWorkerTraversable[]{wireSegmentA});

    private BuiltinChip andX = new BuiltinChip();
    private BuiltinChip andY = new BuiltinChip();
    private BuiltinChip notZ = new BuiltinChip();

    private Worker worker1;
    private Worker worker2;
    private Worker worker3;

    public TestSetup(boolean worker1State, boolean worker2State, boolean worker3State) {
        andX.inputNodes = new BuiltinNode[] {node4, node5};
        andX.outputNodes = new BuiltinNode[] {node6};
        andX.logic = andXGateChipLogic;

        andY.inputNodes = new BuiltinNode[] {node7, node8};
        andY.outputNodes = new BuiltinNode[] {node9};
        andY.logic = andYGateChipLogic;

        notZ.inputNodes = new BuiltinNode[] {node10};
        notZ.outputNodes = new BuiltinNode[] {node11};
        notZ.logic = notZGateChipLogic;

        this.worker1 = new Worker(worker1State, node1);
        this.worker2 = new Worker(worker2State, node2);
        this.worker3 = new Worker(worker3State, node3);

        WorkerScheduler.builtinChips.addAll(Arrays.asList(andX, andY, notZ));
        WorkerScheduler.nextTickWorkers.addAll(Arrays.asList(worker1, worker2, worker3));
    }

    public void display() {
        System.out.printf("(0) -0- -0- (0)------+                                                     \n".replaceAll("0", "%d"), node1.signalState ? 1 : 0, wireSegmentA.signalState ? 1 : 0, wireSegmentB.signalState ? 1 : 0, node4.signalState ? 1 : 0);
        System.out.printf("             |  AND (0) -0- -0- (0)------+           +-------+             \n".replaceAll("0", "%d"), node6.signalState ? 1 : 0, wireSegmentE.signalState ? 1 : 0, wireSegmentF.signalState ? 1 : 0, node7.signalState ? 1 : 0);
        System.out.printf("(0) -0- -0- (0)------+           |  AND (0) -0- -0- (0) NOT (0) -0- -0- (0)\n".replaceAll("0", "%d"), node2.signalState ? 1 : 0, wireSegmentC.signalState ? 1 : 0, wireSegmentD.signalState ? 1 : 0, node5.signalState ? 1 : 0, node9.signalState ? 1 : 0, wireSegmentL.signalState ? 1 : 0, wireSegmentM.signalState ? 1 : 0, node10.signalState ? 1 : 0, node11.signalState ? 1 : 0, wireSegmentN.signalState ? 1 : 0, wireSegmentO.signalState ? 1 : 0, node12.signalState ? 1 : 0);
        System.out.printf("                                 |       |           +-------+             \n");
        System.out.printf("(0) -0- -0- --- --- -0- -0- -0- (0)------+                                 \n".replaceAll("0", "%d"), node3.signalState ? 1 : 0, wireSegmentG.signalState ? 1 : 0, wireSegmentH.signalState ? 1 : 0, wireSegmentI.signalState ? 1 : 0, wireSegmentJ.signalState ? 1 : 0, wireSegmentK.signalState ? 1 : 0, node8.signalState ? 1 : 0);
        System.out.println();
        System.out.println();
    }
}
