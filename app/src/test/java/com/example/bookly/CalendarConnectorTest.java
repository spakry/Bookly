package com.example.bookly;

import org.junit.Test;

import javax.inject.Inject;

import static org.junit.Assert.*;

public class CalendarConnectorTest {


    /*
        We have created a singleton instance of the calendar connector class.
     */

    @Test
    public void testSingleton(){

        assertEquals(calendarConnector , calendarConnector2);
        assertNotNull(calendarConnector.s);
    }

}