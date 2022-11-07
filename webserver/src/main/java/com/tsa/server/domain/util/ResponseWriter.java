package com.tsa.server.domain.util;

import com.tsa.server.domain.dto.Response;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ResponseWriter {
    public static void writeResponse(InputStream inputStream,
                                     OutputStream outputStream,
                                     Response response) {

        byte[] retrievedBytes = new byte[8*1024];
        int count;
        try (var input = new DataInputStream(inputStream);
        var out = new DataOutputStream(outputStream)){

            out.write(response.toString().getBytes());

            while ((count = input.read(retrievedBytes)) != -1){

                out.write(retrievedBytes, 0, count);
                out.flush();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void writeErrorResponse(OutputStream outputStream, Response response) {
        try (var out = new DataOutputStream(outputStream)){

            out.write(response.toString().getBytes());

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }
}
