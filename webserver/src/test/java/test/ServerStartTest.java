package test;

import com.tsa.webserver.Server;
import org.junit.jupiter.api.Test;

class ServerStartTest {

    @Test
    void run() {
        Server server = new Server("src/main/resources", 3000);
        server.run();
    }
}