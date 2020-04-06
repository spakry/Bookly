package com.example.bookly;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class EventFetcher {

    /*
        Returns events from the calendar using calendar injection
     */

    private Calendar calendar;
    public EventFetcher(Calendar calendar) {
        this.calendar = calendar;
    }

    public List<Event> getCompletedSessionAfterTimePaid(String clientId, DateTime lastTimePaid)
            throws GeneralSecurityException, IOException {
        /*
             List of sessions returned is after the last pay period to the present time.
             These are the sessions completed for client in this current pay period.

         */

            DateTime now = new DateTime(System.currentTimeMillis());
            Events events = calendar.events().list(CalendarConnector.CALENDAR_ID)
                    .setTimeMin(lastTimePaid)
                    .setTimeMax(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .setQ("displayName = Mariia")
                    .execute();
            List<Event> items = events.getItems();
            return items;

        }


}
