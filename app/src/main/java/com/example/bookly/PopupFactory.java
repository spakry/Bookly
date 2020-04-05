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

public class PopupFactory {

    private final Context context;
    private String title, content;
    private View view;
    private String clientId;

    public PopupFactory(Context context) {
        this.context=context;
    }

    public PopupFactory setClientId(String clientId){
        this.clientId = clientId;
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
                //add balance
                float addBalance = Float.parseFloat(addCreditEt.getText().toString());

                LocalSqlLiteHelper localSqlLiteHelper = LocalSqlLiteHelper.getInstance(context);
                int success =localSqlLiteHelper.addBalance(clientId,addBalance);
                if(success==1) {
                    popupWindow.dismiss();
                }
                Toast.makeText(context, "There was an error updating balance", Toast.LENGTH_SHORT).show();
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


}
