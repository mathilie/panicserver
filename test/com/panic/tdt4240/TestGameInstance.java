package com.panic.tdt4240;

import org.java_websocket.WebSocket;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(JUnit4.class)
public class TestGameInstance {
    GameInstance gameInstance;


    @Before
    public void init(){
        gameInstance = new GameInstance();
    }

    @Test
    public void shouldSetVehicle(){
        String command = "INIT_GAME";
        String PID = "P-001";
        String VID = "V-002";
        String data[] = {command,PID,VID};
        gameInstance.command(data,null);
        assertEquals(1,gameInstance.getVehicles().size());
        assertEquals("V-002",gameInstance.getVehicles().get(null));
    }

    @Test
    public void shouldSetMapID(){
        gameInstance.setMapID("TestMap");
    }
}
