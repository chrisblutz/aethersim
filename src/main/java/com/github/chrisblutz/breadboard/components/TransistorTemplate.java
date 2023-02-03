package com.github.chrisblutz.breadboard.components;

import com.github.chrisblutz.breadboard.designs.components.Pin;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;

import java.util.Arrays;
import java.util.Map;

public class TransistorTemplate extends ChipTemplate {

    public static final Pin NPN_EMITTER = new Pin();
    public static final Pin NPN_COLLECTOR = new Pin();
    public static final Pin NPN_BASE = new Pin();
    public static final Pin PNP_EMITTER = new Pin();
    public static final Pin PNP_COLLECTOR = new Pin();
    public static final Pin PNP_BASE = new Pin();

    private static TransistorTemplate npnTransistorTemplate;
    private static TransistorTemplate pnpTransistorTemplate;

    private final boolean activeLow;

    private TransistorTemplate(boolean activeLow) {
        this.activeLow = activeLow;
    }

    public boolean isActiveLow() {
        return activeLow;
    }

    public Pin getBase() {
        return isActiveLow() ? PNP_BASE : NPN_BASE;
    }

    public Pin getActiveSignalInput() {
        return isActiveLow() ? PNP_EMITTER : NPN_COLLECTOR;
    }

    public Pin getActiveSignalOutput() {
        return isActiveLow() ? PNP_COLLECTOR : NPN_EMITTER;
    }

    @Override
    protected Map<String, Object> dumpInternalsToYAML(ProjectOutputWriter writer) {
        return null;
    }

    @Override
    public void loadFromYAML(Map<String, Object> yamlMapping) {

    }

    public static TransistorTemplate getNPNTransistorTemplate() {
        // If the template doesn't exist, create it
        if (npnTransistorTemplate == null) {
            // Configure pins
            NPN_COLLECTOR.setId("transistor_npn_collector");
            NPN_COLLECTOR.setName("Collector");
            NPN_COLLECTOR.setChipX(0);
            NPN_COLLECTOR.setChipY(1);
            NPN_BASE.setId("transistor_npn_base");
            NPN_BASE.setName("Base");
            NPN_BASE.setChipX(0);
            NPN_BASE.setChipY(3);
            NPN_EMITTER.setId("transistor_npn_emitter");
            NPN_EMITTER.setName("Emitter");
            NPN_EMITTER.setChipX(6);
            NPN_EMITTER.setChipY(2);

            // Configure template
            npnTransistorTemplate = new TransistorTemplate(false); // NPN transistors are active high
            npnTransistorTemplate.setId("transistor_npn");
            npnTransistorTemplate.setName("NPN");
            npnTransistorTemplate.setWidth(6);
            npnTransistorTemplate.setHeight(4);
            npnTransistorTemplate.setPins(Arrays.asList(NPN_COLLECTOR, NPN_BASE, NPN_EMITTER));
        }

        return npnTransistorTemplate;
    }

    public static TransistorTemplate getPNPTransistorTemplate() {
        // If the template doesn't exist, create it
        if (pnpTransistorTemplate == null) {
            // Configure pins
            PNP_EMITTER.setId("transistor_pnp_emitter");
            PNP_EMITTER.setName("Emitter");
            PNP_EMITTER.setChipX(0);
            PNP_EMITTER.setChipY(1);
            PNP_BASE.setId("transistor_pnp_base");
            PNP_BASE.setName("Base");
            PNP_BASE.setChipX(0);
            PNP_BASE.setChipY(3);
            PNP_COLLECTOR.setId("transistor_pnp_collector");
            PNP_COLLECTOR.setName("Collector");
            PNP_COLLECTOR.setChipX(6);
            PNP_COLLECTOR.setChipY(2);

            // Configure template
            pnpTransistorTemplate = new TransistorTemplate(true); // PNP transistors are active low
            pnpTransistorTemplate.setId("transistor_pnp");
            pnpTransistorTemplate.setName("PNP");
            pnpTransistorTemplate.setWidth(6);
            pnpTransistorTemplate.setHeight(4);
            pnpTransistorTemplate.setPins(Arrays.asList(PNP_EMITTER, PNP_BASE, PNP_COLLECTOR));
        }

        return pnpTransistorTemplate;
    }
}
