package com.aethersim.tests.display;

import com.aethersim.tests.annotations.AetherSimTest;
import com.aethersim.tests.annotations.AetherSimTests;
import org.junit.jupiter.api.DisplayNameGenerator;

import java.lang.reflect.Method;

public abstract class AetherSimDisplayNames extends DisplayNameGenerator.Standard {

    public static class SimulationDisplayNames extends AetherSimDisplayNames {

        @Override
        public String getTopLevelName() {
            return "Simulation";
        }
    }

    public static class ProjectDisplayNames extends AetherSimDisplayNames {

        @Override
        public String getTopLevelName() {
            return "Projects & Designs";
        }
    }

    public static class UIToolkitDisplayNames extends AetherSimDisplayNames {

        @Override
        public String getTopLevelName() {
            return "UIToolkit";
        }
    }

    public abstract String getTopLevelName();

    @Override
    public String generateDisplayNameForClass(Class<?> testClass) {
        if (testClass.isAnnotationPresent(AetherSimTests.class)) {
            return String.format("%s / %s [%s]",
                    getTopLevelName(),
                    testClass.getAnnotation(AetherSimTests.class).value(),
                    testClass.getName()
            );
        } else {
            return String.format("%s / [%s]",
                    getTopLevelName(),
                    testClass.getName()
            );
        }
    }

    @Override
    public String generateDisplayNameForNestedClass(Class<?> nestedClass) {
        String nameSuffix = "";
        while (nestedClass != null) {
            if (nestedClass.isAnnotationPresent(AetherSimTests.class)) {
                nameSuffix = String.format(" / %s [%s]",
                        nestedClass.getAnnotation(AetherSimTests.class).value(),
                        nestedClass.getName()
                ) + nameSuffix;
            } else {
                nameSuffix = String.format(" / [%s]",
                        nestedClass.getName()
                ) + nameSuffix;
            }
            nestedClass = nestedClass.getEnclosingClass();
        }
        return String.format("%s%s", getTopLevelName(), nameSuffix);
    }

    @Override
    public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
        if (testMethod.isAnnotationPresent(AetherSimTest.class)) {
            return String.format("%s [%s]",
                    testMethod.getAnnotation(AetherSimTest.class).value(),
                    testMethod.getName()
            );
        } else {
            return String.format("[%s]",
                    testMethod.getName()
            );
        }
    }
}
