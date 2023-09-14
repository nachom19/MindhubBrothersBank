package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;

import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;

    @GetMapping ("/accounts")
    public List<AccountDTO> getAccounts(){
        return accountService.getAccountsDTO();
    }

    @GetMapping ("/accounts/{id}")
    private ResponseEntity<Object> getAccountById(@PathVariable Long id, Authentication authentication){
        Account account = accountService.findById(id);
        Client client = clientService.findByEmail(authentication.getName());
        if (account == null){
            return new ResponseEntity<>("Account not found", HttpStatus.NOT_FOUND);
        }
        if (account.getOwnerAccount().equals(client)){
            AccountDTO accountDTO = new AccountDTO(account);
            return new ResponseEntity<>(accountDTO, HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>("This account does not belong to you", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("clients/current/accounts")
    public ResponseEntity<Object> createAccount (Authentication authentication){
        Client client = clientService.findByEmail(authentication.getName());
        if (client.getAccounts().size()>=3){
            return new ResponseEntity<>("Already has the maximum number of accounts", HttpStatus.FORBIDDEN);
        }
        // Creación de numero de cuenta usando AccountUtils
        String numberAccount = AccountUtils.getNumberAccount();
        while (accountService.findByNumber(numberAccount) != null);

        Account account = new Account(numberAccount,0.0);
        client.addAccount(account);
        accountService.saveAccount(account);
        return new ResponseEntity<>("Account created succesfully", HttpStatus.CREATED);
    }

    @GetMapping ("clients/current/accounts")
    public List<AccountDTO> getAccounts(Authentication authentication){
        Client client = clientService.findByEmail(authentication.getName());
        return client.getAccounts().stream().map(account -> new AccountDTO(account)).collect(toList());
    }

}
