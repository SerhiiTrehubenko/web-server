package com.tsa.server.domain.web;

import com.tsa.server.domain.interfaces.ContentReader;
import com.tsa.server.domain.interfaces.RequestHandler;
import com.tsa.server.domain.services.ContentReaderImpl;
import com.tsa.server.domain.services.RequestHandlerImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Path;

public class Server {
    private int port;
    private Path pathResources;
    private ContentReader contentReader;

    public Server() {
    }

    public void startServer() {
        int count = 0;
        try (var serverSocket = new ServerSocket(port)) {

            while (!serverSocket.isClosed()) {
                System.out.println("socket: " + count);
                try (var socket = serverSocket.accept()) {

                    RequestHandler requestHandler =
                            new RequestHandlerImpl(socket.getInputStream(),
                                    socket.getOutputStream(), contentReader);
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

    public void setPort(int port) {
        this.port = port;
    }

    public void setWebAppPath(Path pathResources) {
        contentReader = new ContentReaderImpl(pathResources);
        this.pathResources = pathResources;
    }

    public Path getPathResources() {
        return pathResources;
    }

}
