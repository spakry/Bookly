package com.example.bookly;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddClient extends AppCompatActivity {

    public static final String ADD_CLIENT = "add_client";
    private EditText nameEt;
    private EditText balanceEt;
    private EditText rateEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);

         nameEt = findViewById(R.id.name);
         balanceEt = findViewById(R.id.balance);
         rateEt = findViewById(R.id.rate);

    }


    public void finishClick(View view) {

        //primary input check then proceed
        if (nameEt.getText().length() > 0 && Long.valueOf(balanceEt.getText().toString()) > 0 &&
                Long.valueOf(rateEt.getText().toString()) > 0) {

            String name = nameEt.getText().toString();
            long balance = Long.parseLong(balanceEt.getText().toString());
            long rate = Long.parseLong(rateEt.getText().toString());
            Client client = new Client(name, rate, balance);

            //set payment details
            client.setBalanceAfterLastPaid(balance);
            client.setLastPaidDate(DateContract.getTodayDate());


            Intent data = new Intent();
            data.putExtra(ADD_CLIENT, client);
            setResult(RESULT_OK, data);
            finish();

            Toast.makeText(this, "Your client has been added!", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, "Please check your client information!", Toast.LENGTH_SHORT).show();



    }


}
