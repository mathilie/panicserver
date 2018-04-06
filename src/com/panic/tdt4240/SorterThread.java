package com.panic.tdt4240;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class SorterThread implements Runnable {
    static ArrayList<GameInstance> gameInstanceList;
    private Socket client;
    private Scanner in;
    private PrintWriter out;

    public SorterThread(Socket client) throws Exception{
            this.client = client;
            this.in = new Scanner(client.getInputStream());
            this.out = new PrintWriter(client.getOutputStream());
            if (gameInstanceList == null) gameInstanceList = new ArrayList<GameInstance>();
    }

    public SorterThread(){
        if(gameInstanceList==null) gameInstanceList = new ArrayList<GameInstance>();
    }


    @Override
    public void run() {
        while (true){
            if(in.hasNext()){
                System.out.println("command recieved");
                String[] data = in.next().split("//");
                String command=data[0];
                switch (command){
                    case "ENTER":
                        int gameId = Integer.parseInt(data[1]);
                        enterGame(data[2]);
                        break;
                    case "CREATE":
                        createGame();
                        break;
                    case "TEST":
                        System.out.println("Client connected and message recieved");
                    default:
                        close();
                        return;
                }
            } else {
                close();
                return;
            }
        }
    }

    private void createGame() {
        GameInstance game = new GameInstance();
        gameInstanceList.add(game);
        game.run();
    }

    private void enterGame(String datum) {
    }

    private void close(){
        out.flush();
        out.close();
        in.close();
        try {
            client.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
