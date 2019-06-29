package com.revolut.assesment.project.dao;

import com.revolut.assesment.project.constants.ApplicationConstants;
import com.revolut.assesment.project.exception.CurrencyConversionNotSupportedException;
import com.revolut.assesment.project.exception.InsufficientBalanceException;
import com.revolut.assesment.project.exception.NoRecordsFoundException;
import com.revolut.assesment.project.model.Account;
import com.revolut.assesment.project.model.Transaction;
import com.revolut.assesment.project.vo.TransactionVO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Persistence.class, TransactionDao.class})
public class TransactionDaoTest {

    @Test
    public void testPerformDepositTransfer() throws Exception {
        EntityManager em = Mockito.mock(EntityManager.class);

        EntityManagerFactory mockFactory = Mockito.mock(EntityManagerFactory.class);
        Mockito.when(mockFactory.createEntityManager()).thenReturn(em);


        PowerMockito.mockStatic(Persistence.class);
        PowerMockito.doReturn(mockFactory).when(Persistence.class, "createEntityManagerFactory" , Mockito.anyString());

        TransactionDao transactionDao = new TransactionDao();
        EntityTransaction mockTxn = Mockito.mock(EntityTransaction.class);
        Mockito.when(em.getTransaction()).thenReturn(mockTxn);

        Account account = Account.builder().balance(123.00).build();

        int toAccountId = 12;
        Mockito.when(em.find(Mockito.eq(Account.class), Mockito.eq(toAccountId))).thenReturn(account);
        TransactionVO transactionVO = TransactionVO.builder()
                .transactionType(ApplicationConstants.TransactionType.CASH_DEPOSIT)
                .toAccountId(toAccountId)
                .amount(10.00)
                .build();

        Transaction transaction = transactionDao.transact(transactionVO);

        Assert.assertEquals(133.00, account.getBalance(), 0.001);
        Assert.assertEquals(account, transaction.getToAccount());

        Mockito.verify(em, Mockito.times(2)).getTransaction();
        Mockito.verify(em).persist(Mockito.any(Transaction.class));

        Mockito.verify(mockTxn).begin();
        Mockito.verify(mockTxn).commit();
    }
    @Test
    public void testPerformAccountTransfer() throws Exception{
        EntityManager em = Mockito.mock(EntityManager.class);

        EntityManagerFactory mockFactory = Mockito.mock(EntityManagerFactory.class);
        Mockito.when(mockFactory.createEntityManager()).thenReturn(em);


        PowerMockito.mockStatic(Persistence.class);
        PowerMockito.doReturn(mockFactory).when(Persistence.class, "createEntityManagerFactory" , Mockito.anyString());

        String currency = "INR";
        Account fromAccount = Account.builder().balance(123.00).currency(currency).build();
        Account toAccount = Account.builder().balance(31.00).currency(currency).build();
        TransactionDao transactionDao = new TransactionDao();
        EntityTransaction mockTxn = Mockito.mock(EntityTransaction.class);
        Mockito.when(em.getTransaction()).thenReturn(mockTxn);


        int toAccountId = 12;
        int fromAccountId = 25;
        Mockito.when(em.find(Mockito.eq(Account.class), Mockito.eq(toAccountId))).thenReturn(toAccount);
        Mockito.when(em.find(Mockito.eq(Account.class), Mockito.eq(fromAccountId))).thenReturn(fromAccount);
        TransactionVO transactionVO = TransactionVO.builder()
                .transactionType(ApplicationConstants.TransactionType.TRANSFER)
                .toAccountId(toAccountId)
                .fromAccountId(fromAccountId)
                .currency(currency)
                .amount(25.00)
                .build();

        Transaction transaction = transactionDao.transact(transactionVO);

        Assert.assertEquals(56.00, toAccount.getBalance(), 0.001);
        Assert.assertEquals(98.00, fromAccount.getBalance(), 0.001);
        Assert.assertEquals(fromAccount, transaction.getFromAccount());
        Assert.assertEquals(toAccount, transaction.getToAccount());

        Mockito.verify(em, Mockito.times(2)).getTransaction();
        Mockito.verify(em).persist(Mockito.any(Transaction.class));
        Mockito.verify(em).merge(fromAccount);
        Mockito.verify(em).merge(toAccount);
        Mockito.verify(mockTxn).begin();
        Mockito.verify(mockTxn).commit();
    }

