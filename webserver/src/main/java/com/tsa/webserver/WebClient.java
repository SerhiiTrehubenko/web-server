package com.tsa.webserver;

import java.io.DataInputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class WebClient {

    public WebClient() {
    }

    private byte[] result;
    int offset;

    public void request(String request) {
        result = new byte[8 * 1024];
        offset = 0;
        int count;
        try (var socket = new Socket("localhost", 3000);
             var out = new PrintWriter(socket.getOutputStream(), true);
             var input = new DataInputStream(socket.getInputStream())) {

            out.println(request);
            out.println("");

            while ((count = input.read(result, offset, result.length - offset)) != -1) {
                if (offset >= result.length) {
                    byte[] newResult = new byte[(int) (result.length * 1.5)];
                    System.arraycopy(result, 0, newResult, 0, offset);
                    result = newResult;
                }
                offset += count;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getResult() {
        return new String(result, 0, offset);
    }

    public int getLength() {
        return offset;
    }
}
