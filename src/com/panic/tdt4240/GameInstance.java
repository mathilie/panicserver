package com.panic.tdt4240;

import org.java_websocket.WebSocket;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GameInstance{

    private static final int MAX_PLAYER_COUNT = 4;
    private int playerCount;
    private HashMap<WebSocket, Integer> playerIDs;

    private static final int TURN_DURATION = 90;
    private ArrayList<WebSocket> clients;
    private HashMap<WebSocket,String> vehicles;
    private ArrayList<ArrayList<String>> moves;
    private static final String[] ALL_COLORS = {"RED","BLUE","GREEN","YELLOW"};
    private ArrayList<String> colors;

    private String gameName;
    private String gameID;

    private long seed;
    private Random rand;
    private String mapID;
    private String log;
    private int numRecieved;
    private int turnStart;
    private Timer timer;
    private int delay;
    private int period;
    private static int interval;
    private static final AtomicInteger count = new AtomicInteger(0);
    private HashMap<String,String> gameData;


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
            if(client!=null) {
                client.send(mapID);
            }
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
     * Creates a card String based on the moves that have been recieved this turn
     * @return The card string in correct order
     */
    private String createCardString(){
        ArrayList<Integer> priority;
        long seed = Math.abs(rand.nextLong());
        String order = Long.toString(seed).substring(0,5) + "//";

        for(ArrayList<String> list: moves){
            priority = new ArrayList<>();
            ArrayList<Integer> indices = new ArrayList<>();

            //Finds the priorities of the cards and adds to a new ArrayList
            for(int i=0;i<list.size();i++){
                String[] data = list.get(i).split("&");
                int tmp = Integer.parseInt(data[3]);
                priority.add(tmp);
            }

            //Finds the elements with the highest priority left in the Array
            for(int j=0;j<list.size();j++) {
                int maxPriority = -1;
                for (int k = 0; k < list.size(); k++) {
                    //If the current element has a larger priority than the previously discovered max, this should be the new max
                    if (priority.get(k) > maxPriority) {
                        indices.clear();
                        indices.add(k);
                        maxPriority = priority.get(k);
                        //If the element has the same priority as the currently discovered, it should be included as well
                    } else if (priority.get(k) == maxPriority) {
                        indices.add(k);
                    }
                }
                ArrayList<String> tmpArray=new ArrayList<>();

                //Get out the strings with the highest priority
                for(Integer integer:indices){
                    String[] tmpData = list.get(integer).split("&");
                    String tmpString = tmpData[0] + "&" + tmpData[1] + "&" + tmpData[2];
                    tmpArray.add(tmpString);
                    priority.set(integer,-2);
                }

                //randomize the order of similar priorities and write to string
                if(!tmpArray.isEmpty()) {
                    Collections.shuffle(tmpArray,rand);
                    for(String string:tmpArray){
                        order = order + string + "//";
                    }
                }
                tmpArray.clear();
                indices.clear();
            }
        }
        order = order + "TURNEND//";
        log = log + order;
        return order;
    }

    /**
     * Takes the card Array input and puts cards into separate arrays to maintain order
     * @param cardString
     */
    private void writeCardStringToList(String[] cardString){
        for (int i=1 ; i<cardString.length ; i++) {
            if (moves.size() <= i) {
                moves.add(new ArrayList<>());
            }
            moves.get(i).add(cardString[i]);
        }
    }

    /**
     * Sends a request to all clients that forces them to send their currently selected cards.
     */
    private void forceMoves(){
        //TODO: code
    }

    private int setInterval(){
        if(interval==1){
            forceMoves();
            interval = TURN_DURATION;
            timer.cancel();
        }
        else{
            interval--;
        }
        return interval;
    }

    /**
     * Defines which map will be used in the game instance
     * @param ID The map ID
     */
    public void setMapID(String ID){
        this.mapID = ID;
    }

    /**
     * Sends the card string to all clients if all clients have sent their cards
     */
    private void sendCardString(){
        numRecieved++;
        if (clients.size() == numRecieved) {
            numRecieved = 0;
            String sendString = "GET_TURN:" + createCardString();
            for (WebSocket client : clients) {
                client.send(sendString);
            }
            moves.clear();
        }
    }

    /**
     * Sends the log of the current game. Always gets called when someone joins the game
     * @param client client requesting the log
     */
    private void getLog(WebSocket client){
        client.send("GET_LOG:" + log);
    }

    /**
     * Marks the client as ready for the game to start. If all clients have readied up, sends the "START_TURN" command to all clients
     * @param conn Client that has readied up
     * @param VType The Vehicle type the client has selected
     */
    private void clientReady(WebSocket conn,String VType){
        String VID = "";
        String color = "";
        String vehicleString="";
        turnStart++;
        if(count.get()<1000) {
            VID = String.format("%03d", count.incrementAndGet());
        }
        else{
            //TODO: What if someone enters and leaves 1000 times?
        }
        VID = "V-" + VID;
        if(colors.size()>0) {
            color = colors.get(0);
            colors.remove(0);
        }
        vehicleString = VType + "," + VID + "," + color;

        vehicles.put(conn,vehicleString);
        if(turnStart==clients.size()){
            turnStart=0;
            for(WebSocket client:clients){
                if(client!=null) {
                    client.send("GAME_START");
                }
            }
        }
    }

    /**
     * Starts the new round if all players have readied up. Starts a timer after sending "START_TURN" to all clients
     */
    private void beginTurn(){
        turnStart++;
        if (turnStart == clients.size()) {
            for(WebSocket client:clients){
                client.send("BEGIN_TURN");
            }
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    System.out.println(setInterval());
                }
            }, delay, period);
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

    /**
     * Sends the vehicle ID to the client requesting it. If no vehicle ID is set, "NONE" is sent. Format: "ALL_VEHICLES:MY_VEHICLE:MAPID
     * @param client The client requesting a vehicle ID.
     */
    public String sendGameInfo(WebSocket client){
        String sendString = "GAMEINFO:";
        for(Map.Entry<WebSocket,String> vehicle:vehicles.entrySet()){
            sendString = sendString + vehicle.getValue() + "&";
        }
        sendString = sendString.substring(0,sendString.length()-1) + ":";
        sendString = sendString + mapID + ":";
        String myVID = vehicles.get(client);
        myVID = myVID.split(",")[1];
        sendString = sendString + myVID;
        client.send(sendString);
        return sendString;
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
        sendString = sendString + mapID + ":";
        sendString = sendString + Integer.toString(playerIDs.get(client));
        client.send(sendString);
        return sendString;
    }

    public HashMap<WebSocket, String> getVehicles() {
        return vehicles;
    }

    public ArrayList<WebSocket> getClients() {
        return clients;
    }
}
