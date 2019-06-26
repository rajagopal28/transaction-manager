package com.revolut.assesment.project.dao;

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

import static org.junit.Assert.*;

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

        Account mockAccount = Mockito.mock(Account.class);

        User user = User.builder().id(2).build();
        accountDao.addAccount(user, mockAccount);

        Mockito.verify(em, Mockito.times(2)).getTransaction();
        Mockito.verify(em).persist(mockAccount);
        Mockito.verify(mockTxn).begin();
        Mockito.verify(mockAccount).setUser(user);
        Mockito.verify(mockTxn).commit();
    }

    @Test
    public void testGetAccounts() throws Exception {
        EntityManager em = Mockito.mock(EntityManager.class);

        EntityManagerFactory mockFactory = Mockito.mock(EntityManagerFactory.class);
        Mockito.when(mockFactory.createEntityManager()).thenReturn(em);


        PowerMockito.mockStatic(Persistence.class);
        PowerMockito.doReturn(mockFactory).when(Persistence.class, "createEntityManagerFactory" , Mockito.anyString());

        int userId = 3;
        User mockUser = Mockito.mock(User.class);
        Mockito.when(em.find(Mockito.eq(User.class), Mockito.eq(userId))).thenReturn(mockUser);


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


}