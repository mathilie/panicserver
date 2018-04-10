package com.panic.tdt4240;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;

/**
 * @ServerEndpoint gives the relative name for the end point
 * This will be accessed via ws://localhost:8080/EchoChamber/echo
 * Where "localhost" is the address of the host,
 * "EchoChamber" is the name of the package
 * and "echo" is the address to access this class from the server
 */
@ServerEndpoint("/test")
public class JavaxWebsocketServer {
    private ArrayList<GameInstance> gameInstanceList;

    public JavaxWebsocketServer() throws Exception{
        if (gameInstanceList == null) gameInstanceList = new ArrayList<GameInstance>();
    }


    /**
     * @OnOpen allows us to intercept the creation of a new session.
     * The session class allows us to send data to the user.
     * In the method onOpen, we'll let the user know that the handshake was
     * successful.
     */
    @OnOpen
    public void onOpen(Session session){
        System.out.println(session.getId() + " has opened a connection");
/*        try {
            session.getBasicRemote().sendText("Connection Established");
        } catch (IOException ex) {
            ex.printStackTrace();
        }*/
    }

    /**
     * When a user sends a message to the server, this method will intercept the message
     * and allow us to react to it. For now the message is read as a String.
     */
    @OnMessage
    public void onMessage(String message, Session session){
        int counter = 0;
            if(message!= null) {
                System.out.println("command recieved");
                String[] data = message.split("//");
                String command = data[0];
                switch (command) {
                    case "ENTER":
                        int gameId = Integer.parseInt(data[1]);
                        enterGame(data[2]);
                        break;
                    case "CREATE":
                        createGame();
                        break;
                    case "TEST":
                        System.out.println("Client connected and message recieved");
                        return;
                    default:
                        System.out.println("No command. Thread is closing");
                        return;
                }
            }
    }


    /**
     * The user closes the connection.
     *
     * Note: you can't send messages to the client from this method
     */
    @OnClose
    public void onClose(Session session){
        System.out.println("Session " +session.getId()+" has ended");
    }


    private void createGame() {
        GameInstance game = new GameInstance();
        gameInstanceList.add(game);
        game.run();
    }

    private void enterGame(String datum) {
    }
}