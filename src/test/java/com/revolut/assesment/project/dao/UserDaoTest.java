package com.revolut.assesment.project.dao;

import com.revolut.assesment.project.exception.DatabaseException;
import com.revolut.assesment.project.exception.MoreThanOneRecordFoundException;
import com.revolut.assesment.project.exception.NoRecordsFoundException;
import com.revolut.assesment.project.models.User;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class UserDaoTest {

    @Test
    public void testGetAllUsers() throws Exception {
        UserDao userDao = new UserDao();

        QueryRunner mockQueryRunner = Mockito.mock(QueryRunner.class);
        Connection con = Mockito.mock(Connection.class);

        FieldSetter.setField(userDao, UserDao.class.getDeclaredField("queryRunner"), mockQueryRunner);


        ArrayList<User> result = new ArrayList<>();
        Mockito.when(mockQueryRunner.query(Mockito.eq(con), Mockito.anyString(), Mockito.any(ResultSetHandler.class))).thenReturn(result);

        List<User> users = userDao.getUsers(con);
        assertEquals(result, users);
        Mockito.verify(mockQueryRunner).query(Mockito.eq(con), Mockito.anyString(), Mockito.any(ResultSetHandler.class));

    }

    @Test
    public void testCreateUser() throws Exception {
        UserDao userDao = new UserDao();

        QueryRunner mockQueryRunner = Mockito.mock(QueryRunner.class);
        Connection con = Mockito.mock(Connection.class);

        Mockito.when(mockQueryRunner.update(Mockito.eq(con),
                Mockito.anyString(), Mockito.any())).thenReturn(1);

        FieldSetter.setField(userDao, UserDao.class.getDeclaredField("queryRunner"), mockQueryRunner);

        User mockUser = Mockito.mock(User.class);

        boolean condition = userDao.addUser(mockUser, con);
        Mockito.verify(mockQueryRunner).update(Mockito.eq(con), Mockito.anyString(), Mockito.any());
        assertTrue(condition);
    }

    @Test
    public void testGetUser() throws Exception {
        UserDao userDao = new UserDao();

        QueryRunner mockQueryRunner = Mockito.mock(QueryRunner.class);
        Connection con = Mockito.mock(Connection.class);

        FieldSetter.setField(userDao, UserDao.class.getDeclaredField("queryRunner"), mockQueryRunner);


        User result = Mockito.mock(User.class);
        Mockito.when(mockQueryRunner.query(Mockito.eq(con), Mockito.anyString(), Mockito.any(ResultSetHandler.class), Mockito.any(Object[].class))).thenReturn(Arrays.asList(result));

        User user = userDao.getUser(User.builder().build(), con);
        assertEquals(result, user);
        Mockito.verify(mockQueryRunner).query(Mockito.eq(con), Mockito.anyString(), Mockito.any(ResultSetHandler.class), Mockito.any(Object[].class));

    }

    @Test
    public void testGetUsersWithCondition() throws Exception {
        UserDao userDao = new UserDao();

        QueryRunner mockQueryRunner = Mockito.mock(QueryRunner.class);
        Connection con = Mockito.mock(Connection.class);

        FieldSetter.setField(userDao, UserDao.class.getDeclaredField("queryRunner"), mockQueryRunner);


        List<User> result = Arrays.asList(Mockito.mock(User.class));
        Mockito.when(mockQueryRunner.query(Mockito.eq(con), Mockito.anyString(), Mockito.any(ResultSetHandler.class), Mockito.any(Object[].class))).thenReturn(result);

        List<User> users = userDao.getUsers(User.builder().build(), con);
        assertEquals(result, users);
        Mockito.verify(mockQueryRunner).query(Mockito.eq(con), Mockito.anyString(), Mockito.any(ResultSetHandler.class), Mockito.any(Object[].class));

    }

    @Test(expected = NoRecordsFoundException.class)
    public void testGetUserWithNoResultsException() throws Exception {
        UserDao userDao = new UserDao();

        QueryRunner mockQueryRunner = Mockito.mock(QueryRunner.class);
        Connection con = Mockito.mock(Connection.class);

        FieldSetter.setField(userDao, UserDao.class.getDeclaredField("queryRunner"), mockQueryRunner);


        Mockito.when(mockQueryRunner.query(Mockito.eq(con), Mockito.anyString(), Mockito.any(ResultSetHandler.class), Mockito.any(Object[].class))).thenReturn(Arrays.asList());

        userDao.getUser(User.builder().build(), con);
        Mockito.verify(mockQueryRunner).query(Mockito.eq(con), Mockito.anyString(), Mockito.any(ResultSetHandler.class), Mockito.any(Object[].class));

    }

    @Test(expected = MoreThanOneRecordFoundException.class)
    public void testGetUserWithMoreResultsException() throws Exception {
        UserDao userDao = new UserDao();

        QueryRunner mockQueryRunner = Mockito.mock(QueryRunner.class);
        Connection con = Mockito.mock(Connection.class);

        FieldSetter.setField(userDao, UserDao.class.getDeclaredField("queryRunner"), mockQueryRunner);

        User u1 = Mockito.mock(User.class);
        Mockito.when(mockQueryRunner.query(Mockito.eq(con), Mockito.anyString(), Mockito.any(ResultSetHandler.class), Mockito.any(Object[].class))).thenReturn(Arrays.asList(u1, u1));

        userDao.getUser(User.builder().build(), con);
        Mockito.verify(mockQueryRunner).query(Mockito.eq(con), Mockito.anyString(), Mockito.any(ResultSetHandler.class), Mockito.any(Object[].class));

    }

    @Test(expected = DatabaseException.class)
    public void testGetUserWithDBException() throws Exception {
        UserDao userDao = new UserDao();

        QueryRunner mockQueryRunner = Mockito.mock(QueryRunner.class);
        Connection con = Mockito.mock(Connection.class);

        FieldSetter.setField(userDao, UserDao.class.getDeclaredField("queryRunner"), mockQueryRunner);

        Mockito.when(mockQueryRunner.query(Mockito.eq(con), Mockito.anyString(), Mockito.any(ResultSetHandler.class), Mockito.any(Object[].class))).thenThrow(new SQLException());

        userDao.getUser(User.builder().build(), con);
        Mockito.verify(mockQueryRunner).query(Mockito.eq(con), Mockito.anyString(), Mockito.any(ResultSetHandler.class), Mockito.any(Object[].class));

    }

    @Test(expected = DatabaseException.class)
    public void testGetUsersWithDBException() throws Exception {
        UserDao userDao = new UserDao();

        QueryRunner mockQueryRunner = Mockito.mock(QueryRunner.class);
        Connection con = Mockito.mock(Connection.class);

        FieldSetter.setField(userDao, UserDao.class.getDeclaredField("queryRunner"), mockQueryRunner);

        Mockito.when(mockQueryRunner.query(Mockito.eq(con), Mockito.anyString(), Mockito.any(ResultSetHandler.class), Mockito.any(Object[].class))).thenThrow(new SQLException());

        userDao.getUsers(User.builder().build(), con);
        Mockito.verify(mockQueryRunner).query(Mockito.eq(con), Mockito.anyString(), Mockito.any(ResultSetHandler.class), Mockito.any(Object[].class));

    }

    @Test(expected = DatabaseException.class)
    public void testCreateUserWithDBException() throws Exception {
        UserDao userDao = new UserDao();

        QueryRunner mockQueryRunner = Mockito.mock(QueryRunner.class);
        Connection con = Mockito.mock(Connection.class);

        Mockito.when(mockQueryRunner.update(Mockito.eq(con),
                Mockito.anyString(), Mockito.any())).thenThrow(new SQLException());

        FieldSetter.setField(userDao, UserDao.class.getDeclaredField("queryRunner"), mockQueryRunner);

        User mockUser = Mockito.mock(User.class);

        userDao.addUser(mockUser, con);
    }

}