package com.github.chrisblutz.breadboard.saving;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ProjectOutputWriter implements Closeable {

    private File projectFile;
    private ZipOutputStream zipOutputStream;
    private OutputStreamWriter zipOutputStreamWriter;
    private Yaml yaml;

    public ProjectOutputWriter(File projectFile) throws FileNotFoundException {

        this.projectFile = projectFile;
        this.zipOutputStream = new ZipOutputStream(new FileOutputStream(projectFile));
        this.zipOutputStreamWriter = new OutputStreamWriter(zipOutputStream);

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.yaml = new Yaml(options);
    }

    public void initializeProjectFile() throws IOException {
        // TODO Create backup of existing file

        // Make sure the file exists and make sure it is empty
        projectFile.mkdirs();
        projectFile.createNewFile();
    }

    public void writeYamlFile(String prefix, String filename, Map<String, Object> yamlMapping) throws IOException {
        // Determine full filename including prefix and extension
        String fullFilename = String.format("%s%s.yaml", (prefix.length() > 0 ? prefix + "/" : ""), filename);

        // Create zip entry on the stream
        ZipEntry entry = new ZipEntry(fullFilename);
        zipOutputStream.putNextEntry(entry);

        // Write YAML information from mapping
        yaml.dump(yamlMapping, zipOutputStreamWriter);

        // Close the zip entry
        zipOutputStream.closeEntry();
    }

    @Override
    public void close() throws IOException {

        zipOutputStreamWriter.close();
        zipOutputStream.close();
    }
}
