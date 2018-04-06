package com.panic.tdt4240;

import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by magnus on 06.04.2018.
 */

public class GameInstance implements Runnable {
    private ArrayList<Socket> clients;

    public GameInstance(Socket client){
        if(clients==null){
            clients = new ArrayList<>();
        }
        clients.add(client);
    }

    public ArrayList<Socket> getClients() {
        return clients;
    }

    @Override
    public void run() {

    }
}