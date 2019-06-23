package com.revolut.assesment.project.dao.util;


import com.revolut.assesment.project.constants.ApplicationConstants;
import com.revolut.assesment.project.exception.DatabaseException;
import org.apache.commons.dbutils.QueryRunner;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnectionManager {

    private QueryRunner queryRunner = new QueryRunner();

    private final String SQLITE_JDBC_PREFIX = "jdbc:sqlite:";
    private final String SQLITE_DB_FILE_PATH = "transactions.db"; // relative path of the DB file in resources folder

    public Connection getConnection() {
        return getConnection(true);
    }

    public Connection getConnection(boolean enableAutoCommit) {
        try {
            URL res = getClass().getClassLoader().getResource(SQLITE_DB_FILE_PATH);
            File file = Paths.get(res.toURI()).toFile();
            String absolutePath = file.getAbsolutePath();

            String connectionURL = SQLITE_JDBC_PREFIX + absolutePath;

            Connection connection = DriverManager.getConnection(connectionURL);
            connection.setAutoCommit(enableAutoCommit);

            return connection;
        } catch (URISyntaxException | SQLException e ) {
            throw new DatabaseException(e);
        }

    }

    public void commitAndCloseConnection(Connection connection) {
        try {
            if(connection != null) {
                connection.commit();
                closeConnection(connection);
            }
        } catch (SQLException se) {
            throw new DatabaseException(se);
        }

    }

    public void closeConnection(Connection connection) {
        try {
            if(connection != null) {
                connection.close();
            }
        } catch (SQLException se) {
            throw new DatabaseException(se);
        }

    }

    public boolean createTable(Connection conn) throws SQLException {
        try {
            int insertedRecords = queryRunner.update(conn,
                    ApplicationConstants.CREATE_USER_TABLE_QUERY);
            return insertedRecords > 0;
        } catch (SQLException se) {
            throw new DatabaseException(se);
        }
    }

}
