package com.panic.tdt4240;

import org.java_websocket.WebSocket;

import java.util.*;

public class LobbyHandler extends GameInstance {
    private HashMap<String,String> gameData;
    private static final String[] ALL_COLORS = {"RED","BLUE","GREEN","YELLOW"};
    private ArrayList<String> colors;
    static final int MAX_PLAYER_COUNT = 4;

    public LobbyHandler(int gameID, String playerCount,String gameName){
        super(gameID, gameName);
        int playerNum = Integer.parseInt(playerCount);
        playerIDs = new HashMap<>();
        if(playerNum<MAX_PLAYER_COUNT) {
            this.playerCount = playerNum;
        }
        else{
            this.playerCount = MAX_PLAYER_COUNT;
        }
        this.gameID = Integer.toString(gameID);
        this.gameName = gameName;

        colors = new ArrayList<>(Arrays.asList(ALL_COLORS));
        clients = new ArrayList<>();
        vehicles = new HashMap<>();
        gameData = new HashMap<String,String>();
        turnStart = 0;
        numRecieved = 0;
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
     */
    private void clientReady(WebSocket conn,String VType) {
        String VID = "";
        String color = "";
        String vehicleString = "";
        turnStart++;
        if (count.get() < 1000) {
            VID = String.format("%03d", count.incrementAndGet());
        } else {
            //TODO: What if someone enters and leaves 1000 times?
        }
        VID = "V-" + VID;
        if (colors.size() > 0) {
            color = colors.get(0);
            colors.remove(0);
        }
        vehicleString = VType + "," + VID + "," + color;
        vehicles.put(conn,vehicleString);
        sendLobbyInfo(conn);
        if(turnStart>=clients.size()) startGame();
    }

    private void startGame() {
        for(WebSocket client:clients)
            client.send("GAME_START");
        GameHandler game = new GameHandler(Integer.parseInt(gameID), gameName, clients, vehicles);
        GameController.startGame(game, Integer.parseInt(gameID));

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
     */
    public String sendLobbyInfo(WebSocket client){
        String sendString = "LOBBY_INFO:";
        sendString = sendString + Integer.toString(playerCount) + ":";
        sendString = sendString + gameName + ":";
        sendString = sendString + gameID + ":";
        sendString = sendString + mapID + ":";
        for(Map.Entry<WebSocket,Integer> PID:playerIDs.entrySet()){
            sendString = sendString + PID.getValue().toString() + "&";
        }
        sendString = sendString.substring(0,sendString.length()-1) + ":";
        for(WebSocket conn:clients){
            if(playerIDs.containsKey(conn) && vehicles.containsKey(conn)){
                sendString = sendString + vehicles.get(conn);
            }
            else{
                sendString = sendString + "NONE";
            }
        }

        client.send(sendString);
        System.out.println(sendString);
        return sendString;
    }

    /**
     * Removes the client from the game if it is part of it. If the player previously was readied up, reduces the counter for starting game
     * @param client The client to be removed
     */
    @Override
    public void removeClient(WebSocket client){
        if(clients.contains(client)){
            turnStart=0;
            clients.remove(client);
            playerIDs.remove(client);
            for(WebSocket conn:clients){
                sendLobbyInfo(conn);
                //conn.send("UNREADY");
            }
            vehicles.clear();
            colors = new ArrayList<>(Arrays.asList(ALL_COLORS));
            if(clients.size()==0){
                //TODO: terminate game

            }
        }
        else{
            System.out.println("Player not in game.");
        }
    }
}
