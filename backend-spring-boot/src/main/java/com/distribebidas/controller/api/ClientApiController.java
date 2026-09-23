package com.distribebidas.controller.api;

import com.distribebidas.model.dto.BottleReturnDto;
import com.distribebidas.model.dto.ClientPaymentDto;
import com.distribebidas.model.entity.Client;
import com.distribebidas.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@CrossOrigin(origins = "*")
public class ClientApiController {

    private final ClientService clientService;

    public ClientApiController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<List<Client>> getClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClient(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @PostMapping
    public ResponseEntity<Client> saveClient(@Valid @RequestBody Client client) {
        return ResponseEntity.ok(clientService.saveClient(client));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Client> payDebt(@PathVariable Long id, @RequestParam Double amount) {
        return ResponseEntity.ok(clientService.payClientDebt(id, amount));
    }

    @PostMapping("/{id}/pay-dto")
    public ResponseEntity<Client> payDebtWithDto(@PathVariable Long id, @Valid @RequestBody ClientPaymentDto dto) {
        return ResponseEntity.ok(clientService.payClientDebt(id, dto.getAmount()));
    }

    @PostMapping("/{id}/return-bottles")
    public ResponseEntity<Client> returnBottles(@PathVariable Long id, @RequestParam int count) {
        return ResponseEntity.ok(clientService.returnClientBottles(id, count));
    }

    @PostMapping("/{id}/return-bottles-dto")
    public ResponseEntity<Client> returnBottlesWithDto(@PathVariable Long id, @Valid @RequestBody BottleReturnDto dto) {
        return ResponseEntity.ok(clientService.returnClientBottles(id, dto.getCount()));
    }
}
