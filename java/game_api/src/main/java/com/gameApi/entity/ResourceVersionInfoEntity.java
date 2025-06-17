package com.gameApi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
@Data
@TableName("resource_version_info")
public class ResourceVersionInfoEntity {
    @TableId(type = IdType.AUTO)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer id;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer version_id;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Date created_at;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Date updated_at;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer status;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer is_del;
    @JsonProperty("ResourceVersion")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private String ResourceVersion = "";
    @JsonProperty("CodeVersion")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private String CodeVersion = "";
    @JsonProperty("DataTableVersion")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private String DataTableVersion = "";
    @JsonProperty("AudioVersion")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private String AudioVersion = "";
    @JsonProperty("InternalGameVersion")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private Integer InternalGameVersion = 0;
    @JsonProperty("InternalResourceVersion")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private Integer InternalResourceVersion = 0;
    @JsonProperty("InternalCodeVersion")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private Integer InternalCodeVersion = 0;
    @JsonProperty("InternalDataTableVersion")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private Integer InternalDataTableVersion = 0;
    @JsonProperty("AppleReview")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private Integer AppleReview;
    public Boolean getAppleReview() {
        return this.AppleReview != null && this.AppleReview == 1;
    }
    @JsonProperty("OssPath")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    public String OssPath = "";
    @JsonProperty("AppUrl")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    public String AppUrl = "";
    @JsonProperty("RootPath")
    @TableField(exist = false)
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    public String RootPath;
    public String getRootPath() {
        return this.PathRoot;
    }
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String PathRoot = "";
    @JsonProperty("ParadoxVersion")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private String ParadoxVersion = "";
    @JsonProperty("InternalParadoxVersion")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private Integer InternalParadoxVersion = 0;
    @TableField(exist = false)
    @JsonProperty("Code")
    public Integer Code = 200;
}

