package com.panic.tdt4240.gameMakerThread;


import com.panic.tdt4240.GameInstance;
import com.panic.tdt4240.SorterThread;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by magnus on 06.04.2018.
 */

public class GameMaker {
    private static final AtomicInteger count = new AtomicInteger(0);

    public GameMaker(){
        GameInstance gameInstance= new GameInstance(count.incrementAndGet());
        SorterThread.gameInstanceList.add(gameInstance);
    }
}
