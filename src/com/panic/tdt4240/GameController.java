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
    private static HashMap<Integer, Integer> playerIDGameID;
    private static final AtomicInteger gameCount = new AtomicInteger(0);
    private static final AtomicInteger playerCount = new AtomicInteger(0);
    private HashMap<WebSocket,Integer> playerIDs;


    public GameController(){
        if(playerIDs==null) playerIDs = new HashMap<>();
        if(lobbies==null) lobbies = new HashMap<>();
        if(games==null) games = new HashMap<>();
        if(playerIDGameID==null) playerIDGameID = new HashMap<>();
    }

    public void Sort(WebSocket conn, String[] data) {
        System.out.println("command recieved");
        System.out.println(Arrays.toString(data));
        String command=data[0];
        switch (command){
            case "ENTER":
                enterGame(data[1],conn);
                break;
            case "EXIT":
                exitGame(conn);
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
            case "RECONNECT":
                int pId = Integer.parseInt(data[1]);
                int gameID = playerIDGameID.get(pId);
                getGame(gameID).reconnect(pId,conn);
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
        LobbyHandler lobby = new LobbyHandler(gameID, playerCount,gameName);
        lobbies.put(gameID,lobby);
        lobby.addClient(playerIDs.get(conn), conn);
        lobby.setMapID(mapID);
        playerIDGameID.put(playerIDs.get(conn), gameID);
        System.out.println("Created Lobby: " + lobbies.get(gameID).getGameName());
    }


    private void enterGame(String gameID, WebSocket conn) {
        int tmp = Integer.parseInt(gameID);
        boolean entered = getGame(tmp).addClient(playerIDs.get(conn), conn);
        if(entered) playerIDGameID.put(playerIDs.get(conn), tmp);
    }

    private void exitGame(WebSocket conn) {
        disconnected(conn);
    }

    private void getLobbies(WebSocket conn){
        String sendString = "GET_LOBBIES:";
        for(GameInstance gameInstance:lobbies.values()){
            sendString = sendString  + gameInstance.getGameName() + "," + gameInstance.getCurrentPlayerNum() + "," + gameInstance.getMaxPlayerCount() + "," + gameInstance.gameID+"&";
        }
        sendString = sendString.substring(0,sendString.length()-1);
        conn.send(sendString);
    }

    private void getConnection(WebSocket conn, String ID) {
        int pid = Integer.parseInt(ID);
        if (!playerIDs.containsValue(pid)) {
            playerIDs.put(conn, playerCount.incrementAndGet());
        } else if (playerIDGameID.containsKey(pid)) {
            getGame(pid).reconnect(pid, conn);
            conn.send("RECONNECT_GAME:"+playerIDGameID.get(pid));
        } else{
            WebSocket old = getKeysByValue(playerIDs, pid);
            playerIDs.remove(old);
            playerIDs.put(conn, pid);
        }
        conn.send("CONNECTION_ID:" + Integer.toString(playerIDs.get(conn)));
    }


    public static void startGame(GameHandler gh, int id, String mapID){
        if(lobbies.containsKey(id)){
            gh.mapID=mapID;
            games.put(id,gh);
            lobbies.remove(id);
        }
    }

    //TODO add error handling if gameid doesn't exist
    private GameInstance getGame(int gameID){
        if(lobbies.containsKey(gameID)) {
            return lobbies.get(gameID);
        } else if (games.containsKey(gameID)){
            return games.get(gameID);
        } else {
            return null;
        }
    }

    public void disconnected(WebSocket client){
        if(playerIDs.containsKey(client)){
            System.out.println("Found pid");
            int gameId = playerIDGameID.get(playerIDs.get(client));
            boolean destroy = getGame(gameId).removeClient(client);
            System.out.println("Destroy is :"+destroy);
            if(destroy){
                lobbies.remove(gameId);
                games.remove(gameId);
            }
        }
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
