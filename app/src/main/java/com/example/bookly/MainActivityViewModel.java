package com.example.bookly;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {

    private ClientRepository clientRepository;
    private LiveData<List<Client>> clientLiveList;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);

        //Client data is accessed through the repository
        clientRepository = new ClientRepository(getApplication());
        clientLiveList = clientRepository.getAllClients();
    }

    public void useSession(Client client){
        clientRepository.useSession(client);
    }

    public void useSessionCount(Integer id, int count){
        clientRepository.useSession_Count(id,count);
    }



    public void insert(Client client) {
        clientRepository.insertClient(client);
    }

    public void update(Client client) {
        clientRepository.updateClient(client);
    }

    public void delete(Client client) {
        clientRepository.deleteClient(client);
    }

    public void deleteAllClients() {
        clientRepository.deleteAllClients();
    }

    public LiveData<List<Client>> getAllClients() {
        return clientLiveList;
    }
}
