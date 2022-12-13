package com.tsa.server.domain.util;

import com.tsa.server.domain.web.Server;
import org.junit.jupiter.api.Test;

class ServerStartTest {

    @Test
    void startServer() {
        var server = new Server(3000, "/src/main/resources");
        server.start();
    }
}