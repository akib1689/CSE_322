package com.akib;

import java.io.*;

import static com.akib.Worker.BUFFER_SIZE;

public class ImageFileHandler extends RequestHandler {

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
        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                dataOutputStream.write(buffer, 0, bytesRead);
                dataOutputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        writer.flush();

    }

    private String getContentType(File file) {
        String fileName = file.getName();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        switch (extension) {
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            default:
                return "image/jpeg";
        }
    }

    public void setPath(String path) {
        this.path = path;
    }
}
