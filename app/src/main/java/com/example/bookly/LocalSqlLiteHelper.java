package com.example.bookly;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.NonNull;

import android.util.Log;

import java.util.UUID;

public class LocalSqlLiteHelper extends SQLiteOpenHelper {
    private static LocalSqlLiteHelper sInstance;
    private static SQLiteDatabase mDatabase;

    //Table details
    private static String DATABASE_NAME = "bookings.db";
    private static String TABLE_NAME = "Bookings_table";
    public final static String NAME = "name";
    public final static String CLIENTID = "clientId";
    public final static String BALANCE = "balance";
    public final static String RATE = "rate";
    public final static String SESSIONSLEFT = "sessionsLeft";


    private Boolean initialDbCreated = false;



    private LocalSqlLiteHelper(Context context) {
        super(context, TABLE_NAME, null,1);
    }

    public static synchronized LocalSqlLiteHelper getInstance(Context context){
        if (sInstance==null){
            sInstance = new LocalSqlLiteHelper(context);
        }
        return sInstance;
    }


    private boolean isTableExists(String tableName, boolean openDb) {
        if (openDb) {
            if (mDatabase == null || !mDatabase.isOpen()) {
                mDatabase = getReadableDatabase();
            }

            if (!mDatabase.isReadOnly()) {
                mDatabase.close();
                mDatabase = getReadableDatabase();
            }
        }


        String query = "select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'";
        try (Cursor cursor = mDatabase.rawQuery(query, null)) {
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    return true;
                }
            }
            return false;
        }
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


            String createTableQuery = "create table if not exists";
            createTableQuery += TABLE_NAME + " (";
            createTableQuery += NAME+" TEXT,";
            createTableQuery += CLIENTID+" TEXT PRIMARY KEY,";
            createTableQuery += BALANCE+" INTEGER,";
            createTableQuery += RATE+" INTEGER,";
            createTableQuery += SESSIONSLEFT + " INTEGER";
            createTableQuery += ")";

            sqLiteDatabase.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        String dropQ = "drop table if EXISTS "+TABLE_NAME;
        sqLiteDatabase.execSQL(dropQ);
        onCreate(sqLiteDatabase);

    }


    public boolean insertClient(Client client){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(NAME, client.getName());
            contentValues.put(BALANCE, client.getBalance());
            contentValues.put(RATE, client.getRate());
            contentValues.put(CLIENTID, generateClientId());
            db.insert(TABLE_NAME, null, contentValues);

            return true;
        }catch (Exception e){return false;}

    }

    private String generateClientId() {
        String uniqueID = UUID.randomUUID().toString();
        return  uniqueID;
    }

    public void removeTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        String q = "DROP TABLE IF EXISTS "+TABLE_NAME;
        db.execSQL(q);
    }

    public int addBalance(String clientId, float balanceAdd){
        /*
            +1 success
            -1 unsuccess

            Add to client's balance

         */
        try {

            Cursor cursor = getClientFromId(clientId);

            if(cursor.getCount()>0){cursor.moveToFirst();
            int iBalance=cursor.getColumnIndex(BALANCE);
            float curBalance = cursor.getFloat(iBalance);


                String q = "";
                q+= "UPDATE"+ TABLE_NAME+ "SET";
                q+= BALANCE + "=" + balanceAdd+curBalance;
                q+= "WHERE" + clientId + "=" + clientId;
                getWritableDatabase().execSQL(q);
            }


        }
        catch (Exception e){e.printStackTrace();
            return -1;
        }
        return 1;


    }

    private Cursor getClientFromId(String clientId) {

        String query = "";
        query += "SELECT " +"'" +BALANCE+"'";
        query += "FROM " + TABLE_NAME ;
        query += "WHERE" +  "'"+CLIENTID+"'" +"="+ clientId;
        Log.d("sql query: ",query);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        return  cursor;
    }

    public Cursor getClients(){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "";

        query += "SELECT " + " * ";
        query += "FROM " + TABLE_NAME + " ORDER BY "+ "'"+SESSIONSLEFT+ "'" +" ASC ";
        Log.d("sql query: ",query);

        Cursor cursor = db.rawQuery(query,null);
        return cursor;

    }


    public int useSession(@NonNull String clientId) {
       try {
           Cursor cursor = getClientFromId(clientId);
           if (cursor.moveToFirst()) {
               int iBalance = cursor.getColumnIndex(BALANCE);
               int iRate = cursor.getColumnIndex(RATE);
               float cBalance = cursor.getFloat(iBalance);
               float rate = cursor.getFloat(iRate);

               float nBalance = cBalance - rate;
               String q = "";
               q += "UPDATE" + TABLE_NAME + "SET";
               q += BALANCE + "=" + nBalance;
               q += "WHERE" + clientId + "=" + clientId;
               getWritableDatabase().execSQL(q);
           }
       }
       catch (Exception e){e.printStackTrace(); return -1;}

       return 1;
    }
}
