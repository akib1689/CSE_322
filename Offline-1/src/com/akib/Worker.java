package com.akib;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.Socket;

public class Worker extends Thread{
    private Socket socket;

    public static final int BUFFER_SIZE = 32;

    public Worker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        // user the buffered reader to read the http request from the client
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new FileWriter("log.txt", true))) {
            String request = reader.readLine();
            if (request == null){
                System.out.println("ERROR: Empty request");
                return;
            }
            MyRouter router = new MyRouter();
            writer.write(request);
            writer.newLine();
            writer.flush();
            router.route(request, socket.getInputStream(), socket.getOutputStream());



        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }



}
