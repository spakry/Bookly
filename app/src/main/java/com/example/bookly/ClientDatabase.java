package com.example.bookly;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = Client.class, version = 3, exportSchema = false)
public abstract class ClientDatabase extends RoomDatabase {

    private  static ClientDatabase mInstance = null;
    public  abstract Client_Dao client_dao();
    public static synchronized ClientDatabase getInstance(Context context){
        if(mInstance==null){
            mInstance = Room.databaseBuilder(context.getApplicationContext(),
                    ClientDatabase.class, "client_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return mInstance;
    }



}
