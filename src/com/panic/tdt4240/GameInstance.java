package com.panic.tdt4240;

import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.HashMap;

public class GameInstance implements TurnListener{

    private ArrayList<WebSocket> clients;
    private StringHandler handler;
    private long seed;
    private HashMap<String,String> gameData;
    private HashMap<Integer, String> gameHashes;



    public GameInstance(){
        clients = new ArrayList<WebSocket>();
        seed = System.currentTimeMillis();
        handler = new StringHandler(seed);
        gameData = new HashMap<String,String>();
    }

    public void addClient(WebSocket client){
        clients.add(client);
    }

    public long getSeed() {
        return seed;
    }


    public void command(String[] data){
        switch (data[0]){
            case "something":
                //code
                break;

            case "Moves":



            default:

        }

    }


    public void startGame(){
        for(WebSocket client: clients) client.send("START");
        TurnTimer timer = new TurnTimer();
        timer.setListener(this);
        new Thread(timer).start();
        gameHashes = new HashMap<>();
    }

    public void addHash(WebSocket client, String hash){
        gameHashes.put(clients.indexOf(client), hash);
        if(gameHashes.size()==clients.size()){
            
        }
    }

    public void sendGameInfo(){
        for(WebSocket client: clients) client.send(
            gameData.get("map")+"//"+
            gameData.get("colors")
        );
    }


    @Override
    public void turnFinished() {

    }

    @Override
    public void pauseOn() {

    }

    @Override
    public void pauseOff() {

    }
}
