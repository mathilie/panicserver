package com.panic.tdt4240;

/**
 * Created by Mathias on 12.03.2018.
 */

public class StateHandeler implements TurnListener {
    private static final char PAUSE = 'a';
    private static final char PLAY = 'p';
    private static final char SHOW = 's';
    private static final char END = 'e';

    private TurnTimer tt;
    private char gameState;


    @Override
    public void turnFinished() {

    }

    @Override
    public void pauseOn() {

    }

    @Override
    public void pauseOff() {

    }
}
