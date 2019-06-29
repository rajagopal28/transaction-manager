package com.revolut.assesment.project.controller;

import com.revolut.assesment.project.constants.ApplicationConstants;
import com.revolut.assesment.project.dao.TransactionDao;
import com.revolut.assesment.project.exception.CurrencyConversionNotSupportedException;
import com.revolut.assesment.project.exception.DataValidationException;
import com.revolut.assesment.project.exception.InsufficientBalanceException;
import com.revolut.assesment.project.exception.NoRecordsFoundException;
import com.revolut.assesment.project.model.Account;
import com.revolut.assesment.project.model.Transaction;
import com.revolut.assesment.project.vo.MessageVO;
import com.revolut.assesment.project.vo.TransactionVO;
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
@PrepareForTest({Persistence.class, TransactionDao.class, TransactionController.class})
public class TransactionControllerTest {

    @Before
    public void setupPersistenceMock() throws Exception {
        EntityManagerFactory mockFactory = Mockito.mock(EntityManagerFactory.class);
        PowerMockito.mockStatic(Persistence.class);
        PowerMockito.doReturn(mockFactory).when(Persistence.class, "createEntityManagerFactory" , Mockito.anyString());

    }
    @Test
    public void testGetTransactions() throws Exception {

        TransactionController transactionController = new TransactionController();

        TransactionDao mockService = Mockito.mock(TransactionDao.class);

        List<Transaction> expected = Arrays.asList(Mockito.mock(Transaction.class));
        Mockito.when(mockService.getTransactions(Mockito.any(Account.class))).thenReturn(expected);

        FieldSetter.setField(transactionController, transactionController.getClass().getDeclaredField("transactionDao"), mockService);

        Response response = transactionController.getAllAccountTransactions(10, 23);
        Object actual = response.getEntity();

        assertEquals(expected, actual);
        assertEquals(200, response.getStatus());
        Mockito.verify(mockService).getTransactions(Mockito.any(Account.class));
    }

    @Test
    public void testGetTransaction() throws Exception {


        TransactionController transactionController = new TransactionController();

        TransactionDao mockService = Mockito.mock(TransactionDao.class);

        Integer txnId = 123;
        Transaction expected = Mockito.mock(Transaction.class);
        Mockito.when(mockService.getTransaction(Mockito.eq(txnId))).thenReturn(expected);

        FieldSetter.setField(transactionController, transactionController.getClass().getDeclaredField("transactionDao"), mockService);

        Response response = transactionController.getTransaction(12, 13, txnId);
        Object actual = response.getEntity();

        assertEquals(expected, actual);
        assertEquals(200, response.getStatus());
        Mockito.verify(mockService).getTransaction(Mockito.eq(txnId));
    }

    @Test
    public void testCreateTransaction() throws Exception {

        TransactionController transactionController = new TransactionController();

        TransactionDao mockService = Mockito.mock(TransactionDao.class);

        Transaction expected = Mockito.mock(Transaction.class);
        TransactionVO mockVO = Mockito.mock(TransactionVO.class);
        Mockito.when(mockService.transact(mockVO)).thenReturn(expected);

        FieldSetter.setField(transactionController, transactionController.getClass().getDeclaredField("transactionDao"), mockService);

        Response response = transactionController.createTransaction(12, 13, mockVO);
        Object actual = response.getEntity();

        assertEquals(expected, actual);
        assertEquals(201, response.getStatus());
        Mockito.verify(mockService).transact(mockVO);
    }

    @Test
    public void testCreateTransactionWithInsufficientBalance() throws Exception {

        TransactionController transactionController = new TransactionController();

        TransactionDao mockService = Mockito.mock(TransactionDao.class);

        Transaction expected = Mockito.mock(Transaction.class);
        TransactionVO mockVO = Mockito.mock(TransactionVO.class);
        Mockito.when(mockService.transact(mockVO)).thenThrow(new InsufficientBalanceException());

        FieldSetter.setField(transactionController, transactionController.getClass().getDeclaredField("transactionDao"), mockService);

        Response response = transactionController.createTransaction(12, 13, mockVO);
        MessageVO actual = (MessageVO)response.getEntity();

        assertEquals(ApplicationConstants.RESPONSE_ERROR_INSUFFICIENT_BALANCE, actual.getMessage());
        assertEquals(304, response.getStatus());
        Mockito.verify(mockService).transact(mockVO);
    }

    @Test
    public void testCreateTransactionWithCurrencyMismatch() throws Exception {

        TransactionController transactionController = new TransactionController();

        TransactionDao mockService = Mockito.mock(TransactionDao.class);

        TransactionVO mockVO = Mockito.mock(TransactionVO.class);
        Mockito.when(mockService.transact(mockVO)).thenThrow(new CurrencyConversionNotSupportedException());

        FieldSetter.setField(transactionController, transactionController.getClass().getDeclaredField("transactionDao"), mockService);

        Response response = transactionController.createTransaction(12, 13, mockVO);
        MessageVO actual = (MessageVO)response.getEntity();

        assertEquals(ApplicationConstants.RESPONSE_ERROR_CURRENCY_CONVERSION_NOT_DONE, actual.getMessage());
        assertEquals(304, response.getStatus());
        Mockito.verify(mockService).transact(mockVO);
    }

    @Test
    public void testCreateTransactionWithInvalidData() throws Exception {

        TransactionController transactionController = new TransactionController();

        TransactionDao mockService = Mockito.mock(TransactionDao.class);

        TransactionVO mockVO = Mockito.mock(TransactionVO.class);
        final String someTxnField = "someTxnField";
        Mockito.when(mockService.transact(mockVO)).thenThrow(new DataValidationException(someTxnField));

        FieldSetter.setField(transactionController, transactionController.getClass().getDeclaredField("transactionDao"), mockService);

        Response response = transactionController.createTransaction(12, 13, mockVO);
        MessageVO actual = (MessageVO)response.getEntity();

        assertEquals(ApplicationConstants.RESPONSE_ERROR_DATA_VALIDATION_FAILED_WITH+someTxnField, actual.getMessage());
        assertEquals(304, response.getStatus());
        Mockito.verify(mockService).transact(mockVO);
    }

    @Test
    public void testCreateTransactionWithNoDataFound() throws Exception {

        TransactionController transactionController = new TransactionController();

        TransactionDao mockService = Mockito.mock(TransactionDao.class);

        TransactionVO mockVO = Mockito.mock(TransactionVO.class);
        Mockito.when(mockService.transact(mockVO)).thenThrow(new NoRecordsFoundException());

        FieldSetter.setField(transactionController, transactionController.getClass().getDeclaredField("transactionDao"), mockService);

        Response response = transactionController.createTransaction(12, 13, mockVO);
        MessageVO actual = (MessageVO)response.getEntity();

        assertEquals(ApplicationConstants.RESPONSE_ERROR_UNABLE_TO_FIND_RECORD, actual.getMessage());
        assertEquals(404, response.getStatus());
        Mockito.verify(mockService).transact(mockVO);
    }
}