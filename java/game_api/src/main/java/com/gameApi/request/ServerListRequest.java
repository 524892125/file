package com.gameApi.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ServerListRequest extends  VersionRequest{
    @NotNull(message = "userId is null")
    private Long userId;
}
