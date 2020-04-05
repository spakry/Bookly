package com.example.bookly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.AndroidViewModel;

public class PopupFactory {

    private final Context context;
    private String title, content;
    private View view;
    private Client client;
    private UpdateBalanceListener updateBalanceListener;

    public PopupFactory(Context context) {
        this.context=context;
    }

    public PopupFactory setClient(Client client){
        this.client = client;
        return this;
    }

    public PopupFactory setView(View view){
        this.view=view;
        return this;
    }
    public PopupFactory setTitle(String title){
        this.title=title;
        return this;
    }
    public PopupFactory setMessage(String message){
        this.content=message;
        return this;
    }
    public PopupFactory setUpdateBalanceListener(UpdateBalanceListener updateBalanceListener) {
        this.updateBalanceListener = updateBalanceListener;
        return this;
    }

    public PopupWindow build(){
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        final EditText addCreditEt = popupView.findViewById(R.id.addCreditEt);


        Button okBtn = popupView.findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //update balance of client
                //get snapshot of updated balance at time of update
                //record date of update

                //update the client model.
                long addBalance = Long.parseLong(addCreditEt.getText().toString());
                long pastBalance = client.getBalance();
                long newBalance = pastBalance+addBalance;
                client.setBalance(newBalance);
                client.setLastPaidDate(DateContract.getTodayDate());
                client.setBalanceAfterLastPaid(newBalance);

                //update client in room.
                updateBalanceListener.onBalanceUpdate(client);

                Toast.makeText(context, "The client balance has been updated!", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        });

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                popupWindow.dismiss();
                return true;
            }
        });

        return popupWindow;
    }


    public  interface UpdateBalanceListener{
        void onBalanceUpdate(Client updatedClient);
    }


}
