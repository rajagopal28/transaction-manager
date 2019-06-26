package com.revolut.assesment.project.constants;

public interface ApplicationConstants {
    enum AccountType {
        SAVINGS,
        CHECKING,
        CREDIT
    }

    String SQLITE_DB_NAME = "transactions-db";

    String SELECT_ALL_QUERY_p1 = "SELECT T from ";
    String SELECT_ALL_QUERY_p2 = " T ";

    String SELECT_ALL_QUERY_p3 = " WHERE ";

    String RESPONSE_ERROR_DATABAS_ISSUE = "Error accessing database!";
    String RESPONSE_ERROR_RECORD_NOT_CREATED = "Record Creation Failed!";
    String RESPONSE_ERROR_UNABLE_TO_FIND_USER = "Unable to find single User with given data!";

}
