package com.gameApi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
@TableName("resource_version")
public class ResourceVersionEntity {
    @TableId(type = IdType.AUTO)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer id;

    private String channel;

    private String version;
    private String client_version;

    private Date created_at;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Date updated_at;
}
