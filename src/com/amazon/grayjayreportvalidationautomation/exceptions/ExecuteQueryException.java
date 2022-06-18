package com.amazon.grayjayreportvalidationautomation.exceptions;

public class ExecuteQueryException extends Exception {

    public ExecuteQueryException(String message) {
        super(message);
    }

    public ExecuteQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExecuteQueryException(Throwable cause) {
        super(cause);
    }
}
