package com.revolut.assesment.project.exception;

public class DatabaseException extends RuntimeException {
    public DatabaseException(Exception ex) {
        super(ex);
    }
}
