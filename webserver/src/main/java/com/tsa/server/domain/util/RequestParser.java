package com.tsa.server.domain.util;

import com.tsa.server.domain.dto.Request;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class RequestParser {

    private final static String SPLIT_WHITE_SPACE = " ";
    private  static String line;

    public static Request parseRequest(InputStream inputStream) {
        Request request = new Request();
        try  {
            var input = new BufferedReader(new InputStreamReader(inputStream));
            injectUri(input, request);
            injectHeaders(input, request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return request;
    }

    public static void injectUri(BufferedReader input, Request request) throws IOException {
        line = input.readLine();
        String[] splitLine = line.split(SPLIT_WHITE_SPACE);
        if (splitLine.length < 3) {
            throw  new RuntimeException("bad request");
        }
        request.setUri(splitLine[1]);
        request.setHttpMethod(splitLine[0]);
    }

    public static void injectHeaders(BufferedReader input, Request request) throws IOException {
        Map<String, String> headers = new HashMap<>();

        while (true) {
            line = input.readLine();
            if (line == null || line.isEmpty()) break;
            String keyHeader = line.substring(0, line.indexOf(SPLIT_WHITE_SPACE)).trim();
            String valueHeader = line.substring(line.indexOf(SPLIT_WHITE_SPACE)).trim();
            headers.put(keyHeader, valueHeader);
        }
        request.setHeaders(headers);
    }
}
