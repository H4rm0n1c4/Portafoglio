package com.H4rm0n1c4.Portafoglio.account;

import com.H4rm0n1c4.Portafoglio.account.model.CustomerAccount;
import com.H4rm0n1c4.Portafoglio.account.model.GameEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    private final Logger logger = LoggerFactory.getLogger(AccountService.class);

    public CustomerAccount createCustomerAccount(CustomerAccount account) {
        CustomerAccount customerAccount = accountRepository.CreateNewCustomerAccount(account);
        return customerAccount;
    }

    public Double processGameTransaction(GameEvent event, GameEvent.EventType type, CustomerAccount customer) {
        Double customerBalance = 0.0;
        switch (type) {
            case WIN -> {
                customerBalance = accountRepository.handleWin(event, customer);
            }
            case PURCHASE -> {
                customerBalance = accountRepository.handlePurchase(event, customer);
            }
            default -> {
                // If still have time figure out a smarter error handling method
                throw new RuntimeException("Wrong event type given");
            }
        }

        return customerBalance;
    }
}
