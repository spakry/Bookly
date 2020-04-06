package com.example.bookly;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Entrance extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    public static final String USER_DATA = "USER_DATA";
    private Gson gson = new GsonBuilder().create();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);

        //checkPrevLogin();
    }

    private void checkPrevLogin() {
        AccountSharedPrefHelper ap = new AccountSharedPrefHelper(this);
        if(ap.didLogin()){

            goToMain(ap.getUser());
        }
    }

    private void goToMain(GoogleSignInAccount account) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(USER_DATA,account);
        startActivity(intent);
        finish();
    }

    public void loginGoogle(View view) {
        //user clicks login with google.

        //authenticate user with google signin flow.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            String email = account.getEmail();
            if(email!=null) {

                //store user data
                AccountSharedPrefHelper sp = new AccountSharedPrefHelper(this);
                String json = gson.toJson(account);
                sp.putUser(json);

                goToMain(account);
            }

        } catch (ApiException e) {
            Log.w("", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "There was an error logging in!", Toast.LENGTH_SHORT).show();
        }

    }
}
