package com.tsa.server.domain.services;

import com.tsa.server.domain.dto.Response;
import com.tsa.server.domain.exceptions.WebServerException;
import com.tsa.server.domain.exceptions.WriteToSocketException;
import com.tsa.server.domain.interfaces.ContentReader;
import com.tsa.server.domain.util.HttpMethod;
import com.tsa.server.domain.util.ResponseWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class DefaultRequestHandlerTest {

    private final String startingLine = "GET /index.html HTTP/1.1\n";

    private final String headers = "Host: localhost:3000\r\n" +
            "User-Agent: Mozilla/5.0 (X11; U; Linux i686; ru; rv:1.9b5) Gecko/2008050509 Firefox/3.0b5\r\n" +
            "Accept: text/html\r\n" +
            "Connection: keep-alive\r\n";

    @Test
    void testCheckHttpMethodOnImplementationTrowsException() {
        var handler = new DefaultRequestHandler();
        assertThrows(WebServerException.class,
                () -> handler.checkHttpMethodOnImplementation(HttpMethod.DELETE));
    }

    @Test
    void testCheckHttpMethodOnImplementationDoesNotThrowException() {
        var handler = new DefaultRequestHandler();
        assertDoesNotThrow(() -> handler.checkHttpMethodOnImplementation(HttpMethod.GET));
    }

    @Test
    void testHandleResponseBadRequestWhenInvalidHeader() {
        String invalidStartingLine = "GET/index.html HTTP/1.1\n";
        var inputFromSocket = getInputFromSocket(invalidStartingLine + headers);
        var outputToSocket = getOutputStream();
        var contentReader = getContentReader();

        String expected = "HTTP/1.1 400 Bad Request\r\n\r\n";

        DefaultRequestHandler requestHandler = new DefaultRequestHandler(inputFromSocket, outputToSocket, contentReader);

        assertThrows(RuntimeException.class, requestHandler::handle);
        assertEquals(expected, outputToSocket.toString());

    }

    @Test
    void testHandleResponseBadRequestWhenInvalidStartingLine() {
        String invalidHeader = "User-Agent:Mozilla/5.0 (X11; U; Linux i686; ru; rv:1.9b5) Gecko/2008050509 Firefox/3.0b5\n";
        var inputFromSocket = getInputFromSocket(startingLine + invalidHeader);
        var outputToSocket = getOutputStream();
        var contentReader = getContentReader();

        String expected = "HTTP/1.1 400 Bad Request\r\n\r\n";

        DefaultRequestHandler requestHandler = new DefaultRequestHandler(inputFromSocket, outputToSocket, contentReader);

        assertThrows(RuntimeException.class, requestHandler::handle);
        assertEquals(expected, outputToSocket.toString());

    }

    @Test
    void testHandleResponseNotImplementedWhenHttpMethodIsNotImplemented() {
        String notImplementedHttpMethodRequest = "POST /index.html HTTP/1.1\n";
        var inputFromSocket = getInputFromSocket(notImplementedHttpMethodRequest + headers);
        var outputToSocket = getOutputStream();
        var contentReader = getContentReader();

        String expected = "HTTP/1.1 501 Not Implemented\r\n\r\n";

        DefaultRequestHandler requestHandler = new DefaultRequestHandler(inputFromSocket, outputToSocket, contentReader);

        assertThrows(RuntimeException.class, requestHandler::handle);
        assertEquals(expected, outputToSocket.toString());
    }

    @Test
    void testHandleResponseNotFoundWhenThereIsNoFileWithProvidedUri() {
        String httpRequestWithInvalidUri = "GET /index HTTP/1.1\n";
        var inputFromSocket = getInputFromSocket(httpRequestWithInvalidUri + headers);
        var outputToSocket = getOutputStream();
        var contentReader = getContentReader();

        String expected = "HTTP/1.1 404 Not Found\r\n\r\n";

        DefaultRequestHandler requestHandler = new DefaultRequestHandler(inputFromSocket, outputToSocket, contentReader);

        assertThrows(RuntimeException.class, requestHandler::handle);
        assertEquals(expected, outputToSocket.toString());
    }

    /**
     * It is possible to generate an Exception on static method ResponseWriter.writeSuccessResponse() but when
     * it is time to write ResponseWriter.writeErrorResponse(INTERNAL_SERVER_ERROR) this method does not see OutputToSocket ->
     * I was not able to resolve this problem. (What can you suggest?)
     */
    @Test
    void testHandleResponseInternalServerErrorWhenProblemsDuringReadingFile() {
        var inputFromSocket = getInputFromSocket(startingLine + headers);
        var outputToSocket = getOutputStream();
        var contentReader = getContentReader();

        String expected = "500 Internal Server Error\r\n\r\n";
        try (MockedStatic<ResponseWriter> mockedStatic = Mockito.mockStatic(ResponseWriter.class)) {
            mockedStatic.when(
                    () -> ResponseWriter.writeSuccessResponse(
                            any(InputStream.class), any(OutputStream.class), any(Response.class))).thenThrow(new WriteToSocketException());
            mockedStatic.when(
                    () -> ResponseWriter.writeErrorResponse(
                            any(OutputStream.class), any(Response.class))).thenCallRealMethod();
            DefaultRequestHandler requestHandler = new DefaultRequestHandler(inputFromSocket, outputToSocket, contentReader);
            assertThrows(RuntimeException.class, requestHandler::handle);
//            assertEquals(expected, outputToSocket.toString());
        }
    }

    @Test
    void testHandleWhenEverythingIsCorrect() {
        String expected = "HTTP/1.1 200 OK\r\n\r\nHello world!";
        String startingLine = "GET /for-test.txt HTTP/1.1\n";
        var inputFromSocket = getInputFromSocket(startingLine + headers);
        var outputToSocket = getOutputStream();
        var contentReader = getContentReader();

        DefaultRequestHandler requestHandler = new DefaultRequestHandler(inputFromSocket, outputToSocket, contentReader);
        assertDoesNotThrow(requestHandler::handle);
        assertEquals(expected, outputToSocket.toString());
    }

    private InputStream getInputFromSocket(String httpRequest) {
        return new ByteArrayInputStream(httpRequest.getBytes());
    }

    private ByteArrayOutputStream getOutputStream() {
        return new ByteArrayOutputStream();
    }

    private ContentReader getContentReader() {
        return new DefaultContentReader("src/main/resources");
    }

}