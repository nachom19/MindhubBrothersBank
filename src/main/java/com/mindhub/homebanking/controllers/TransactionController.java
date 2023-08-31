package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping ("/api")
public class TransactionController {
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    TransactionRepository transactionRepository;

    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object>createTransaction (@RequestParam Double amount,
                                                    @RequestParam String fromAccountNumber,
                                                    @RequestParam String toAccountNumber,
                                                    @RequestParam String description,
                                                    Authentication authentication){
        Client client = clientRepository.findByEmail(authentication.getName());

        Account accountOrigin = accountRepository.findByNumber(fromAccountNumber);
        Account accountDestination = accountRepository.findByNumber(toAccountNumber);

        //Verificar que los parametros no esten vacios
        if (fromAccountNumber.isBlank()) {
            return new ResponseEntity<>("Missing to complete the Origin Account Number ", HttpStatus.FORBIDDEN);
        }
        if (toAccountNumber.isBlank()) {
            return new ResponseEntity<>("Missing to complete the Destination Account Number", HttpStatus.FORBIDDEN);
        }
        if (amount.toString().isBlank()) {
            return new ResponseEntity<>("Missing to complete the Amount", HttpStatus.FORBIDDEN);
        }
        if (description.isBlank()) {
            return new ResponseEntity<>("Missing to complete the Description", HttpStatus.FORBIDDEN);
        }
        // Verificar que los numeros de cuenta no sean iguales
        //accountOrigin == null
        if (fromAccountNumber.equals(toAccountNumber)) {
            return new ResponseEntity<>("The destination account cannot be the same as the source account", HttpStatus.FORBIDDEN);
        }
        //Verificar que la cuenta de origen exista
        if (accountOrigin.getNumber() == null) {
            return new ResponseEntity<>("The selected account does not exist", HttpStatus.FORBIDDEN);
        }
        //Verificar que la cuenta de origen  sea del cliente autenticado
        if (!client.getAccounts().stream().anyMatch(account -> account.getNumber().equals(fromAccountNumber))) {
            return new ResponseEntity<>("The selected account does not belong to you", HttpStatus.FORBIDDEN);
        }
        //Verificar que la cuenta de destino exista
        if (accountDestination.getNumber() == null) {
            return new ResponseEntity<>("The selected account does not exist", HttpStatus.FORBIDDEN);
        }
        //Verificar que la cuenta origen tega saldo
        if (accountOrigin.getBalance()< amount) {
            return new ResponseEntity<>("The selected account does not exist", HttpStatus.FORBIDDEN);
        }
        //transaccion de DEBITO
        Transaction transactionDebit = new Transaction(TransactionType.DEBIT, -amount, description +" "+accountDestination.getNumber(), LocalDate.now());
        accountOrigin.addTransaction(transactionDebit);
        accountOrigin.setBalance(accountOrigin.getBalance() - amount);
        transactionRepository.save(transactionDebit);
        accountRepository.save(accountOrigin);
        //transaccion de CREDITO
        Transaction transactionCredit = new Transaction(TransactionType.CREDIT, amount, description +" "+accountOrigin.getNumber(), LocalDate.now());
        accountDestination.addTransaction(transactionCredit);
        accountDestination.setBalance(accountDestination.getBalance() + amount);
        transactionRepository.save(transactionCredit);
        accountRepository.save(accountDestination);

        return new ResponseEntity<>("The transfer has been completed successfully", HttpStatus.ACCEPTED);
    }
}
