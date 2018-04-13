package com.panic.tdt4240;

import org.java_websocket.WebSocket;

import java.util.ArrayList;

public class GameInstance{

    private static final int MAX_PLAYER_COUNT = 4;
    private static final int TURN_DURATION = 90;
    private ArrayList<WebSocket> clients;
    private ArrayList<ArrayList<String>> moves;
    private StringHandler handler;
    private long seed;
    private String history;
    private int numRecieved;
    private Timer timer;
    private int delay;
    private int period;
    private static int interval;


    public GameInstance(){
        timer = new Timer();
        interval = TURN_DURATION;
        delay = 1000;
        period = 1000;

        clients = new ArrayList<WebSocket>();
        seed = System.currentTimeMillis();
        handler = new StringHandler(seed);
        history = "";
        numRecieved = 0;
    }

    public void addClient(WebSocket client){
        if(clients.size()>=MAX_PLAYER_COUNT) {
            clients.add(client);
        }
        else{
            System.out.println("Attempted to join a full game");
        }
    }

    public long getSeed() {
        return seed;
    }


    public void command(String[] data){
        switch (data[0]){
            case "TIMER":
                //code
                break;

            case "MOVES":
                ArrayList<String> cardArray = new ArrayList<>(Arrays.asList(data));
                cardArray.remove(0);
                writeCardStringToList(cardArray);
                numRecieved++;
                if(clients.size()==numRecieved){
                    numRecieved=0;
                    String sendString = createCardString();
                    for(WebSocket client:clients){
                        client.send(sendString);
                    }
                    moves.clear();
                }
                break;

            case "BEGIN_TURN":
                timer.scheduleAtFixedRate(new TimerTask() {

                    @Override
                    public void run(){
                        System.out.println(setInterval());
                    }
                }, delay, period);
                //code
                break;

            default:

        }

    }

    /**
     * Creates a card String based on the
     * @param order The current order string. if none exist, initiates to an empty string
     * @return The card string in correct order
     */
    private String createCardString(){
        String order = "";
        ArrayList<Integer> priority;

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
                    long seed = Math.abs(rand.nextLong());
                    String tmpString = tmpData[0] + "&" + tmpData[1] + "&" + tmpData[2] + "&" + Long.toString(seed).substring(0,5);
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
    private void writeCardStringToList(ArrayList<String> cardString){
        for (int i=0 ; i<cardString.size() ; i++){
            if (moves.size()<=i){
                moves.add(new ArrayList<>());
            }
            moves.get(i).add(cardString.get(i));
        }
    }

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

}
