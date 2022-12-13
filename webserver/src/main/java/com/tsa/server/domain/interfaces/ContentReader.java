package com.tsa.server.domain.interfaces;

import com.tsa.server.domain.exceptions.WebServerException;

import java.io.InputStream;

public interface ContentReader {
    InputStream getConnectionToContent(String filePath) throws WebServerException;
}
