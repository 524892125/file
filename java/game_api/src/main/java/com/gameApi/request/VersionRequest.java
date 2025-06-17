package com.gameApi.request;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class VersionRequest {
    @NotNull(message = "clientVersion is null")
    private String clientVersion;

    @NotNull(message = "channel is null")
    private String channel;

    @NotNull(message = "deviceNo is null")
    @TableField(exist = false)
    private String deviceNo;
}
