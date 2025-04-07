package com.H4rm0n1c4.Portafoglio.account.model;

public record GameEvent(int eventId,
                        String customerId,
                        double amount) {
    public enum EventType {
        PURCHASE,
        WIN,
    }
}
