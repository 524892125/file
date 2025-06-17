package com.gameApi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gameApi.common.IntegerToDateUtil;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("gm_tools_announcement")
public class OutNoticeEntity {
    @TableId(type = IdType.AUTO)
    public Long id;
    public String title;
    public String content;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public LocalDateTime start;
    @JsonProperty("startTS")
    public String getStartShow () {
        return IntegerToDateUtil.getLocalDateTimeStr(start);
    }
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public LocalDateTime end;
    @JsonProperty("lastTime")
    public long getEndShow () {
        return IntegerToDateUtil.localDateTimeToSecond(end);
    }
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String server;
    @JsonProperty("priority")
    public Integer sort;
    public Integer status;
    public String picture;
    public Integer type;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String env;
}
