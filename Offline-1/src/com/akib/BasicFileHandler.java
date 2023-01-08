package com.akib;

import java.io.*;

import static com.akib.Worker.BUFFER_SIZE;

public class BasicFileHandler extends RequestHandler {

    private String path;
    @Override
    public void handle(InputStream inputStream, OutputStream outputStream) {
        // read the file from the path
        // read the file content
        // send the file content to the client
        // send response
        File file = new File(path);
        PrintWriter writer = new PrintWriter(outputStream);
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        writer.println("HTTP/1.1 200 OK");
        writer.println("Server: Java HTTP Server : 1.0");
        writer.println("Content-Type: " + getContentType(file));
        writer.println("Content-Length: " + file.length());
        writer.println();
        writer.flush();

        // send the file content
        try (FileInputStream fileInputStream = new FileInputStream(path)){
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                dataOutputStream.write(buffer, 0, bytesRead);
                dataOutputStream.flush();
            }
            writer.flush();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private String getContentType(File file){
        if (file.toPath().toString().endsWith(".html")) {
            return "text/html";
        } else {
            return "application/octet-stream";
        }
    }

    public void setPath(String path) {
        this.path = path;
    }
}
