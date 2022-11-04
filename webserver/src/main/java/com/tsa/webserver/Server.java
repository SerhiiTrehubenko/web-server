package com.tsa.webserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {
    private final static byte[] CRLF = "\r\n".getBytes();
    private final static String ROOT_PATH = Path.of("").toAbsolutePath().toString();
    public final static String HOME_REQUEST = "GET /index.html HTTP/1.1";
    public final static String IMAGE_SOURCE_REQUEST = "GET /image/Windows.jpg HTTP/1.1";
    public final static String CSS_REQUEST = "GET /css/styles.css HTTP/1.1";
    private final static String OK = "HTTP/1.1 200 OK";
    private final static String NOT_FOUND = "HTTP/1.1 404 Not Found";
    //    private final static String SEVER_ERROR = "HTTP/1.1 500 Internal Server Error";
    private final static byte[] BUFFER = new byte[8 * 1024];
    private static final List<String> REQEST = new ArrayList<>();

    public void setPort(int port) {
        this.port = port;
    }

    public void setWebAppPath(String webAppPath) {
        this.webAppPath = webAppPath;
    }

    private String webAppPath;

    private int port;

    @Override
    public void run() {

        try (var server = new ServerSocket(port)) {
            while (!server.isClosed()) {
                try (var socket = server.accept();
                     var input = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String line;
                    while (!(line = input.readLine()).isEmpty()) {
                        REQEST.add(line);
                        System.out.println(line);
                    }
                    String request = REQEST.get(0).split(" ")[1];
                    responce(request, socket);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                REQEST.clear();
                System.out.println("******************");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void responseError(Socket socket, String message) {
        try (var outImage = new DataOutputStream(socket.getOutputStream())) {
            outImage.write(message.getBytes());
            outImage.write(CRLF);
            outImage.write(CRLF);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void responce(String filePath, Socket socket) {
        try (var inputFile = new FileInputStream(Paths.get(ROOT_PATH, webAppPath, filePath).toFile());
             var outImage = new DataOutputStream(socket.getOutputStream())) {
            outImage.write(Server.OK.getBytes());
            outImage.write(CRLF);
            outImage.write(CRLF);
            int count;
            while ((count = inputFile.read(BUFFER)) != -1) {
                outImage.write(BUFFER, 0, count);
                outImage.flush();
            }
        } catch (Exception e) {
            responseError(socket, NOT_FOUND);
            throw new RuntimeException(e);
        }
    }

}
