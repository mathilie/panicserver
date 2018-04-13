package com.panic.tdt4240.test;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

public class EmptyClient extends WebSocketClient {

	public EmptyClient(URI serverUri, Draft draft) {
		super(serverUri, draft);
	}
	public EmptyClient(URI serverURI) {
		super(serverURI);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		send("Hello, it is me. Mario :)");
		System.out.println("new connection opened");
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.println("closed with exit code " + code + " additional info: " + reason);
	}

	@Override
	public void onMessage(String message) {
		System.out.println("received message: " + message);
		switch(message) {
			case "newTurn":
				//dostuff
				break;
			case "onMessage":

				break;
		}

	}

	@Override
	public void onMessage(ByteBuffer message) {
		System.out.println("received ByteBuffer");
	}

	@Override
	public void onError(Exception ex) {
		System.err.println("an error occurred:" + ex);
	}

	public static void main(String[] args) throws URISyntaxException {		
		WebSocketClient client = new EmptyClient(new URI("ws://panicserver.herokuapp.com"));
		client.connect();
		client.send("Hello world");

	}


	static class Listener{
		String returnstring="";
		public void onMessage(String s){
			returnstring = s;
		}
		public String waitForReturn(){
			while(returnstring.length()<1){
			}
			String s = returnstring;
			returnstring = "";
			return s;
		}

}
}