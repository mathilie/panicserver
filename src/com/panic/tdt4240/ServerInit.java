package com.panic.tdt4240;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerInit {
    private static ServerSocket ss;
    private static Socket client;
    public static void main(String args[]){
         try{
             ss = new ServerSocket(Integer.parseInt(args[0]));
            while (true){
                try {
                    client = ss.accept();
                    System.out.println("Connection "+client.toString()+" accepted");
                    new Thread(new SorterThread(client)).start();
                    client = null;
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }catch(Exception er){}
    }
}
