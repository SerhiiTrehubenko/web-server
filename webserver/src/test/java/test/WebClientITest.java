package test;

import com.tsa.webserver.WebClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;



class WebClientITest {
    public static final String HOME_REQUEST = "GET /index.html HTTP/1.1";
    public static final String IMAGE_SOURCE_REQUEST = "GET /image/Windows.jpg HTTP/1.1";
    public static final String CSS_REQUEST = "GET /css/styles.css HTTP/1.1";
    WebClient webClient = new WebClient(3000);

    @Test
    void testHomePage() {
        webClient.request(HOME_REQUEST);
        String result = webClient.getBufferAsString();
        assertTrue(result.contains("HTTP/1.1 200 OK"));
        assertTrue(result.contains("<h1>Hello world!!!</h1>"));
    }

    @Test
    void testCssFile() {
        webClient.request(CSS_REQUEST);
        String result = webClient.getBufferAsString();
        assertTrue(result.contains("HTTP/1.1 200 OK"));
        assertTrue(result.contains("text-align: center;"));
    }

    @Test
    void testImage() {
        webClient.request(IMAGE_SOURCE_REQUEST);
        assertEquals(928056, webClient.getBufferLength());
    }

    @Test
    void testNotFound() {
        webClient.request("GET /Hello");
        String result = webClient.getBufferAsString();
        assertTrue(result.contains("HTTP/1.1 404 Not Found"));
    }
}