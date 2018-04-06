package com.panic.tdt4240.test;

import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionTest {
    public static void main(String[] args) {
        try{
        System.out.println();
        Socket s = new Socket("panicserver.heroku.com", 80);
            PrintWriter out = new PrintWriter(s.getOutputStream());
            out.println("TEST");
            out.flush();
            out.close();
            s.close();
        } catch (Exception e) {e.printStackTrace();}
    }
}
