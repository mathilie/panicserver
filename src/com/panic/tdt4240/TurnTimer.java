package com.panic.tdt4240;

/**
 * Created by Mathias on 12.03.2018.
 */

public class TurnTimer implements Runnable {

    private long globalClock;
    private long duration;
    private boolean pause;
    private TurnListener listener;
    private boolean running = true;

    public TurnTimer(){
        reset();
    }


    //TODO
    public float getTimeLeft(){
        return 90.0f;
    }

    public boolean setTimer(long duration) {
        if(globalClock==0 && duration==0) {
            this.duration = duration;
            pause = false;
            return true;
        }
        return false;
    }

    public boolean setTimer() {
        return setTimer(60000);
    }

    public boolean reset(){
        duration = 0;
        globalClock = 0;
        pause = true;
        return true;
    }

    public boolean pause() {
        return true;
    }

    public void stopTimer(){
        running = false;
    }

    public void setListener(TurnListener tl){this.listener = tl; }

    @Override
    public void run() {
        long oldTime;
        long currentTime = System.currentTimeMillis();
        while (running) {
            oldTime = currentTime;
            currentTime = System.currentTimeMillis();
            if (!pause) {
                globalClock += currentTime - oldTime;
                if (globalClock > duration) {
                    listener.turnFinished();
                    reset();
                }
            }
        }
    }
}
