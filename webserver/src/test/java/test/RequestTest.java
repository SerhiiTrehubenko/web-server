package test;

import com.tsa.server.domain.dto.Request;
import com.tsa.server.domain.dto.Response;
import com.tsa.server.domain.interfaces.ContentReader;
import com.tsa.server.domain.services.DefaultContentReader;
import com.tsa.server.domain.services.DefaultRequestHandler;
import com.tsa.server.domain.util.HttpStatus;
import com.tsa.server.domain.util.RequestParser;
import com.tsa.server.domain.util.ResponseWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class RequestTest {

    private final String requestText = "GET / HTTP/1.1\n" +
            "Host: localhost:3000\n" +
            "Connection: keep-alive\n";

    private final String resultResponseWriter = "Hello world!!!";

    private final String resultHandler = "All rights reserved.";

    @DisplayName("Test class: RequestParser; (sub)method: injectUri()")
    @Test
    void testInjectUri() {
        String httpMethod = "GET";
        String uri = "/";
        Request request = new Request();
        try (var input = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(requestText.getBytes())))) {
            RequestParser.injectUri(input, request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(httpMethod, request.getHttpMethod().toString());
        assertEquals(uri, request.getUri());
    }

    @DisplayName("Test class: RequestParser; (sub)method: injectHeaders()")
    @Test
    void testInjectHeaders() throws Exception {
        String headKey = "Host:";
        String headValue = "localhost:3000";
        Request request = new Request();
        try (var input = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(requestText.getBytes())))) {
            input.readLine();
            RequestParser.injectHeaders(input, request);
        }
        assertEquals(headValue, request.getHeaders().get(headKey));
    }

    @DisplayName("Test class: RequestParser; (main)method: parseRequest()")
    @Test
    void testParseRequest() {
        String httpMethod = "GET";
        String uri = "/";
        String headKey = "Host:";
        String headValue = "localhost:3000";

        Request request = RequestParser.parseRequest(new ByteArrayInputStream(requestText.getBytes()));

        assertEquals(httpMethod, request.getHttpMethod().toString());
        assertEquals(uri, request.getUri());
        assertEquals(headValue, request.getHeaders().get(headKey));
    }

    @DisplayName("Test class: DefaultContentReader; (main)method: readContent()")
    @Test
    void testReadContent() throws Exception {
        DefaultContentReader contentReader = new DefaultContentReader("/src/main/resources");
        try (InputStream inputStream = contentReader.getConnectionToContent("text.txt")) {
            assertEquals(1633, inputStream.available());
        }
    }

    @DisplayName("Test class: ResponseWriter; method: writeResponse()")
    @Test
    void testWriteResponse() throws Exception {
        try (var input = new ByteArrayInputStream(resultResponseWriter.getBytes());
             var out = new ByteArrayOutputStream()) {

            ResponseWriter.writeSuccessResponse(input, out, new Response(HttpStatus.OK));

            assertTrue(out.toString().contains(resultResponseWriter) &&
                    out.toString().contains("HTTP/1.1 200 OK"));
        }
    }

    @DisplayName("Test class: ResponseWriter; method: writeErrorResponse()")
    @Test
    void testWriteErrorResponse() throws Exception {
        try (var out = new ByteArrayOutputStream()) {
            ResponseWriter.writeErrorResponse(out, new Response(HttpStatus.BAD_REQUEST));
            assertEquals("HTTP/1.1 400 Bad Request\r\n\r\n", out.toString());
        }
    }

    @DisplayName("Test class: DefaultRequestHandler; (main)method: handle(); expected: text")
    @Test
    void testHandleOKText() {
        String result = initHandler("GET /text.txt HTTP/1.1\n");
        assertTrue(result.contains(resultHandler) && result.contains("HTTP/1.1 200 OK"));
    }

    @DisplayName("Test class: DefaultRequestHandler; (main)method: handle(); expected: (HTTP Method) NOT_IMPLEMENTED")
    @Test
    void testHandleHttpMethodNotImplemented() {
        assertThrows(RuntimeException.class,
                () -> initHandler("POST /text.txt HTTP/1.1\n"),
                "HTTP method is not implemented");
    }

    @DisplayName("Test class: DefaultRequestHandler; (main)method: handle(); expected: NOT_FOUND")
    @Test
    void testHandleHttpMethodNotFound() {
        assertThrows(RuntimeException.class,
                () -> initHandler("GET /text HTTP/1.1\n"),
                "FILE is not found");

    }

    private String initHandler(String request) {
        String result;
        ContentReader contentReader = new DefaultContentReader("/src/main/resources");
        try (var input = new ByteArrayInputStream(request.getBytes());
             var out = new ByteArrayOutputStream()) {
            var requestHandler = new DefaultRequestHandler(input, out, contentReader);
            requestHandler.handle();
            result = out.toString();
            assertTrue(out.toString().contains(resultHandler) && out.toString().contains("HTTP/1.1 200 OK"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}