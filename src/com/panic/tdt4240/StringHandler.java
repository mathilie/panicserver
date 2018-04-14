package com.panic.tdt4240;

import java.util.*;

/**
 * Created by magnus on 06.04.2018.
 */

public class StringHandler {
    private Random rand;
    private static final int MAX_PLAYER_COUNT = 4;
    private ArrayList<String> players;
    private ArrayList<ArrayList<String>> moves;
    private String history;
    private String mapID;
    private String gameName;

    public StringHandler(){
        rand = new Random();
        moves = new ArrayList<>();
        history = "";
    }

    public StringHandler(Long seed){
        rand = new Random(seed);
        moves = new ArrayList<>();
        history = "";
    }

    /**
     * Runs when lobby gets created. Defines the map to be run and
     * @param data
     */
    public void createLobby(String data){
        String[] elements = data.split("//");
        readMapID(elements[0]);
    }

    public void addToMoves(ArrayList<String> moveList){
        moves.add(moveList);
    }

    public ArrayList<ArrayList<String>> getMoves() {
        return moves;
    }

    private void readMapID(String mapID){
        this.mapID=mapID;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}