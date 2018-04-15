package com.panic.tdt4240;

import org.java_websocket.WebSocket;

import java.util.*;

public class GameHandler extends GameInstance implements TurnListener{
    private Timer timer;
    private final long seed;
    private static final int TURN_DURATION = 90;
    private HashMap<Integer, String> gameHashes;
    private ArrayList<ArrayList<String>> moves;
    private int turnStart;
    private int delay;
    private int period;
    private static int interval;
    private String log;
    private Random rand;


    public GameHandler(int gameID, String gameName, ArrayList<WebSocket> clients, HashMap<WebSocket, String> vehicles){
        super(gameID, gameName);
        seed = 1;
        super.clients = clients;
        super.vehicles = vehicles;
    }

    /**
     * The method used to decide which methods to be run
     * @param data The given input for the methods. The first element is always the command string which decides method call
     * @param conn The client sending the request
     */
    @Override
    public void command(String[] data, WebSocket conn){
        switch (data[0]) {
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
            default:
        }


    }

    @Override
    public void removeClient(WebSocket ws) {

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

    //TODO What's this what's this?

/*    public void addHash(WebSocket client, String hash){
        gameHashes.put(clients.indexOf(client), hash);
        if(gameHashes.size()==clients.size()){


        vehicles.put(client,vehicleString);
        if(turnStart==clients.size()){
            turnStart=0;
            for(WebSocket client:clients){
                if(client!=null) {
                    client.send("GAME_START");
                }
            }

        }
    }*/


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



    //TODO: Java timer vs self made timer
    //Overrides from timer
    @Override
    public void turnFinished() {

    }

    @Override
    public void pauseOn() {

    }

    @Override
    public void pauseOff() {

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
    public void startGame(){
        for(WebSocket client: clients) client.send("START");
        //timer = new TurnTimer();
        //timer.setListener(this);
        //new Thread(timer).start();
        gameHashes = new HashMap<>();
    }


}
