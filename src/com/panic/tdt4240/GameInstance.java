package com.panic.tdt4240;

import java.net.Socket;
import java.util.ArrayList;

public class GameInstance implements Runnable {

    ArrayList<Socket> clients;

    public GameInstance(){
        clients = new ArrayList<>();
    }

    public void addClient(Socket client){

    }
    @Override
    public void run() {

    }
}
