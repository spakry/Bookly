package com.example.bookly;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

class ClientRecyclerAdapter extends RecyclerView.Adapter<ClientRecyclerAdapter.ViewHolder> {

    /*
        Adapter for recyclerview of current clients.


     */

    private final Activity activity;
    private List<Client> clientList;
    private onUseSessionListener onUseSessionListener;



    public ClientRecyclerAdapter(Activity activity) {
        this.activity = activity;
        this.clientList = new ArrayList<>();
    }

    public void setClientList(List<Client> list){
        this.clientList = list;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ClientRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.client_recycler_layout,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder v, int i) {
        //set values
        final Client client = clientList.get(i);
        final int clientId = client.getId();
        String name = client.getName();

        //values
        long sessionRate=client.getRate();
        long balance=client.getBalance();;
        int sessionsLeft = (int) ((int)balance/sessionRate);

        //display values
        v.name.setText(name);
        v.sessionRate.setText("Rate: " + String.valueOf(sessionRate));
        v.sessionsLeft.setText("Sessions Left: " + sessionsLeft);
        v.useSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onUseSessionListener.usedSession(client);
                    Toast.makeText(activity, "Used one session", Toast.LENGTH_SHORT).show();
            }
        });

        v.addCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open add credit dialogue
                PopupFactory popupFactory = new PopupFactory(activity);
                PopupWindow popup = popupFactory.setClient(client).setView(view)
                        .setUpdateBalanceListener((PopupFactory.UpdateBalanceListener) activity).build();
                popup.showAtLocation(view, Gravity.CENTER, 0, 300);
            }
        });
    }


    @Override
    public int getItemCount() {
        return clientList.size();
    }

    public void setOnUseSessionListener(onUseSessionListener onUseSessionListener) {
        this.onUseSessionListener = onUseSessionListener;
    }

    public interface onUseSessionListener{
        void usedSession(Client client);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,sessionsLeft,sessionRate,addCredit,useSession;

        public ViewHolder(@NonNull View v) {
            super(v);
            name = v.findViewById(R.id.tvName);
            sessionsLeft = v.findViewById(R.id.tvSessionsLeft);
            sessionRate = v.findViewById(R.id.tvSessionRate);
            addCredit = v.findViewById(R.id.tvAddCredit);
            useSession = v.findViewById(R.id.tvUseSession);

        }
    }
}
