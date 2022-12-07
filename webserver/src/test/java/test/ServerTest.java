package test;

import com.tsa.server.domain.web.Server;
import org.junit.jupiter.api.Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

    @Test
    void startServer() {
        var server = new Server(3000, "/src/main/resources");
        server.startServer();
    }
}