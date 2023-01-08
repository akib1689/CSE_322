package com.akib;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class NotFoundHandler extends RequestHandler{
    @Override
    public void handle(InputStream inputStream, OutputStream outputStream) {
        PrintWriter writer = new PrintWriter(outputStream);
        // send response
        writer.println("HTTP/1.1 404 Not Found");
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
                "    <h1>\n" +
                "        Error! 404: Content Not Found\n" +
                "    </h1>\n" +
                "</body>\n" +
                "</html>");
        writer.flush();
    }
}
