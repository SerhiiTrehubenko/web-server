package com.tsa.server.domain.util;

import com.tsa.server.domain.dto.Request;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RequestParser {

    private final static Pattern SPLIT_WHITE_SPACE = Pattern.compile(" ");

    public static Request parseRequest(InputStream inputStream) {
        Request request = new Request();
        try {
            var input = new BufferedReader(new InputStreamReader(inputStream));
            injectUri(input, request);
            injectHeaders(input, request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return request;
    }

    public static void injectUri(BufferedReader inputFromSocket, Request request) throws IOException {
        String startingline = inputFromSocket.readLine();
        String[] splitLine = SPLIT_WHITE_SPACE.split(startingline);
        if (splitLine.length != 3) {
            throw new RuntimeException("bad request");
        }
        request.setHttpMethod(splitLine[0]);
        request.setUri(splitLine[1]);
    }

    public static void injectHeaders(BufferedReader inputFromSocket, Request request) throws IOException {
        Map<String, String> headers = new HashMap<>();
        char headDelimiter = ' ';
        String header;
        while (true) {
            header = inputFromSocket.readLine();
            if (header == null || header.isEmpty()) {
                break;
            }
            String keyHeader = header.substring(0, header.indexOf(headDelimiter)).trim();
            String valueHeader = header.substring(header.indexOf(headDelimiter)).trim();
            headers.put(keyHeader, valueHeader);
        }
        request.setHeaders(headers);
    }
}
