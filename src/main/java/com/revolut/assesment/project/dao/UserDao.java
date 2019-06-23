package com.revolut.assesment.project.dao;

import com.revolut.assesment.project.constants.ApplicationConstants;
import com.revolut.assesment.project.models.User;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserDao {
    private QueryRunner queryRunner = new QueryRunner();

    public List<User> getUsers(Connection conn) throws SQLException {
        ResultSetHandler<List<User>> resultHandler = new BeanListHandler<>(User.class);
        return queryRunner.query(conn, ApplicationConstants.GET_ALL_USERS_QUERY, resultHandler);
    }

    public boolean addUser(User user, Connection conn) throws SQLException {
        int insertedRecords = queryRunner.update(conn,
                ApplicationConstants.INSERT_INTO_USER_QUERY, user.getFirstName(),
                        user.getLastName(), user.getGender(), user.getEmail(),
                        user.getPhoneNumber(), user.getDateOfBirth(),
                        user.getTimeCreated());
        return insertedRecords > 0;
    }


}
