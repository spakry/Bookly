package com.example.bookly;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class CalendarConnector {
    public static final String CALENDAR_ID = "primary";
    private  final String APPLICATION_NAME = "Bookly";
    private  final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private  final String TOKENS_DIRECTORY_PATH = "tokenss";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
//    private  final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String[] SCOPES = { CalendarScopes.CALENDAR };

    private  final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private final Context context;
    private String PREF_ACCOUNT_NAME = "NAME";

    public CalendarConnector(Context context) {
        this.context = context;
    }

    /**
     * Creates an authorized Credential object.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */

    private  GoogleAccountCredential getCredentials(String accountName){

        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                context, Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(accountName);

      return credential;
    }

    public Calendar getCalendar( String accountName)throws  IOException, GeneralSecurityException{
        final NetHttpTransport HTTP_TRANSPORT =new com.google.api.client.http.javanet.NetHttpTransport();
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(accountName))
                .setApplicationName(APPLICATION_NAME)
                .build();

        return service;
    }

    public  List<Event> getEvents(Calendar service) throws IOException {
        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list(CALENDAR_ID)
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            System.out.println("No upcoming events found.");
        } else {
            System.out.println("Upcoming events");
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                System.out.printf("%s (%s)\n", event.getSummary(), start);
            }
        }
        return items;
    }


}
