package com.revolut.assesment.project.controller;

import com.revolut.assesment.project.dao.AccountDao;
import com.revolut.assesment.project.dao.UserDao;
import com.revolut.assesment.project.model.Account;
import com.revolut.assesment.project.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Persistence.class, AccountDao.class, AccountController.class})
public class AccountControllerTest {

    @Before
    public void setupPersistenceMock() throws Exception {
        EntityManagerFactory mockFactory = Mockito.mock(EntityManagerFactory.class);
        PowerMockito.mockStatic(Persistence.class);
        PowerMockito.doReturn(mockFactory).when(Persistence.class, "createEntityManagerFactory" , Mockito.anyString());

    }
    @Test
    public void testGetAccounts() throws Exception {

        AccountController accountController = new AccountController();

        AccountDao mockService = Mockito.mock(AccountDao.class);

        List<Account> expected = Arrays.asList(Mockito.mock(Account.class));
        Mockito.when(mockService.getAccounts(Mockito.any(User.class))).thenReturn(expected);

        FieldSetter.setField(accountController, accountController.getClass().getDeclaredField("accountDao"), mockService);

        Response response = accountController.getAllUserAccounts(10);
        Object actual = response.getEntity();

        assertEquals(expected, actual);
        assertEquals(200, response.getStatus());
        Mockito.verify(mockService).getAccounts(Mockito.any(User.class));
    }

    @Test
    public void testGetAccount() throws Exception {


        AccountController accountController = new AccountController();

        AccountDao mockService = Mockito.mock(AccountDao.class);

        Integer accId = 123;
        Account expected = Mockito.mock(Account.class);
        Mockito.when(mockService.getAccount(Mockito.eq(accId))).thenReturn(expected);

        FieldSetter.setField(accountController, accountController.getClass().getDeclaredField("accountDao"), mockService);

        Response response = accountController.getAccount(accId);
        Object actual = response.getEntity();

        assertEquals(expected, actual);
        assertEquals(200, response.getStatus());
        Mockito.verify(mockService).getAccount(Mockito.eq(accId));
    }

    @Test
    public void testCreateAccount() throws Exception {

        AccountController accountController = new AccountController();

        AccountDao mockService = Mockito.mock(AccountDao.class);

        Integer accId = 123;
        Account expected = Mockito.mock(Account.class);
        Mockito.when(mockService.getAccount(Mockito.eq(accId))).thenReturn(expected);

        FieldSetter.setField(accountController, accountController.getClass().getDeclaredField("accountDao"), mockService);

        Response response = accountController.createAccount(accId, expected);
        Object actual = response.getEntity();

        assertEquals(expected, actual);
        assertEquals(201, response.getStatus());
        Mockito.verify(mockService).addAccount(Mockito.any(Account.class));
    }


}