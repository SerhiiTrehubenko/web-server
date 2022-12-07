package com.tsa.webserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import static com.tsa.webserver.HttpStatus.*;

public class Server implements Runnable {
    private static final byte[] CRLF = "\r\n".getBytes();
    private static final String ROOT_PATH = Path.of("").toAbsolutePath().toString();
    private static final byte[] BUFFER = new byte[8 * 1024];
    private static final List<String> BROWSER_REQUEST = new ArrayList<>();

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
                        BROWSER_REQUEST.add(line);
                    }
                    String uri = getUriFromRequest();
                    response(uri, socket);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                BROWSER_REQUEST.clear();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getUriFromRequest() {
        return BROWSER_REQUEST.get(0).split(" ")[1];
    }

    private void response(String uri, Socket socket) {
        try (var inputFile = new FileInputStream(Paths.get(ROOT_PATH, webAppPath, uri).toFile());
             var outImage = new DataOutputStream(socket.getOutputStream())) {
            outImage.write(OK.getStatus().getBytes());
            outImage.write(CRLF);
            outImage.write(CRLF);
            int count;
            while ((count = inputFile.read(BUFFER)) != -1) {
                outImage.write(BUFFER, 0, count);
                outImage.flush();
            }
        } catch (FileNotFoundException e) {
            responseError(socket, NOT_FOUND.getStatus());
            throw new RuntimeException(e);
        } catch(IOException e) {
            responseError(socket, SEVER_ERROR.getStatus());
            throw new RuntimeException(e);
        }
    }

    private void responseError(Socket socket, String httpStatus) {
        try (var outImage = new DataOutputStream(socket.getOutputStream())) {
            outImage.write(httpStatus.getBytes());
            outImage.write(CRLF);
            outImage.write(CRLF);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
