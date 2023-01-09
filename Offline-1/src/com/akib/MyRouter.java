package com.akib;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class is used to route the request to the appropriate handler
 * according to the request path
 */
public class MyRouter {
    private final String masterDirectory;

    public MyRouter() {
        this.masterDirectory = "Offline_1 materials/Offline 1/root";
    }
    public void route(String path, InputStream inputStream, OutputStream outputStream) {
        PrintWriter writer = new PrintWriter(outputStream);
        String[] pathParts = path.split(" ");
        String requestPath = pathParts[1];
        requestPath = requestPath.replace("%20", " ");
        File file = new File(masterDirectory + requestPath);
        RequestHandler handler;
        // upload request handler
        if (pathParts[0].trim().equals("UPLOAD")){
            handler = new UploadHandler();
            Path p = Paths.get(masterDirectory, "uploaded",  requestPath);
            ((UploadHandler)handler).setPath(p.toString());
            handler.handle(inputStream, outputStream);
            return;
        }

        // upload request handled only get request remains
        if (!(pathParts[0].trim().equals("GET")
               && pathParts[pathParts.length-1].trim().equals("HTTP/1.1"))) {

            // invalid request
            writer.println("HTTP/1.1 400 Bad Request");
            writer.println("Content-Type: text/html");
            writer.println();
            writer.println("<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>400 Bad Request</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <h1>\n" +
                    "        400 Bad Request\n" +
                    "    </h1>\n" +
                    "</body>\n" +
                    "</html>");
            writer.flush();
            // close the connection
            return;
        }

        if (file.isDirectory()) {
            // if the path is a directory
            handler = new DirectoryRequestHandler();
            ((DirectoryRequestHandler)handler).setPath(file.getPath());
            handler.handle(inputStream, outputStream);
        } else if (file.isFile()) {
            // there is a path present in the request
            // route to the appropriate handler
            // text file handler
            if (file.toPath().toString().endsWith(".txt")) {
                // relativize the path with the master directory
                handler = new TextFileHandler();
                ((TextFileHandler) handler).setPath(file.getPath());
                handler.handle(inputStream, outputStream);
            }else if (file.toPath().toString().endsWith(".jpg")
                        || file.toPath().toString().endsWith(".png")){
                handler = new ImageFileHandler();
                ((ImageFileHandler) handler).setPath(file.getAbsolutePath());
                handler.handle(inputStream, outputStream);
            } else {
                // handle the other file types
                handler = new BasicFileHandler();
                ((BasicFileHandler) handler).setPath(file.getPath());
                handler.handle(inputStream, outputStream);
            }
        } else {
            // invalid request
            // not found handler
            handler = new NotFoundHandler();
            handler.handle(inputStream, outputStream);
        }

    }
}
