package com.akib;

import java.io.*;

public class DirectoryRequestHandler extends RequestHandler {
    private String path;
    @Override
    public void handle(InputStream inputStream, OutputStream outputStream) {
        String directory = path;
        String response = getDirectoryTree(directory);
//        System.out.println(response);

        PrintWriter writer = new PrintWriter(outputStream);
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
                "    <h1>\n" +
                "        Directory Tree of " + directory + "\n" +
                "    </h1>\n" +
                response +
                "</body>\n" +
                "</html>");
        writer.flush();
    }

    public void setPath(String path) {
        this.path = path;
    }




    private String getDirectoryTree(String directory) {
        // traverse the directory "Offline_1 materials" and send the tree structure of the file tree to the client
        StringBuilder tree = new StringBuilder();
        File file = new File(directory);
        if (file.isDirectory()) {
            File[] files = file.listFiles(f -> !f.isHidden());
            if (files == null) {
                return "";
            }
            for (File f : files) {
                // append the file name and the link to the file
                // find the relative path to the master directory and then create the link
                String relativePath = f.getParentFile().toURI().relativize(f.toURI()).toString();
                if (f.isDirectory()) {
                    // use the bold italic font for the directory name
                    tree.append("<a href=\"").append(relativePath).append("\">");
                    tree.append("<b><i>").append(f.getName()).append("</i></b><br>");
                    tree.append("</a>");
                } else {
                    tree.append("<a href=\"").append(relativePath).append("\">").append(f.getName()).append("</a><br>");
                }
            }
        } else {
            System.out.println("Not a directory");
        }
        return tree.toString();
    }
}
