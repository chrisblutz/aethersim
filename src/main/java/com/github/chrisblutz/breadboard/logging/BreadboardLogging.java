package com.github.chrisblutz.breadboard.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BreadboardLogging {

    private static Logger allPurposeLogger = LogManager.getLogger("Breadboard");
    private static Logger simulationLogger = LogManager.getLogger("Simulation");

    public static Logger getLogger() {
        return allPurposeLogger;
    }

    public static Logger getSimulationLogger() {
        return simulationLogger;
    }

    public static void logEnvironmentInformation() {
        // Collect basic operating system information
        String os = String.format("Operating System: %s (%s) [%s]",
                System.getProperty("os.name"),
                System.getProperty("os.version"),
                System.getProperty("os.arch")
        );
        getLogger().info(os);

        // Collect information about the Java version
        String java = String.format("Java: Java %s (%s)",
                System.getProperty("java.version"),
                System.getProperty("java.vendor")
        );
        getLogger().info(java);

        // Collect basic information on system resources
        String maxMemory = LogUtils.formatBytes(Runtime.getRuntime().totalMemory());
        String memory = String.format("JVM Memory: %s",
            maxMemory
        );
        getLogger().info(memory);
        int processorCount = Runtime.getRuntime().availableProcessors();
        String processors = String.format("Logical Threads: %d",
                processorCount
        );
        getLogger().info(processors);
    }
}
