package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}


	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository,
									  AccountRepository accountRepository,
									  TransactionRepository transactionRepository,
									  LoanRepository loanRepository,
									  ClientLoanRepository clientLoanRepository){
		return (args) -> {
			// Creación de clientes
			Client client1 = new Client("Melba","Morel","melba@mindhub.com");
			Client client2 = new Client("Aarón","Beltrán","abeltran@mindhub.com");

			clientRepository.save(client1);
			clientRepository.save(client2);

			//Creacion de cuentas

			Account account1 = new Account("VIN001",5000.00);
			client1.addAccount(account1);
			accountRepository.save(account1);

			Account account2 = new Account("VIN002", 7500.0);
			account2.setCreationDate(LocalDate.now().plusDays(1));
			client1.addAccount(account2);
			accountRepository.save(account2);

			Account account3 = new Account("VIN003",9000.00);
			client2.addAccount(account3);
			accountRepository.save(account3);

			Account account4 = new Account("VIN004", 3500.0);
			account4.setCreationDate(LocalDate.now().plusDays(2));
			client2.addAccount(account4);
			accountRepository.save(account4);

			//Creacion de transacciones
			Transaction transaction1 = new Transaction(TransactionType.CREDIT, 3000.0, "Salary",LocalDate.now());
			account1.addTransaction(transaction1);
			transactionRepository.save(transaction1);

			Transaction transaction2 = new Transaction(TransactionType.DEBIT, -1000.0, "Rent",LocalDate.now());
			account1.addTransaction(transaction2);
			transactionRepository.save(transaction2);

			Transaction transaction3 = new Transaction(TransactionType.CREDIT, 5500.0, "Salary",LocalDate.now());
			account2.addTransaction(transaction3);
			transactionRepository.save(transaction3);

			//creacion de tipos de prestamos
			Loan loan1 = new Loan("Mortagge",500000.0, List.of(12,24,36,48,60));
			loanRepository.save(loan1);

			Loan loan2 = new Loan("Personal",100000.0, List.of(6,12,24));
			loanRepository.save(loan2);

			Loan loan3 = new Loan("Car",300000.0, List.of(6,12,24,36));
			loanRepository.save(loan3);

			// creacion y asignacion de prestamos
			ClientLoan clientLoan1 = new ClientLoan(400000.0, 60);
			client1.addClient(clientLoan1);
			loan1.addLoan(clientLoan1);
			clientLoanRepository.save(clientLoan1);

			ClientLoan clientLoan2 = new ClientLoan(50000.0, 12);
			client1.addClient(clientLoan2);
			loan2.addLoan(clientLoan2);
			clientLoanRepository.save(clientLoan2);

			ClientLoan clientLoan3 = new ClientLoan(100000.0, 24);
			client2.addClient(clientLoan3);
			loan2.addLoan(clientLoan3);
			clientLoanRepository.save(clientLoan3);

			ClientLoan clientLoan4 = new ClientLoan(200000.0, 36);
			client2.addClient(clientLoan4);
			loan3.addLoan(clientLoan4);
			clientLoanRepository.save(clientLoan4);

		};
	}

}
