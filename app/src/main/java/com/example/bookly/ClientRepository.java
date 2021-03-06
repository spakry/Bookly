package com.example.bookly;

import android.app.Application;
import androidx.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class ClientRepository {

    /*
        Repository envelope for accessing the client resource.

     */


    private Client_Dao dao;
    private LiveData<List<Client>> allClients;

    public ClientRepository(Application application) {
        ClientDatabase clientDatabase = ClientDatabase.getInstance(application);
        dao=clientDatabase.client_dao();
        allClients= dao.getAllClients();
    }

    public void insertClient(Client clients){
        new InsertClient(dao).execute(clients);
    }

    public void updateClient(Client clients){
        new UpdateClients(dao).execute(clients);
    }

    public void deleteClient(Client clients){
        new DeleteClients(dao).execute(clients);
    }
    public void deleteAllClients(){
        new DeleteAllClients(dao).execute();
    }

    public LiveData<List<Client>> getAllClients( ){
        return allClients;
    }

    public void useSession(Client client){
        String id = String.valueOf(client.getId());
        new UseSessionTask(dao).execute(id);
    }

    private static class UseSessionTask extends AsyncTask<String, Void, Void> {
        private Client_Dao clientDao;

        private UseSessionTask(Client_Dao clientDao) {
            this.clientDao = clientDao;
        }

        @Override
        protected Void doInBackground(String... ids) {

            Client client = clientDao.getClient(ids[0]);

            //decrement balance by one session
            long bal = client.getBalance();
            bal = bal-client.getRate();
            client.setBalance(bal);

            clientDao.update(client);
            return null;
        }
    }


    public void useSession_Count(int id,int count){

        new UseSessionCountTask(dao,count).execute(String.valueOf(id));
    }

    private static class UseSessionCountTask extends AsyncTask<String, Void, Void> {
        private Client_Dao clientDao;
        private int useCount = 0;

        private UseSessionCountTask(Client_Dao clientDao, Integer useCount) {
            this.clientDao = clientDao;
            this.useCount = useCount;
        }

        @Override
        protected Void doInBackground(String... ids) {

            Client client = clientDao.getClient(ids[0]);

            //decrement balance by one session
            long bal = client.getBalance();
            bal = bal-client.getRate()*useCount;
            client.setBalance(bal);
            clientDao.update(client);

            return null;
        }
    }

    private static class InsertClient extends AsyncTask<Client, Void, Void> {
        private Client_Dao clientDao;

        private InsertClient(Client_Dao clientDao) {
            this.clientDao = clientDao;
        }

        @Override
        protected Void doInBackground(Client... clients) {
            for (Client client : clients){
                clientDao.insert(client);
            }
            return null;
        }
    }

    private static class UpdateClients extends AsyncTask<Client, Void, Void> {
        private Client_Dao clientDao;

        private UpdateClients(Client_Dao clientDao) {
            this.clientDao = clientDao;
        }

        @Override
        protected Void doInBackground(Client... clients) {
            for (Client client : clients){
                clientDao.update(client);
            }
            return null;
        }
    }

    private static class DeleteClients extends AsyncTask<Client, Void, Void> {
        private Client_Dao clientDao;

        private DeleteClients(Client_Dao clientDao) {
            this.clientDao = clientDao;
        }

        @Override
        protected Void doInBackground(Client... clients) {
            for (Client client : clients){
                clientDao.delete(client);
            }
            return null;
        }
    }

    private static class DeleteAllClients extends AsyncTask<Void, Void, Void> {
        private Client_Dao clientDao;

        private DeleteAllClients(Client_Dao clientDao) {
            this.clientDao = clientDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
                clientDao.deleteAllNotes();
            return null;
        }
    }

}
