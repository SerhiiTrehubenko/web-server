package com.tsa.server.domain.util;

import com.tsa.server.domain.dto.Response;
import com.tsa.server.domain.exceptions.WriteToSocketException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResponseWriterTest {

    private final String content = "Hello world";
    private final byte[] input = content.getBytes();

    @Mock
    private InputStream inputStream;

    @Mock
    private OutputStream outputStream;

    @Mock
    private BufferedOutputStream bufferedOutputStream;


    @Test
    void writeErrorResponseThrowsRuntimeExceptionWhenIOExceptionOccurs() throws IOException {
        doThrow(IOException.class).when(bufferedOutputStream).write(any(byte[].class));
        assertThrows(RuntimeException.class, () -> ResponseWriter.writeErrorResponse(bufferedOutputStream, getResponseOk()));
    }

    @Test
    void writeResponseTrowsWriteToSocketExceptionWhenIOExceptionOccurs() throws IOException {
        doThrow(IOException.class).when(outputStream).write(any(byte[].class));
        assertThrows(WriteToSocketException.class, () -> ResponseWriter.writeResponse(outputStream, getResponseOk()));
    }

    @Test
    void writeResponseBodyIOExceptionIsThrownWhenReadFile() throws IOException {

        when(inputStream.read(any(byte[].class))).thenThrow(IOException.class);
        assertThrows(IOException.class, () -> ResponseWriter.writeResponseBody(inputStream, outputStream));
    }

    @Test
    void writeResponseBodyWriteToSocketExceptionIsThrownWhenWriteResponse() throws IOException {

        doThrow(IOException.class).when(outputStream).write(any(byte[].class), any(int.class), any(int.class));
        assertThrows(WriteToSocketException.class, () -> ResponseWriter.writeResponseBody(inputStream, outputStream));
    }

    @Test
    void responseToBytes() {
        String expected = "HTTP/1.1 200 OK\r\n\r\n";
        var response = getResponseOk();
        byte[] result = ResponseWriter.responseToBytes(response);

        assertEquals(19, result.length);
        assertEquals(expected, new String(result));
    }

    @Test
    void testWriteResponseBody() throws IOException, WriteToSocketException {
        var output = getOutputStream();
        ResponseWriter.writeResponseBody(getInputStream(), output);

        assertEquals(input.length, output.toByteArray().length);
        assertEquals(content, output.toString());
    }

    @Test
    void testWriteResponse() throws WriteToSocketException {
        String expected = "HTTP/1.1 200 OK\r\n\r\n";
        var output = getOutputStream();

        ResponseWriter.writeResponse(output, getResponseOk());

        assertEquals(expected.length(), output.toByteArray().length);
        assertEquals(expected, output.toString());
    }

    @Test
    void testWriteErrorResponse() {
        String expected = "HTTP/1.1 404 Not Found\r\n\r\n";
        var output = getOutputStream();

        ResponseWriter.writeErrorResponse(output, getResponseNotFound());

        assertEquals(expected.length(), output.toByteArray().length);
        assertEquals(expected, output.toString());
    }

    @Test
    void testWriteSuccessResponse() throws WriteToSocketException {
        String expected = "HTTP/1.1 200 OK\r\n\r\nHello world";
        var output = getOutputStream();

        ResponseWriter.writeSuccessResponse(getInputStream(), output, getResponseOk());

        assertEquals(expected.length(), output.toByteArray().length);
        assertEquals(expected, output.toString());
    }

    private Response getResponseOk() {
        return new Response(HttpStatus.OK);
    }

    private Response getResponseNotFound() {
        return new Response(HttpStatus.NOT_FOUND);
    }

    private InputStream getInputStream() {
        return new ByteArrayInputStream(input);
    }

    private ByteArrayOutputStream getOutputStream() {
        return new ByteArrayOutputStream();
    }
}