package com.mindhub.homebanking.controllers;


import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import com.mindhub.homebanking.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LoanController {

    @Autowired
    private LoanService loanService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ClientLoanService clientLoanService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/loans")
    public List<LoanDTO> getLoans(){
        return loanService.getLoansDTO();
    }

    @Transactional
    @PostMapping("/loans")
    public ResponseEntity<Object>loanApplication(@RequestBody LoanApplicationDTO loanApplicationDTO, Authentication authentication){
        Client client = clientService.findByEmail(authentication.getName());
        Loan loan = loanService.findById(loanApplicationDTO.getLoanId());
        Account account = accountService.findByNumber(loanApplicationDTO.getToAccountNumber());

        //Verificar que los parametros no esten vacios
        if (loanApplicationDTO.getLoanId() == 0) {
            return new ResponseEntity<>("Missing to complete the data", HttpStatus.FORBIDDEN);
        }
        if (loanApplicationDTO.getAmount() == null) {
            return new ResponseEntity<>("Missing to complete the data", HttpStatus.FORBIDDEN);
        }
        if (loanApplicationDTO.getPayments() == 0) {
            return new ResponseEntity<>("Missing to complete the data", HttpStatus.FORBIDDEN);
        }
        if (loanApplicationDTO.getToAccountNumber().isBlank()) {
            return new ResponseEntity<>("Missing to complete the data", HttpStatus.FORBIDDEN);
        }
        //verificar que el monto sea mayor que 0
        if (loanApplicationDTO.getAmount()<=0){
            return new ResponseEntity<>("The amount must be greater than 0", HttpStatus.FORBIDDEN);
        }
        //Verificar que las coutas seran mayores que 0
        if (loanApplicationDTO.getPayments()<=0){
            return new ResponseEntity<>("The payments must be greater than 0", HttpStatus.FORBIDDEN);
        }
        //Verificar que el préstamo exista
        if (!loanService.existsById(loanApplicationDTO.getLoanId())){
            return new ResponseEntity<>("The loan you want to access does not exist", HttpStatus.FORBIDDEN);
        }
        //Verificar que el monto solicitado no exceda el monto máximo del préstamo
        if (loanApplicationDTO.getAmount()> loan.getMaxAmount()){
            return new ResponseEntity<>("The loan cannot exceed the maximum of "+loan.getMaxAmount(), HttpStatus.FORBIDDEN);
        }
        //Verificar que la cantidad de cuotas se encuentre entre las disponibles del préstamo
        if (!loan.getPayments().contains(loanApplicationDTO.getPayments())){
            return new ResponseEntity<>("Requested payments are not available", HttpStatus.FORBIDDEN);
        }
        //Verificar que la cuenta de destino exista
        if (loanApplicationDTO.getToAccountNumber() == null) {
            return new ResponseEntity<>("The selected account does not exist", HttpStatus.FORBIDDEN);
        }
        //Verificar que la cuenta de destino  sea del cliente autenticado
        if (!client.getAccounts().stream().anyMatch(account1 -> account1.getNumber().equals(loanApplicationDTO.getToAccountNumber()))) {
            return new ResponseEntity<>("The selected account does not belong to you", HttpStatus.FORBIDDEN);
        }
        // Creaccion de prestamo
        ClientLoan clientLoan = new ClientLoan(loanApplicationDTO.getAmount()*1.20, loanApplicationDTO.getPayments());
        loan.addLoan(clientLoan);
        client.addClient(clientLoan);
        clientLoanService.saveClientLoan(clientLoan);

        //creaccion de transaccion
        Transaction transaction =new Transaction(TransactionType.CREDIT, loanApplicationDTO.getAmount(), loan.getName()+" loan approved", LocalDate.now());
        account.addTransaction(transaction);
        transactionService.saveTransaction(transaction);

        //modificacion del saldo de la cuenta
        account.setBalance(account.getBalance()+loanApplicationDTO.getAmount());
        accountService.saveAccount(account);

        return new ResponseEntity<>("The "+ loan.getName() +" Loan has been completed successfully", HttpStatus.CREATED);

    }
}
