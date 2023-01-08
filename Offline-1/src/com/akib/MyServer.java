package com.akib;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServer {
    static final  int PORT = 5086;
    public static void main(String[] args) throws IOException {
        // open server socket
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port: " + PORT);

       while (true) {
            // wait for client connection
            Socket socket = serverSocket.accept();;

            // create new thread for client
            Worker worker = new Worker(socket);
            worker.start();
       }
    }
}
