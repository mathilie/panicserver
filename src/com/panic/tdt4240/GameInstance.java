package com.panic.tdt4240;

import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by magnus on 06.04.2018.
 */

public class GameInstance implements Runnable {
    private int ID;
    private ArrayList<Socket> clients;

    public GameInstance(int ID,Socket client){
        this.ID=ID;
        if(clients==null){
            clients = new ArrayList<>();
        }
        clients.add(client);
    }
    public GameInstance(Socket client){
        if(clients==null){
            clients = new ArrayList<>();
        }
        clients.add(client);
    }

    public int getID() {
        return ID;
    }

    public ArrayList<Socket> getClients() {
        return clients;
    }

    @Override
    public void run() {

    }
}
