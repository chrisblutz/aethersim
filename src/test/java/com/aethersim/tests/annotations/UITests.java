package com.aethersim.tests.annotations;

import com.aethersim.tests.display.AetherSimDisplayNames;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Tag("ui")
@DisplayNameGeneration(AetherSimDisplayNames.UIToolkitDisplayNames.class)
public @interface UITests {
}
