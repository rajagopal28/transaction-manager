package com.revolut.assesment.project.dao;

import com.revolut.assesment.project.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Persistence.class, UserDao.class})
public class UserDaoTest {

    @Test
    public void testGetAllUsers() throws Exception {

        EntityManager em = Mockito.mock(EntityManager.class);

        EntityManagerFactory mockFactory = Mockito.mock(EntityManagerFactory.class);
        Mockito.when(mockFactory.createEntityManager()).thenReturn(em);


        PowerMockito.mockStatic(Persistence.class);
        PowerMockito.doReturn(mockFactory).when(Persistence.class, "createEntityManagerFactory" , Mockito.anyString());

        UserDao userDao = new UserDao();

        TypedQuery mockQuery = Mockito.mock(TypedQuery.class);
        CriteriaQuery<User> q = Mockito.mock(CriteriaQuery.class);
        CriteriaBuilder cb = Mockito.mock(CriteriaBuilder.class);
        Root<User> sm = Mockito.mock(Root.class);

        Mockito.when(q.select(Mockito.eq(sm))).thenReturn(q);
        Mockito.when(q.from(Mockito.eq(User.class))).thenReturn(sm);
        Mockito.when(cb.createQuery(Mockito.eq(User.class))).thenReturn(q);
        Mockito.when(em.getCriteriaBuilder()).thenReturn(cb);
        List<User> expected = new ArrayList<>();
        Mockito.when(mockQuery.getResultList()).thenReturn(expected);
        Mockito.when(em.createQuery(Mockito.eq(q))).thenReturn(mockQuery);

        List<User> users = userDao.getUsers();
        assertEquals(expected, users);
        Mockito.verify(mockQuery).getResultList();
        Mockito.verify(em).createQuery(Mockito.eq(q));
        Mockito.verify(em).getCriteriaBuilder();
        Mockito.verify(cb).createQuery(Mockito.eq(User.class));
        Mockito.verify(q).from(Mockito.eq(User.class));
        Mockito.verify(q).select(Mockito.eq(sm));


        Mockito.verify(mockFactory).createEntityManager();

    }

    @Test
    public void testGetUser() throws Exception {

        EntityManager em = Mockito.mock(EntityManager.class);

        EntityManagerFactory mockFactory = Mockito.mock(EntityManagerFactory.class);
        Mockito.when(mockFactory.createEntityManager()).thenReturn(em);


        PowerMockito.mockStatic(Persistence.class);
        PowerMockito.doReturn(mockFactory).when(Persistence.class, "createEntityManagerFactory" , Mockito.anyString());

        UserDao userDao = new UserDao();

        Integer mockUser = 10;
        User expected = Mockito.mock(User.class);
        Mockito.when(em.find(Mockito.eq(User.class),Mockito.eq(mockUser))).thenReturn(expected);

        User actual = userDao.getUser(mockUser);
        assertEquals(expected, actual);
        Mockito.verify(em).find(Mockito.eq(User.class),Mockito.eq(mockUser));
        Mockito.verify(mockFactory).createEntityManager();
    }

    @Test
    public void testCreateUser() throws Exception {
        EntityManager em = Mockito.mock(EntityManager.class);

        EntityManagerFactory mockFactory = Mockito.mock(EntityManagerFactory.class);
        Mockito.when(mockFactory.createEntityManager()).thenReturn(em);


        PowerMockito.mockStatic(Persistence.class);
        PowerMockito.doReturn(mockFactory).when(Persistence.class, "createEntityManagerFactory" , Mockito.anyString());

        UserDao userDao = new UserDao();
        EntityTransaction mockTxn = Mockito.mock(EntityTransaction.class);
        Mockito.when(em.getTransaction()).thenReturn(mockTxn);

        User mockUser = Mockito.mock(User.class);
        userDao.addUser(mockUser);

        Mockito.verify(em, Mockito.times(2)).getTransaction();
        Mockito.verify(em).persist(mockUser);
        Mockito.verify(mockTxn).begin();
        Mockito.verify(mockTxn).commit();
    }

}