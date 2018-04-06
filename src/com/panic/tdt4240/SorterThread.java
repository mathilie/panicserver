package com.panic.tdt4240;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class SorterThread implements Runnable {

    static HashMap<Integer, GameInstance> gameInstances;
    private static final AtomicInteger count = new AtomicInteger(0);
    private Socket client;
    private Scanner in;
    private PrintWriter out;

    public SorterThread(Socket client) throws Exception{
            this.client = client;
            this.in = new Scanner(client.getInputStream());
            this.out = new PrintWriter(client.getOutputStream());
            if (gameInstances == null) gameInstances = new HashMap<Integer,GameInstance>();
    }

    public SorterThread(){
        if(gameInstances==null) gameInstances = new HashMap<Integer, GameInstance>();
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
        GameInstance game = new GameInstance(client);
        gameInstances.put(count.incrementAndGet(),game);
        game.run();
    }

    private void enterGame(String ID) {
        int tmp = Integer.parseInt(ID);
        GameInstance game = gameInstances.get(tmp);
        game.run();

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
