package com.panic.tdt4240;

import org.java_websocket.WebSocket;

import java.util.ArrayList;

public class GameInstance implements Runnable {

    private ArrayList<WebSocket> clients;
    private StringHandler handler;
    private long seed;


    public GameInstance(){
        clients = new ArrayList<WebSocket>();
        seed = System.currentTimeMillis();
        handler = new StringHandler(seed);
    }

    public void addClient(WebSocket client){
        clients.add(client);
    }

    public void command(String[] data){

    }
    @Override
    public void run() {

    }

}
