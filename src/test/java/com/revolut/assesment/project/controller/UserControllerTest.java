package com.revolut.assesment.project.controller;

import com.revolut.assesment.project.constants.ApplicationConstants;
import com.revolut.assesment.project.dao.UserDao;
import com.revolut.assesment.project.exception.DataValidationException;
import com.revolut.assesment.project.exception.NoRecordsFoundException;
import com.revolut.assesment.project.model.User;
import com.revolut.assesment.project.vo.MessageVO;
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

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Persistence.class, UserDao.class, UserController.class})
public class UserControllerTest {

    @Before
    public void setupPersistenceMock() throws Exception {
        EntityManagerFactory mockFactory = Mockito.mock(EntityManagerFactory.class);
        PowerMockito.mockStatic(Persistence.class);
        PowerMockito.doReturn(mockFactory).when(Persistence.class, "createEntityManagerFactory" , Mockito.anyString());

    }
    @Test
    public void testGetUsers() throws Exception {

        UserController userController = new UserController();

        UserDao mockService = Mockito.mock(UserDao.class);

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

        UserDao mockService = Mockito.mock(UserDao.class);

        Mockito.when(mockService.getUsers()).thenThrow(new RuntimeException());

        FieldSetter.setField(userController, userController.getClass().getDeclaredField("userService"), mockService);

        Response response = userController.getAllUsers();
        MessageVO actual = (MessageVO)response.getEntity();

        assertEquals(ApplicationConstants.RESPONSE_ERROR_GENERIC_MESSAGE, actual.getMessage());
        assertEquals(500, response.getStatus());
        Mockito.verify(mockService).getUsers();
    }

    @Test
    public void testGetUser() throws Exception {

        UserController userController = new UserController();

        UserDao mockService = Mockito.mock(UserDao.class);

        User expected = Mockito.mock(User.class);
        Mockito.when(mockService.getUser(Mockito.any(Integer.class))).thenReturn(expected);

        FieldSetter.setField(userController, userController.getClass().getDeclaredField("userService"), mockService);

        Response response = userController.getUser(123);
        Object actual = response.getEntity();

        assertEquals(expected, actual);
        assertEquals(200, response.getStatus());
        Mockito.verify(mockService).getUser(Mockito.any(Integer.class));
    }

    @Test
    public void testGetUserExceptionNotFound() throws Exception {

        UserController userController = new UserController();

        UserDao mockService = Mockito.mock(UserDao.class);

        User expected = Mockito.mock(User.class);
        Mockito.when(mockService.getUser(Mockito.any(Integer.class))).thenThrow(new NoRecordsFoundException());

        FieldSetter.setField(userController, userController.getClass().getDeclaredField("userService"), mockService);

        Response response = userController.getUser(123);
        MessageVO actual = (MessageVO)response.getEntity();

        assertEquals(ApplicationConstants.RESPONSE_ERROR_UNABLE_TO_FIND_RECORD, actual.getMessage());
        assertEquals(404, response.getStatus());
        Mockito.verify(mockService).getUser(Mockito.any(Integer.class));
    }

    @Test
    public void testGetUserDatabaseException() throws Exception {

        UserController userController = new UserController();

        UserDao mockService = Mockito.mock(UserDao.class);

        User expected = Mockito.mock(User.class);
        Mockito.when(mockService.getUser(Mockito.any(Integer.class))).thenThrow(new RuntimeException());

        FieldSetter.setField(userController, userController.getClass().getDeclaredField("userService"), mockService);

        Response response = userController.getUser(123);
        MessageVO actual = (MessageVO)response.getEntity();

        assertEquals(ApplicationConstants.RESPONSE_ERROR_GENERIC_MESSAGE, actual.getMessage());
        assertEquals(500, response.getStatus());
        Mockito.verify(mockService).getUser(Mockito.any(Integer.class));
    }

    @Test
    public void testCreateUser() throws Exception {

        UserController userController = new UserController();

        UserDao mockService = Mockito.mock(UserDao.class);

        User expected = Mockito.mock(User.class);


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

        UserDao mockService = Mockito.mock(UserDao.class);

        User mock = Mockito.mock(User.class);

        final String someInvalidField = "someField";
        Mockito.doThrow(new DataValidationException(someInvalidField)).when(mockService).addUser(mock);

        FieldSetter.setField(userController, userController.getClass().getDeclaredField("userService"), mockService);

        Response response = userController.createUser(mock);
        MessageVO actual = (MessageVO)response.getEntity();

        assertEquals(ApplicationConstants.RESPONSE_ERROR_DATA_VALIDATION_FAILED_WITH+ someInvalidField, actual.getMessage());
        assertEquals(400, response.getStatus());
        Mockito.verify(mockService).addUser(Mockito.any(User.class));
    }

    @Test
    public void testCreateUserWithDatabaseException() throws Exception {

        UserController userController = new UserController();

        UserDao mockService = Mockito.mock(UserDao.class);

        User expected = Mockito.mock(User.class);
        Mockito.doThrow(new RuntimeException()).when(mockService).addUser(Mockito.any(User.class));

        FieldSetter.setField(userController, userController.getClass().getDeclaredField("userService"), mockService);

        Response response = userController.createUser(expected);
        MessageVO actual = (MessageVO)response.getEntity();

        assertEquals(ApplicationConstants.RESPONSE_ERROR_GENERIC_MESSAGE, actual.getMessage());
        assertEquals(500, response.getStatus());
        Mockito.verify(mockService).addUser(Mockito.any(User.class));
    }
}