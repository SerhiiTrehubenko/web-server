package com.tsa.server.domain.services;

import com.tsa.server.domain.interfaces.ContentReader;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DefaultContentReader implements ContentReader {
    private final String appPath;

    public DefaultContentReader(String appPath) {
        this.appPath = appPath;
    }

    @Override
    public InputStream getConnectionToContent(String uri) throws FileNotFoundException {
        String classPath = Path.of("").toAbsolutePath().toString();
        return new FileInputStream(Paths.get(classPath, appPath, uri).toFile());
    }
}
