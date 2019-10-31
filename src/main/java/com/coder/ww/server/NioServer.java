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
        Selector selector = null;
        ServerSocketChannel serverSocketChannel = null;
        ServerSocket serverSocket = null;
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocket = serverSocketChannel.socket();
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
        } finally {
            try {
                serverSocket.close();
                serverSocketChannel.close();
                selector.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private static void handle(SelectionKey selectionKey) {
        if (selectionKey.isAcceptable()) {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (selectionKey.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            handleRead(socketChannel, selectionKey);
        }
    }

    private static void handleRead(SocketChannel socketChannel, SelectionKey selectionKey) {
        if (socketChannel.isOpen() && socketChannel.isConnected()) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(10);
            try {
                int status;
                while (true) {
                    if ((status = socketChannel.read(byteBuffer)) > 0) {
                        byteBuffer.flip();
                        while (byteBuffer.hasRemaining()) {
                            byte b = byteBuffer.get();
                            System.out.println(b);
                        }
                    } else {
                        if (status == -1) {
                            System.out.println("status:" + status);
                            cancel(selectionKey);
                            close(socketChannel);
                        }
                        break;
                    }
                }
                byteBuffer.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void close(SocketChannel socketChannel) {
        try {
            socketChannel.socket().close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private static void cancel(SelectionKey selectionKey) {
        selectionKey.cancel();
    }
}
