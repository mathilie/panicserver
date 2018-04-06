package com.panic.tdt4240.gameMakerThread;


import com.panic.tdt4240.GameInstance;
import com.panic.tdt4240.SorterThread;

import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by magnus on 06.04.2018.
 */

public class GameMaker implements Runnable{
    private static final AtomicInteger count = new AtomicInteger(0);

    public GameMaker(Socket client){
        GameInstance gameInstance= new GameInstance(count.incrementAndGet(),client);
        SorterThread.gameInstanceList.add(gameInstance);
        new Thread(gameInstance).run();
    }
}
