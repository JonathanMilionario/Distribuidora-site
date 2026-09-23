package com.distribebidas.service;

import com.distribebidas.model.entity.Client;
import com.distribebidas.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Client getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));
    }

    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    @Transactional
    public Client payClientDebt(Long clientId, Double amount) {
        Client client = getClientById(clientId);
        double current = client.getCreditBalance() != null ? client.getCreditBalance() : 0.0;
        double newBalance = Math.max(0.0, current - amount);
        client.setCreditBalance(newBalance);
        return clientRepository.save(client);
    }

    @Transactional
    public Client returnClientBottles(Long clientId, int count) {
        Client client = getClientById(clientId);
        int current = client.getReturnableBottlesPending() != null ? client.getReturnableBottlesPending() : 0;
        int newPending = Math.max(0, current - count);
        client.setReturnableBottlesPending(newPending);
        return clientRepository.save(client);
    }

    public List<Client> getClientsWithDebt() {
        return clientRepository.findClientsWithDebt();
    }
}
