package test;

import com.tsa.server.domain.dto.Request;
import com.tsa.server.domain.dto.Response;
import com.tsa.server.domain.interfaces.ContentReader;
import com.tsa.server.domain.services.ContentReaderImpl;
import com.tsa.server.domain.services.RequestHandlerImpl;
import com.tsa.server.domain.util.HttpStatus;
import com.tsa.server.domain.util.RequestParser;
import com.tsa.server.domain.util.ResponseWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class RequestTest {

    @DisplayName("Test class: RequestParser; (sub)method: injectUri()")
    @Test
    void testInjectUri() {
        Request request = new Request();
        try (var input = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(requestText.getBytes())))) {
            RequestParser.injectUri(input, request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(request);
        assertEquals("Request{httpMethod=GET, uri='/', headers=null}", request.toString());
    }

    @DisplayName("Test class: RequestParser; (sub)method: injectHeaders()")
    @Test
    void testInjectHeaders() {
        Request request = new Request();
        try (var input = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(requestText.getBytes())))) {
            input.readLine();
            RequestParser.injectHeaders(input, request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(resultInjectHeaders, request.toString());
    }

    @DisplayName("Test class: RequestParser; (main)method: parseRequest()")
    @Test
    void testParseRequest() {
        Request request = RequestParser.parseRequest(new ByteArrayInputStream(requestText.getBytes()));
        assertEquals(requestParserResult, request.toString());
    }

    @DisplayName("Test class: ContentReaderImpl; (main)method: readContent()")
    @Test
    void testReadContent() {
        ContentReaderImpl contentReader = new ContentReaderImpl(Path.of("/src/main/resources"));
        try (InputStream inputStream = contentReader.readContent("text.txt")) {
            assertEquals(1633, inputStream.available());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("Test class: ResponseWriter; method: writeResponse()")
    @Test
    void testWriteResponse() {
        try (var input = new ByteArrayInputStream(resultResponseWriter.getBytes());
             var out = new ByteArrayOutputStream()) {
            ResponseWriter.writeResponse(input, out, new Response(HttpStatus.OK));
            assertTrue(out.toString().contains(resultResponseWriter) &&
                    out.toString().contains("HTTP/1.1 200 OK"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("Test class: ResponseWriter; method: writeErrorResponse()")
    @Test
    void testWriteErrorResponse() {
        try (var out = new ByteArrayOutputStream()) {
            ResponseWriter.writeErrorResponse(out, new Response(HttpStatus.BAD_REQUEST));
            assertEquals("HTTP/1.1 400 Bad Request\r\n\r\n", out.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("Test class: RequestHandlerImpl; (main)method: handle(); expected: text")
    @Test
    void testHandleOKText() {
        String result = initHandler("GET /text.txt HTTP/1.1\n");
        assertTrue(result.contains(resultHandler) && result.contains("HTTP/1.1 200 OK"));
    }

    @DisplayName("Test class: RequestHandlerImpl; (main)method: handle(); expected: (HTTP Method) NOT_IMPLEMENTED")
    @Test
    void testHandleHttpMethodNotImplemented() {
        assertThrows(RuntimeException.class,
                () -> initHandler("POST /text.txt HTTP/1.1\n"),
                "HTTP method is not implemented");
    }

    @DisplayName("Test class: RequestHandlerImpl; (main)method: handle(); expected: NOT_FOUND")
    @Test
    void testHandleHttpMethodNotFound() {
        assertThrows(RuntimeException.class,
                () -> initHandler("GET /text HTTP/1.1\n"),
                "FILE is not found");

    }

    private String initHandler(String request) {
        String result;
        ContentReader contentReader = new ContentReaderImpl(Path.of("/src/main/resources"));
        try (var input = new ByteArrayInputStream(request.getBytes());
             var out = new ByteArrayOutputStream()) {
            var requestHandler = new RequestHandlerImpl(input, out, contentReader);
            requestHandler.handle();
            result = out.toString();
            assertTrue(out.toString().contains(resultHandler) && out.toString().contains("HTTP/1.1 200 OK"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private final String requestText = "GET / HTTP/1.1\n" +
            "Host: localhost:3000\n" +
            "Connection: keep-alive\n" +
            "sec-ch-ua: \"Chromium\";v=\"106\", \"Google Chrome\";v=\"106\", \"Not;A=Brand\";v=\"99\"\n" +
            "sec-ch-ua-mobile: ?0\n" +
            "sec-ch-ua-platform: \"Windows\"\n" +
            "Upgrade-Insecure-Requests: 1\n" +
            "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36\n" +
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\n" +
            "Sec-Fetch-Site: none\n" +
            "Sec-Fetch-Mode: navigate\n" +
            "Sec-Fetch-User: ?1\n" +
            "Sec-Fetch-Dest: document\n" +
            "Accept-Encoding: gzip, deflate, br\n" +
            "Accept-Language: ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7\n" +
            "Cookie: UserSettings=language=1; __RequestVerificationToken_Lw__=k7sbQQaLLq5BWugYJXm6oo7t93kabxvXxZFGrrW3Tqov9fbi255P2iyYhf6vHnZ+Elf0cGqH5Z5M2zddLuf/MII2S/YBlBVFvybd7JWQsUbJR9CY3qfVD3/o6qdiF+HkpkX869d9D6KEriZymlhI+sxDP5MGBpLKQ/CguHksiSw=; ASP.NET_SessionId=1oyth351h3x2nnfzeyeesluy; JSESSIONID=983E4D01AEB39499EA78EA9B9BB63D5F; Idea-252ab8b1=4cfb5efc-d214-4ded-86f2-2c4da3afddcf\n" +
            "";
    private final String requestParserResult = "Request{httpMethod=GET, uri='/', headers={User-Agent:=Mozilla/5.0" +
            " (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36," +
            " Connection:=keep-alive, Upgrade-Insecure-Requests:=1, sec-ch-ua:=\"Chromium\";v=\"106\", \"Google Chrome\"" +
            ";v=\"106\", \"Not;A=Brand\";v=\"99\", sec-ch-ua-platform:=\"Windows\", Sec-Fetch-Dest:=document," +
            " Sec-Fetch-Mode:=navigate, Cookie:=UserSettings=language=1; __RequestVerificationToken_Lw__=k7sbQQaL" +
            "Lq5BWugYJXm6oo7t93kabxvXxZFGrrW3Tqov9fbi255P2iyYhf6vHnZ+Elf0cGqH5Z5M2zddLuf/MII2S/YBlBVFvybd7JWQsUbJ" +
            "R9CY3qfVD3/o6qdiF+HkpkX869d9D6KEriZymlhI+sxDP5MGBpLKQ/CguHksiSw=; ASP.NET_SessionId=1oyth351h3x2nnf" +
            "zeyeesluy; JSESSIONID=983E4D01AEB39499EA78EA9B9BB63D5F; Idea-252ab8b1=4cfb5efc-d214-4ded-86f2-2c4da" +
            "3afddcf, Sec-Fetch-Site:=none, Accept-Language:=ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7, sec-ch-ua-mobi" +
            "le:=?0, Accept:=text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/a" +
            "png,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9, Accept-Encoding:=gzip, deflate, br, Host:=loc" +
            "alhost:3000, Sec-Fetch-User:=?1}}";
    private final String resultInjectHeaders = "Request{httpMethod=null, uri='null', headers={User-Agent:=Mozilla/5.0 " +
            "(Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 " +
            "Safari/537.36, Connection:=keep-alive, Upgrade-Insecure-Requests:=1, sec-ch-ua:=\"Chromium\";" +
            "v=\"106\", \"Google Chrome\";v=\"106\", \"Not;A=Brand\";v=\"99\", sec-ch-ua-platform:=\"Windows\"," +
            " Sec-Fetch-Dest:=document, Sec-Fetch-Mode:=navigate, Cookie:=UserSettings=language=1; " +
            "__RequestVerificationToken_Lw__=k7sbQQaLLq5BWugYJXm6oo7t93kabxvXxZFGrrW3Tqov9fbi255P2iyYhf" +
            "6vHnZ+Elf0cGqH5Z5M2zddLuf/MII2S/YBlBVFvybd7JWQsUbJR9CY3qfVD3/o6qdiF+HkpkX869d9D6KEriZymlhI+" +
            "sxDP5MGBpLKQ/CguHksiSw=; ASP.NET_SessionId=1oyth351h3x2nnfzeyeesluy; JSESSIONID=983E4D01AEB" +
            "39499EA78EA9B9BB63D5F; Idea-252ab8b1=4cfb5efc-d214-4ded-86f2-2c4da3afddcf, Sec-Fetch-Site:=" +
            "none, Accept-Language:=ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7, sec-ch-ua-mobile:=?0, Accept:=t" +
            "ext/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q" +
            "=0.8,application/signed-exchange;v=b3;q=0.9, Accept-Encoding:=gzip, deflate, br, Host:=loca" +
            "lhost:3000, Sec-Fetch-User:=?1}}";
    private final String resultResponseWriter = "Hello world!!!";

    private final String resultHandler = "All rights reserved.";
}