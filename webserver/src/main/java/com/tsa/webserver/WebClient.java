package com.tsa.webserver;

import java.io.DataInputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class WebClient {
    private static final int DEFAULT_BUFFER_SIZE = 8 * 1024;
    private static final double DEFAULT_GROW_COEFFICIENT = 1.5;
    private final int port;
    private final double customCoefficient;
    private byte[] buffer;
    int position;

    public WebClient(int port) {
        this(port, DEFAULT_BUFFER_SIZE, DEFAULT_GROW_COEFFICIENT);
    }

    public WebClient(int port, int capacity) {
       this(port, capacity, DEFAULT_GROW_COEFFICIENT);
    }

    public WebClient(int port, int capacity, double growCoefficient) {
        if (capacity <= 0) {
            throw new RuntimeException("Capacity can not be less then 0, you provided: " + capacity);
        }
        if (growCoefficient <= 1) {
            throw new RuntimeException("Grow coefficient can not be less then 1, you provided: " + growCoefficient);
        }
        this.port = port;
        buffer = new byte[capacity];
        customCoefficient = growCoefficient;
    }

    public void request(String request) {
        int count;
        try (var socket = new Socket("localhost", port);
             var out = new PrintWriter(socket.getOutputStream(), true);
             var input = new DataInputStream(socket.getInputStream())) {

            out.println(request);
            out.println("\r\n");

            while ((count = input.read(buffer, position, buffer.length - position)) != -1) {
                ensureCapacityAndGrow();
                position += count;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private int getCoefficientNewBuffer() {
        return (int) (buffer.length * (customCoefficient == DEFAULT_GROW_COEFFICIENT ? DEFAULT_GROW_COEFFICIENT : customCoefficient) + 1);
    }

    private void ensureCapacityAndGrow() {
        if (position >= buffer.length) {
            byte[] increasedBuffer = new byte[getCoefficientNewBuffer()];
            System.arraycopy(buffer, 0, increasedBuffer, 0, position);
            buffer = increasedBuffer;
        }
    }

    public String getBufferAsString() {
        return new String(buffer, 0, position);
    }

    public int getBufferLength() {
        return position;
    }
}
