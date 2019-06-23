package com.revolut.assesment.project.models;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class TransactionTest {

    @Test
    public void testTransactionBuilder() {
        Date timeCreated = new Date();
        String currency = "INR";
        Transaction transaction = Transaction.builder()
                .fromAccount(101)
                .toAccount(202)
                .amount(23.00)
                .currency(currency)
                .timeCreated(timeCreated)
                .id(100)
                .build();
        assertNotNull(transaction);
        assertEquals(100, transaction.getId().intValue());
        assertEquals(101, transaction.getFromAccount().intValue());
        assertEquals(202, transaction.getToAccount().intValue());
        assertEquals(timeCreated, transaction.getTimeCreated());
        assertEquals(currency, transaction.getCurrency());
        assertEquals(23.00, transaction.getAmount(), 0.001);
    }

}