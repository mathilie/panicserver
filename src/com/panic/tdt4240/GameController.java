package com.panic.tdt4240;

import org.java_websocket.WebSocket;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
                getConnection(conn);
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


    private void createGame(WebSocket conn, String mapID, String playerCount, String gameName) {
        int gameID = gameCount.incrementAndGet();
        LobbyHandler lobby = new LobbyHandler(gameID, playerCount,gameName);
        lobby.setMapID(mapID);
        lobby.addClient(playerIDs.get(conn), conn);
        lobbies.put(gameID,lobby);
        lobby.sendLobbyInfo(conn);
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

    private void getConnection(WebSocket conn){
        if(!playerIDs.containsKey(conn)){
            playerIDs.put(conn,playerCount.incrementAndGet());
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
        } else{
            return games.get(gameID);
        }
    }

    public void disconnected(WebSocket client){
        //for(GameInstance client:getLobbies();)
    }
}
