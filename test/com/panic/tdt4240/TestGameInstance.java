package com.panic.tdt4240;

import org.java_websocket.WebSocket;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class TestGameInstance {
    private GameInstance gameInstance;

    private int PID1;
    private int PID2;

    @Mock
    private WebSocket testSocket1;

    @Mock
    private WebSocket testSocket2;


    @Before
    public void init(){
        gameInstance = new GameInstance(1, "2","TEST_GAME");
        testSocket1 = mock(WebSocket.class);
        PID1 = 1;
        testSocket2 = mock(WebSocket.class);
        PID2 = 2;
    }

    @Test
    public void shouldSetVehicle(){
        String command = "INIT_GAME";
        String vType = "TEST_TYPE";
        String data[] = {command,vType};
        gameInstance.addClient(PID1,testSocket1);
        gameInstance.command(data,testSocket1);
        assertEquals(1,gameInstance.getVehicles().size());
        assertEquals("TEST_TYPE,V-003,RED",gameInstance.getVehicles().get(testSocket1));
    }

    @Test
    public void shouldUpdateColorList(){
        String command1 = "INIT_GAME";
        String command2 = "LEAVE_GAME";
        String vType = "TEST_TYPE";

        String[] data1 = {command1,vType};
        gameInstance.addClient(PID1, testSocket1);
        gameInstance.command(data1,testSocket1);

        assertEquals(1,gameInstance.getClients().size());
        assertEquals("TEST_TYPE,V-004,RED",gameInstance.getVehicles().get(testSocket1));

        String[] data2 = {command2};
        gameInstance.command(data2,testSocket1);

        assertEquals(0,gameInstance.getClients().size());
        assertEquals(0,gameInstance.getVehicles().size());

        gameInstance.addClient(PID2, testSocket2);
        gameInstance.command(data1,testSocket2);
        assertEquals(1,gameInstance.getVehicles().size());
        assertEquals("TEST_TYPE,V-005,BLUE",gameInstance.getVehicles().get(testSocket2));

        gameInstance.addClient(PID1, testSocket1);
        gameInstance.command(data1,testSocket1);
        assertEquals(2,gameInstance.getVehicles().size());
        assertEquals("TEST_TYPE,V-006,GREEN",gameInstance.getVehicles().get(testSocket1));
    }

    @Test
    public void shouldCreateSendGameInfoString(){
        String[] data = {"INIT_GAME","TEST_TYPE"};
        String[] requestGameInfo = {"GAMEINFO"};
        String MapID = "M-001";
        gameInstance.addClient(PID1, testSocket1);
        gameInstance.addClient(PID2, testSocket2);
        gameInstance.setMapID(MapID);
        gameInstance.command(data,testSocket1);
        gameInstance.command(data,testSocket2);
        String returnString = gameInstance.sendGameInfo(testSocket1);
        assertEquals("GAMEINFO:TEST_TYPE,V-002,BLUE&TEST_TYPE,V-001,RED:M-001:V-001",returnString);

    }

    @Test
    public void shouldSetMapID(){
        gameInstance.setMapID("TestMap");
    }
}
