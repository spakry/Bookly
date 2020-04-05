package com.example.bookly;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import org.mortbay.jetty.Main;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ClientRecyclerAdapter.onUseSessionListener,
    PopupFactory.UpdateBalanceListener{

    private static final int ADD_CLIENT_CODE = 1;
    private RecyclerView recyclerView;
    private TextView textView;

    private MainActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //get user information
        GoogleSignInAccount gs =getIntent().getParcelableExtra(Entrance.USER_DATA);
        if(gs!=null) {
            TextView tv = findViewById(R.id.welcomeTv);
            tv.setText("Hello " + gs.getGivenName() + " !");
        }


        //INITIALIZE UI ELEMENTS
        recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(RecyclerViewLayoutManager);
        textView=findViewById(R.id.tv);

        final ClientRecyclerAdapter adapter = new ClientRecyclerAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setOnUseSessionListener(this);


        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        viewModel.getAllClients().observe(this, new Observer<List<Client>>() {
            @Override
            public void onChanged(@Nullable List<Client> clients) {
                //Let Activity be the observer of live client list and act accordingly

                if (clients.size()>0){
                    TextView textView = findViewById(R.id.emptyTv);
                    textView.setVisibility(View.INVISIBLE);}

                adapter.setClientList(clients);
            }

        });

        try {
            CalendarConnector calendarConnector = new CalendarConnector(getApplicationContext());
            Calendar calendar = calendarConnector.getCalendar(gs.getEmail());
            new GetEventsAsync(calendar,this).execute();

        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void usedSession(Client client) {
        viewModel.useSession(client);
    }


    public void addClient(View view) {
        Intent intent =new Intent(this,AddClient.class);
        startActivityForResult(intent,ADD_CLIENT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == ADD_CLIENT_CODE) {
            if (resultCode == RESULT_OK) {
                Client client = (Client) data.getSerializableExtra(AddClient.ADD_CLIENT);
                viewModel.insert(client);
            } else {
                Toast.makeText(this, "There was an issue inserting the client", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode==GetEventsAsync.REQUEST_AUTHORIZATION){
            if(resultCode== RESULT_OK){
                //Calendar permission success.
                //proceed to get and process.

            }
        }


    }

    @Override
    public void onBalanceUpdate(Client updatedClient) {
        viewModel.update(updatedClient);
    }


    private class GetEventsAsync extends AsyncTask<Void,Void, List<Event>>{

        public static final int REQUEST_AUTHORIZATION = 1;
        private Calendar calendar;
        private Activity activity;

        public GetEventsAsync(Calendar calendar,Activity activity){
            this.calendar=calendar;
            this.activity = activity;
        }

        @Override
        protected List<Event> doInBackground(Void... voids) {

            DateTime now = new DateTime(System.currentTimeMillis());
            Events events = null;
            try {
                try {
                    Date date = new Date();
                    long nowMili = date.getTime();
                    long fourWeek = 2419000000L;
                    DateTime dateTime = new DateTime(nowMili-fourWeek);
                    events = calendar.events().list(CalendarConnector.CALENDAR_ID)
                            .setTimeMax(now)
                            .setTimeMin(dateTime)
                            .setOrderBy("startTime")
                            .setSingleEvents(true)
                            .execute();
                } catch (UserRecoverableAuthIOException userRecoverableException) {
                    activity.startActivityForResult(
                            userRecoverableException.getIntent(),
                            REQUEST_AUTHORIZATION);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            }catch (Exception e){e.printStackTrace();
            return null;}
        }

        @Override
        protected void onPostExecute(List<Event> events) {

            for (Event event : events){
                if(event.getSummary()!=null)
                    Log.d("event",event.getSummary());
            }

            //calculate sessions used for client in current pay period
            adjustSessions(computeSession(events));

        }


    }

    private void adjustSessions(List<Pair<Integer, Integer>> list) {
        /*
            Decrement sessions for each client in the roster.
         */
        for (Pair<Integer, Integer> pair : list){
            Integer id  = pair.first;
            Integer usedCount  = pair.second;
            Toast.makeText(this, "Updated sessions from Google Calendar!", Toast.LENGTH_SHORT).show();
            viewModel.useSessionCount(id,usedCount);
        }


    }

    private List<Pair<Integer,Integer>> computeSession(List<Event> events) {
        List<Pair<Integer,Integer>> list = new ArrayList<Pair<Integer,Integer>>();

        List<Client> clients= viewModel.getAllClients().getValue();


        for(Client client :clients){
            String name = client.getName();
            Integer id = client.getId();
            Date lastPaid = new Date();

            Date now = new Date();
            long nowMillis = now.getTime();

             try {
                 lastPaid=new SimpleDateFormat(DateContract.PAY_DATE_FORMAT).parse(client.getLastPaidDate());
                 Log.d("lastPaid", String.valueOf(lastPaid));

            } catch (ParseException e) {
                e.printStackTrace();
            }
            long lastPaidMillis = lastPaid.getTime();
            Log.d("lastPaid", String.valueOf(lastPaidMillis));

             //compute num of events in the past after last pay date
            int count = 0;
            for (Event event : events){
                String title = "";
                if (event.getSummary()!=null) {
                    title = event.getSummary();
                }
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }

                long eventDate = start.getValue();
                Log.d("name", String.valueOf(title.equals(name)));
                Log.d("time", String.valueOf(eventDate< nowMillis && eventDate>lastPaidMillis));
                if(title.equalsIgnoreCase(name)==true ){
                    Log.d("StartTime", String.valueOf(eventDate));
                }
                if (title.equalsIgnoreCase(name) && eventDate< nowMillis && eventDate>lastPaidMillis){
                    //count as used session
                    count++;
                }
            }
            Log.d("count", String.valueOf(count));

            list.add(new Pair<Integer,Integer>(id,count));
        }

        return list;
    }

}
