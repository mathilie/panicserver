package com.panic.tdt4240;

/**
 * Created by Mathias on 12.03.2018.
 */

public class ServerInit {
    public static void main(String args[]){
        try {
            new ConnectorTester().create();
            System.out.println("server setup done");
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("server setup failed");
        }
    }

}
