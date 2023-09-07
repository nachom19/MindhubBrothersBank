package com.mindhub.homebanking.services;


import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;

import java.util.List;

public interface AccountService {

    Account findByNumber(String number);

    void saveAccount(Account account);

    List<AccountDTO> getAccountsDTO();

    Account findById(long id);

}
