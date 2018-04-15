package com.panic.tdt4240;

import org.java_websocket.WebSocket;

import java.util.HashMap;

public class LobbyHandler extends GameInstance {
    private HashMap<String,String> gameData;
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
     * @param VType The Vehicle type the client has selected*/
    private void clientReady(WebSocket conn, String VType) {
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
    }

    public void startGame(){
        for(WebSocket client: clients) client.send("START");
        //timer = new TurnTimer();
        //timer.setListener(this);
        //new Thread(timer).start();
        gameHashes = new HashMap<>();
    }

    /**
     * Sends a string containng the max number of players, the game name, the game ID, the map being used and all the vehicles curently in use. Formatted as:
     * "LOBBY_INFO:MAX_PLAYERS:GAME_NAME:GAME_ID:MAP_ID"
     * @param client
     * @return
     */
    public String sendLobbyInfo(WebSocket client){
        String sendString = "LOBBY_INFO:";
        sendString = sendString + Integer.toString(MAX_PLAYER_COUNT) + ":";
        sendString = sendString + gameName + ":";
        sendString = sendString + gameID + ":";
        sendString = sendString + mapID;
        client.send(sendString);
        return sendString;
    }

}
