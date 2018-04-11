package com.panic.tdt4240;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerInit {
    public static void main(int args[]){
        try {
            ServerSocket ss = new ServerSocket(args[0]);
            Socket client = ss.accept();
            new SorterThread(client).run();
        } catch (Exception e){
            e.printStackTrace();
        }


    }

}
