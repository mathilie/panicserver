package com.panic.tdt4240;

import org.java_websocket.WebSocket;

import java.util.*;

public class GameHandler extends GameInstance implements TurnListener{
    private TurnTimer timer;
    private long seed;
    private ArrayList<ArrayList<Card>> moves;
    private int turnStart;
    private String log;
    private SanityChecker sc;
    private Thread timerThread;
    private int playersAlive;


    public GameHandler(int gameID, String gameName, HashMap<WebSocket, List> playerMap, HashMap<Integer, WebSocket> idMap){
        super(gameID, gameName, playerMap, idMap);
        moves = new ArrayList<>();
        log = "";
        timer = new TurnTimer();
        updateSeed();
        moves = new ArrayList<>();
        playersAlive = playerIDs.size();
        sc = new SanityChecker();
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
                writeCardStringToList(Arrays.stream(data).skip(1).toArray(String[]::new));
                break;
            case "ENTERED_RUN_EFFECTS_STATE":
                sendCardString();
                break;
            case "END_RUN_EFFECTS_STATE":
                sc.sanityPassed();//todo
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
                reconect(Integer.parseInt(data[0]), conn);
                break;
            case "DESTROY":
                destroy();
                break;
            default:
        }
    }


    //String: TOGAME//GameID//DESTROY//VID//PID
    private void destroy() {
        //int vidToDestroy = Integer.parseInt(data[1]);
    }

    @Override //TODO
    public boolean removeClient(WebSocket ws) {
        if(playerIDs.containsValue(ws)){
            disconnect(ws);
            if (playerIDs.size()==0){
                return true;
            }
        }
        return false;
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
        returnString=returnString;
        returnString=returnString+"TURNEND//";
        log = log + returnString;
        return returnString;
    }

    /**
     * Takes the card Array input and puts cards into separate arrays to maintain order
     * @param cardString
     */
    private void writeCardStringToList(String[] cardString){
        ArrayList<Card> playerCards = new ArrayList<Card>();
        for (String card: cardString) playerCards.add(new Card(card));
        moves.add(playerCards);
        numRecieved++;
        System.out.println("numRecieved: " + numRecieved + ", playersAlive = " + playersAlive);
        if(numRecieved==playersAlive){
            for(WebSocket client:players.keySet()) client.send("TURN_END");
            numRecieved=0;
        }
    }


    /**
     * Sends the card string to all clients if all clients have sent their cards
     */
    private void sendCardString(){
        numRecieved++;
        if (playersAlive == numRecieved) {
            numRecieved = 0;
            timerThread.interrupt();
            String sendString = "GET_TURN:" + createCardString();
            for (WebSocket client : playerIDs.values()) {
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
        if (turnStart == playersAlive) {
            for(WebSocket client:players.keySet()){
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
        for(WebSocket client:players.keySet()) client.send("TURN_END");
    }



    /**
     * Sends the vehicle ID to the client requesting it. If no vehicle ID is set, "NONE" is sent. Format: "ALL_VEHICLES:MY_VEHICLE:MAPID
     * //
     * @param client The client requesting a vehicle ID.
     */
    public String sendGameInfo(WebSocket client){
        String sendString = "GAME_INFO:";
        //TODO ----What info?
        for(WebSocket player:players.keySet()){ //VTYPE,VID,COLOR&
            sendString = sendString
                    + players.get(player).get(1)+","
                    + players.get(player).get(2)+","
                    + players.get(player).get(3)+"&";
        }
        sendString = sendString.substring(0,sendString.length()-1) + ":";
        sendString = sendString + mapID + ":";
        String myVID = (String) players.get(client).get(2);
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
        seed = new Random().nextLong();
    }


    //TODO make game out of lobby
    public void startGame(){
        for(WebSocket client:players.keySet()) client.send("START");
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
        HashMap<WebSocket, String> gameHashes = new HashMap<>();
        HashMap<Integer, List<Boolean>> destroy = new HashMap<Integer, List<Boolean>>();  //<vehicleIDtoDestroy, votes>


        public void FinishTurn(){
            sanityChecks();
        }
        //Todo sanityCHecks
        public void sanityChecks() {
            numRecieved++;
            if(numRecieved==playersAlive) {
                numRecieved=0;
                sanityPassed();
            }
        }


        protected void addDestroy(Integer vid, boolean vote) {
            if (destroy.containsKey(vid)) {
                destroy.get(vid).add(vote);
                if (destroy.get(vid).size() == playersAlive && !destroy.get(vid).contains(false)) {
                    destroyVehicle(vid);
                }
            } else {
                destroy.put(vid,new ArrayList<Boolean>());
                addDestroy(vid,vote);
            }
        }


        //TODO not implemented
        protected void addHash (WebSocket client, String hash){
            gameHashes.put(client, hash);
            if (gameHashes.size() == playersAlive) {
                if (turnStart == playersAlive) {
                    turnStart = 0;
                    for (WebSocket player : players.keySet()) {
                        if (player != null) {
                            player.send("GAME_START");
                        }
                    }
                }
            }
        }


        private void destroyVehicle(Integer vid) {
        }

        private void sanityPassed(){
            for(WebSocket conn:players.keySet()) conn.send("VALID_STATE");
        }
    }
        //NOT TO BE IMPLEMENTED DUE TO TIME CONSTRAIN

    private void writeIncrementToFile(){
        //Writes current increment to file in case of server crash.
    }

    private void writeLog(String s){
        //Write moves to log, to make reconnecting possible.
    }

    private void reconect(int playerID, WebSocket player){
        addClient(playerID, player);
    }
}
