package com.elsebaey.book.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public ResponseEntity<ExceptionResponse> handleException(LockedException exp){
        return ResponseEntity.
                status(UNTHORIZED).
                body(
                        ExceptionResponse.builder()
                                .businessErrorCode(ACCOUNT_LOCKED.getcode())
                                .businessErrorDescription(ACCOUNT_LOCKED.getDescription())
                                .error(exp.getMessage())
                                .build());
    }
}
