package com.example.bookly;

import androidx.lifecycle.LiveData;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface Client_Dao {

    @Insert
    void insert(Client client);

    @Update
    void update(Client client);

    @Delete
    void delete(Client client);

    @Query("DELETE FROM client_table")
    void deleteAllNotes();

    @Query("SELECT * FROM client_table ORDER BY balance ASC")
    LiveData<List<Client>> getAllClients();


    @Query("SELECT * FROM client_table WHERE id = :clientId")
    Client getClient(String clientId);



}
