package com.panic.tdt4240;

import org.java_websocket.WebSocket;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GameInstance implements TurnListener{


    private int playerCount;
    private HashMap<WebSocket, Integer> playerIDs;
    private ArrayList<WebSocket> clients;
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
    private static final String[] ALL_COLORS = {"RED","BLUE","GREEN","YELLOW"};
    private ArrayList<String> colors;

    private String gameName;
    private String gameID;

    private Random rand;
    private String log;
    private static final AtomicInteger count = new AtomicInteger(0);


    public GameInstance(int gameID, String playerCount,String gameName){
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
        timer = new Timer();
        interval = TURN_DURATION;
        delay = 1000;
        period = 1000;

        moves = new ArrayList<>();
        colors = new ArrayList<>(Arrays.asList(ALL_COLORS));
        rand = new Random();
        clients = new ArrayList<>();
        vehicles = new HashMap<>();
        seed = System.currentTimeMillis();
        gameData = new HashMap<String,String>();

        log = "";
        turnStart = 0;
        numRecieved = 0;
    }

    /**
     * Adds a client to the list of clients if there are not more than the max number of players
     * @param client The client requesting to join the game
     */
    public void addClient(int playerID, WebSocket client){
        if(clients.size()<=playerCount) {
            clients.add(client);
            playerIDs.put(client,playerID);
        }
        else{
            System.out.println("Attempted to join a full game");
        }
    }

    /**
     * The method used to decide which methods to be run
     * @param data The given input for the methods. The first element is always the command string which decides method call
     * @param conn The client sending the request
     */
    public void command(String[] data, WebSocket conn){
        switch (data[0]) {

            case "INIT_GAME":
                clientReady(conn,data[1]);
                break;

            case "GET_LOG":
                getLog(conn);
                break;

            case "GAME_INFO":
                sendGameInfo(conn);
                break;

            case "SEND_CARDS":
                writeCardStringToList(data);
                break;

            case "SEND_RUN_EFFECT_STATE":
                sendCardString();
                break;

            case "BEGIN_TURN":
                beginTurn();
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
     * Removes the client from the game if it is part of it. If the player previously was readied up, reduces the counter for starting game
     * @param client The client to be removed
     */
    private void removeClient(WebSocket client){
        if(clients.contains(client)){
            turnStart=0;
            clients.remove(client);
            playerIDs.remove(client);
            for(WebSocket conn:clients){
                sendLobbyInfo(conn);
                conn.send("UNREADY");
            }
            if(vehicles.containsKey(client)) {
                String color = vehicles.get(client).split(",")[2];
                colors.add(color);
                vehicles.remove(client);
            }
            if(clients.size()==0){
                //TODO: terminate game

            }
        }
        else{
            System.out.println("Player not in game.");
        }
    }


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
}
