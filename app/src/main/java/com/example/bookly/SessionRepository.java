package com.example.bookly;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class SessionRepository {

    /*
        Envelope to access backend transactions.
     */

    private SessionDao dao;
    private LiveData<List<SessionRecord>> allSessions;

    public SessionRepository(Application application) {
        SessionDatabase sessionDatabase = SessionDatabase.getInstance(application);
        dao=sessionDatabase.sessionDao();
        allSessions= dao.getAllSessions();
    }

    public void insertSessionRecord(SessionRecord SessionRecords){
        new SessionRepository.Insert(dao).execute(SessionRecords);
    }

    public void updateSessionRecord(SessionRecord SessionRecords){
        new SessionRepository.Update(dao).execute(SessionRecords);
    }

    public void deleteSessionRecord(SessionRecord SessionRecords){
        new SessionRepository.Delete(dao).execute(SessionRecords);
    }
    public void deleteAllSessionRecords(){
        new SessionRepository.DeleteAll(dao).execute();
    }

    public LiveData<List<SessionRecord>> getAllSessionRecords( ){
        return allSessions;
    }

    private static class Insert extends AsyncTask<SessionRecord, Void, Void> {
        private SessionDao dao;

        private Insert(SessionDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(SessionRecord... sessionRecords) {
            for(SessionRecord sessionRecord:sessionRecords){
                dao.insert(sessionRecord);
            }
            return null;
        }
    }

    private static class Delete extends AsyncTask<SessionRecord, Void, Void> {
        private SessionDao dao;

        private Delete(SessionDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(SessionRecord... sessionRecords) {
            for(SessionRecord sessionRecord:sessionRecords){
                dao.delete(sessionRecord);
            }
            return null;
        }
    }

    private static class DeleteAll extends AsyncTask<Void, Void, Void> {
        private SessionDao dao;

        private DeleteAll(SessionDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            dao.deleteAll();
            return null;
        }
    }


    private static class Update extends AsyncTask<SessionRecord, Void, Void> {
        private SessionDao dao;

        private Update(SessionDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(SessionRecord... sessionRecords) {

            for (SessionRecord sessionRecord : sessionRecords)
                dao.update(sessionRecord);

            return null;
        }
    }
}
