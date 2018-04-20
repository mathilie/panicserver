package com.panic.tdt4240;

/**
 * Created by Mathias on 12.03.2018.
 */

public class TurnTimer implements Runnable {

    private long globalClock;
    private long duration;
    private boolean pause = true;
    private TurnListener listener;
    private boolean running = false;

    public TurnTimer(){
        reset();
    }


    //TODO
    public float getTimeLeft(){
        return (duration-globalClock)/1000-5;
    }

    public boolean setTimer(long duration) {
        reset();
        if(globalClock==0 && this.duration==0) {
            this.duration = duration;
            return true;
        }
        return false;
    }

    public boolean setTimer() {
        return setTimer(95000);
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
        pause = true;
    }

    public void startTimer(){
        pause = false;
    }

    public void setListener(TurnListener tl){this.listener = tl; }

    @Override
    public void run() {
        long oldTime;
        long currentTime = System.currentTimeMillis();
        while (true) {
            oldTime = currentTime;
            currentTime = System.currentTimeMillis();
            if (!pause) {
                globalClock += currentTime - oldTime;
                if (globalClock > duration) {
                    listener.turnFinished();
                    return;
                }
            }
        }
    }

}
