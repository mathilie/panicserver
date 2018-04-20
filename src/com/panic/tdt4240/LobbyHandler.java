package com.panic.tdt4240;

import org.java_websocket.WebSocket;

import java.util.*;

public class LobbyHandler extends GameInstance {
    private static final String[] ALL_COLORS = {"RED","BLUE","GREEN","YELLOW"};
    private ArrayList<String> colors;
    static final int MAX_PLAYER_COUNT = 4;

    public LobbyHandler(int gameID, String playerCount,String gameName){
        super(gameID, gameName);
        int playerNum = Integer.parseInt(playerCount);
        if(playerNum<MAX_PLAYER_COUNT) {
            this.localMaxPlayerCount = playerNum;
        }
        else{
            this.localMaxPlayerCount = MAX_PLAYER_COUNT;
        }
        this.gameID = Integer.toString(gameID); //overflødig?
        this.gameName = gameName;               //overflødig?
        colors = new ArrayList<>(Arrays.asList(ALL_COLORS)); //clientReady, remove client
    }

    /**
     * Defines which map will be used in the game instance
     * @param ID The map ID
     */
    public void setMapID(String ID){
        this.mapID = ID;
    }

    /**
     * Marks the client as ready for the game to start. If all clients have readied up, sends the "START_TURN" command to all clients
     * @param conn Client that has readied up
     * @param VType The Vehicle type the client has selected
     *///TODO Vehicle and clients colors?
    private void clientReady(WebSocket conn,String VType) {
        String VID = "";
        String color = "";
        turnStart++;
        if (count.get() < 1000) {
            VID = String.format("%03d", count.incrementAndGet());
        } else {} //TODO: What if someone enters and leaves 1000 times?
        VID = "V-" + VID;
        if (colors.size() > 0) {
            color = colors.get(0);
            colors.remove(0);
        }
        players.get(conn).set(1,VType);
        players.get(conn).set(2,VID);
        players.get(conn).set(3,color);
        for(WebSocket player:players.keySet()) sendLobbyInfo(player);
        if(turnStart>=players.size()) startGame();
    }

    //TODO remove Client, switch vehicle and clients
    private void startGame() {
        for(WebSocket client:playerIDs.values())
            client.send("GAME_START");
        GameHandler game = new GameHandler(Integer.parseInt(gameID), gameName, players, playerIDs);
        GameController.startGame(game, Integer.parseInt(gameID), mapID);
    }


    /**
     * The method used to decide which methods to be run
     * @param data The given input for the methods. The first element is always the command string which decides method call
     * @param conn The client sending the request
     */
    @Override
    public void command(String[] data, WebSocket conn){
        switch (data[0]) {
            case "VEHICLE_SET":
                clientReady(conn,data[1]);
                break;
            case "LEAVE_GAME":
                removeClient(conn);
                break;
            case "GET_LOBBY_INFO":
                sendLobbyInfo(conn);
                break;
            default:
        }
    }

    /**
     * Sends a string containng the max number of players, the game name, the game ID, the map being used and all the vehicles curently in use. Formatted as:
     * "LOBBY_INFO:MAX_PLAYERS:GAME_NAME:GAME_ID:MAP_ID"
     * @param client
     * @return
     *///TODO did changes IS THIS TEE RIGHT INFO?
    public String sendLobbyInfo(WebSocket client){
        String sendString = "LOBBY_INFO:";
        sendString = sendString + Integer.toString(localMaxPlayerCount) + ":";
        sendString = sendString + gameName + ":";
        sendString = sendString + gameID + ":";
        sendString = sendString + mapID + ":";
        for(int PID:playerIDs.keySet()){
            sendString = sendString + PID + "&";
        }
        sendString = sendString.substring(0,sendString.length()-1) + ":";
        for(WebSocket conn:players.keySet()){
            sendString = sendString + players.get(conn).get(1)+"&";  //Gets vehicleType
        }
        sendString=sendString.substring(0,sendString.length()-1);   //Removes & at end
        client.send(sendString);
        System.out.println(sendString);
        return sendString;
    }

    @Override
    public boolean addClient(int playerID, WebSocket client){
        boolean returnbool = super.addClient(playerID, client);
        for(WebSocket player:players.keySet()) sendLobbyInfo(player);
        return returnbool;
    }

    /**
     * Removes the client from the game if it is part of it. If the player previously was readied up, reduces the counter for starting game
     * @param client The client to be removed
     * @return true if game is to be terminated
     *///TODO did changes
    @Override
    public boolean removeClient(WebSocket client){
        if(players.containsKey(client)){
            turnStart=0;
            disconnect(client);
            vehiclesClear();
            colors = new ArrayList<>(Arrays.asList(ALL_COLORS));
            if(playerIDs.size()<=0){
                return true;
            }
            for(WebSocket conn:playerIDs.values()){
                sendLobbyInfo(conn);
            }
        }
        else{
            System.out.println("Player not in game.");
        }
        return false;
    }

    private void vehiclesClear() {
        for(WebSocket player:players.keySet()){
            players.get(player).set(1,"NONE"); //vType
            players.get(player).set(2,"NONE"); //vId
            players.get(player).set(3,"NONE"); //color
        }
    }
}
