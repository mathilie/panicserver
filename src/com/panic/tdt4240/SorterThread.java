package com.panic.tdt4240;

import java.net.Socket;
import java.util.ArrayList;

public class SorterThread implements Runnable {
    public static ArrayList<GameInstance> gameInstanceList;
    Socket client;

    public SorterThread(Socket client){
        this.client = client;
        if(gameInstanceList==null) gameInstanceList = new ArrayList<GameInstance>();
    }

    @Override
    public void run() {
        System.out.println("Hello World!");
    }
}
