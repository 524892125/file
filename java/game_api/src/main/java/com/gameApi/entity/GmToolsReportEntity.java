package com.gameApi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("gm_tools_report")
public class GmToolsReportEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long uid;
    private Long report_uid;
    private String type;
    private String content;
    private Date created_time;
    private Integer status;
    private String remark;
}
