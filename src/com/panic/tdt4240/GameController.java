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
                enterGame(data[1],conn, data[2]);
                break;
            case "CREATE":
                createGame();
                break;
            case "TEST":
                System.out.println("Client connected and message recieved");
                return;
            case "TOGAME":
                toGame(data);
            default:
                close();
                return;
        }
        System.out.println("No command. Thread is closing");
        close();
        return;
    }

    private void toGame(String[] data) {
        int gameID = Integer.parseInt(data[1]);
        String[] dataToGame = Arrays.stream(data).skip(2).toArray(String[]::new);
        gameInstances.get(gameID).command(dataToGame);
    }

    private void createGame() {
        GameInstance game = new GameInstance();
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
