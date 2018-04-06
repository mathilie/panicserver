package com.panic.tdt4240;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerInit {
    public static void main(String args[]){
        while (true){
            try {
                ServerSocket ss = new ServerSocket(Integer.parseInt(args[0]));
                Socket client = ss.accept();
                System.out.println("Connection "+client.toString()+" accepted");
                new Thread(new SorterThread(client)).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
