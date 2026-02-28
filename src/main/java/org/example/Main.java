package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        final int port = 8080;
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4,
                4,
                0,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(50));
        try(ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("Server is running on "+port);
            while(true) {
                Socket socket = serverSocket.accept();
                threadPoolExecutor.execute(()->handleRequest(socket));
            }
        }catch (Exception ex){
            System.out.println("failed to handle request "+ ex.getMessage());
        }
    }

    public static void handleRequest(Socket socket){
        try(InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream()){
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = bufferedReader.readLine();
            String[] links = line.split(" ");
            String method = links[0];
            String path = links[1];
            Thread.sleep(1000);
            if(method.equalsIgnoreCase("GET") && path.equalsIgnoreCase("/Home")){
                writeResponse(outputStream);
            }
        }catch (Exception ex){
            System.out.println("Failed to handle request "+ex.getMessage());
        }finally {
            try {
                socket.close();
            }catch (Exception ex){
                System.out.println("socket not close "+ ex.getMessage());
            }
        }
    }

    public static void writeResponse(OutputStream outputStream) throws IOException {
        String message = "Hi, This is response from sever :) ";
        String httpResponse = """
                HTTP/1.1 200 OK
                Content-Type: text/plain
                content-length:""" + message.length()+ "\n\n"+
                message;
        outputStream.write(httpResponse.getBytes());
        outputStream.flush();
    }
}