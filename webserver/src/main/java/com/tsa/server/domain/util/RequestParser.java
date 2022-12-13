package com.tsa.server.domain.util;

import com.tsa.server.domain.dto.Request;
import com.tsa.server.domain.exceptions.WebServerException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class RequestParser {
    private final static String WHITE_SPACE_DELIMITER = " ";
    private final static Pattern STARTING_LINE_FORMAT = Pattern.compile("\\b[GETPOSDLUACH]{3,6} [!-/\\w\\d:-@]+ HTTP/\\d{1,3}\\.\\d{1,3}");
    private final static Pattern HEADER_FORMAT = Pattern.compile("\\b[\\w\\d-]+: [!-/\\w\\d:-@ ]+");


    public static Request parseRequest(InputStream inputFromSocket) throws WebServerException {
        Request request = new Request();

        var inputSocket = new BufferedReader(new InputStreamReader(inputFromSocket));
        injectUri(inputSocket, request);
        injectHeaders(inputSocket, request);

        return request;
    }

    static void injectUri(BufferedReader inputFromSocket, Request request) throws WebServerException {

        String startingLine = invalidStartingLineThrowsWebServerException(inputFromSocket);

        String[] splitStartingLine = startingLine.split(WHITE_SPACE_DELIMITER);

        String httpMethodFromStartingLine = splitStartingLine[0];
        invalidHttpMethodThrowsWebServerException(httpMethodFromStartingLine);

        request.setHttpMethod(httpMethodFromStartingLine);

        String uriFromStartingLine = splitStartingLine[1];
        request.setUri(uriFromStartingLine);
    }

    static void injectHeaders(BufferedReader inputFromSocket, Request request) throws WebServerException {
        Map<String, String> headers = new HashMap<>();
        try {
            String headerLine = inputFromSocket.readLine();

            while (headerLine != null && !headerLine.trim().isEmpty()) {
                invalidHeaderFormatThrowsIllegalArgumentException(headerLine);

                String headerTag = headerLine.substring(0, headerLine.indexOf(WHITE_SPACE_DELIMITER)).trim();
                String headerDescription = headerLine.substring(headerLine.indexOf(WHITE_SPACE_DELIMITER)).trim();
                headers.put(headerTag, headerDescription);

                headerLine = inputFromSocket.readLine();
            }
            request.setHeaders(headers);
        } catch (IOException e) {
            throw new WebServerException(e, HttpStatus.BAD_REQUEST);
        }
    }

    static String invalidStartingLineThrowsWebServerException(BufferedReader inputFromSocket) throws WebServerException {
        String startingLine = null;
        try {
            startingLine = inputFromSocket.readLine();
            Objects.requireNonNull(startingLine, "Starting Line is null");
            if (!STARTING_LINE_FORMAT.matcher(startingLine).matches()) {
                throw new WebServerException("Starting Line does not comply to conventional format: "
                        + startingLine, HttpStatus.BAD_REQUEST);
            }
            return startingLine;
        } catch (NullPointerException | IOException e) {
            throw new WebServerException("Starting Line does not comply to conventional format: "
                    + startingLine, HttpStatus.BAD_REQUEST);
        }
    }

    static void invalidHttpMethodThrowsWebServerException(String httpMethod) throws WebServerException {
        for (HttpMethod httpMethodInEnum : HttpMethod.values()) {
            if (Objects.equals(httpMethod, httpMethodInEnum.toString())) {
                return;
            }
        }
        throw new WebServerException("Provided http method is invalid: " + httpMethod, HttpStatus.BAD_REQUEST);
    }

    static void invalidHeaderFormatThrowsIllegalArgumentException(String header) throws WebServerException {
        if (!HEADER_FORMAT.matcher(header).matches()) {
            throw new WebServerException("Invalid header: " + header, HttpStatus.BAD_REQUEST);
        }
    }
}
