package com.panic.tdt4240;

import org.java_websocket.WebSocket;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class GameController {

    static HashMap<Integer, GameInstance> gameInstances;
    private static final AtomicInteger count = new AtomicInteger(0);


    public GameController(){
        if(gameInstances==null) gameInstances = new HashMap<>();
    }

    public void Sort(WebSocket conn, String[] data) {
        System.out.println("command recieved");
        String command=data[0];
        switch (command){
            case "ENTER":
                if(data.length>2) enterGame(data[1],conn, data[2]); // IF RECONNECTING
                else enterGame(data[1],conn, null);
                break;
            case "CREATE":
                createGame(conn, data[1]);
                break;
            case "TEST":
                System.out.println("Client connected and message recieved");
                conn.send("OK");
                conn.close();
                return;
            case "TOGAME":
                toGame(data, conn);
            default:
                close();
                return;
        }
        System.out.println("No command. Thread is closing");
        close();
        return;
    }

    private void toGame(String[] data, WebSocket conn) {
        int gameID = Integer.parseInt(data[1]);
        String[] dataToGame = Arrays.stream(data).skip(2).toArray(String[]::new); // removes switch command and gameID
        gameInstances.get(gameID).command(dataToGame, conn);
    }

    private void createGame(WebSocket conn, String mapID) {
        GameInstance game = new GameInstance();
        game.setMapID(mapID);
        game.addClient(conn);
        gameInstances.put(count.incrementAndGet(),game);
    }

    private void enterGame(String gameID, WebSocket conn, String playerID) {
        int tmp = Integer.parseInt(gameID);
        GameInstance game = gameInstances.get(tmp);
        game.addClient(conn);
    }

    private void close(){
    }
}
