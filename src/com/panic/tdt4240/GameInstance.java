package com.panic.tdt4240;

import org.java_websocket.WebSocket;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class GameInstance{

    //TODO make private/protected
    int localMaxPlayerCount;

    HashMap<Integer, WebSocket> playerIDs; //TODO reconnecting
    HashMap<WebSocket,List> players; //playerID, vehicleType, vehicleId, color || used in sendLobbyInfo, removeClient, clientReady
    String mapID;
    int numRecieved;
    int turnStart;
    String gameName;
    String gameID;
    static final AtomicInteger count = new AtomicInteger(0);


    public GameInstance(int gameID,String gameName){
        playerIDs = new HashMap<Integer, WebSocket>();
        players = new HashMap<WebSocket, List>();
        this.gameID = Integer.toString(gameID);
        this.gameName = gameName;
        turnStart = 0;
        numRecieved = 0;
    }


    //Constructor for used when making a GameHandler
    public GameInstance(int gameID, String gameName, HashMap<WebSocket, List> playerMap, HashMap<Integer, WebSocket> idMap) {
        playerIDs = idMap;
        players = playerMap;
        this.gameID = Integer.toString(gameID);
        this.gameName = gameName;
        turnStart = 0;
        numRecieved = 0;
    }

    /**
     * Adds a client to the list of clients if there are not more than the max number of players
     * @param client The client requesting to join the game
     */
    protected boolean addClient(int playerID, WebSocket client){
        if(players.size()< localMaxPlayerCount &&!playerIDs.containsKey(playerID)) {    //new player
            playerIDs.put(playerID, client);
            ArrayList playerData = new ArrayList();
            playerData.addAll(Arrays.asList(playerID,"NONE", "NONE", "NONE")); //PID,VType, VehicleID,Color
            players.put(client,playerData);
            client.send("LOBBY_SUCCESSFUL:"+gameID);
            return true;
        } else if(playerIDs.containsKey(playerID)) {                                    //Reconnecting player
            WebSocket old = playerIDs.replace(playerID, client); //so sexy
            players.put(client, players.get(old));
            players.remove(old);
            client.send("LOBBY_SUCCESSFUL:"+gameID);
            return true;
        }
        else{                                                                          //Lobby/game full
            System.out.println("Attempted to join a full game");
            client.send("LOBBY_FAILED");
            return false;
        }
    }


    public void reconnect(int playerID, WebSocket client){
        addClient(playerID,client);
    }

    /**
     * The method used to decide which methods to be run
     * @param data The given input for the methods. The first element is always the command string which decides method call
     * @param conn The client sending the request
     */
    public abstract void command(String[] data, WebSocket conn);


    public abstract boolean removeClient(WebSocket ws);


    public HashMap<WebSocket, String> getVehicles() { //TODO must return something else? Maybe vehicle string
        HashMap<WebSocket, String> returnMap = new HashMap<WebSocket, String>();
        for(WebSocket player:players.keySet()){
            returnMap.put(player, (String) players.get(player).get(1));
        }
        return returnMap;
    }

    public ArrayList<WebSocket> getClients() {
        return new ArrayList<WebSocket>(playerIDs.values());
    }

    public String getGameName() {
        return gameName;
    }

    public int getCurrentPlayerNum() { return playerIDs.size(); }

    public int getMaxPlayerCount() { return localMaxPlayerCount; }


    //TODO
    public void disconnect(WebSocket client){
        List player = players.remove(client);
        playerIDs.remove((Integer) player.get(0));
    }
}
