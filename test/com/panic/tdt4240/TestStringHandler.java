package com.panic.tdt4240;

import com.panic.tdt4240.StringHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;


public class TestStringHandler {

    private StringHandler stringHandler;

    @Before
    public void setUp() {
        stringHandler = new StringHandler((long) 10);
    }

    @Test
    public void shouldCreateStringFromScratch() {
        String s1 = "Test1&TestPerson1&TestAsteroid1&1";
        String s2 = "Test2&TestPerson2&TestAsteroid2&2";
        String s3 = "Test3&TestPerson3&TestAsteroid3&3";
        String s4 = "Test4&TestPerson4&TestAsteroid4&4";

        String s5 = "Test5&TestPerson5&TestAsteroid5&1";
        String s6 = "Test6&TestPerson6&TestAsteroid6&2";
        String s7 = "Test7&TestPerson7&TestAsteroid7&2";
        String s8 = "Test8&TestPerson8&TestAsteroid8&2";

        ArrayList<String> testList1 = new ArrayList<>();
        testList1.add(s1);
        testList1.add(s2);
        testList1.add(s3);
        testList1.add(s4);

        ArrayList<String> testList2 = new ArrayList<>();
        testList2.add(s5);
        testList2.add(s6);
        testList2.add(s7);
        testList2.add(s8);

        stringHandler.addToMoves(testList1);
        stringHandler.addToMoves(testList2);

        String result = stringHandler.createCardString(null);
        assertEquals("Test4&TestPerson4&TestAsteroid4&49726//Test3&TestPerson3&TestAsteroid3&47556//Test2&TestPerson2&TestAsteroid2&10920//Test1&TestPerson1&TestAsteroid1&45031//Test7&TestPerson7&TestAsteroid7&68365//" +
                "Test8&TestPerson8&TestAsteroid8&26511//Test6&TestPerson6&TestAsteroid6&33423//Test5&TestPerson5&TestAsteroid5&52989//TURNEND//", result);
    }
    @Test
    public void ShouldAddToString() {
        String s1 = "Test1&TestPerson1&TestAsteroid1&1";
        String s2 = "Test2&TestPerson2&TestAsteroid2&2";
        String s3 = "Test3&TestPerson3&TestAsteroid3&3";
        String s4 = "Test4&TestPerson4&TestAsteroid4&4";

        String startString = s4 + "//" + s3 + "//" + s2 + "//" + s1 + "//";

        String s5 = "Test5&TestPerson5&TestAsteroid5&1";
        String s6 = "Test6&TestPerson6&TestAsteroid6&2";
        String s7 = "Test7&TestPerson7&TestAsteroid7&2";
        String s8 = "Test8&TestPerson8&TestAsteroid8&2";

        ArrayList<String> testList = new ArrayList<>();
        testList.add(s5);
        testList.add(s6);
        testList.add(s7);
        testList.add(s8);
        stringHandler.addToMoves(testList);
        String result = stringHandler.createCardString(startString);
        assertEquals("Test4&TestPerson4&TestAsteroid4&4//Test3&TestPerson3&TestAsteroid3&3//Test2&TestPerson2&TestAsteroid2&2//Test1&TestPerson1&TestAsteroid1&1//Test8&TestPerson8&TestAsteroid8&10920//" +
                "Test6&TestPerson6&TestAsteroid6&49726//Test7&TestPerson7&TestAsteroid7&47556//Test5&TestPerson5&TestAsteroid5&33423//TURNEND//", result);
    }

    @Test
    public void shouldWriteToMoveList(){
        String s1 = "Test1&TestPerson1&TestAsteroid1&1";
        String s2 = "Test2&TestPerson2&TestAsteroid2&2";
        String s3 = "Test3&TestPerson3&TestAsteroid3&3";
        String s4 = "Test4&TestPerson4&TestAsteroid4&4";
        String testString = s1 + "//" + s2 + "//" + s3 + "//" + s4 + "//";
        stringHandler.writeCardStringToList(testString);
        assertEquals(4, stringHandler.getMoves().size());
        assertEquals(s1 , stringHandler.getMoves().get(0).get(0));
    }
}
