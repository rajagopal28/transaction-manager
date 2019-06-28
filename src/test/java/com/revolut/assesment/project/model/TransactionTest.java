package com.revolut.assesment.project.model;

import com.revolut.assesment.project.constants.ApplicationConstants;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Date;

import static org.junit.Assert.*;

public class TransactionTest {

    @Test
    public void testTransactionBuilder() {
        long timeCreated = System.currentTimeMillis();
        String currency = "INR";
        Account fromAccount = Mockito.mock(Account.class);
        Account toAccount = Mockito.mock(Account.class);
        Transaction transaction = Transaction.builder()
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(23.00)
                .currency(currency)
                .timeCreated(timeCreated)
                .transactionType(ApplicationConstants.TransactionType.TRANSFER)
                .id(100)
                .build();
        assertNotNull(transaction);
        assertEquals(100, transaction.getId().intValue());
        assertEquals(fromAccount, transaction.getFromAccount());
        assertEquals(toAccount, transaction.getToAccount());
        assertEquals(timeCreated, transaction.getTimeCreated());
        assertEquals(currency, transaction.getCurrency());
        assertEquals(23.00, transaction.getAmount(), 0.001);
    }

}