package com.panic.tdt4240;

import org.java_websocket.WebSocket;

import java.util.*;

public class GameHandler extends GameInstance implements TurnListener{
    private TurnTimer timer;
    private long seed;
    private ArrayList<ArrayList<Card>> moves;
    private int turnStart;
    private String log;
    private Random rand;
    private SanityChecker sc;
    private Thread timerThread;


    public GameHandler(int gameID, String gameName, ArrayList<WebSocket> clients, HashMap<WebSocket, String> v){
        super(gameID, gameName, clients, v);
        rand = new Random();
        log = "";
        super.clients = clients;
        updateSeed();
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
                writeCardStringToList(Arrays.copyOfRange(data, 1, data.length-1));
                break;
            case "ENTERED_RUN_EFFECT_STATE":
                sendCardString();
                break;
            case "BEGIN_TURN":
                beginTurn();
                break;
            case "LEAVE_GAME":
                removeClient(conn);
                break;
            case "GET_TIME_LEFT":
                conn.send(String.valueOf(timer.getTimeLeft()));
                break;
            case "RECONNECT":
                conn.send("TO BE IMPLEMENTED LATER. SORRY!");
                conn.close();
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
    public String createCardString(){
        String returnString = "";
        ArrayList<Card> roundOfCards = new ArrayList<Card>();
        for(int i=0;i<3;i++){
            for(ArrayList<Card> player:moves) {
                if(player.size()>i) roundOfCards.add(player.get(i));
            }
            Collections.shuffle(roundOfCards);
            Collections.sort(roundOfCards);
            for(Card nextCard:roundOfCards) returnString=returnString+nextCard+"//";
            roundOfCards.clear();
        }
        returnString=returnString+"TURNEND//";
        log = log + returnString;
        return returnString;
    }

    /**
     * Takes the card Array input and puts cards into separate arrays to maintain order
     * @param cardString
     */
    private void writeCardStringToList(String[] cardString){
        System.out.println("Alive and good");
        ArrayList<Card> playerCards = new ArrayList<Card>();
        for (String card: cardString) playerCards.add(new Card(card));
        moves.add(playerCards);
        numRecieved++;
        System.out.println("numRecieved: " + numRecieved + ", clients.size = " + clients.size());
        if(numRecieved==vehicles.size()){
            for(WebSocket client:clients) client.send("TURN_END");
            numRecieved=0;
        }
    }


    /**
     * Sends the card string to all clients if all clients have sent their cards
     */
    private void sendCardString(){
        numRecieved++;
        if (clients.size() == numRecieved) {
            numRecieved = 0;
            timerThread.interrupt();
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
     * Starts the new round if all players have readied up. Starts a timer after sending "START_TURN" to all clients
     */
    private void beginTurn(){
        turnStart++;
        if (turnStart == clients.size()) {
            for(WebSocket client:clients){
                client.send("BEGIN_TURN:"+timer.getTimeLeft());
            }
            timer.setTimer();
            timerThread = new Thread(timer);
            timerThread.start();
        }
    }


    //Overrides from timer
    /**
     * Sends a request to all clients that forces them to send their currently selected cards.
     */
    @Override
    public void turnFinished() {
        for(WebSocket client:clients) client.send("TURN_END");
    }



    /**
     * Sends the vehicle ID to the client requesting it. If no vehicle ID is set, "NONE" is sent. Format: "ALL_VEHICLES:MY_VEHICLE:MAPID
     * @param client The client requesting a vehicle ID.
     */
    public String sendGameInfo(WebSocket client){
        String sendString = "GAME_INFO:";
        for(Map.Entry<WebSocket,String> vehicle:vehicles.entrySet()){
            sendString = sendString + vehicle.getValue() + "&";
        }
        sendString = sendString.substring(0,sendString.length()-1) + ":";
        sendString = sendString + mapID + ":";
        String myVID = vehicles.get(client);
        myVID = myVID.split(",")[1];
        sendString = sendString + myVID + ":";
        sendString = sendString + Long.toString(getSeed()).substring(0,5);
        if(!log.isEmpty()) {
            sendString = sendString + ":" + log;
        }
        client.send(sendString);
        System.out.println(sendString);
        return sendString;
    }

    private long getSeed() {
        return seed;
    }
    private void updateSeed(){
        seed = rand.nextLong();
    }


    //TODO make game out of lobby
    public void startGame(){
        for(WebSocket client: clients) client.send("START");
        timer = new TurnTimer();
        timer.setListener(this);
    }

    protected class Card implements Comparable<Card>{
        private String cardString;
        private int priority;
        protected Card(String cardString){
            this.cardString = cardString;
            priority=Integer.parseInt(cardString.split("&")[3]);
        }
        @Override
        public String toString(){
            return cardString;
        }
        @Override
        public int compareTo(Card o) {
            if(o==null) return 1;
            return o.priority-this.priority; //This is inverted so that the highest priority is returned as the lowest card to function in the sort method.
        }
    }
    class SanityChecker {
        HashMap<Integer, String> gameHashes = new HashMap<>();

        public void addHash(WebSocket client, String hash) {
            gameHashes.put(clients.indexOf(client), hash);
            if (gameHashes.size() == clients.size()) {

                if (turnStart == clients.size()) {
                    turnStart = 0;
                    for (WebSocket clientt : clients) {
                        if (client != null) {
                            client.send("GAME_START");
                        }
                    }

                }
            }
        }
    }

    //NOT TO BE IMPLEMENTED DUE TO TIME CONSTRAIN

    private void writeIncrementToFile(){
        //Writes current increment to file in case of server crash.
    }

    private void writeLog(String s){
        //Write moves to log, to make reconnecting possible.
    }

    private void reconect(int playerID){

    }
}
