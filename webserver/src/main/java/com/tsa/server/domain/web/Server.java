package com.tsa.server.domain.web;

import com.tsa.server.domain.interfaces.ContentReader;
import com.tsa.server.domain.interfaces.RequestHandler;
import com.tsa.server.domain.services.DefaultContentReader;
import com.tsa.server.domain.services.DefaultRequestHandler;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    private final int port;
    private final String webAppPath;

    public Server(int port, String webAppPath) {
        this.port = port;
        this.webAppPath = webAppPath;
    }

    public void start() {
        int count = 0;
        ContentReader contentReader = new DefaultContentReader(webAppPath);
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
