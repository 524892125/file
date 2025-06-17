package com.gameApi.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class NoticeRequest {
    @NotNull(message = "channel is null")
    private String channel;
}
