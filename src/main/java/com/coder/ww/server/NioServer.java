package com.coder.ww.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioServer {

    public static void main(String[] args) {
        try {
            Selector selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            ServerSocket serverSocket = serverSocketChannel.socket();
            serverSocket.bind(new InetSocketAddress("127.0.0.1", 28080));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                // 循环等待
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    handle(selectionKey);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handle(SelectionKey selectionKey) {
        if (selectionKey.isAcceptable()) {
            try {
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                SocketChannel socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (selectionKey.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(4);
            try {
                while (socketChannel.read(byteBuffer) > 0) {
                    byteBuffer.flip();
                    while (byteBuffer.hasRemaining()) {
                        byte b = byteBuffer.get();
                        System.out.println(b);
                    }
                }
                byteBuffer.clear();
            } catch (Exception e) {
                e.printStackTrace();
                selectionKey.cancel();
                try {
                    socketChannel.socket().close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
