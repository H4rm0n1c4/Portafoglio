package com.H4rm0n1c4.Portafoglio.account;

import com.H4rm0n1c4.Portafoglio.TestBase;
import com.H4rm0n1c4.Portafoglio.account.model.CustomerAccount;
import com.H4rm0n1c4.Portafoglio.account.model.GameEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AccountRepositoryTest extends TestBase {

    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    public void populateDatabase() {
        jdbcTemplate.execute("INSERT INTO customer_accounts (customer_id, name, balance) VALUES ('12345', 'Bob', 50.0)");
        jdbcTemplate.execute("INSERT INTO customer_accounts (customer_id, name, balance) VALUES ('67890', 'Lily', 0.0)");
        jdbcTemplate.execute("INSERT INTO customer_accounts (customer_id, name, balance) VALUES ('13579', 'Lisa', 1000.0)");
    }

    @Test
    public void CreateNewCustomerAccount_shouldCreateNewAccountIfAccountValid() {
        String customerId = "24680";
        String name = "Benjamin";
        Double balance = 100.0;
        CustomerAccount account = new CustomerAccount(customerId, name, balance);

        CustomerAccount createdAccount = accountRepository.CreateNewCustomerAccount(account);

        assertEquals("24680", createdAccount.customerId());
        assertEquals("Benjamin", createdAccount.name());
        assertEquals(100.0, createdAccount.balance());
    }

    @Test
    public void getCustomer_ShouldReturnValidCustomer() {
        Optional<CustomerAccount> customerAccount = accountRepository.getCustomer("67890");

        assertEquals("67890", customerAccount.get().customerId());
        assertEquals("Lily", customerAccount.get().name());
        assertEquals(0.0, customerAccount.get().balance());
    }

    @Test
    public void handleWin_ShouldReturnBalanceIfTransactionValid() {
        Optional<CustomerAccount> customerAccount = accountRepository.getCustomer("67890");
        int eventId = 1;
        String winCustomerId = "67890";
        Double amount = 10.0;
        GameEvent event = new GameEvent(eventId, winCustomerId, amount);

        Double winningBalance = accountRepository.handleWin(event, customerAccount.get());
        assertEquals(10.0, winningBalance);
    }

    @Test
    public void handlePurchase_ShouldReturnBalanceIfTransactionValid() {
        Optional<CustomerAccount> customerAccount = accountRepository.getCustomer("12345");
        int eventId = 1;
        String customerId = "12345";
        Double amount = 10.0;
        GameEvent event = new GameEvent(eventId, customerId, amount);

        Double winningBalance = accountRepository.handlePurchase(event, customerAccount.get());
        assertEquals(40.0, winningBalance);
    }
}