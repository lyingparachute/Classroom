package com.example.classroom.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailException extends RuntimeException {
    private static final String LOG_ERROR_EXCEPTION_OCCURRED_MSG =
            "An exception occurred, which will cause a '{}' response.";
    private static final String LOG_ERROR_EXCEPTION_OCCURRED_WITH_CAUSE_MSG =
            "An exception occurred, which will cause a '{}' response with cause '{}'.";

    public EmailException(String message) {
        super(message);
        log.warn(LOG_ERROR_EXCEPTION_OCCURRED_MSG, message);
    }

    public EmailException(String message, Throwable cause) {
        super(message, cause);
        log.warn(LOG_ERROR_EXCEPTION_OCCURRED_WITH_CAUSE_MSG, message, cause);
    }
}
