package test;

import com.tsa.webserver.WebClient;
import org.junit.jupiter.api.Test;

import static com.tsa.webserver.Server.*;
import static org.junit.jupiter.api.Assertions.*;

class ServerTest {
    WebClient webClient = new WebClient();

    @Test
    void testHomePage() {
        webClient.request(HOME_REQUEST);
        String result = webClient.getResult();
        assertTrue(result.contains("HTTP/1.1 200 OK"));
        assertTrue(result.contains("<h1>Hello world!!!</h1>"));
    }

    @Test
    void testCssFile() {
        webClient.request(CSS_REQUEST);
        String result = webClient.getResult();
        assertTrue(result.contains("HTTP/1.1 200 OK"));
        assertTrue(result.contains("text-align: center;"));
    }

    @Test
    void testImage() {
        webClient.request(IMAGE_SOURCE_REQUEST);
        assertEquals(928054, webClient.getLength());
    }

    @Test
    void testNotFound() {
        webClient.request("GET /Hello");
        String result = webClient.getResult();
        assertTrue(result.contains("HTTP/1.1 404 Not Found"));
    }

//    @Test
//    void TestServerError() {
//        webClient.request(SERVER_ERROR_REQUEST);
//        String result = webClient.getResult();
//        assertTrue(result.contains("HTTP/1.1 500 Internal Server Error"));
//    }
}