package com.revolut.assesment.project.service;

import com.revolut.assesment.project.dao.UserDao;
import com.revolut.assesment.project.dao.util.SQLiteConnectionManager;
import com.revolut.assesment.project.model.User;

import java.sql.Connection;
import java.util.List;

public class UserService {
    private UserDao userDao = new UserDao();
    private SQLiteConnectionManager connectionManager = new SQLiteConnectionManager();

    public List<User> getUsers() {
        Connection connection = connectionManager.getConnection();
        List<User> users = userDao.getUsers(connection);
        connectionManager.closeConnection(connection);
        return users;
    }

    public User getUser(User user) {
        Connection connection = connectionManager.getConnection();
        User foundUser = userDao.getUser(user, connection);
        connectionManager.closeConnection(connection);
        return foundUser;
    }

    public boolean addUser(User user) {
        Connection connection = connectionManager.getConnection();// auto commit connection
        boolean b = userDao.addUser(user, connection);
        connectionManager.closeConnection(connection);
        return b;
    }
}
