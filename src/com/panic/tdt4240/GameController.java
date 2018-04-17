package com.panic.tdt4240;

import org.java_websocket.WebSocket;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


public class GameController {

    private static HashMap<Integer, LobbyHandler> lobbies;
    private static HashMap<Integer, GameHandler> games;
    private static final AtomicInteger gameCount = new AtomicInteger(0);
    private static final AtomicInteger playerCount = new AtomicInteger(0);
    private HashMap<WebSocket,Integer> playerIDs;


    public GameController(){
        if(playerIDs==null) playerIDs = new HashMap<>();
        if(lobbies==null) lobbies = new HashMap<>();
        if(games==null) games = new HashMap<>();
    }

    public void Sort(WebSocket conn, String[] data) {
        System.out.println("command recieved");
        System.out.println(Arrays.toString(data));
        String command=data[0];
        switch (command){
            case "ENTER":
                enterGame(data[1],conn);
                break;
            case "CREATE":
                createGame(conn, data[1], data[2], data[3]);
                break;
            case "TEST":
                System.out.println("Client connected and message recieved");
                conn.send("OK");
                conn.close();
                return;
            case "TOGAME":
                toGame(data, conn);
                break;
            case "GET_LOBBIES":
                getLobbies(conn);
                break;
            case "CONNECTION_ID":
                getConnection(conn, data[1]);
                //TODO: code
                break;
            default:
                return;
        }
    }

    private void toGame(String[] data, WebSocket conn) {
        int gameID = Integer.parseInt(data[1]);
        String[] dataToGame = Arrays.stream(data).skip(2).toArray(String[]::new); // removes switch command and gameID
            getGame(gameID).command(dataToGame, conn);
    }


    //Todo
    private void createGame(WebSocket conn, String mapID, String playerCount, String gameName) {
        int gameID = gameCount.incrementAndGet();
        LobbyHandler game = new LobbyHandler(gameID, playerCount,gameName);
        game.setMapID(mapID);
        game.addClient(playerIDs.get(conn), conn);
        lobbies.put(gameID,game);
        game.sendLobbyInfo(conn);
        System.out.println("Created Lobby: " + lobbies.get(gameID).getGameName());
    }


    private void enterGame(String gameID, WebSocket conn) {
        int tmp = Integer.parseInt(gameID);
        getGame(tmp).addClient(playerIDs.get(conn), conn);
    }

    private void getLobbies(WebSocket conn){
        String sendString = "GET_LOBBIES:";
        for(Map.Entry<Integer,LobbyHandler> gameInstance:lobbies.entrySet()){
            sendString = sendString + gameInstance.getValue().getGameName() + "," + gameInstance.getValue().getCurrentPlayerNum() + "," + gameInstance.getValue().getMaxPlayerCount() + "," +  gameInstance.getKey() + "&";
        }
        sendString = sendString.substring(0,sendString.length()-1);
        conn.send(sendString);
    }

    private void getConnection(WebSocket conn, String ID){
        if(!playerIDs.containsValue(Integer.parseInt(ID))){
            playerIDs.put(conn,playerCount.incrementAndGet());
        } else { //TODO fix reconnecting!!!
            WebSocket oldConn = getKeysByValue(playerIDs, Integer.parseInt(ID));
            playerIDs.remove(oldConn);
            playerIDs.put(conn, Integer.parseInt(ID));
            for(GameInstance game:lobbies.values()) game.reconnect(Integer.parseInt(ID), conn, oldConn);
            for(GameInstance game:games.values()) game.reconnect(Integer.parseInt(ID), conn, oldConn);
        }
        conn.send("CONNECTION_ID:" + Integer.toString(playerIDs.get(conn)));
    }


    public static void startGame(GameHandler gh, int id){
        if(lobbies.containsKey(id)){
            games.put(id,gh);
            lobbies.remove(id);
        }
    }

    //TODO add error handling if gameid doesn't exist
    private GameInstance getGame(int gameID){
        if(lobbies.containsKey(gameID)) {
            return lobbies.get(gameID);
        } else{
            return games.get(gameID);
        }
    }

    public void disconnected(WebSocket client){
        //for(GameInstance client:getLobbies();)
    }

    //MAGIC!
    public static <T, E> T getKeysByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void writeIncrementTOFile() throws IOException {
        String str = gameCount.toString();
        BufferedWriter writer = new BufferedWriter(new FileWriter("increments.com"));
        writer.write(str);
        writer.close();
    }


}
