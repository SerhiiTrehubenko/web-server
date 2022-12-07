package com.tsa.server.domain.web;

import com.tsa.server.domain.interfaces.ContentReader;
import com.tsa.server.domain.interfaces.RequestHandler;
import com.tsa.server.domain.services.DefaultContentReader;
import com.tsa.server.domain.services.DefaultRequestHandler;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    private final int port;
    private final ContentReader contentReader;

    public Server(int port, String path) {
        this.port = port;
        this.contentReader = new DefaultContentReader(path);
    }

    public void startServer() {
        int count = 0;
        try (var serverSocket = new ServerSocket(port)) {

            while (!serverSocket.isClosed()) {
                System.out.println("socket: " + count);
                try (var socket = serverSocket.accept();
                     var inputSocket = socket.getInputStream();
                     var outputSocket = socket.getOutputStream()) {

                    RequestHandler requestHandler =
                            new DefaultRequestHandler(inputSocket,
                                    outputSocket, contentReader);
                    requestHandler.handle();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                count++;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
