package com.revolut.assesment.project.controller;

import com.revolut.assesment.project.exception.DatabaseException;
import com.revolut.assesment.project.exception.NoRecordsFoundException;
import com.revolut.assesment.project.model.User;
import com.revolut.assesment.project.service.UserService;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;

import static org.junit.Assert.*;

public class UserControllerTest {

    @Test
    public void testGetUsers() throws Exception {

        UserController userController = new UserController();

        UserService mockService = Mockito.mock(UserService.class);

        List<User> expected = Arrays.asList(Mockito.mock(User.class));
        Mockito.when(mockService.getUsers()).thenReturn(expected);

        FieldSetter.setField(userController, userController.getClass().getDeclaredField("userService"), mockService);

        Response response = userController.getAllUsers();
        Object actual = response.getEntity();

        assertEquals(expected, actual);
        assertEquals(200, response.getStatus());
        Mockito.verify(mockService).getUsers();
    }

    @Test
    public void testGetUsersWithError() throws Exception {

        UserController userController = new UserController();

        UserService mockService = Mockito.mock(UserService.class);

        List<User> expected = Arrays.asList(Mockito.mock(User.class));
        Mockito.when(mockService.getUsers()).thenThrow(new DatabaseException(new Exception()));

        FieldSetter.setField(userController, userController.getClass().getDeclaredField("userService"), mockService);

        Response response = userController.getAllUsers();
        Object actual = response.getEntity();

        assertNull(actual);
        assertEquals(500, response.getStatus());
        Mockito.verify(mockService).getUsers();
    }

    @Test
    public void testGetUser() throws Exception {

        UserController userController = new UserController();

        UserService mockService = Mockito.mock(UserService.class);

        User expected = Mockito.mock(User.class);
        Mockito.when(mockService.getUser(Mockito.any(User.class))).thenReturn(expected);

        FieldSetter.setField(userController, userController.getClass().getDeclaredField("userService"), mockService);

        Response response = userController.getUser(123);
        Object actual = response.getEntity();

        assertEquals(expected, actual);
        assertEquals(200, response.getStatus());
        Mockito.verify(mockService).getUser(Mockito.any(User.class));
    }

    @Test
    public void testGetUserExceptionNotFound() throws Exception {

        UserController userController = new UserController();

        UserService mockService = Mockito.mock(UserService.class);

        User expected = Mockito.mock(User.class);
        Mockito.when(mockService.getUser(Mockito.any(User.class))).thenThrow(new NoRecordsFoundException());

        FieldSetter.setField(userController, userController.getClass().getDeclaredField("userService"), mockService);

        Response response = userController.getUser(123);
        Object actual = response.getEntity();

        assertNull(actual);
        assertEquals(404, response.getStatus());
        Mockito.verify(mockService).getUser(Mockito.any(User.class));
    }

    @Test
    public void testGetUserDatabaseException() throws Exception {

        UserController userController = new UserController();

        UserService mockService = Mockito.mock(UserService.class);

        User expected = Mockito.mock(User.class);
        Mockito.when(mockService.getUser(Mockito.any(User.class))).thenThrow(new DatabaseException(new Exception()));

        FieldSetter.setField(userController, userController.getClass().getDeclaredField("userService"), mockService);

        Response response = userController.getUser(123);
        Object actual = response.getEntity();

        assertNull(actual);
        assertEquals(500, response.getStatus());
        Mockito.verify(mockService).getUser(Mockito.any(User.class));
    }

    @Test
    public void testCreateUser() throws Exception {

        UserController userController = new UserController();

        UserService mockService = Mockito.mock(UserService.class);

        User expected = Mockito.mock(User.class);
        Mockito.when(mockService.addUser(Mockito.any(User.class))).thenReturn(true);

        FieldSetter.setField(userController, userController.getClass().getDeclaredField("userService"), mockService);

        Response response = userController.createUser(expected);
        Object actual = response.getEntity();

        assertEquals(expected, actual);
        assertEquals(201, response.getStatus());
        Mockito.verify(mockService).addUser(Mockito.any(User.class));
    }

    @Test
    public void testCreateUserNotCreated() throws Exception {

        UserController userController = new UserController();

        UserService mockService = Mockito.mock(UserService.class);

        User expected = Mockito.mock(User.class);
        Mockito.when(mockService.addUser(Mockito.any(User.class))).thenReturn(false);

        FieldSetter.setField(userController, userController.getClass().getDeclaredField("userService"), mockService);

        Response response = userController.createUser(expected);
        Object actual = response.getEntity();

        assertEquals(expected, actual);
        assertEquals(304, response.getStatus());
        Mockito.verify(mockService).addUser(Mockito.any(User.class));
    }

    @Test
    public void testCreateUserWithDatabaseException() throws Exception {

        UserController userController = new UserController();

        UserService mockService = Mockito.mock(UserService.class);

        User expected = Mockito.mock(User.class);
        Mockito.when(mockService.addUser(Mockito.any(User.class))).thenThrow(new DatabaseException(new Exception()));

        FieldSetter.setField(userController, userController.getClass().getDeclaredField("userService"), mockService);

        Response response = userController.createUser(expected);
        Object actual = response.getEntity();

        assertNull(actual);
        assertEquals(500, response.getStatus());
        Mockito.verify(mockService).addUser(Mockito.any(User.class));
    }
}