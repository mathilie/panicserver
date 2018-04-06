package com.panic.tdt4240;

import java.net.Socket;
import java.util.ArrayList;

public class SorterThread implements Runnable {
    static ArrayList<Integer> gameInstanceList;
    Socket client;

    public SorterThread(Socket client){
        this.client = client;
        if(gameInstanceList==null) gameInstanceList = new ArrayList<Integer>();
    }

    @Override
    public void run() {
        System.out.println("Hello World!");
    }
}
