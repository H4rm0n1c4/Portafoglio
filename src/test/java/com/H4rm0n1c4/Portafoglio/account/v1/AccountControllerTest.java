package com.H4rm0n1c4.Portafoglio.account.v1;

import com.H4rm0n1c4.Portafoglio.TestBase;
import com.H4rm0n1c4.Portafoglio.account.model.CustomerAccount;
import com.H4rm0n1c4.Portafoglio.account.model.GameEvent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class AccountControllerTest extends TestBase {

    @Autowired
    protected MockMvc mockMvc;

    @BeforeEach
    public void populateDatabase() {
        jdbcTemplate.execute("INSERT INTO customer_accounts (customer_id, name, balance) VALUES ('12345', 'Bob', 50.0)");
        jdbcTemplate.execute("INSERT INTO customer_accounts (customer_id, name, balance) VALUES ('67890', 'Lily', 0.0)");
        jdbcTemplate.execute("INSERT INTO customer_accounts (customer_id, name, balance) VALUES ('13579', 'Lisa', 1000.0)");
    }

    @Test
    public void newCustomerAccount_SuccessWithValidRequest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String customerId = "24680";
        String name = "Benjamin";
        Double balance = 100.0;
        CustomerAccount account = new CustomerAccount(customerId, name, balance);
        String requestJson = objectMapper.writeValueAsString(account);
        MvcResult result = mockMvc.perform(post("/api/v1/customer", "24680")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(requestJson)).andExpect(status().isOk()).andReturn();
        String responseBody = result.getResponse().getContentAsString();
        CustomerAccount resultAccount = objectMapper.readValue(responseBody, new TypeReference<>() {
        });

        assertEquals("24680", resultAccount.customerId());
        assertEquals("Benjamin", resultAccount.name());
        assertEquals(100.0, resultAccount.balance());
    }

    @Test
    public void newCustomerAccount_Returns400BadRequestIfCustomerNameOrCustomerIdIsEmpty() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String emptyCustomerId = "";
        String emptyName = "";
        String customerId = "24680";
        String name = "Benjamin";
        Double balance = 100.0;
        CustomerAccount emptycIdAccount = new CustomerAccount(emptyCustomerId, name, balance);
        String requestJson1 = objectMapper.writeValueAsString(emptycIdAccount);
        mockMvc.perform(post("/api/v1/customer", "24680")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(requestJson1)).andExpect(status().isBadRequest()).andReturn();

        CustomerAccount emptyNameAccount = new CustomerAccount(customerId, emptyName, balance);
        String requestJson2 = objectMapper.writeValueAsString(emptyNameAccount);
        mockMvc.perform(post("/api/v1/customer", "24680")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(requestJson2)).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void newCustomerAccount_Returns409ConflictIfCustomerIdAlreadyHasAnAccount() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String customerId = "12345";
        String name = "Bob";
        Double balance = 100.0;
        CustomerAccount account = new CustomerAccount(customerId, name, balance);
        String requestJson = objectMapper.writeValueAsString(account);
        mockMvc.perform(post("/api/v1/customer", "24680")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(requestJson)).andExpect(status().isConflict()).andReturn();
    }

    @Test
    public void newGameEvent_SuccessWithValidRequest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        int winEventId = 1;
        int purchaseEventId = 2;
        String winCustomerId = "12345";
        String purchaseCustomerId = "13579";
        Double amount = 10.0;
        GameEvent winEvent = new GameEvent(winEventId, winCustomerId, amount);
        GameEvent purchaseEvent = new GameEvent(purchaseEventId, purchaseCustomerId, amount);
        String winRequestJson = objectMapper.writeValueAsString(winEvent);
        String purchaseRequestJson = objectMapper.writeValueAsString(purchaseEvent);

        MvcResult winResult = mockMvc.perform(post("/api/v1/transaction/{eventType}", "WIN")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(winRequestJson)).andExpect(status().isOk()).andReturn();

        MvcResult purchaseResult = mockMvc.perform(post("/api/v1/transaction/{eventType}", "PURCHASE")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(purchaseRequestJson)).andExpect(status().isOk()).andReturn();

        String winResponseBody = winResult.getResponse().getContentAsString();
        String purchaseResponseBody = purchaseResult.getResponse().getContentAsString();
        assertEquals("60.0", winResponseBody);
        assertEquals("990.0", purchaseResponseBody);
    }

    @Test
    public void newGameEvent_404NotFoundIfCustomerDoesntExist() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        int eventId = 1;
        String customerId = "99999";
        Double amount = 10.0;
        GameEvent event = new GameEvent(eventId, customerId, amount);
        String requestJson = objectMapper.writeValueAsString(event);

        mockMvc.perform(post("/api/v1/transaction/{eventType}", "WIN")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(requestJson)).andExpect(status().isNotFound()).andReturn();

    }

    @Test
    public void newGameEvent_400BadRequestIfBalanceIsTooLowForPurchase() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        int eventId = 1;
        String customerId = "67890";
        Double amount = 10.0;
        GameEvent event = new GameEvent(eventId, customerId, amount);
        String requestJson = objectMapper.writeValueAsString(event);

        mockMvc.perform(post("/api/v1/transaction/{eventType}", "PURCHASE")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(requestJson)).andExpect(status().isBadRequest()).andReturn();

    }
}