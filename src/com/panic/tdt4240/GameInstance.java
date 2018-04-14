package com.panic.tdt4240;

import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

public class GameInstance implements TurnListener{

    private ArrayList<WebSocket> clients;
    private StringHandler handler;
    private final long seed;
    private HashMap<String,String> gameData;
    private HashMap<Integer, String> gameHashes;
    private static final int MAX_PLAYER_COUNT = 4;
    private static final int TURN_DURATION = 90;
    private ArrayList<ArrayList<String>> moves;
    private HashMap<WebSocket,String> vehicles;
    String mapID;
    private String history;
    private int numRecieved;
    private int turnStart;
    private Timer timer;
    private int delay;
    private int period;
    private static int interval;


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
        //timer = new TurnTimer();
        //timer.setListener(this);
        //new Thread(timer).start();
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
