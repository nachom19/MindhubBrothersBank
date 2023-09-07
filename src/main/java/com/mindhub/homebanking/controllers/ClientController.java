package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping ("/api")
public class ClientController {

    @Autowired
    private ClientService clientService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountService accountService;


    @GetMapping ("/clients")
    public List<ClientDTO> getClients(){
        return clientService.getClientsDTO();
    }

    @GetMapping ("/clients/{id}")
    private ClientDTO getClientById(@PathVariable Long id) {
        return clientService.findById(id);
    }

    @GetMapping ("/clients/current")
    public ClientDTO getCurrentClient(Authentication authentication) {
        return new ClientDTO(clientService.findByEmail(authentication.getName()));
    }

    @PostMapping ("/clients")
    public ResponseEntity<Object> register(
            @RequestParam String firstName, @RequestParam String lastName,
            @RequestParam String email, @RequestParam String password) {

        if (firstName.isBlank()) {
            return new ResponseEntity<>("Missing to complete the name", HttpStatus.FORBIDDEN);
        }
        if (lastName.isBlank()) {
            return new ResponseEntity<>("Missing to complete the lastname", HttpStatus.FORBIDDEN);
        }
        if (email.isBlank()) {
            return new ResponseEntity<>("Missing to complete the email", HttpStatus.FORBIDDEN);
        }
        if (password.isBlank()) {
            return new ResponseEntity<>("Missing to complete the password", HttpStatus.FORBIDDEN);
        }

        if (clientService.findByEmail(email) !=  null) {
            return new ResponseEntity<>("Email already in use", HttpStatus.FORBIDDEN);
        }

        Client client = new Client(firstName, lastName, email, passwordEncoder.encode(password));

        String numberAccount;
        do {
            Random random = new Random();
            numberAccount = "VIN-" + random.nextInt(99999999);
        }while (accountService.findByNumber(numberAccount) != null);

        Account account = new Account(numberAccount,0.0);
        client.addAccount(account);
        clientService.saveClient(client);
        accountService.saveAccount(account);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }


}
