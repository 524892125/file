package com.gameApi.exception;

import lombok.Getter;

public class MyException extends RuntimeException {
    private final String message;
    @Getter
    private final Integer code;

    public MyException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

}

