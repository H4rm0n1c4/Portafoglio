package com.H4rm0n1c4.Portafoglio.account;

import com.H4rm0n1c4.Portafoglio.account.model.CustomerAccount;
import com.H4rm0n1c4.Portafoglio.account.model.GameEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class AccountRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    @Transactional
    public CustomerAccount CreateNewCustomerAccount(CustomerAccount account) {
        jdbc.update("INSERT INTO customer_accounts (customer_id, name, balance)" + "VALUES (:customerId, :name, :balance)", Map.of(
                "customerId", account.customerId(),
                "name", account.name(),
                "balance", account.balance()
        ));

        return jdbc.queryForObject("SELECT customer_id, name, balance from customer_accounts WHERE customer_id = :customerId",
                Map.of("customerId", account.customerId()), (rs, rowNum) -> new CustomerAccount(
                        rs.getString("customer_id"),
                        rs.getString("name"),
                        rs.getDouble("balance")
                ));
    }

    public Optional<CustomerAccount> getCustomer(String customerId) {
        List<CustomerAccount> customerAccounts = jdbc.query("SELECT customer_id, name, balance from customer_accounts WHERE customer_id = :customerId",
                Map.of("customerId", customerId), (rs, rowNum) -> new CustomerAccount(
                        rs.getString("customer_id"),
                        rs.getString("name"),
                        rs.getDouble("balance")
                ));
        return customerAccounts.stream().findFirst();
    }

    @Transactional
    public Double handleWin(GameEvent event, CustomerAccount customer) {
        jdbc.update("INSERT INTO game_events (event_id, customer_id, type, amount)" +
                "VALUES (:eventId, :customerId, :type, :amount)", Map.of(
                "eventId", event.eventId(),
                "customerId", customer.customerId(),
                "type", GameEvent.EventType.WIN.name(),
                "amount", event.amount()
        ));

        Double newBalance = customer.balance() + event.amount();
        jdbc.update("UPDATE customer_accounts SET balance = :newBalance WHERE customer_id = :customerId",
                Map.of("newBalance", newBalance,
                        "customerId", customer.customerId()));
        return newBalance;
    }

    @Transactional
    public Double handlePurchase(GameEvent event, CustomerAccount customer) {
        jdbc.update("INSERT INTO game_events (event_id, customer_id, type, amount)" +
                "VALUES (:eventId, :customerId, :type, :amount)", Map.of(
                "eventId", event.eventId(),
                "customerId", customer.customerId(),
                "type", GameEvent.EventType.PURCHASE.name(),
                "amount", event.amount()
        ));

        Double newBalance = customer.balance() - event.amount();
        jdbc.update("UPDATE customer_accounts SET balance = :newBalance WHERE customer_id = :customerId",
                Map.of("newBalance", newBalance,
                        "customerId", customer.customerId()));
        return newBalance;
    }
}
