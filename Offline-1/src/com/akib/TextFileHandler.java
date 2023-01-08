package com.akib;

import java.io.*;

public class TextFileHandler extends RequestHandler {
    private String path;
    @Override
    public void handle(InputStream inputStream, OutputStream outputStream) {
        // read the file from the path
        File file = new File(path);
        PrintWriter writer = new PrintWriter(outputStream);
        // read the file content
        // send the file content to the client
        String response = "<h1>File Content: " + file.getName() + "</h1><br>";
        response += "<pre>" + getFileContent(file, inputStream, outputStream) + "</pre>";
        // send response
        writer.println("HTTP/1.1 200 OK");
        writer.println("Content-Type: text/html");
        writer.println();
        writer.println("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Document</title>\n" +
                "</head>\n" +
                "<body>\n" +
                response +
                "</body>\n" +
                "</html>");
        writer.flush();
    }

    private String getFileContent(File file, InputStream inputStream, OutputStream outputStream) {
        // read the file content
        // return the file content
        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("<br>");
            }
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
            NotFoundHandler notFoundHandler = new NotFoundHandler();
            notFoundHandler.handle(inputStream, outputStream);
        }
        return "";
    }

    public void setPath(String path) {
        this.path = path;
    }
}
