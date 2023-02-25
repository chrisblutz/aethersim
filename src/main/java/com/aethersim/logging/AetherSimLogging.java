package com.aethersim.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AetherSimLogging {

    private static final Logger allPurposeLogger = LogManager.getLogger("AetherSim");
    private static final Logger simulationLogger = LogManager.getLogger("Simulation");
    private static final Logger interfaceLogger = LogManager.getLogger("Interface");
    private static final Logger pluginManagerLogger = LogManager.getLogger("PluginManager");

    public static Logger getLogger() {
        return allPurposeLogger;
    }

    public static Logger getSimulationLogger() {
        return simulationLogger;
    }

    public static Logger getInterfaceLogger() {
        return interfaceLogger;
    }

    public static Logger getPluginManagerLogger() {
        return pluginManagerLogger;
    }

    public static void logEnvironmentInformation() {
        getLogger().info("Environment Information:");

        // Collect basic operating system information
        String os = String.format("    Operating System: %s (%s) [%s]",
                System.getProperty("os.name"),
                System.getProperty("os.version"),
                System.getProperty("os.arch")
        );
        getLogger().info(os);

        // Collect information about the Java version
        String java = String.format("    Java: Java %s (%s)",
                System.getProperty("java.version"),
                System.getProperty("java.vendor")
        );
        getLogger().info(java);

        // Collect basic information on system resources
        String maxMemory = LogUtils.formatBytes(Runtime.getRuntime().totalMemory());
        String memory = String.format("    JVM Memory: %s",
            maxMemory
        );
        getLogger().info(memory);
        int processorCount = Runtime.getRuntime().availableProcessors();
        String processors = String.format("    Logical Threads: %d",
                processorCount
        );
        getLogger().info(processors);
    }

    public static void logAetherSimInformation() {
        getLogger().info("AetherSim Information:");

        // Collect basic operating system information
        String os = String.format("    Operating System: %s (%s) [%s]",
                System.getProperty("os.name"),
                System.getProperty("os.version"),
                System.getProperty("os.arch")
        );
        getLogger().info(os);

        // Collect information about the Java version
        String java = String.format("    Java: Java %s (%s)",
                System.getProperty("java.version"),
                System.getProperty("java.vendor")
        );
        getLogger().info(java);

        // Collect basic information on system resources
        String maxMemory = LogUtils.formatBytes(Runtime.getRuntime().totalMemory());
        String memory = String.format("    JVM Memory: %s",
                maxMemory
        );
        getLogger().info(memory);
        int processorCount = Runtime.getRuntime().availableProcessors();
        String processors = String.format("    Logical Threads: %d",
                processorCount
        );
        getLogger().info(processors);
    }
}
