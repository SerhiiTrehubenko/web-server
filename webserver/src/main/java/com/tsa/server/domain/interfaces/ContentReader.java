package com.tsa.server.domain.interfaces;

import java.io.FileNotFoundException;
import java.io.InputStream;

public interface ContentReader {
    InputStream getConnectionToContent(String filePath) throws FileNotFoundException;
}
