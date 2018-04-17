package com.panic.tdt4240;

import org.java_websocket.WebSocket;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class GameInstance{

    //TODO make private/protected
    int playerCount;
    HashMap<WebSocket, Integer> playerIDs;
    ArrayList<WebSocket> clients;
    HashMap<WebSocket,String> vehicles;
    String mapID;
    int numRecieved;
    int turnStart;
    String gameName;
    String gameID;
    static final AtomicInteger count = new AtomicInteger(0);


    public GameInstance(int gameID,String gameName){
        playerIDs = new HashMap<>();
        this.gameID = Integer.toString(gameID);
        this.gameName = gameName;
        clients = new ArrayList<>();
        vehicles = new HashMap<>();
        turnStart = 0;
        numRecieved = 0;
    }

    public GameInstance(int gameID, String gameName, ArrayList<WebSocket> cli, HashMap<WebSocket,String> v){
        playerIDs = new HashMap<>();
        this.gameID = Integer.toString(gameID);
        this.gameName = gameName;
        clients = new ArrayList<>();
        vehicles = new HashMap<>();
        turnStart = 0;
        numRecieved = 0;
        clients = cli;
        vehicles = v;
    }

    /**
     * Adds a client to the list of clients if there are not more than the max number of players
     * @param client The client requesting to join the game
     */
    protected void addClient(int playerID, WebSocket client){
        if(clients.size()<playerCount) {
            clients.add(client);
            playerIDs.put(client,playerID);
            client.send("LOBBY_SUCCESSFUL:"+gameID);
        }
        else{
            System.out.println("Attempted to join a full game");
            client.send("LOBBY_FAILED");
        }
    }

    /**
     * The method used to decide which methods to be run
     * @param data The given input for the methods. The first element is always the command string which decides method call
     * @param conn The client sending the request
     */
    public abstract void command(String[] data, WebSocket conn);


    public abstract void removeClient(WebSocket ws);


    public HashMap<WebSocket, String> getVehicles() {
        return vehicles;
    }

    public ArrayList<WebSocket> getClients() {
        return clients;
    }

    public String getGameName() {
        return gameName;
    }

    public int getCurrentPlayerNum() {
        return clients.size();
    }

    public int getMaxPlayerCount() {
        return playerCount;
    }


    //TODO
    public void disconnect(){
        playerCount--;
    }


}
