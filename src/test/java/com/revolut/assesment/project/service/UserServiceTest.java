package com.revolut.assesment.project.service;

import com.revolut.assesment.project.dao.UserDao;
import com.revolut.assesment.project.dao.util.SQLiteConnectionManager;
import com.revolut.assesment.project.model.User;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class UserServiceTest {

    @Test
    public void testGetUsers() throws Exception {
        UserDao mockDao = Mockito.mock(UserDao.class);
        SQLiteConnectionManager mockManager = Mockito.mock(SQLiteConnectionManager.class);

        UserService userService = new UserService();

        FieldSetter.setField(userService, userService.getClass().getDeclaredField("connectionManager"), mockManager);
        FieldSetter.setField(userService, userService.getClass().getDeclaredField("userDao"), mockDao);

        Connection mockConnection = Mockito.mock(Connection.class);
        Mockito.when(mockManager.getConnection()).thenReturn(mockConnection);

        List<User> ts = Arrays.asList(Mockito.mock(User.class));
        Mockito.when(mockDao.getUsers(mockConnection)).thenReturn(ts);

        List<User> actual = userService.getUsers();
        assertEquals(ts, actual);
        Mockito.verify(mockManager).getConnection();
        Mockito.verify(mockDao).getUsers(mockConnection);
    }

    @Test
    public void testGetUsersWithCondition() throws Exception {
        UserDao mockDao = Mockito.mock(UserDao.class);
        SQLiteConnectionManager mockManager = Mockito.mock(SQLiteConnectionManager.class);

        UserService userService = new UserService();

        FieldSetter.setField(userService, userService.getClass().getDeclaredField("connectionManager"), mockManager);
        FieldSetter.setField(userService, userService.getClass().getDeclaredField("userDao"), mockDao);

        Connection mockConnection = Mockito.mock(Connection.class);
        Mockito.when(mockManager.getConnection()).thenReturn(mockConnection);

        User u1 = Mockito.mock(User.class);
        Mockito.when(mockDao.getUser(u1, mockConnection)).thenReturn(u1);

        User actual = userService.getUser(u1);
        assertEquals(u1, actual);
        Mockito.verify(mockManager).getConnection();
        Mockito.verify(mockDao).getUser(u1, mockConnection);
    }

    @Test
    public void testCreateUser() throws Exception {
        UserDao mockDao = Mockito.mock(UserDao.class);
        SQLiteConnectionManager mockManager = Mockito.mock(SQLiteConnectionManager.class);

        UserService userService = new UserService();

        FieldSetter.setField(userService, userService.getClass().getDeclaredField("connectionManager"), mockManager);
        FieldSetter.setField(userService, userService.getClass().getDeclaredField("userDao"), mockDao);

        Connection mockConnection = Mockito.mock(Connection.class);
        Mockito.when(mockManager.getConnection()).thenReturn(mockConnection);

        User u1 = Mockito.mock(User.class);
        boolean expected = true;
        Mockito.when(mockDao.addUser(u1, mockConnection)).thenReturn(expected);

        boolean actual = userService.addUser(u1);
        assertTrue(actual);
        Mockito.verify(mockManager).getConnection();
        Mockito.verify(mockDao).addUser(u1, mockConnection);

        System.out.println(new Date());
    }


}