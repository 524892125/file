package com.gameApi.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gameApi.exception.ErrorException;
import lombok.Getter;

import java.text.SimpleDateFormat;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result {
    private int code;
    private String msg;
    private Object data;

    // 无参构造函数
    public Result() {
    }

    public Result(int i, String success, Object o) {
        this.code = i;
        this.msg = success;
        this.data = o;
    }

    public static Result ok(Object d){
        return new Result(200,"success",d);
    }

    public static Result failed (String message){
        return new Result(500,"failed",null);
    }

    public static Result exception (String d, Integer code){
        return new Result(code, d, null);
    }

    public static String stringResultWithBom (Object data) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return  "\uFEFF" + objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorException("json解析失败", 500);
        }
    }

}
