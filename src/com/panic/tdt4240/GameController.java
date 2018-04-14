package com.panic.tdt4240;

import org.java_websocket.WebSocket;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class GameController {

    private static HashMap<Integer, GameInstance> gameInstances;
    private static final AtomicInteger gameCount = new AtomicInteger(0);
    private static final AtomicInteger playerCount = new AtomicInteger(0);
    private HashMap<WebSocket,Integer> playerIDs;


    public GameController(){
        if(playerIDs==null) playerIDs = new HashMap<>();
        if(gameInstances==null) gameInstances = new HashMap<>();
    }

    public void Sort(WebSocket conn, String[] data) {
        System.out.println("command recieved");
        System.out.println(Arrays.toString(data));
        String command=data[0];
        switch (command){
            case "ENTER":
                enterGame(data[1],conn);
                break;
            case "CREATE":
                createGame(conn, data[1], data[2], data[3]);
                break;
            case "TEST":
                System.out.println("Client connected and message recieved");
                conn.send("OK");
                conn.close();
                return;
            case "TOGAME":
                toGame(data, conn);
                break;
            case "GET_LOBBIES":
                getLobbies(conn);
                break;
            case "CONNECTION_ID":
                getConnection(conn);
                //TODO: code
                break;
            default:
                close();
                return;
        }
    }

    private void toGame(String[] data, WebSocket conn) {
        int gameID = Integer.parseInt(data[1]);
        String[] dataToGame = Arrays.stream(data).skip(2).toArray(String[]::new); // removes switch command and gameID
        gameInstances.get(gameID).command(dataToGame, conn);
    }

    private void createGame(WebSocket conn, String mapID, String playerCount, String gameName) {
        int gameID = gameCount.incrementAndGet();
        GameInstance game = new GameInstance(gameID, playerCount,gameName);
        game.setMapID(mapID);
        game.addClient(playerIDs.get(conn), conn);
        gameInstances.put(gameID,game);
        game.sendLobbyInfo(conn);
    }

    private void enterGame(String gameID, WebSocket conn) {
        int tmp = Integer.parseInt(gameID);
        GameInstance game = gameInstances.get(tmp);
        game.addClient(playerIDs.get(conn), conn);
    }
    private void getLobbies(WebSocket conn){
        String sendString = "LOBBIES:";
        for(Map.Entry<Integer,GameInstance> gameInstance:gameInstances.entrySet()){
            sendString = sendString + gameInstance.getKey().toString() + "&";
        }
        sendString = sendString.substring(0,sendString.length()-1);
        conn.send(sendString);
    }

    private void getConnection(WebSocket conn){
        if(!playerIDs.containsKey(conn)){
            playerIDs.put(conn,playerCount.incrementAndGet());
        }
        conn.send("CONNECTION_ID:" + Integer.toString(playerIDs.get(conn)));
    }

    private void close(){
    }
}
