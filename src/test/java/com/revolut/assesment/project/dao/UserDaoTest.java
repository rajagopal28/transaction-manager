package com.revolut.assesment.project.dao;

import com.revolut.assesment.project.models.User;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
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

}