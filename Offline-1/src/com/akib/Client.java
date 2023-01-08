package com.akib;

import java.io.IOException;
import java.util.Scanner;

/**
 * Class to upload the file to the server
 * @author Akib
 */
public class Client {
    public static void main(String[] args) {
        // write your code here
        Scanner scanner = new Scanner(System.in);

        while (true){
            Thread thread;
            try {
                String line = scanner.nextLine();
                if (line.equals("EXIT")){
                    break;
                }
                thread = new ClientWorker(line);
                thread.start();
            } catch (IOException e) {
                System.out.println("Could not connect to Server");
            }
        }


    }
}
