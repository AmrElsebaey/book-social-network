package com.elsebaey.book.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum BusinessErrorCodes {
    NO_CODE(0,NOT_IMPLEMENTED,"No code"),
    INCORRECT_CURRENT_PASSWORD(300, BAD_REQUEST,"Incorrect password"),
    NEW_PASSWORD_MISMATCH(301, BAD_REQUEST,"New password mismatch"),
    ACCOUNT_LOCKED(302,FORBIDDEN,"Account locked"),
    ACCOUNT_DISABLED(303,FORBIDDEN,"Account disabled"),
    BAD_CREDENTIALS(304,FORBIDDEN,"Bad credentials"),
    ;
    @Getter
    private final int code;
    @Getter
    private final String description;
    @Getter
    private final HttpStatus httpStatus;

    BusinessErrorCodes(int code, HttpStatus httpStatus, String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
