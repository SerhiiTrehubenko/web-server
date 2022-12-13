package com.tsa.server.domain.services;

import com.tsa.server.domain.exceptions.WebServerException;
import com.tsa.server.domain.interfaces.ContentReader;
import com.tsa.server.domain.util.HttpStatus;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DefaultContentReader implements ContentReader {
    private final String appPath;

    public DefaultContentReader(String appPath) {
        this.appPath = appPath;
    }

    @Override
    public InputStream getConnectionToContent(String uri) {
        String classPath = getPathToFile(uri);
        try {
            return new FileInputStream(classPath);
        } catch (FileNotFoundException e) {
            throw new WebServerException(e, HttpStatus.NOT_FOUND);
        }
    }

    String getPathToFile(String uri) {
        String pathPrefix = Path.of("").toAbsolutePath().toString();
        return Paths.get(pathPrefix, appPath, uri).toString();
    }
}
