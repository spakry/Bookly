package com.example.bookly;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.api.client.util.DateTime;

@Entity(tableName = "session_table")
public class SessionRecord {
    /*
        Model for session
        Room entity.
     */

    @PrimaryKey
    @NonNull
    private String dateTime;
    private int clientId;
    private boolean updateBalance;


    public SessionRecord(@NonNull String dateTime, int clientId) {
        this.dateTime = dateTime;
        this.clientId = clientId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public boolean isUpdateBalance() {
        return updateBalance;
    }

    public void setUpdateBalance(boolean updateBalance) {
        this.updateBalance = updateBalance;
    }
}
