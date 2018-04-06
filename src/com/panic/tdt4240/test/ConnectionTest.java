package com.panic.tdt4240.test;

import java.net.Socket;

public class ConnectionTest {
    public static void main(String[] args) {
        try{
        System.out.println();
        Socket s = new Socket("panicserver.heroku.com", 80);
        } catch (Exception e) {e.printStackTrace();}
    }
}
