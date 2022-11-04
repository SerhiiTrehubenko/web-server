package test;

import com.tsa.webserver.Server;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServerStartTest {

    @Test
    void run() {
        Server server = new Server();
        server.setPort(3000);
        server.setWebAppPath("src/main/resources");
        server.run();
    }
}