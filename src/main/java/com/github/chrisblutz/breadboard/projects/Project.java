package com.github.chrisblutz.breadboard.projects;

import com.github.chrisblutz.breadboard.components.ChipTemplate;
import com.github.chrisblutz.breadboard.designs.Design;
import com.github.chrisblutz.breadboard.saving.BreadboardSavable;
import com.github.chrisblutz.breadboard.saving.ProjectOutputWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Project implements BreadboardSavable {

    public String id;
    public String name;

    public List<ChipTemplate> projectSpecificChipTemplates = new ArrayList<>();
    public Design design;

    @Override
    public Map<String, Object> dumpToYAML(ProjectOutputWriter writer) {
        Map<String, Object> yamlMapping = new LinkedHashMap<>();

        // Put basic information
        yamlMapping.put("Id", id);
        yamlMapping.put("Name", name);

        // Construct mapping for top-level project design
        yamlMapping.put("Design", design.dumpToYAML(writer));

        // For each project-specific chip, create a new file in the project for it
        for (ChipTemplate chipTemplate : projectSpecificChipTemplates) {
            try {
                writer.writeYamlFile("chips", chipTemplate.getId(), chipTemplate.dumpToYAML(writer));
            } catch (IOException e) {
                // TODO
                e.printStackTrace();
            }
        }

        return yamlMapping;
    }

    @Override
    public void loadFromYAML(Map<String, Object> yamlMapping) {

    }
}
