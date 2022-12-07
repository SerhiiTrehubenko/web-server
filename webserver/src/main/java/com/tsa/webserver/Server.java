package com.tsa.webserver;

import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.tsa.webserver.HttpStatus.*;

public class Server implements Runnable {
    private static final byte[] CRLF = "\r\n".getBytes();
    private final String webAppPath;
    private final int port;

    public Server(String webAppPath, int port) {
        this.webAppPath = webAppPath;
        this.port = port;
    }

    @Override
    public void run() {
        try (var server = new ServerSocket(port)) {
            while (!server.isClosed()) {
                try (var socket = server.accept();
                     var input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     var output = new DataOutputStream(socket.getOutputStream())) {

                    String uri = getUriFromRequest(input);
                    response(uri, output);

                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getUriFromRequest(BufferedReader inputFromSocket) throws IOException {
        String startingLine = inputFromSocket.readLine();
        return startingLine.split(" ")[1];
    }

    private void response(String uri, OutputStream outputToSocket) throws IOException {
        String rootPath = Paths.get(Path.of("").toAbsolutePath().toString(), webAppPath).toString();

        try (var inputFromFile = new FileInputStream(new File(rootPath, uri))) {

            writeStartingLineAndHeaders(outputToSocket, OK.getStatus());
            writeMessageBody(inputFromFile, outputToSocket);

        } catch (FileNotFoundException e) {
            responseError(outputToSocket, NOT_FOUND.getStatus());
            throw new FileNotFoundException(e.getMessage());
        } catch (IOException e) {
            responseError(outputToSocket, SEVER_ERROR.getStatus());
            throw new IOException(e);
        }
    }

    private void writeStartingLineAndHeaders(OutputStream outputToSocket, String httpStatus) throws IOException {
        outputToSocket.write(httpStatus.getBytes());
        outputToSocket.write(CRLF);
        outputToSocket.write(CRLF);
    }

    private void writeMessageBody(InputStream inputFromFile, OutputStream outputToSocket) throws IOException {
        byte[] buffer = new byte[8 * 1024];
        int count;
        while ((count = inputFromFile.read(buffer)) != -1) {
            outputToSocket.write(buffer, 0, count);
            outputToSocket.flush();
        }
    }

    private void responseError(OutputStream outputToSocket, String httpStatus) throws IOException {
        outputToSocket.write(httpStatus.getBytes());
        outputToSocket.write(CRLF);
        outputToSocket.write(CRLF);
    }

}
