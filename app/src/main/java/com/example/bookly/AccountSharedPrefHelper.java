package com.example.bookly;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

class AccountSharedPrefHelper {
    private final Activity a;
    public final String ACCOUNT_KEY = "ACCOUNT";
    private Gson gson = new GsonBuilder().create();


    public AccountSharedPrefHelper(Activity a) {
        this.a = a;
    }

    public void putUser(String userJson){
        SharedPreferences sharedPref = a.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ACCOUNT_KEY,userJson);
        editor.commit();
    }
    public GoogleSignInAccount getUser (){
        SharedPreferences sharedPref = a.getPreferences(Context.MODE_PRIVATE);
        String user = sharedPref.getString(ACCOUNT_KEY,"");
        GoogleSignInAccount account = gson.fromJson(user, GoogleSignInAccount.class);
        return account;
    }
    public void wipeUser(){
        SharedPreferences sharedPref = a.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(ACCOUNT_KEY);
        editor.commit();
    }

    public boolean didLogin(){
        if (this.getUser().equals(""))return false;
        return true;
    }


}
