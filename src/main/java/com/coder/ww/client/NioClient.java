package com.coder.ww.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class NioClient {

    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("127.0.0.1",28080));
        OutputStream outputStream = socket.getOutputStream();
        byte[] bytes = "Hello NIO".getBytes();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
        socket.close();
    }
}
