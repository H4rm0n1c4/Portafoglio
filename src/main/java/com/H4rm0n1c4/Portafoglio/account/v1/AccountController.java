package com.H4rm0n1c4.Portafoglio.account.v1;

import com.H4rm0n1c4.Portafoglio.ErrorCode;
import com.H4rm0n1c4.Portafoglio.account.AccountRepository;
import com.H4rm0n1c4.Portafoglio.account.AccountService;
import com.H4rm0n1c4.Portafoglio.account.model.CustomerAccount;
import com.H4rm0n1c4.Portafoglio.account.model.GameEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class AccountController {

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    private final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @PostMapping(value = "/api/v1/transaction/{eventType}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> newGameEvent(@PathVariable("eventType") GameEvent.EventType type,
                                               @RequestBody GameEvent request) {
        Optional<CustomerAccount> customer = accountRepository.getCustomer(request.customerId());
        if (!customer.isPresent()) {
            logger.error("Customer: {} doesnt exist", request.customerId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorCode.CUSTOMER_NOT_FOUND.name());
        }
        if (customer.get().balance() < request.amount() && type.equals(GameEvent.EventType.PURCHASE)) {
            logger.error("Not enough balance for the purchase");
            return ResponseEntity.badRequest().body(ErrorCode.NOT_ENOUGH_BALANCE.name());
        }
        Double balance = accountService.processGameTransaction(request, type, customer.get());
        return ResponseEntity.status(HttpStatus.OK).body(balance.toString());
    }

    @PostMapping(value = "/api/v1/customer/{customerId}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerAccount> newCustomerAccount(@PathVariable("customerId") int customerId, @RequestBody CustomerAccount request) {
        if (request.name() == null || request.name().isEmpty() || request.customerId().isEmpty()) {
            logger.error("Customer name or customer id cannot be null or empty");
            return ResponseEntity.badRequest().build();
        }
        Optional<CustomerAccount> customer = accountRepository.getCustomer(request.customerId());
        if(customer.isPresent()){
            logger.error("Customer: " + customer.get().customerId() + " already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        CustomerAccount account = accountService.createCustomerAccount(request);
        return ResponseEntity.status(HttpStatus.OK).body(account);
    }
}
