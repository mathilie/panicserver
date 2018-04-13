package com.panic.tdt4240;

import org.java_websocket.WebSocket;

import java.util.*;

public class GameInstance{

    private static final int MAX_PLAYER_COUNT = 4;
    private static final int TURN_DURATION = 90;
    private ArrayList<WebSocket> clients;
    private ArrayList<ArrayList<String>> moves;
    private HashMap<WebSocket,String> vehicles;
    private long seed;
    Random rand;
    String mapID;
    private String history;
    private int numRecieved;
    private int turnStart;
    private Timer timer;
    private int delay;
    private int period;
    private static int interval;


    public GameInstance(){
        timer = new Timer();
        interval = TURN_DURATION;
        delay = 1000;
        period = 1000;

        moves = new ArrayList<>();
        rand = new Random();
        clients = new ArrayList<>();
        vehicles = new HashMap<>();
        seed = System.currentTimeMillis();

        history = "";
        turnStart = 0;
        numRecieved = 0;
    }

    public void addClient(WebSocket client){
        if(clients.size()>=MAX_PLAYER_COUNT) {
            clients.add(client);
            client.send(mapID);
        }
        else{
            System.out.println("Attempted to join a full game");
        }
    }

    public long getSeed() {
        return seed;
    }


    public void command(String[] data, WebSocket conn){
        switch (data[0]) {

            case "INIT_GAME":
                clientReady(conn,data[1]);
                break;

            case "GET_MAP":
                writeMapID();
                break;

            case "GAME_INFO":
                sendGameInfo(conn);
                break;

            case "MOVES":
                writeCardStringToList(data);
                sendcardString();
                break;

            case "BEGIN_TURN":
                beginTurn();
                //code
                break;

            case "LEAVE_GAME":
                removeClient(conn);
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
                if(tmpArray!=null) {
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
        history = history + order;
        return order;
    }

    /**
     * Takes the card Array input and puts cards into separate arrays to maintain order
     * @param cardString
     */
    private void writeCardStringToList(String[] cardString){
        numRecieved++;
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

    private void writeMapID() {
        for(WebSocket client:clients){
            client.send(mapID);
        }
    }

    /**
     * Sends the card string to all clients if all clients have sent their cards
     */
    private void sendcardString(){
        if (clients.size() == numRecieved) {
            numRecieved = 0;
            String sendString = createCardString();
            for (WebSocket client : clients) {
                client.send(sendString);
            }
            moves.clear();
        }
    }

    /**
     * Marks the client as ready for the game to start. If all clients have readied up, sends the "START_TURN" command to all clients
     * @param conn Client that has readied up
     * @param VType The Vehicle type the client has selected
     */
    private void clientReady(WebSocket conn,String VType){
        turnStart++;

        vehicles.put(conn,VType);
        if(turnStart==clients.size()){
            turnStart=0;
            for(WebSocket client:clients){
                client.send("START_TURN");
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
     * Starts the new round if all players have readied up. Starts a timer after sending "START_TURN" to all clients
     */
    private void beginTurn(){
        turnStart++;
        if (turnStart == clients.size()) {
            for(WebSocket client:clients){
                client.send("START_TURN");
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
            if(vehicles.containsKey(client)) {
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
    private void sendGameInfo(WebSocket client){
        String sendString = "GAMEINFO:";
        for(Map.Entry<WebSocket,String> vehicle:vehicles.entrySet()){
            sendString = sendString + vehicle.getValue() + "&";
        }
        sendString = sendString.substring(0,sendString.length()-1) + ":";
        sendString = sendString + mapID + ":";
        String myVID = vehicles.get(client).split("&")[1];
        String tmpString = sendString + myVID;
        client.send(tmpString);
    }

    protected HashMap<WebSocket, String> getVehicles() {
        return vehicles;
    }
}
