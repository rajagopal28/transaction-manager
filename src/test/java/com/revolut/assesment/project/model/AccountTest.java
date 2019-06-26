package com.revolut.assesment.project.model;

import com.revolut.assesment.project.constants.ApplicationConstants;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Date;

import static org.junit.Assert.*;

public class AccountTest {
    @Test
    public void testAccountBuilder() {
        double balance = 100.00;
        String curr = "USD";
        Date timeCreated = new Date();
        String accountNumber = "341232BC3243";
        User mockUser = Mockito.mock(User.class);
        Account account = Account.builder()
                .accountType(ApplicationConstants.AccountType.CREDIT)
                .balance(balance).currency(curr)
                .timeCreated(timeCreated)
                .id(100)
                .accountNumber(accountNumber)
                .user(mockUser)
                .build();
        assertNotNull(account);
        assertEquals(ApplicationConstants.AccountType.CREDIT, account.getAccountType());
        assertEquals(curr, account.getCurrency());
        assertEquals(100, account.getId().intValue());
        assertEquals(mockUser, account.getUser());
        assertEquals(accountNumber, account.getAccountNumber());
        assertEquals(timeCreated, account.getTimeCreated());
        assertEquals(balance, account.getBalance(), 0.001);
    }
}