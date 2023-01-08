package com.akib;

import java.io.*;

import static com.akib.Worker.BUFFER_SIZE;

public class UploadHandler extends RequestHandler{
    private String path;
    @Override
    public void handle(InputStream inputStream, OutputStream outputStream) {
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream));
        writer.println("HTTP/1.1 200 OK");
        writer.println();
        writer.flush();                 // Acknowledgement

        int bytes = 0;
        File file = new File(path);

        try {
            if (file.createNewFile()){
                System.out.println("File created");
            }
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                long fileSize = dataInputStream.readLong();
                byte[] fileData = new byte[BUFFER_SIZE];
                while (fileSize > 0 && (bytes = dataInputStream.read(fileData, 0, (int) Math.min(fileData.length, fileSize))) != -1) {
                    fileOutputStream.write(fileData, 0, bytes);
                    fileSize -= bytes;
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setPath(String path){
        this.path = path;
    }
}
