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
    private SessionRepository sessionRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //get user information
        GoogleSignInAccount gs =getIntent().getParcelableExtra(Entrance.USER_DATA);
        if(gs!=null) {
            TextView tv = findViewById(R.id.welcomeTv);
            String name = gs.getGivenName().substring(0,1).toUpperCase()+ gs.getGivenName().substring(1);
            tv.setText("Hello " + name + " !");
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


        //Initiate the view model of this
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        viewModel.getAllClients().observe(this, new Observer<List<Client>>() {
            @Override
            public void onChanged(@Nullable List<Client> clients) {

                //Let Activity be the observer of live client list and act accordingly

                if (clients.size()>0){
                    TextView textView = findViewById(R.id.emptyTv);
                    textView.setVisibility(View.INVISIBLE);}

                //relay changed client info to adapter.
                adapter.setClientList(clients);
            }

        });

        //observe the list of sessions
        sessionRepository = new SessionRepository(getApplication());
        sessionRepository.getAllSessionRecords().observe(this, new Observer<List<SessionRecord>>() {
            @Override
            public void onChanged(List<SessionRecord> sessionRecords) {


                //Check if client balance has been adjusted for the session,
                for (SessionRecord session: sessionRecords){
                    if(session.isUpdateBalance()==false) {
                        int clientId = session.getClientId();
                        Client client = fetchClient(clientId);
                        session.setUpdateBalance(true);
                        viewModel.useSession(client);
                    }
                }

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

    private Client fetchClient(int clientId) {
        //fetch the client using the id using an snapshot of the live list.
        List<Client> clientList= viewModel.getAllClients().getValue();
        for(Client client: clientList)
            if(client.getId()==clientId)
                return client;

            return null;
    }

    @Override
    public void usedSession(Client client) {
        //callback method from the adapter
        viewModel.useSession(client);
    }


    @Override
    public void onBalanceUpdate(Client updatedClient) {
        //callback method from the adapter
        viewModel.update(updatedClient);
    }



    public void addClient(View view) {
        Intent intent =new Intent(this,AddClient.class);
        startActivityForResult(intent,ADD_CLIENT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == ADD_CLIENT_CODE) {
            if (resultCode == RESULT_OK) {
                //add the client to storage
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
            List<SessionRecord> sessionList =  computeSession(events);
            updateDB(sessionList);

        }


    }

    private void updateDB(List<SessionRecord> sessionList) {
        //sort the list of sessions counted as used.
        //event summary must == client name.
        // Now > session time  >last pay date
        List<SessionRecord> sessionRecordsLocal = sessionRepository.getAllSessionRecords().getValue();
        for(SessionRecord session:sessionList){
            String dateTime = session.getDateTime();
            int id = session.getClientId();

            for (SessionRecord sLocal:sessionRecordsLocal){
                if (sLocal.getDateTime().equalsIgnoreCase(dateTime) && sLocal.getClientId()==id)
                    return;
            }

            //if not found add this session to local record.
            sessionRepository.insertSessionRecord(session);
        }

    }

    private List<SessionRecord> computeSession(List<Event> events) {
        List<SessionRecord> list = new ArrayList<SessionRecord>();
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
                    Log.d("startTimeString",start.toString());
                    list.add(new SessionRecord(start.toString(),client.getId()));
                    count++;
                }
            }
            Log.d(client.getName()+"count", String.valueOf(count));
            return list;

        }

        return list;
    }

}