    @Test
    public void testGetTransactions() throws Exception {
        EntityManager em = Mockito.mock(EntityManager.class);

        EntityManagerFactory mockFactory = Mockito.mock(EntityManagerFactory.class);
        Mockito.when(mockFactory.createEntityManager()).thenReturn(em);


        PowerMockito.mockStatic(Persistence.class);
        PowerMockito.doReturn(mockFactory).when(Persistence.class, "createEntityManagerFactory" , Mockito.anyString());

        Account mockAccount = Mockito.mock(Account.class);

        TransactionDao transactionDao = new TransactionDao();
        TypedQuery<Transaction> typedQuery = Mockito.mock(TypedQuery.class);
        ParameterExpression<Account> p = Mockito.mock(ParameterExpression.class);
        Root<Transaction> sm = Mockito.mock(Root.class);
        CriteriaQuery<Transaction> query = Mockito.mock(CriteriaQuery.class);
        CriteriaBuilder cb = Mockito.mock(CriteriaBuilder.class);
        Predicate pr = Mockito.mock(Predicate.class);

        Path<Object> pa = Mockito.mock(Path.class);

        List<Transaction> expected = new ArrayList<>();

        Mockito.when(typedQuery.getResultList()).thenReturn(expected);

        Mockito.when(sm.get(Mockito.anyString())).thenReturn(pa);

        Mockito.when(cb.parameter(Mockito.eq(Account.class))).thenReturn(p);
        Mockito.when(cb.equal(Mockito.any(Expression.class), Mockito.any(Expression.class))).thenReturn(pr);
        Mockito.when(cb.or(Mockito.eq(pr), Mockito.eq(pr))).thenReturn(pr);

        Mockito.when(query.select(Mockito.eq(sm))).thenReturn(query);
        Mockito.when(query.where(Mockito.any(Predicate.class))).thenReturn(query);
        Mockito.when(query.where(Mockito.any(Expression.class))).thenReturn(query);
        Mockito.when(query.from(Mockito.eq(Transaction.class))).thenReturn(sm);

        Mockito.when(cb.createQuery(Mockito.eq(Transaction.class))).thenReturn(query);
        Mockito.when(em.createQuery(Mockito.eq(query))).thenReturn(typedQuery);

        Mockito.when(em.getCriteriaBuilder()).thenReturn(cb);

        List<Transaction> transactions = transactionDao.getTransactions(mockAccount);

        assertEquals(expected, transactions);
        Mockito.verify(em).createQuery(Mockito.eq(query));
        Mockito.verify(em).getCriteriaBuilder();

        Mockito.verify(cb).createQuery(Mockito.eq(Transaction.class));

        Mockito.verify(query).select(Mockito.eq(sm));
        Mockito.verify(query).where(Mockito.any(Predicate.class));
        Mockito.verify(query).where(Mockito.any(Expression.class));
        Mockito.verify(query).from(Mockito.eq(Transaction.class));

    }

    @Test(expected = InsufficientBalanceException.class)
    public void testGetTransactionWithInsufficientBalance() throws Exception{
        EntityManager em = Mockito.mock(EntityManager.class);

        EntityManagerFactory mockFactory = Mockito.mock(EntityManagerFactory.class);
        Mockito.when(mockFactory.createEntityManager()).thenReturn(em);


        PowerMockito.mockStatic(Persistence.class);
        PowerMockito.doReturn(mockFactory).when(Persistence.class, "createEntityManagerFactory" , Mockito.anyString());

        String currency = "INR";
        Account fromAccount = Account.builder().balance(110.00).currency(currency).build();
        Account toAccount = Account.builder().balance(45.00).currency(currency).build();
        TransactionDao transactionDao = new TransactionDao();
        EntityTransaction mockTxn = Mockito.mock(EntityTransaction.class);
        Mockito.when(em.getTransaction()).thenReturn(mockTxn);


        int toAccountId = 12;
        int fromAccountId = 25;
        Mockito.when(em.find(Mockito.eq(Account.class), Mockito.eq(toAccountId))).thenReturn(toAccount);
        Mockito.when(em.find(Mockito.eq(Account.class), Mockito.eq(fromAccountId))).thenReturn(fromAccount);
        TransactionVO transactionVO = TransactionVO.builder()
                .transactionType(ApplicationConstants.TransactionType.TRANSFER)
                .toAccountId(toAccountId)
                .fromAccountId(fromAccountId)
                .currency(currency)
                .amount(125.00)
                .build();

        transactionDao.transact(transactionVO);

        Mockito.verify(em, Mockito.times(2)).getTransaction();
        Mockito.verify(mockTxn).begin();
        Mockito.verify(mockTxn).commit();
    }

