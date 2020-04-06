package com.example.bookly;

import android.media.MediaCas;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
@Dao
public interface SessionDao {

    @Insert
    void insert(SessionRecord sessionRecord);

    @Update
    void update(SessionRecord sessionRecord);

    @Delete
    void delete(SessionRecord sessionRecord);

    @Query("DELETE FROM SESSION_TABLE")
    void deleteAll();

    @Query("SELECT * FROM SESSION_TABLE ORDER BY clientId ASC")
    LiveData<List<SessionRecord>> getAllSessions();

    @Query("SELECT * FROM SESSION_TABLE WHERE clientId = :clientId")
    List<SessionRecord> getSessionsOf(String clientId);
}
