package com.revolut.assesment.project.dao;

import com.revolut.assesment.project.constants.ApplicationConstants;
import com.revolut.assesment.project.exception.DataValidationException;
import com.revolut.assesment.project.exception.NoRecordsFoundException;
import com.revolut.assesment.project.model.Account;
import com.revolut.assesment.project.model.User;
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
@PrepareForTest({Persistence.class, AccountDao.class})
public class AccountDaoTest {

    @Test
    public void testCreateAccount() throws Exception{
        EntityManager em = Mockito.mock(EntityManager.class);

        EntityManagerFactory mockFactory = Mockito.mock(EntityManagerFactory.class);
        Mockito.when(mockFactory.createEntityManager()).thenReturn(em);


        PowerMockito.mockStatic(Persistence.class);
        PowerMockito.doReturn(mockFactory).when(Persistence.class, "createEntityManagerFactory" , Mockito.anyString());

        AccountDao accountDao = new AccountDao();
        EntityTransaction mockTxn = Mockito.mock(EntityTransaction.class);
        Mockito.when(em.getTransaction()).thenReturn(mockTxn);

        Account account = Account.builder()
                .balance(10.0)
                .currency("")
                .accountType(ApplicationConstants.AccountType.CREDIT)
                .accountNumber("232").build();

        accountDao.addAccount(account);

        Mockito.verify(em, Mockito.times(2)).getTransaction();
        Mockito.verify(em).persist(account);
        Mockito.verify(mockTxn).begin();
        Mockito.verify(mockTxn).commit();
    }

    @Test
    public void testGetAccounts() throws Exception {
        EntityManager em = Mockito.mock(EntityManager.class);

        EntityManagerFactory mockFactory = Mockito.mock(EntityManagerFactory.class);
        Mockito.when(mockFactory.createEntityManager()).thenReturn(em);


        PowerMockito.mockStatic(Persistence.class);
        PowerMockito.doReturn(mockFactory).when(Persistence.class, "createEntityManagerFactory" , Mockito.anyString());

        User mockUser = Mockito.mock(User.class);

        AccountDao accountDao = new AccountDao();
        TypedQuery<Account> typedQuery = Mockito.mock(TypedQuery.class);
        ParameterExpression<User> p = Mockito.mock(ParameterExpression.class);
        Root<Account> sm = Mockito.mock(Root.class);
        CriteriaQuery<Account> query = Mockito.mock(CriteriaQuery.class);
        CriteriaBuilder cb = Mockito.mock(CriteriaBuilder.class);
        Predicate pr = Mockito.mock(Predicate.class);

        Path<Object> pa = Mockito.mock(Path.class);

        List<Account> expected = new ArrayList<>();

        Mockito.when(typedQuery.getResultList()).thenReturn(expected);

        Mockito.when(sm.get(Mockito.anyString())).thenReturn(pa);

        Mockito.when(cb.parameter(Mockito.eq(User.class))).thenReturn(p);
        Mockito.when(cb.equal(Mockito.eq(pa), Mockito.eq(p))).thenReturn(pr);

        Mockito.when(query.select(Mockito.eq(sm))).thenReturn(query);
        Mockito.when(query.where(Mockito.any(Predicate.class))).thenReturn(query);
        Mockito.when(query.where(Mockito.any(Expression.class))).thenReturn(query);
        Mockito.when(query.from(Mockito.eq(Account.class))).thenReturn(sm);

        Mockito.when(cb.createQuery(Mockito.eq(Account.class))).thenReturn(query);
        Mockito.when(em.createQuery(Mockito.eq(query))).thenReturn(typedQuery);

        Mockito.when(em.getCriteriaBuilder()).thenReturn(cb);

        List<Account> accounts = accountDao.getAccounts(mockUser);

        assertEquals(expected, accounts);
        Mockito.verify(em).createQuery(Mockito.eq(query));
        Mockito.verify(em).getCriteriaBuilder();

        Mockito.verify(cb).createQuery(Mockito.eq(Account.class));

        Mockito.verify(query).select(Mockito.eq(sm));
        Mockito.verify(query).where(Mockito.any(Predicate.class));
        Mockito.verify(query).where(Mockito.any(Expression.class));
        Mockito.verify(query).from(Mockito.eq(Account.class));

    }


    @Test
    public void testGetAccount() throws Exception {
        EntityManager em = Mockito.mock(EntityManager.class);

        EntityManagerFactory mockFactory = Mockito.mock(EntityManagerFactory.class);
        Mockito.when(mockFactory.createEntityManager()).thenReturn(em);


        PowerMockito.mockStatic(Persistence.class);
        PowerMockito.doReturn(mockFactory).when(Persistence.class, "createEntityManagerFactory" , Mockito.anyString());

        AccountDao accountDao = new AccountDao();

        Integer accountId = 10;
        Account expected = Mockito.mock(Account.class);
        Mockito.when(em.find(Mockito.eq(Account.class),Mockito.eq(accountId))).thenReturn(expected);

        Account actual = accountDao.getAccount(accountId);
        assertEquals(expected, actual);
        Mockito.verify(em).find(Mockito.eq(Account.class),Mockito.eq(accountId));
        Mockito.verify(mockFactory).createEntityManager();
    }

    @Test(expected = NoRecordsFoundException.class)
    public void testGetAccountNoRecord() throws Exception {
        EntityManager em = Mockito.mock(EntityManager.class);

        EntityManagerFactory mockFactory = Mockito.mock(EntityManagerFactory.class);
        Mockito.when(mockFactory.createEntityManager()).thenReturn(em);


        PowerMockito.mockStatic(Persistence.class);
        PowerMockito.doReturn(mockFactory).when(Persistence.class, "createEntityManagerFactory" , Mockito.anyString());

        AccountDao accountDao = new AccountDao();

        Integer accountId = 10;
        Account expected = null;
        Mockito.when(em.find(Mockito.eq(Account.class),Mockito.eq(accountId))).thenReturn(expected);

        accountDao.getAccount(accountId);
        Mockito.verify(em).find(Mockito.eq(Account.class),Mockito.eq(accountId));
        Mockito.verify(mockFactory).createEntityManager();
    }

    @Test(expected = DataValidationException.class)
    public void testCreateAccountWithMissingData() throws Exception{
        EntityManager em = Mockito.mock(EntityManager.class);

        EntityManagerFactory mockFactory = Mockito.mock(EntityManagerFactory.class);
        Mockito.when(mockFactory.createEntityManager()).thenReturn(em);


        PowerMockito.mockStatic(Persistence.class);
        PowerMockito.doReturn(mockFactory).when(Persistence.class, "createEntityManagerFactory" , Mockito.anyString());

        AccountDao accountDao = new AccountDao();
        EntityTransaction mockTxn = Mockito.mock(EntityTransaction.class);
        Mockito.when(em.getTransaction()).thenReturn(mockTxn);

        Account mockAccount = Mockito.mock(Account.class);

        accountDao.addAccount(mockAccount);

        Mockito.verify(em, Mockito.times(2)).getTransaction();
        Mockito.verify(em).persist(mockAccount);
        Mockito.verify(mockTxn).begin();
        Mockito.verify(mockTxn).commit();
    }


}