    @Test(expected = CurrencyConversionNotSupportedException.class)
    public void testGetTransactionWithCurrencyMisMatchException() throws Exception{
        EntityManager em = Mockito.mock(EntityManager.class);

        EntityManagerFactory mockFactory = Mockito.mock(EntityManagerFactory.class);
        Mockito.when(mockFactory.createEntityManager()).thenReturn(em);


        PowerMockito.mockStatic(Persistence.class);
        PowerMockito.doReturn(mockFactory).when(Persistence.class, "createEntityManagerFactory" , Mockito.anyString());


        Account fromAccount = Account.builder().balance(11.00).currency("USD").build();
        Account toAccount = Account.builder().balance(4.00).currency("INR").build();
        TransactionDao transactionDao = new TransactionDao();
        EntityTransaction mockTxn = Mockito.mock(EntityTransaction.class);
        Mockito.when(em.getTransaction()).thenReturn(mockTxn);


        int toAccountId = 12;
        int fromAccountId = 25;
        Mockito.when(em.find(Mockito.eq(Account.class), Mockito.eq(toAccountId))).thenReturn(toAccount);
        Mockito.when(em.find(Mockito.eq(Account.class), Mockito.eq(fromAccountId))).thenReturn(fromAccount);
        TransactionVO transactionVO = TransactionVO.builder()
                .transactionType(ApplicationConstants.TransactionType.TRANSFER)
                .toAccountId(toAccountId)
                .fromAccountId(fromAccountId)
                .currency("USD")
                .amount(12.00)
                .build();

        transactionDao.transact(transactionVO);

        Mockito.verify(em, Mockito.times(2)).getTransaction();
        Mockito.verify(mockTxn).begin();
        Mockito.verify(mockTxn).commit();
    }

    @Test(expected = NoRecordsFoundException.class)
    public void testGetTransactionWithAccountNotFound() throws Exception{
        EntityManager em = Mockito.mock(EntityManager.class);

        EntityManagerFactory mockFactory = Mockito.mock(EntityManagerFactory.class);
        Mockito.when(mockFactory.createEntityManager()).thenReturn(em);


        PowerMockito.mockStatic(Persistence.class);
        PowerMockito.doReturn(mockFactory).when(Persistence.class, "createEntityManagerFactory" , Mockito.anyString());


        Account fromAccount = Account.builder().balance(11.00).currency("USD").build();
        Account toAccount = null;
        TransactionDao transactionDao = new TransactionDao();
        EntityTransaction mockTxn = Mockito.mock(EntityTransaction.class);
        Mockito.when(em.getTransaction()).thenReturn(mockTxn);


        int toAccountId = 12;
        int fromAccountId = 25;
        Mockito.when(em.find(Mockito.eq(Account.class), Mockito.eq(toAccountId))).thenReturn(toAccount);
        Mockito.when(em.find(Mockito.eq(Account.class), Mockito.eq(fromAccountId))).thenReturn(fromAccount);
        TransactionVO transactionVO = TransactionVO.builder()
                .transactionType(ApplicationConstants.TransactionType.TRANSFER)
                .toAccountId(toAccountId)
                .fromAccountId(fromAccountId)
                .currency("USD")
                .amount(12.00)
                .build();

        transactionDao.transact(transactionVO);

        Mockito.verify(em, Mockito.times(2)).getTransaction();
        Mockito.verify(mockTxn).begin();
        Mockito.verify(mockTxn).commit();
    }

    @Test
    public void testGetTransaction() throws Exception {
        EntityManager em = Mockito.mock(EntityManager.class);

        EntityManagerFactory mockFactory = Mockito.mock(EntityManagerFactory.class);
        Mockito.when(mockFactory.createEntityManager()).thenReturn(em);


        PowerMockito.mockStatic(Persistence.class);
        PowerMockito.doReturn(mockFactory).when(Persistence.class, "createEntityManagerFactory" , Mockito.anyString());

        TransactionDao transactionDao = new TransactionDao();

        Integer txnId = 10;
        Transaction expected = Mockito.mock(Transaction.class);
        Mockito.when(em.find(Mockito.eq(Transaction.class),Mockito.eq(txnId))).thenReturn(expected);

        Transaction actual = transactionDao.getTransaction(txnId);
        assertEquals(expected, actual);
        Mockito.verify(em).find(Mockito.eq(Transaction.class),Mockito.eq(txnId));
        Mockito.verify(mockFactory).createEntityManager();
    }

    @Test(expected = NoRecordsFoundException.class)
    public void testGetTransactionNotFound() throws Exception {
        EntityManager em = Mockito.mock(EntityManager.class);

        EntityManagerFactory mockFactory = Mockito.mock(EntityManagerFactory.class);
        Mockito.when(mockFactory.createEntityManager()).thenReturn(em);


        PowerMockito.mockStatic(Persistence.class);
        PowerMockito.doReturn(mockFactory).when(Persistence.class, "createEntityManagerFactory" , Mockito.anyString());

        TransactionDao transactionDao = new TransactionDao();

        Integer txnId = 10;
        Transaction expected = null;
        Mockito.when(em.find(Mockito.eq(Transaction.class),Mockito.eq(txnId))).thenReturn(expected);

        transactionDao.getTransaction(txnId);
    }
}