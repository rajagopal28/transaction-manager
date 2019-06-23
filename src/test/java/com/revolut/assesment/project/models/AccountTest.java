package com.revolut.assesment.project.models;

import com.revolut.assesment.project.constants.ApplicationConstants;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class AccountTest {
    @Test
    public void testAccountBuilder() {
        double balance = 100.00;
        String curr = "USD";
        Date timeCreated = new Date();
        Account account = Account.builder()
                .accountType(ApplicationConstants.AccountType.CREDIT)
                .balance(balance).currency(curr)
                .timeCreated(timeCreated)
                .id(100)
                .userId(121)
                .build();
        assertNotNull(account);
        assertEquals(ApplicationConstants.AccountType.CREDIT, account.getAccountType());
        assertEquals(curr, account.getCurrency());
        assertEquals(100, account.getId().intValue());
        assertEquals(121, account.getUserId().intValue());
        assertEquals(timeCreated, account.getTimeCreated());
        assertEquals(balance, account.getBalance(), 0.001);
    }
}