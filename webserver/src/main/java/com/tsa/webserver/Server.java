package com.tsa.webserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {

    private final static String ROOT_PATH = Path.of("").toAbsolutePath().toString();
    public final static String HOME_REQUEST = "GET / HTTP/1.1";
    private final static String IMAGE_REQUEST = "GET /image HTTP/1.1";
    public final static String SERVER_ERROR_REQUEST = "GET /error HTTP/1.1";
    public final static String IMAGE_SOURCE_REQUEST = "GET /image/Windows.jpg HTTP/1.1";
    private final static String TEXT_REQUEST = "GET /text HTTP/1.1";
    public final static String CSS_REQUEST = "GET /css/styles.css HTTP/1.1";
    private final static String HOME_PAGE = "src/main/resources/index.html";
    private final static String IMAGE_PAGE = "src/main/resources/image.html";
    private final static String TEXT_PAGE = "src/main/resources/text.html";
    private final static String CSS_SOURCE = "src/main/resources/css/styles.css";
    public final static String IMAGE_SOURCE = "src/main/resources/image/Windows.jpg";

    private final static String OK = "HTTP/1.1 200 OK";
    private final static String NOT_FOUND = "HTTP/1.1 404 Not Found";
    private final static String SEVER_ERROR = "HTTP/1.1 500 Internal Server Error";
    private final static byte[] BUFFER = new byte[8 * 1024];
    private static final List<String> REQEST = new ArrayList<>();

    @Override
    public void run() {

        try (var server = new ServerSocket(3000)) {
            while (!server.isClosed()) {
                try (var socket = server.accept();
                     var input = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String line;
                    while (!(line = input.readLine()).isEmpty()) {
                        REQEST.add(line);
                        System.out.println(line);
                    }
                    switch (REQEST.get(0)) {
                        case HOME_REQUEST -> responce(HOME_PAGE, socket);
                        case IMAGE_REQUEST -> responce(IMAGE_PAGE, socket);
                        case IMAGE_SOURCE_REQUEST -> responce(IMAGE_SOURCE, socket);
                        case CSS_REQUEST -> responce(CSS_SOURCE, socket);
                        case TEXT_REQUEST -> responce(TEXT_PAGE, socket);
                        case SERVER_ERROR_REQUEST -> responce("/file.txt", socket);
                        default -> responseError(socket, NOT_FOUND);
                    }
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
            outImage.write("\n".getBytes());
            outImage.write("\n".getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void responce(String filePath, Socket socket) {
        try (var inputFile = new FileInputStream(new File(ROOT_PATH, filePath));
             var outImage = new DataOutputStream(socket.getOutputStream())) {
            outImage.write(Server.OK.getBytes());
            outImage.write("\n".getBytes());
            outImage.write("\n".getBytes());
            int count;
            while ((count = inputFile.read(BUFFER)) != -1) {
                outImage.write(BUFFER, 0, count);
                outImage.flush();
            }
        } catch (Exception e) {
            responseError(socket, SEVER_ERROR);
            throw new RuntimeException(e);
        }
    }

}
