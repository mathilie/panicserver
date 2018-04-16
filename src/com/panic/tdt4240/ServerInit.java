package com.panic.tdt4240;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;


public class ServerInit extends WebSocketServer {
    GameController c = new GameController();
    public ServerInit(InetSocketAddress address) {
        super(address);
    }


    public static void main(String[] args) {
        String host = "0.0.0.0";
        int port = Integer.parseInt(args[0]);

        WebSocketServer server = new ServerInit(new InetSocketAddress(host, port));
        server.setConnectionLostTimeout(15);
        server.run();
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        String[] data = message.split("//");
        c.Sort(conn,data);
    }


    //===============SYSOUT ON EVENT ONLY==============================

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        //conn.send("Welcome to the server!"); //This method sends a message to the new client
        //broadcast( "new connection: " + handshake.getResourceDescriptor() ); //This method sends a message to all clients connected
        System.out.println("new connection to " + conn.getRemoteSocketAddress());
        //TODO see if player is reconnecting to game
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
        //TODO see if player was active in a game
    }


    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
        System.out.println("received ByteBuffer from "	+ conn.getRemoteSocketAddress());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("an error occured on connection " + conn.getRemoteSocketAddress()  + ":" + ex);
    }

    @Override
    public void onStart() {
        System.out.println("server started successfully");
    }


}