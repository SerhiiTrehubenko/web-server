package com.tsa.server.domain.services;

import com.tsa.server.domain.exceptions.WebServerException;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class DefaultContentReaderTest {

    @Test
    void testGetPathToFile() {
        String absolutePath = Path.of("").toAbsolutePath().toString();
        String[] pathToSource = {"src", "main", "resources"};
        String uri = "/index.html";
        String expectedPathToFile = Paths.get(absolutePath, pathToSource[0], pathToSource[1], pathToSource[2], uri).toString();

        var contentReader = createContentReader();

        String resultPath = contentReader.getPathToFile("/index.html");

        assertEquals(expectedPathToFile, resultPath);
    }

    @Test
    void testGetConnectionToContent() {
        var contentReader = createContentReader();

        assertThrows(WebServerException.class, () -> contentReader.getConnectionToContent("/index.txt"));
    }

    private DefaultContentReader createContentReader() {
        String[] pathToSource = {"src", "main", "resources"};
        String pathToFile = Paths.get(pathToSource[0], pathToSource[1], pathToSource[2]).toString();
        return new DefaultContentReader(pathToFile);
    }
}