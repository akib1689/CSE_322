package com.akib;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public abstract class RequestHandler {
    public abstract void handle(InputStream inputStream, OutputStream outputStream);
}
