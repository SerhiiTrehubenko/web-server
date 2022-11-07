package com.tsa.server.domain.services;

import com.tsa.server.domain.interfaces.ContentReader;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ContentReaderImpl implements ContentReader {
    private static final String ROOT = Path.of("").toAbsolutePath().toString();
    private final Path appPath;

    public ContentReaderImpl(Path appPath) {
        this.appPath = appPath;
    }
    @Override
    public InputStream readContent(String filePath) throws FileNotFoundException {
        return new FileInputStream(Paths.get(ROOT, appPath.toString(), filePath).toFile());
    }
}
