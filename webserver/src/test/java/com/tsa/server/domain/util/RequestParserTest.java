package com.tsa.server.domain.util;

import com.tsa.server.domain.dto.Request;
import com.tsa.server.domain.exceptions.WebServerException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class RequestParserTest {

    private final String browserRequest = "GET /index.html HTTP/1.1\n" +
            "Host: localhost:3000\r\n" +
            "User-Agent: Mozilla/5.0 (X11; U; Linux i686; ru; rv:1.9b5) Gecko/2008050509 Firefox/3.0b5\r\n" +
            "Accept: text/html\r\n" +
            "Connection: keep-alive\r\n";

    @DisplayName("Test invalidStartingLineThrowsWebServerException() - NullPointerException is thrown when StartingLine is null")
    @Test
    void testInvalidStartingLineThrowsWebServerExceptionThrowsExceptionWhenStartingLineIsNull() {
        assertThrows(WebServerException.class,
                () -> RequestParser.invalidStartingLineThrowsWebServerException(null));
    }

    @DisplayName("Test invalidStartingLineThrowsWebServerException() - WebServerException is thrown when StartingLine does not comply" +
            " to a conventional format White Spaces Are Absent In HttpRequest (case: GET/index.htmlHTTP/1.1)")
    @Test
    void testInvalidStartingLineThrowsWebServerExceptionThrowsExceptionWhenWhiteSpacesAreAbsentInHttpRequest() {
        String startingLine = "GET/index.htmlHTTP/1.1";

        assertThrows(WebServerException.class,
                () -> RequestParser.invalidStartingLineThrowsWebServerException(getBufferedReader(startingLine)));
    }

    @DisplayName("Test invalidStartingLineThrowsWebServerException() - WebServerException is thrown when StartingLine does not comply" +
            " to a conventional format One White Space Is Present After HttpMethod (case: GET /index.htmlHTTP/1.1)")
    @Test
    void testInvalidStartingLineThrowsWebServerExceptionThrowsExceptionWhenOneWhiteSpaceIsPresentAfterHttpMethod() {
        String startingLine = "GET /index.htmlHTTP/1.1";

        assertThrows(WebServerException.class,
                () -> RequestParser.invalidStartingLineThrowsWebServerException(getBufferedReader(startingLine)));
    }

    @DisplayName("Test invalidStartingLineThrowsWebServerException() - WebServerException is thrown when StartingLine does not comply" +
            " to a conventional format One White Space Is Present After Uri (case: GET/index.html HTTP/1.1)")
    @Test
    void testInvalidStartingLineThrowsWebServerExceptionThrowsExceptionWhenOneWhiteSpaceIsPresentAfterUri() {
        String startingLine = "GET/index.html HTTP/1.1";

        assertThrows(WebServerException.class,
                () -> RequestParser.invalidStartingLineThrowsWebServerException(getBufferedReader(startingLine)));
    }

    @DisplayName("Test invalidStartingLineThrowsWebServerException() - WebServerException is thrown when StartingLine does not comply" +
            " to a conventional format Double White Space Are Present Instead of Single (case: GET/index.html HTTP/1.1)")
    @Test
    void testInvalidStartingLineThrowsWebServerExceptionThrowsExceptionWhenDoubleWhiteSpaceArePresentInsteadOfSingle() {
        String startingLine = "GET  /index.html  HTTP/1.1";

        assertThrows(WebServerException.class,
                () -> RequestParser.invalidStartingLineThrowsWebServerException(getBufferedReader(startingLine)));
    }

    @DisplayName("Test invalidStartingLineThrowsWebServerException() - WebServerException is thrown when StartingLine does not comply" +
            " to a conventional format HttpMethod is absent (case: /index.html HTTP/1.1)")
    @Test
    void testInvalidStartingLineThrowsWebServerExceptionThrowsExceptionWhenHttpMethodIsAbsent() {
        String startingLine = " /index.html HTTP/1.1";

        assertThrows(WebServerException.class,
                () -> RequestParser.invalidStartingLineThrowsWebServerException(getBufferedReader(startingLine)));
    }

    @DisplayName("Test invalidStartingLineThrowsWebServerException() - WebServerException is thrown when StartingLine does not comply" +
            " to a conventional format Uri is absent (case: GET HTTP/1.1)")
    @Test
    void testInvalidStartingLineThrowsWebServerExceptionThrowsExceptionWhenUriIsAbsent() {
        String startingLine = "GET HTTP/1.1";

        assertThrows(WebServerException.class,
                () -> RequestParser.invalidStartingLineThrowsWebServerException(getBufferedReader(startingLine)));
    }

    @DisplayName("Test invalidStartingLineThrowsWebServerException() - WebServerException is thrown when StartingLine does not comply" +
            " to a conventional format HTTP version is absent (case: GET /index.html )")
    @Test
    void testInvalidStartingLineThrowsWebServerExceptionThrowsExceptionWhenHttpVersionIsAbsent() {
        String startingLine = "GET /index.html ";

        assertThrows(WebServerException.class,
                () -> RequestParser.invalidStartingLineThrowsWebServerException(getBufferedReader(startingLine)));
    }

    @DisplayName("Test invalidStartingLineThrowsWebServerException() - WebServerException is thrown when StartingLine does not comply" +
            " to a conventional format HTTP does not have a version (case: GET /index.html HTTP/)")
    @Test
    void testInvalidStartingLineThrowsWebServerExceptionThrowsExceptionWhenHttpDoesNotHaveAVersion() {
        String startingLine = "GET /index.html HTTP/";

        assertThrows(WebServerException.class,
                () -> RequestParser.invalidStartingLineThrowsWebServerException(getBufferedReader(startingLine)));
    }

    @DisplayName("Test invalidStartingLineThrowsWebServerException() - WebServerException is thrown when StartingLine does not comply" +
            " to a conventional format a HTTP version does not have a dot as delimiter (case: GET /index.html HTTP/11)")
    @Test
    void testInvalidStartingLineThrowsWebServerExceptionThrowsExceptionWhenHttpVersionWithoutDotDelimiter() {
        String startingLine = "GET /index.html HTTP/11";

        assertThrows(WebServerException.class,
                () -> RequestParser.invalidStartingLineThrowsWebServerException(getBufferedReader(startingLine)));
    }

    @DisplayName("Test invalidStartingLineThrowsWebServerException() - WebServerException is thrown when StartingLine does not comply" +
            " to a conventional format a HTTP version does not have a decimal part (case: GET /index.html HTTP/1.)")
    @Test
    void testInvalidStartingLineThrowsWebServerExceptionThrowsExceptionWhenHttpVersionWithoutDecimalPart() {
        String startingLine = "GET /index.html HTTP/1.";

        assertThrows(WebServerException.class,
                () -> RequestParser.invalidStartingLineThrowsWebServerException(getBufferedReader(startingLine)));
    }

    @DisplayName("Test invalidStartingLineThrowsWebServerException() - WebServerException is thrown when StartingLine does not comply" +
            " to a conventional format a HTTP version does not have a integer part (case: GET /index.html HTTP/.1)")
    @Test
    void testInvalidStartingLineThrowsWebServerExceptionThrowsExceptionWhenHttpVersionWithoutIntegerPart() {
        String startingLine = "GET /index.html HTTP/.1";

        assertThrows(WebServerException.class,
                () -> RequestParser.invalidStartingLineThrowsWebServerException(getBufferedReader(startingLine)));
    }

    @DisplayName("Test invalidStartingLineThrowsWebServerException() - WebServerException is thrown when StartingLine does not comply" +
            " to a conventional format a HTTP version integer part can not include more then three numbers (case: GET /index.html HTTP/1111.1)")
    @Test
    void testInvalidStartingLineThrowsWebServerExceptionThrowsExceptionWhenHttpVersionIntegerPartNoMoreThenThreeNumber() {
        String startingLine = "GET /index.html HTTP/1111.1";

        assertThrows(WebServerException.class,
                () -> RequestParser.invalidStartingLineThrowsWebServerException(getBufferedReader(startingLine)));
    }

    @DisplayName("Test invalidStartingLineThrowsWebServerException() - WebServerException is thrown when StartingLine does not comply" +
            " to a conventional format a HTTP version decimal part can not include more then three numbers (case: GET /index.html HTTP/111.2222)")
    @Test
    void testInvalidStartingLineThrowsWebServerExceptionThrowsExceptionWhenHttpVersionDecimalPartNoMoreThenThreeNumber() {
        String startingLine = "GET /index.html HTTP/111.2222";

        assertThrows(WebServerException.class,
                () -> RequestParser.invalidStartingLineThrowsWebServerException(getBufferedReader(startingLine)));
    }

    @DisplayName("Test invalidStartingLineThrowsWebServerException() - WebServerException is thrown when StartingLine does not comply" +
            " to a conventional format a HTTP method cannot have less then three letters (case: GE /index.html HTTP/1.1)")
    @Test
    void testInvalidStartingLineThrowsWebServerExceptionThrowsExceptionWhenHttpMethodNoLessThreeLetters() {
        String startingLine = "GE /index.html HTTP/1.1";

        assertThrows(WebServerException.class,
                () -> RequestParser.invalidStartingLineThrowsWebServerException(getBufferedReader(startingLine)));
    }

    @DisplayName("Test invalidStartingLineThrowsWebServerException() - WebServerException is thrown when StartingLine does not comply" +
            " to a conventional format a HTTP method cannot have more then six letters (case: DELETEEEE /index.html HTTP/1.1)")
    @Test
    void testInvalidStartingLineThrowsWebServerExceptionThrowsExceptionWhenHttpMethodHasMoreThenSixLetters() {
        String startingLine = "DELETEEEE /index.html HTTP/1.1";

        assertThrows(WebServerException.class,
                () -> RequestParser.invalidStartingLineThrowsWebServerException(getBufferedReader(startingLine)));
    }

    @DisplayName("Test invalidStartingLineThrowsWebServerException() - WebServerException is thrown when StartingLine does not comply" +
            " to a conventional format a HTTP method can have only certain letters [GETPOSDLUACH] (case: DKLETE /index.html HTTP/1.1)")
    @Test
    void testInvalidStartingLineThrowsWebServerExceptionThrowsExceptionWhenHttpMethodHasOnlyCertainLetters() {
        String startingLine = "DKLETE /index.html HTTP/1.1";

        assertThrows(WebServerException.class,
                () -> RequestParser.invalidStartingLineThrowsWebServerException(getBufferedReader(startingLine)));
    }

    @DisplayName("Test invalidStartingLineThrowsWebServerException() - WebServerException is thrown when StartingLine does not comply" +
            " to a conventional format a HTTP request cannot begin with white space [GETPOSDLUACH] (case: _DELETE /index.html HTTP/1.1)")
    @Test
    void testInvalidStartingLineThrowsWebServerExceptionThrowsExceptionWhenHttpRequestCannotBeginWithWhiteSpace() {
        String startingLine = " DELETE /index.html HTTP/1.1";

        assertThrows(WebServerException.class,
                () -> RequestParser.invalidStartingLineThrowsWebServerException(getBufferedReader(startingLine)));
    }

    @DisplayName("Test invalidHeaderFormatThrowsWebServerException(), throws WebServerException when header has a white space as a delimiter in the first part")
    @Test
    void testInvalidHeaderFormatThrowsWebServerExceptionWhiteSpaceInTheFirstPart() {
        String invalidHeader = "User Agent: Mozilla/5.0 (X11; U; Linux i686; ru; rv:1.9b5) Gecko/2008050509 Firefox/3.0b5";

        assertThrows(WebServerException.class, () -> RequestParser.invalidHttpMethodThrowsWebServerException(invalidHeader));
    }

    @DisplayName("Test invalidHeaderFormatThrowsWebServerException(), throws WebServerException when header does not have a white space after a semicolon")
    @Test
    void testInvalidHeaderFormatThrowsWebServerExceptionWhiteSpaceAfterSemicolonIsAbsent() {
        String invalidHeader = "User-Agent:Mozilla/5.0 (X11; U; Linux i686; ru; rv:1.9b5) Gecko/2008050509 Firefox/3.0b5";

        assertThrows(WebServerException.class, () -> RequestParser.invalidHttpMethodThrowsWebServerException(invalidHeader));
    }

    @DisplayName("Test invalidHeaderFormatThrowsWebServerException(), throws WebServerException when header does not have a semicolon as a delimiter")
    @Test
    void testInvalidHeaderFormatThrowsWebServerExceptionSemicolonIsAbsent() {
        String invalidHeader = "User-Agent Mozilla/5.0 (X11; U; Linux i686; ru; rv:1.9b5) Gecko/2008050509 Firefox/3.0b5";

        assertThrows(WebServerException.class, () -> RequestParser.invalidHttpMethodThrowsWebServerException(invalidHeader));
    }

    @DisplayName("Test invalidHeaderFormatThrowsWebServerException(), throws WebServerException when header begins with white space")
    @Test
    void testInvalidHeaderFormatThrowsWebServerExceptionBeginsWithWhiteSpace() {
        String invalidHeader = " User-Agent: Mozilla/5.0 (X11; U; Linux i686; ru; rv:1.9b5) Gecko/2008050509 Firefox/3.0b5";

        assertThrows(WebServerException.class, () -> RequestParser.invalidHttpMethodThrowsWebServerException(invalidHeader));
    }

    @DisplayName("Test invalidHttpMethodThrowsWebServerException() - throws exception when invalid Http method is provided")
    @Test
    void invalidHttpMethodThrowsWebServerException() {
        String httpMethod = "INSERT";

        assertThrows(WebServerException.class, () -> RequestParser.invalidHttpMethodThrowsWebServerException(httpMethod));
    }

    @DisplayName("Test injectUri() - parse valid http request")
    @Test
    void testInjectUriParseValidHttpRequest() throws WebServerException {
        String httpMethod = "GET";
        String uri = "/index.html";
        Request request = getRequestInstance();

        var input = getBufferedReader(browserRequest);
        RequestParser.injectUri(input, request);


        assertEquals(httpMethod, request.getHttpMethod().toString());
        assertEquals(uri, request.getUri());
    }

    @DisplayName("Test injectHeaders()")
    @Test
    void testInjectHeaders() throws Exception {
        String headKey = "User-Agent:";
        String headValue = "Mozilla/5.0 (X11; U; Linux i686; ru; rv:1.9b5) Gecko/2008050509 Firefox/3.0b5";
        Request request = getRequestInstance();

        var input = getBufferedReader(browserRequest);
        input.readLine();
        RequestParser.injectHeaders(input, request);

        assertEquals(headValue, request.getHeaders().get(headKey));
    }

    @DisplayName("Test parseRequest()")
    @Test
    void testParseRequest() throws WebServerException {
        String httpMethod = "GET";
        String uri = "/index.html";
        String headKey = "User-Agent:";
        String headValue = "Mozilla/5.0 (X11; U; Linux i686; ru; rv:1.9b5) Gecko/2008050509 Firefox/3.0b5";

        Request request = RequestParser.parseRequest(new ByteArrayInputStream(browserRequest.getBytes()));

        assertEquals(httpMethod, request.getHttpMethod().toString());
        assertEquals(uri, request.getUri());
        assertEquals(headValue, request.getHeaders().get(headKey));
    }

    private Request getRequestInstance() {
        return new Request();
    }

    private BufferedReader getBufferedReader(String browserRequest) {
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(browserRequest.getBytes())));
    }
}