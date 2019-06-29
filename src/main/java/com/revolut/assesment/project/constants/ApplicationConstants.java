package com.revolut.assesment.project.constants;

public interface ApplicationConstants {
    enum AccountType {
        SAVINGS,
        CHECKING,
        CREDIT
    }

    enum TransactionType {
        CASH_DEPOSIT,
        CHEQUE_DEPOSIT,
        TRANSFER
    }

    String SQLITE_DB_NAME = "transactions-db";

    String RESPONSE_ERROR_GENERIC_MESSAGE = "Internal Server Error! Please check Logs!";
    String RESPONSE_ERROR_RECORD_NOT_CREATED = "Record Creation Failed!";
    String RESPONSE_ERROR_CURRENCY_CONVERSION_NOT_DONE = "Unable to process transaction! Currency Conversion Not enabled!!";
    String RESPONSE_ERROR_INSUFFICIENT_BALANCE = "Unable to process transaction! Insufficient Balance in you Account!!";
    String RESPONSE_ERROR_UNABLE_TO_FIND_USER = "Unable to find single User with given data!";
    String RESPONSE_ERROR_DATA_VALIDATION_FAILED_WITH = "Required Field(s) are Invalid! Fields :";

}
