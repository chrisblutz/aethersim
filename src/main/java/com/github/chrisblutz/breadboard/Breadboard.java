package com.github.chrisblutz.breadboard;

import com.github.chrisblutz.breadboard.logging.BreadboardLogging;
import com.github.chrisblutz.breadboard.simulationproto.LogicState;
import com.github.chrisblutz.breadboard.simulationproto.standard.mesh.MeshConnector;
import com.github.chrisblutz.breadboard.simulationproto.standard.mesh.MeshDriver;
import com.github.chrisblutz.breadboard.simulationproto.standard.mesh.MeshEdge;
import com.github.chrisblutz.breadboard.simulationproto.standard.mesh.MeshVertex;
import com.github.chrisblutz.breadboard.simulationproto.standard.threading.MeshSimulationCoordinator;
import com.github.chrisblutz.breadboard.ui.BreadboardUI;
import com.github.chrisblutz.breadboard.ui.toolkit.UIGraphics;

import java.awt.image.BufferedImage;
import java.util.Set;

public class Breadboard {
    public static void main(String[] args) {
        BreadboardLogging.getLogger().info("--- Starting Breadboard ---");
        BreadboardLogging.logEnvironmentInformation();
        BreadboardUI.initialize();

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
//        MeshDriver driverR = new MeshDriver(vertexInR, LogicState.UNCONNECTED, LogicState.UNKNOWN);
//        MeshDriver driverS = new MeshDriver(vertexInS, LogicState.UNCONNECTED, LogicState.UNKNOWN);
//        MeshDriver driverR1 = new MeshDriver(vertexR1, LogicState.LOW, LogicState.UNKNOWN);
//        MeshDriver driverS1 = new MeshDriver(vertexS1, LogicState.LOW, LogicState.UNKNOWN);
//        MeshDriver driverRD = new MeshDriver(vertexR2, LogicState.UNKNOWN, LogicState.HIGH);
//        MeshDriver driverSD = new MeshDriver(vertexS2, LogicState.UNKNOWN, LogicState.HIGH);
//
//        MeshSimulationCoordinator coordinator = new MeshSimulationCoordinator();
//        coordinator.start(new MeshDriver[] {
//                driverR,
//                driverS,
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
//        int index = 25;
//        while (--index >= 0) {
//            coordinator.tick();
//
//            System.out.println("\n\n");
//            System.out.println("Vertex InR: " + vertexInR.getActualState() + " (" + vertexInR.getSuggestedState() + ")");
//            System.out.println("Vertex InS: " + vertexInS.getActualState() + " (" + vertexInS.getSuggestedState() + ")");
//            System.out.println("Vertex R1: " + vertexR1.getActualState() + " (" + vertexR1.getSuggestedState() + ")");
//            System.out.println("Vertex R2: " + vertexR2.getActualState() + " (" + vertexR2.getSuggestedState() + ")");
//            System.out.println("Vertex S1: " + vertexS1.getActualState() + " (" + vertexS1.getSuggestedState() + ")");
//            System.out.println("Vertex S2: " + vertexS2.getActualState() + " (" + vertexS2.getSuggestedState() + ")");
//            System.out.println("Connector RC1: " + connectorRC1.isConnected() + " (" + connectorRC1.currentDelay + ")");
//            System.out.println("Connector RC2: " + connectorRC2.isConnected() + " (" + connectorRC2.currentDelay + ")");
//            System.out.println("Connector SC1: " + connectorSC1.isConnected() + " (" + connectorSC1.currentDelay + ")");
//            System.out.println("Connector SC2: " + connectorSC2.isConnected() + " (" + connectorSC2.currentDelay + ")");
//
//            try {
//                Thread.sleep(1000);
//            }catch (Exception e) {}
//        }
//        coordinator.stop();
    }
}
