package com.revolut.assesment.project.dao;

import com.revolut.assesment.project.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.persistence.*;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DriverManager.class, UserDao.class})
public class UserDaoTest {

    @Test
    public void testGetAllUsers() {

        EntityManager em = Mockito.mock(EntityManager.class);

        EntityManagerFactory mockFactory = Mockito.mock(EntityManagerFactory.class);
        Mockito.when(mockFactory.createEntityManager()).thenReturn(em);

        BDDMockito.given(Persistence.createEntityManagerFactory(Mockito.anyString())).willReturn(mockFactory);


        UserDao userDao = new UserDao();

        Query mockQuery = Mockito.mock(Query.class);

        List<User> expected = new ArrayList<>();
        Mockito.when(mockQuery.getResultList()).thenReturn(expected);
        Mockito.when(em.createQuery(Mockito.anyString())).thenReturn(mockQuery);

        List<User> users = userDao.getUsers(null);
        assertEquals(expected, users);
        Mockito.verify(mockQuery).getResultList();
        Mockito.verify(em).createQuery(Mockito.anyString());
        Mockito.verify(mockFactory).createEntityManager();

    }

    @Test
    public void testGetUser()  {
        UserDao userDao = new UserDao();

        EntityManager em = Mockito.mock(EntityManager.class);

        EntityManagerFactory mockFactory = Mockito.mock(EntityManagerFactory.class);
        Mockito.when(mockFactory.createEntityManager()).thenReturn(em);

        BDDMockito.given(Persistence.createEntityManagerFactory(Mockito.anyString())).willReturn(mockFactory);

        User mockUser = Mockito.mock(User.class);
        User expected = Mockito.mock(User.class);
        Mockito.when(em.find(Mockito.eq(User.class),Mockito.eq(mockUser))).thenReturn(expected);

        User actual = userDao.getUsers(mockUser, null);
        assertEquals(expected, actual);
        Mockito.verify(em).find(Mockito.eq(User.class),Mockito.eq(mockUser));
        Mockito.verify(em).createQuery(Mockito.anyString());
        Mockito.verify(mockFactory).createEntityManager();
    }

    @Test
    public void testCreateUser() {
        UserDao userDao = new UserDao();

        EntityManager em = Mockito.mock(EntityManager.class);

        EntityManagerFactory mockFactory = Mockito.mock(EntityManagerFactory.class);
        Mockito.when(mockFactory.createEntityManager()).thenReturn(em);

        BDDMockito.given(Persistence.createEntityManagerFactory(Mockito.anyString())).willReturn(mockFactory);

        EntityTransaction mockTxn = Mockito.mock(EntityTransaction.class);
        Mockito.when(em.getTransaction()).thenReturn(mockTxn);

        User mockUser = Mockito.mock(User.class);
        userDao.addUser(mockUser, null);

        Mockito.verify(em).getTransaction();
        Mockito.verify(em).persist(mockUser);
        Mockito.verify(mockTxn).begin();
        Mockito.verify(mockTxn).commit();
    }

}