package com.gameApi.exception;

import com.gameApi.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MyException.class)
    @ResponseBody
    public ResponseEntity<String> handleCustomException(MyException ex) {
        return new ResponseEntity<>(Result.stringResultWithBom(Result.exception(ex.getMessage(), ex.getCode())), HttpStatus.OK);
    }

    @ExceptionHandler(ErrorException.class)
    @ResponseBody
    public ResponseEntity<String> handleErrorException(ErrorException ex) {
        return new ResponseEntity<>(Result.stringResultWithBom(Result.exception(ex.getMessage(), ex.getCode())), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResponseEntity<String> handleValidationException(BindException ex) {
        log.info("BindException: {}", ex.getBindingResult().getFieldError());
        if ( ex.getBindingResult().getFieldError().isBindingFailure() ) { // 没有设置文字，直接提示
            return new ResponseEntity<>(Result.stringResultWithBom(Result.exception(ex.getBindingResult().getFieldError().getField() + "参数错误", 400)), HttpStatus.OK);
        }
        String message = ex.getBindingResult().getFieldError().getDefaultMessage();
        return new ResponseEntity<>(Result.stringResultWithBom(Result.exception(message, 400)), HttpStatus.OK);
    }
}

