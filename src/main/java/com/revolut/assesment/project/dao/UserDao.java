package com.revolut.assesment.project.dao;

import com.revolut.assesment.project.constants.ApplicationConstants;
import com.revolut.assesment.project.dao.util.QueryUtil;
import com.revolut.assesment.project.exception.DatabaseException;
import com.revolut.assesment.project.exception.MoreThanOneRecordFoundException;
import com.revolut.assesment.project.exception.NoRecordsFoundException;
import com.revolut.assesment.project.model.User;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private QueryRunner queryRunner = new QueryRunner();

    public List<User> getUsers(Connection conn) {
        try {
            ResultSetHandler<List<User>> resultHandler = new BeanListHandler<>(User.class);
            return queryRunner.query(conn, ApplicationConstants.GET_ALL_USERS_QUERY, resultHandler);
        } catch (SQLException se) {
            throw new DatabaseException(se);
        }

    }

    public boolean addUser(User user, Connection conn) {
        try {
            int insertedRecords = queryRunner.update(conn,
                    ApplicationConstants.INSERT_INTO_USER_QUERY, user.getFirstName(),
                    user.getLastName(), user.getGender(), user.getEmail(),
                    user.getPhoneNumber(), user.getDob(),
                    new Date(System.currentTimeMillis()));
            return insertedRecords > 0;
        } catch (SQLException se) {
            throw new DatabaseException(se);
        }
    }

    public List<User> getUsers(User user, Connection conn) {
        try {
            ResultSetHandler<List<User>> resultHandler = new BeanListHandler<>(User.class);
            ArrayList<Object> params = new ArrayList<>();
            String sql = ApplicationConstants.GET_ALL_USERS_QUERY + QueryUtil.getFindUserWhereClause(user, params);
            return queryRunner.query(conn, sql, resultHandler, params.toArray());
        } catch (SQLException se) {
            throw new DatabaseException(se);
        }
    }

    public User getUser(User user, Connection conn) {
        List<User> users = getUsers(user, conn);
        if(users.isEmpty()) {
            throw new NoRecordsFoundException();
        }
        if(users.size() > 1) {
            throw new MoreThanOneRecordFoundException();
        }
        return users.get(0);
    }

}
