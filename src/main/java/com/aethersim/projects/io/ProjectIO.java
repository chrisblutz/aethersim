package com.aethersim.projects.io;

import com.aethersim.designs.templates.ChipTemplate;
import com.aethersim.logging.AetherSimLogging;
import com.aethersim.plugins.Plugin;
import com.aethersim.projects.Project;
import com.aethersim.projects.Scope;
import com.aethersim.projects.io.data.DataContext;
import com.aethersim.projects.io.data.DataMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ProjectIO {

    public static void write(Project project, File projectFile) {
        // Track the used plugins for all written files
        Set<Plugin> usedPlugins = new HashSet<>();

        // Build metadata object for this file
        FileMetadata metadata = new FileMetadata(usedPlugins);

        try (ProjectWriter writer = new ProjectWriter(projectFile)) {
            // Write the main project design file
            DataContext projectContext = new DataContext();
            DataMap projectData = projectContext.serialize(project, true);
            writer.write("project", projectData);
            projectData.dispose();

            // Write all project-scope chip template files
            for (ChipTemplate template : ChipTemplate.getForScope(Scope.PROJECT)) {
                DataContext templateContext = new DataContext();
                DataMap templateData = templateContext.serialize(template, true);
                writer.write("chip_templates", template.getId(), templateData);
                templateData.dispose();
            }

            // Add all used plugins to the used plugins set
            usedPlugins.addAll(projectContext.getUsedPlugins());

            // Write the metadata file
            DataContext metadataContext = new DataContext();
            DataMap metadataData = metadataContext.serialize(metadata, true);
            writer.write("metadata", metadataData);
            metadataData.dispose();
        } catch (FileNotFoundException e) {
            AetherSimLogging.getLogger().error("Could not write to project file '" + projectFile.getPath() + "'.", e);
        } catch (IOException e) {
            AetherSimLogging.getLogger().error("An error occurred while writing to project file '" + projectFile.getPath() + "'.", e);
        }
    }
}
