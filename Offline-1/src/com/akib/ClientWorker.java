package com.akib;

import java.io.*;
import java.net.Socket;

import static com.akib.MyServer.PORT;
import static com.akib.Worker.BUFFER_SIZE;

public class ClientWorker extends Thread{
    private final String request;             // in format UPLOAD <filename>
    private final Socket socket;

    public ClientWorker(String request) throws IOException {
        this.request = request;
        socket = new Socket("localhost", PORT);
    }

    @Override
    public void run() {
        // split the request string
        String[] requestParts = request.split(" ");
        String command = requestParts[0];
        String filename = requestParts[1];
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()))){
            // send the request to the server
            if (command.equals("UPLOAD")){
                // send the file to the server
                File file = new File(filename);
                if (!file.exists()){
                    System.out.println("ERROR: File not found");
                    System.out.println("Sending Empty Request");
                }else {
                    String reqLine = "UPLOAD " + filename;      // in format "UPLOAD /<file.extension>"
                    writer.println(reqLine);
                    writer.flush();

                    String response = reader.readLine();        //in format "HTTP/1.1 200 OK"
                    System.out.println(response);
                    String[] responsePart = response.split(" ");
                    if (responsePart[1].equals("200") && responsePart[2].equals("OK")){
                        uploadFile(file);
                    }
                }
            }else {
                writer.println(this.request);
                writer.flush();
                String response = reader.readLine();
                System.out.println(response);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            System.out.println();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void uploadFile(File file) {
        try (FileInputStream fileToUpload = new FileInputStream(file);
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())){
            int bytes;
            dataOutputStream.writeLong(file.length());
            dataOutputStream.flush();
            byte[] fileData = new byte[BUFFER_SIZE];
            while ((bytes = fileToUpload.read(fileData)) != -1){
                dataOutputStream.write(fileData, 0, bytes);
                dataOutputStream.flush();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
