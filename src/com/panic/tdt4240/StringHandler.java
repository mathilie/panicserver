package com.panic.tdt4240;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Created by magnus on 06.04.2018.
 */

public class StringHandler {
    private Random rand;
    private static final int MAX_PLAYER_COUNT = 4;
    private ArrayList<ArrayList<String>> moves;
    private String history;

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


    public void addToMoves(ArrayList<String> moveList){
        moves.add(moveList);
    }

    public String createCardString(String order){

        if(order==null) {
            order = "";
        }
        ArrayList<Integer> priority;

        for(ArrayList<String> list: moves){
            priority = new ArrayList<>();
            ArrayList<Integer> indices = new ArrayList<>();

            //Finds the priorities of the cards and adds to a new ArrayList
            for(int i=0;i<list.size();i++){
                int tmp = Integer.parseInt(list.get(i).split("&")[3]);
                priority.add(tmp);
            }

            //Finds the elements with the highest priority left in the Array
            for(int j=0;j<list.size();j++) {
                int maxPriority = -1;
                for (int k = 0; k < list.size(); k++) {
                    //If the current element has a larger priority than the previously discovered max, this should be the new max
                    if (priority.get(k) > maxPriority) {
                        indices.clear();
                        indices.add(k);
                        maxPriority = priority.get(k);
                    //If the element has the same priority as the currently discovered, it should be included as well
                    } else if (priority.get(k) == maxPriority) {
                        indices.add(k);
                    }
                }
                ArrayList<String> tmpArray=new ArrayList<>();

                //Get out the strings with the highest priority
                for(Integer integer:indices){
                    tmpArray.add(list.get(integer));
                    priority.set(integer,-2);
                }

                //randomize the order of similar priorities and write to string
                if(tmpArray!=null) {
                    Collections.shuffle(tmpArray,rand);
                    for(String string:tmpArray){
                        order = order + string + "//";
                    }
                }
                tmpArray.clear();
                indices.clear();
            }
        }
        order = order + "TURNEND//";
        history = history + order;
        return order;
    }

    public void writeCardStringToList(String cardString){
        String[] data = cardString.split("//");
        for (int i=0 ; i<data.length ; i++){
            if (moves.size()<=i){
                moves.add(new ArrayList<>());
            }
            moves.get(i).add(data[i]);
        }
    }

    public ArrayList<ArrayList<String>> getMoves() {
        return moves;
    }
}