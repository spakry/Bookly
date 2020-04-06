package com.example.bookly;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = SessionRecord.class, version = 1, exportSchema = false)
public abstract class SessionDatabase  extends RoomDatabase {

    private static SessionDatabase mInstance = null;
    public  abstract SessionDao sessionDao();

    public static synchronized SessionDatabase getInstance(Context context){
        if(mInstance==null){
            mInstance = Room.databaseBuilder(context.getApplicationContext(),
                    SessionDatabase.class, "client_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return mInstance;
    }
}
