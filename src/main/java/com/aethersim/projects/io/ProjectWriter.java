package com.aethersim.projects.io;

import com.aethersim.projects.io.data.DataMap;
import com.aethersim.projects.io.exceptions.ProjectIOException;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ProjectWriter implements Closeable {

    public static final String DEFAULT_FILE_PREFIX = "";
    public static final String DEFAULT_FILE_EXTENSION = ".yaml";

    private final File projectFile;
    private final ZipOutputStream zipOutputStream;
    private final OutputStreamWriter zipOutputStreamWriter;
    private final Yaml yaml;

    public ProjectWriter(File projectFile) throws IOException {
        // If the project file is a directory, throw an exception
        if (projectFile.isDirectory())
            throw new ProjectIOException("Project file '" + projectFile.getPath() + "' is a directory.");

        this.projectFile = projectFile;

        // Make the parent directories of the files to ensure they exist, and then create the file itself
        File parent = projectFile.getParentFile();
        if (parent != null)
            parent.mkdirs();
        projectFile.createNewFile();

        this.zipOutputStream = new ZipOutputStream(new FileOutputStream(projectFile));
        this.zipOutputStreamWriter = new OutputStreamWriter(zipOutputStream);

        DumperOptions yamlDumpOptions = new DumperOptions();
        this.yaml = new Yaml(yamlDumpOptions);
    }

    public void write(String filename, DataMap dataMap) throws IOException {
        write(DEFAULT_FILE_PREFIX, filename, dataMap);
    }

    public void write(String prefix, String filename, DataMap data) throws IOException {
        // Sanitize the prefix and append a trailing slash if necessary
        prefix = prefix.replace('\\', '/');
        if (prefix.length() > 0 && !prefix.endsWith("/"))
            prefix += "/";
        // Determine the full filename, including prefix and extension
        String fullFilename = prefix + filename + DEFAULT_FILE_EXTENSION;

        // Create a zip entry on the zip stream
        ZipEntry entry = new ZipEntry(fullFilename);
        zipOutputStream.putNextEntry(entry);

        // Dump data map to file
        yaml.dump(data.getRawMap(), zipOutputStreamWriter);

        // Close the current zip entry
        zipOutputStream.closeEntry();
    }

    @Override
    public void close() throws IOException {
        // Close the writer (which also closes the underlying streams)
        zipOutputStreamWriter.close();
    }
}
